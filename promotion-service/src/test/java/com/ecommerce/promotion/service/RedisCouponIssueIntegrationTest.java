package com.ecommerce.promotion.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ecommerce.commonlib.exception.BusinessException;
import com.ecommerce.promotion.model.Promotion;
import com.ecommerce.promotion.model.PromotionIssue;
import com.ecommerce.promotion.model.enumeration.ApplyTo;
import com.ecommerce.promotion.model.enumeration.DiscountType;
import com.ecommerce.promotion.model.enumeration.UsageType;
import com.ecommerce.promotion.repository.PromotionIssueRepository;
import com.ecommerce.promotion.repository.PromotionRepository;
import com.ecommerce.promotion.viewmodel.StorefrontCouponVm;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** Claims against real MySQL and Redis. Skipped when Docker is not available. */
@SpringBootTest(properties = {
        "promotion.issue.store=redis",
        // the test calls the synchronizer itself
        "promotion.issue.count-sync-interval=1h",
        // the tests settle stale claims themselves, with their own age
        "promotion.issue.stale-claim-after=1h"
})
@Testcontainers(disabledWithoutDocker = true)
class RedisCouponIssueIntegrationTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");

    @Container
    @ServiceConnection(name = "redis")
    static GenericContainer<?> redis = new GenericContainer<>("redis:7.4-alpine").withExposedPorts(6379);

    @Autowired
    private PromotionIssueService issueService;
    @Autowired
    private StorefrontCouponService storefrontCouponService;
    @Autowired
    private IssuedCountSynchronizer issuedCountSynchronizer;
    @Autowired
    private CouponStockStore couponStockStore;
    @Autowired
    private PromotionRepository promotionRepository;
    @Autowired
    private PromotionIssueRepository promotionIssueRepository;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @AfterEach
    void clearSecurity() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void neverIssuesMoreThanTheLimitUnderConcurrency() throws Exception {
        Promotion coupon = openCoupon(50);

        List<String> outcomes = claimConcurrently(coupon.getId(), 300, i -> "user-" + i);

        Map<String, Long> counts = tally(outcomes);
        assertEquals(50, counts.get("OK"));
        assertEquals(250, counts.get("PRM-6002"));
        assertEquals(50, issuedRows(coupon));
        assertEquals("0", stock(coupon));
    }

    @Test
    void sameUserGetsOneCouponEvenWhenRacing() throws Exception {
        Promotion coupon = openCoupon(10);

        List<String> outcomes = claimConcurrently(coupon.getId(), 20, i -> "same-user");

        Map<String, Long> counts = tally(outcomes);
        assertEquals(1, counts.get("OK"));
        assertEquals(19, counts.get("PRM-6003"));
        assertEquals(1, issuedRows(coupon));
        assertEquals("9", stock(coupon));
    }

    @Test
    void rebuildsRedisFromMysqlWhenKeysAreMissing() {
        Promotion coupon = openCoupon(5);
        for (String user : List.of("a", "b", "c")) {
            insertIssue(coupon, user);
        }
        couponStockStore.evict(coupon.getId());

        assertEquals("PRM-6003", claim(coupon.getId(), "a"));
        assertEquals("OK", claim(coupon.getId(), "d"));
        assertEquals("1", stock(coupon));
    }

    @Test
    void mysqlDuplicateGivesTheStockBack() {
        Promotion coupon = openCoupon(5);
        assertTrue(couponStockStore.loadIfAbsent(coupon.getId(), 5));
        // A row Redis does not know about, e.g. written while Redis was being rebuilt.
        insertIssue(coupon, "x");

        assertEquals("PRM-6003", claim(coupon.getId(), "x"));
        assertEquals("5", stock(coupon));
        assertTrue(Boolean.TRUE.equals(
                redisTemplate.opsForSet().isMember(CouponStockStore.usersKey(coupon.getId()), "x")));
    }

    @Test
    void claimWithoutMysqlRowIsGivenBack() {
        Promotion coupon = openCoupon(5);
        assertTrue(couponStockStore.loadIfAbsent(coupon.getId(), 5));
        // What a Redis timeout leaves behind: the script ran, the caller gave up before MySQL.
        assertEquals(CouponStockStore.ClaimResult.CLAIMED, couponStockStore.tryClaim(coupon.getId(), "ghost"));
        assertEquals("PRM-6003", claim(coupon.getId(), "ghost"));
        assertEquals("4", stock(coupon));

        // Other tests' claims share this Redis, so check this coupon rather than the returned count.
        couponStockStore.settleStaleClaims(Duration.ZERO, this::issuedInMysql);

        assertEquals("5", stock(coupon));
        assertEquals("OK", claim(coupon.getId(), "ghost"));
        assertEquals("4", stock(coupon));
    }

    @Test
    void settlingKeepsClaimsThatReachedMysql() {
        Promotion coupon = openCoupon(5);
        assertEquals("OK", claim(coupon.getId(), "done"));
        // Confirmed on success, so nothing is left pending.
        assertEquals(0L, redisTemplate.opsForHash().size(CouponStockStore.pendingKey(coupon.getId())));

        // A claim whose row was written but whose confirmation was lost.
        assertEquals(CouponStockStore.ClaimResult.CLAIMED, couponStockStore.tryClaim(coupon.getId(), "late"));
        insertIssue(coupon, "late");

        couponStockStore.settleStaleClaims(Duration.ZERO, this::issuedInMysql);
        assertEquals("3", stock(coupon));
        assertEquals(0L, redisTemplate.opsForHash().size(CouponStockStore.pendingKey(coupon.getId())));
    }

    @Test
    void recentClaimsAreLeftAlone() {
        Promotion coupon = openCoupon(5);
        assertTrue(couponStockStore.loadIfAbsent(coupon.getId(), 5));
        assertEquals(CouponStockStore.ClaimResult.CLAIMED, couponStockStore.tryClaim(coupon.getId(), "inflight"));

        couponStockStore.settleStaleClaims(Duration.ofMinutes(1), this::issuedInMysql);
        assertEquals("4", stock(coupon));
        assertTrue(Boolean.TRUE.equals(
                redisTemplate.opsForHash().hasKey(CouponStockStore.pendingKey(coupon.getId()), "inflight")));
    }

    @Test
    void upcomingCouponIsNotOpenAndUnknownCouponIsNotFound() {
        Instant now = Instant.now();
        Promotion upcoming = saveCoupon(5, now.plus(1, ChronoUnit.DAYS), now.plus(2, ChronoUnit.DAYS));

        assertEquals("PRM-6001", claim(upcoming.getId(), "u"));
        assertEquals("ERR-0404", claim(999_999L, "u"));
    }

    @Test
    void listShowsLiveCountAndWhetherTheCallerClaimed() {
        Promotion coupon = openCoupon(3);
        assertEquals("OK", claim(coupon.getId(), "lister"));
        assertEquals("OK", claim(coupon.getId(), "other"));

        StorefrontCouponVm mine = asUser("lister", this::findInList).apply(coupon.getId());
        assertEquals(2, mine.issuedCount());
        assertTrue(mine.claimed());

        SecurityContextHolder.clearContext();
        StorefrontCouponVm anonymous = findInList(coupon.getId());
        assertFalse(anonymous.claimed());
    }

    @Test
    void synchronizerCopiesIssuedRowsIntoIssuedCount() {
        Promotion coupon = openCoupon(5);
        assertEquals("OK", claim(coupon.getId(), "s1"));
        assertEquals("OK", claim(coupon.getId(), "s2"));
        assertEquals(0, promotionRepository.findById(coupon.getId()).orElseThrow().getIssuedCount());

        issuedCountSynchronizer.sync();

        assertEquals(2, promotionRepository.findById(coupon.getId()).orElseThrow().getIssuedCount());
    }

    // ------------------------------------------------------------------

    private Promotion openCoupon(int issueLimit) {
        Instant now = Instant.now();
        return saveCoupon(issueLimit, now.minus(1, ChronoUnit.HOURS), now.plus(1, ChronoUnit.HOURS));
    }

    private Promotion saveCoupon(int issueLimit, Instant start, Instant end) {
        return promotionRepository.save(Promotion.builder()
                .name("test coupon")
                .slug("test-" + UUID.randomUUID())
                .discountType(DiscountType.FIXED)
                .discountAmount(1000L)
                .usageType(UsageType.UNLIMITED)
                .applyTo(ApplyTo.PRODUCT)
                .isActive(true)
                .startDate(start)
                .endDate(end)
                .issueLimit(issueLimit)
                .build());
    }

    private void insertIssue(Promotion coupon, String userId) {
        promotionIssueRepository.saveAndFlush(PromotionIssue.builder()
                .promotion(coupon).userId(userId).issuedAt(Instant.now()).build());
    }

    private StorefrontCouponVm findInList(Long promotionId) {
        return storefrontCouponService.listCoupons().stream()
                .filter(c -> c.promotionId().equals(promotionId))
                .findFirst().orElseThrow();
    }

    /** "OK" or the error code of the BusinessException thrown. */
    private String claim(Long promotionId, String userId) {
        return asUser(userId, (Long id) -> {
            try {
                issueService.issue(id);
                return "OK";
            } catch (BusinessException e) {
                return e.getErrorCode();
            }
        }).apply(promotionId);
    }

    private List<String> claimConcurrently(Long promotionId, int requests, Function<Integer, String> userOf)
            throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(64);
        try {
            List<Callable<String>> tasks = new ArrayList<>();
            for (int i = 0; i < requests; i++) {
                String userId = userOf.apply(i);
                tasks.add(() -> claim(promotionId, userId));
            }
            List<String> outcomes = new ArrayList<>();
            for (Future<String> f : pool.invokeAll(tasks)) {
                outcomes.add(f.get());
            }
            return outcomes;
        } finally {
            pool.shutdown();
            SecurityContextHolder.clearContext();
        }
    }

    private static <T, R> Function<T, R> asUser(String userId, Function<T, R> action) {
        return arg -> {
            Jwt jwt = Jwt.withTokenValue("t").header("alg", "none").subject(userId).build();
            SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
            return action.apply(arg);
        };
    }

    private static Map<String, Long> tally(List<String> outcomes) {
        return outcomes.stream().collect(Collectors.groupingBy(o -> o, Collectors.counting()));
    }

    private long issuedRows(Promotion coupon) {
        return promotionIssueRepository.findUserIdsByPromotionId(coupon.getId()).size();
    }

    private boolean issuedInMysql(Long promotionId, String userId) {
        return promotionIssueRepository.findByPromotion_IdAndUserId(promotionId, userId).isPresent();
    }

    private String stock(Promotion coupon) {
        return redisTemplate.opsForValue().get(CouponStockStore.stockKey(coupon.getId()));
    }
}
