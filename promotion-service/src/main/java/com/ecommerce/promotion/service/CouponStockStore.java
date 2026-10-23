package com.ecommerce.promotion.service;

import com.ecommerce.promotion.repository.PromotionIssueRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiPredicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

/**
 * First-come coupon state in Redis, per promotion:
 * <ul>
 *   <li>{@code coupon:{id}:stock} — coupons still left</li>
 *   <li>{@code coupon:{id}:users} — ids of users who already claimed one</li>
 *   <li>{@code coupon:{id}:pending} — claims whose MySQL row is not confirmed yet, with the
 *       claim time</li>
 * </ul>
 * The {@code {id}} hash tag keeps both keys in one slot, so a script can touch them together
 * on a Redis Cluster too.
 *
 * <p>MySQL ({@code promotion_issue}) stays the source of truth. When the keys are missing
 * (first claim, or Redis lost its data) they are rebuilt from it; one pod rebuilds while the
 * others wait, so a burst of first claims does not read the issue table a thousand times.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CouponStockStore {

    private static final int LOAD_BATCH = 1000;
    private static final Duration LOAD_LOCK_TTL = Duration.ofSeconds(10);
    private static final Duration LOAD_WAIT = Duration.ofSeconds(3);
    private static final long LOAD_POLL_MILLIS = 50;

    private static final RedisScript<Long> CLAIM =
            RedisScript.of(new ClassPathResource("redis/coupon-claim.lua"), Long.class);
    private static final RedisScript<Long> UNCLAIM =
            RedisScript.of(new ClassPathResource("redis/coupon-unclaim.lua"), Long.class);

    private final StringRedisTemplate redis;
    private final PromotionIssueRepository promotionIssueRepository;
    // ReentrantLock rather than synchronized: virtual threads would pin their carrier on a monitor.
    private final ConcurrentHashMap<Long, ReentrantLock> loadLocks = new ConcurrentHashMap<>();

    static String stockKey(Long promotionId) {
        return "coupon:{" + promotionId + "}:stock";
    }

    static String usersKey(Long promotionId) {
        return "coupon:{" + promotionId + "}:users";
    }

    static String pendingKey(Long promotionId) {
        return "coupon:{" + promotionId + "}:pending";
    }

    private static List<String> claimKeys(Long promotionId) {
        return List.of(stockKey(promotionId), usersKey(promotionId), pendingKey(promotionId));
    }

    private static String loadLockKey(Long promotionId) {
        return "coupon:{" + promotionId + "}:loading";
    }

    public enum ClaimResult {
        CLAIMED,
        SOLD_OUT,
        ALREADY_CLAIMED,
        /** Redis has no state for this coupon yet; call {@link #loadIfAbsent} and try again. */
        NOT_LOADED
    }

    /** Takes one coupon for the user if any are left and the user has none yet. */
    public ClaimResult tryClaim(Long promotionId, String userId) {
        Long result = redis.execute(CLAIM, claimKeys(promotionId), userId,
                String.valueOf(Instant.now().toEpochMilli()));
        return switch (result == null ? -1 : result.intValue()) {
            case 0 -> ClaimResult.CLAIMED;
            case 1 -> ClaimResult.SOLD_OUT;
            case 2 -> ClaimResult.ALREADY_CLAIMED;
            default -> ClaimResult.NOT_LOADED;
        };
    }

    /** The claim's MySQL row is written: it no longer needs to be checked. */
    public void confirmClaim(Long promotionId, String userId) {
        redis.opsForHash().delete(pendingKey(promotionId), userId);
    }

    /**
     * Undoes {@link #tryClaim} after the MySQL write failed.
     *
     * @param forgetUser false when MySQL already holds this user's coupon, so they stay a claimer
     */
    public void undoClaim(Long promotionId, String userId, boolean forgetUser) {
        redis.execute(UNCLAIM, claimKeys(promotionId), userId, forgetUser ? "1" : "0");
    }

    /**
     * Settles claims still pending after {@code olderThan}: confirmed if MySQL has the row,
     * given back if not. Such a claim is left behind when the caller stopped waiting for
     * Redis (timeout) after the script had already run, or the pod died before writing.
     *
     * @param issuedInMysql (promotion id, user id) → whether the issue row exists
     * @return number of claims given back
     */
    public int settleStaleClaims(Duration olderThan, BiPredicate<Long, String> issuedInMysql) {
        long cutoff = Instant.now().minus(olderThan).toEpochMilli();
        int givenBack = 0;
        ScanOptions pattern = ScanOptions.scanOptions().match("coupon:{*}:pending").count(100).build();
        try (Cursor<String> keys = redis.scan(pattern)) {
            while (keys.hasNext()) {
                String key = keys.next();
                Long promotionId = Long.valueOf(key.substring(key.indexOf('{') + 1, key.indexOf('}')));
                for (Map.Entry<Object, Object> claim : redis.opsForHash().entries(key).entrySet()) {
                    String userId = (String) claim.getKey();
                    if (Long.parseLong((String) claim.getValue()) > cutoff) {
                        continue;
                    }
                    if (issuedInMysql.test(promotionId, userId)) {
                        confirmClaim(promotionId, userId);
                    } else {
                        undoClaim(promotionId, userId, true);
                        givenBack++;
                        log.warn("Gave back coupon {} claimed by {}: no issue row after {}", promotionId, userId, olderThan);
                    }
                }
            }
        }
        return givenBack;
    }

    /**
     * How many of each coupon are gone, read from the live stock in one round trip.
     * Coupons Redis has no state for are left out.
     *
     * @param issueLimits promotion id to its total quantity
     */
    public Map<Long, Integer> issuedCounts(Map<Long, Integer> issueLimits) {
        List<Long> ids = new ArrayList<>(issueLimits.keySet());
        List<String> stocks = redis.opsForValue().multiGet(ids.stream().map(CouponStockStore::stockKey).toList());
        Map<Long, Integer> issued = new HashMap<>();
        for (int i = 0; i < ids.size(); i++) {
            String stock = stocks == null ? null : stocks.get(i);
            if (stock != null) {
                int limit = issueLimits.get(ids.get(i));
                issued.put(ids.get(i), Math.min(limit, Math.max(0, limit - Integer.parseInt(stock))));
            }
        }
        return issued;
    }

    /** Seeds a coupon that has no claims yet. Does nothing if its stock is already there. */
    public void prime(Long promotionId, int issueLimit) {
        redis.opsForValue().setIfAbsent(stockKey(promotionId), String.valueOf(Math.max(0, issueLimit)));
    }

    /** Removes a coupon's keys, e.g. after the promotion is deleted. */
    public void evict(Long promotionId) {
        redis.delete(claimKeys(promotionId));
    }

    /**
     * Rebuilds stock and claimers from MySQL unless Redis already has them.
     *
     * @return false if another pod was rebuilding and did not finish in time
     */
    public boolean loadIfAbsent(Long promotionId, int issueLimit) {
        ReentrantLock local = loadLocks.computeIfAbsent(promotionId, id -> new ReentrantLock());
        local.lock();
        try {
            if (isLoaded(promotionId)) {
                return true;
            }
            Boolean acquired = redis.opsForValue().setIfAbsent(loadLockKey(promotionId), "1", LOAD_LOCK_TTL);
            if (!Boolean.TRUE.equals(acquired)) {
                return waitForLoad(promotionId);
            }
            try {
                if (!isLoaded(promotionId)) {
                    load(promotionId, issueLimit);
                }
                return true;
            } finally {
                redis.delete(loadLockKey(promotionId));
            }
        } finally {
            local.unlock();
        }
    }

    private void load(Long promotionId, int issueLimit) {
        List<String> userIds = promotionIssueRepository.findUserIdsByPromotionId(promotionId);
        // Claimers first, stock last: until stock exists every claim asks for a reload,
        // so nobody is admitted against a half-built claimer set.
        redis.delete(List.of(usersKey(promotionId), pendingKey(promotionId)));
        for (int from = 0; from < userIds.size(); from += LOAD_BATCH) {
            List<String> batch = userIds.subList(from, Math.min(from + LOAD_BATCH, userIds.size()));
            redis.opsForSet().add(usersKey(promotionId), batch.toArray(String[]::new));
        }
        int remaining = Math.max(0, issueLimit - userIds.size());
        redis.opsForValue().set(stockKey(promotionId), String.valueOf(remaining));
        log.info("Loaded coupon {} into Redis: {} left, {} claimed", promotionId, remaining, userIds.size());
    }

    private boolean isLoaded(Long promotionId) {
        return Boolean.TRUE.equals(redis.hasKey(stockKey(promotionId)));
    }

    private boolean waitForLoad(Long promotionId) {
        long deadline = System.nanoTime() + LOAD_WAIT.toNanos();
        while (System.nanoTime() < deadline) {
            if (isLoaded(promotionId)) {
                return true;
            }
            try {
                Thread.sleep(LOAD_POLL_MILLIS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return isLoaded(promotionId);
    }
}
