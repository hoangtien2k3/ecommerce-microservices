package com.ecommerce.promotion.service;

import com.ecommerce.commonlib.exception.BusinessException;
import com.ecommerce.commonlib.exception.ErrorCode;
import com.ecommerce.commonlib.security.AuthenticationUtils;
import com.ecommerce.promotion.config.PromotionIssueProperties;
import com.ecommerce.promotion.model.Promotion;
import com.ecommerce.promotion.model.PromotionIssue;
import com.ecommerce.promotion.repository.PromotionIssueRepository;
import com.ecommerce.promotion.repository.PromotionRepository;
import com.ecommerce.promotion.service.CouponStockStore.ClaimResult;
import com.ecommerce.promotion.service.CouponWindowCache.CouponWindow;
import com.ecommerce.promotion.viewmodel.PromotionIssueVm;
import java.time.Instant;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Hands out first-come coupons, one per user, never more than {@code issueLimit}.
 *
 * <ul>
 *   <li><b>redis</b> (default): a Lua script checks the claimer set and decrements the stock in
 *       one step, so claims do not queue on a row lock; only winners insert into
 *       {@code promotion_issue}. If that insert fails the claim is given back in Redis.</li>
 *   <li><b>db</b>: a conditional UPDATE on the promotion row, then the insert, in one
 *       transaction. Kept for comparing the two under load.</li>
 * </ul>
 */
@Slf4j
@Service
public class PromotionIssueService {

    private static final String DUPLICATE_ISSUE_CONSTRAINT = "uk_promotion_issue_promotion_user";

    private final PromotionRepository promotionRepository;
    private final PromotionIssueRepository promotionIssueRepository;
    private final CouponStockStore couponStockStore;
    private final CouponWindowCache couponWindowCache;
    private final PromotionIssueProperties properties;
    private final TransactionTemplate transactionTemplate;

    public PromotionIssueService(PromotionRepository promotionRepository,
                                 PromotionIssueRepository promotionIssueRepository,
                                 CouponStockStore couponStockStore,
                                 CouponWindowCache couponWindowCache,
                                 PromotionIssueProperties properties,
                                 PlatformTransactionManager transactionManager) {
        this.promotionRepository = promotionRepository;
        this.promotionIssueRepository = promotionIssueRepository;
        this.couponStockStore = couponStockStore;
        this.couponWindowCache = couponWindowCache;
        this.properties = properties;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public PromotionIssueVm issue(Long promotionId) {
        String userId = AuthenticationUtils.requireUserId();
        Instant now = Instant.now();
        return properties.getStore() == PromotionIssueProperties.Store.REDIS
                ? issueWithRedis(promotionId, userId, now)
                : transactionTemplate.execute(status -> issueWithDatabase(promotionId, userId, now));
    }

    // ------------------------------------------------------------------
    // redis
    // ------------------------------------------------------------------

    private PromotionIssueVm issueWithRedis(Long promotionId, String userId, Instant now) {
        CouponWindow window = couponWindowCache.get(promotionId)
                .orElseThrow(() -> BusinessException.notFound("promotion.coupon.not.found", promotionId));
        if (!window.isOpen(now)) {
            throw BusinessException.of(ErrorCode.COUPON_NOT_OPEN);
        }
        switch (claimInRedis(window, userId)) {
            case SOLD_OUT -> throw BusinessException.of(ErrorCode.COUPON_SOLD_OUT);
            case ALREADY_CLAIMED -> throw BusinessException.of(ErrorCode.COUPON_ALREADY_CLAIMED);
            case NOT_LOADED -> throw BusinessException.of(ErrorCode.COUPON_STORE_UNAVAILABLE);
            case CLAIMED -> {
                // fall through to the MySQL write below
            }
        }
        PromotionIssueVm issued;
        try {
            issued = transactionTemplate.execute(status -> save(promotionId, userId, now));
        } catch (DataIntegrityViolationException ex) {
            if (isDuplicateIssue(ex)) {
                // MySQL already has this user's coupon: keep them a claimer, give the stock back.
                undoClaim(promotionId, userId, false);
                throw BusinessException.of(ErrorCode.COUPON_ALREADY_CLAIMED);
            }
            undoClaim(promotionId, userId, true);
            throw ex;
        } catch (RuntimeException ex) {
            undoClaim(promotionId, userId, true);
            throw ex;
        }
        try {
            couponStockStore.confirmClaim(promotionId, userId);
        } catch (DataAccessException e) {
            // Harmless: StaleClaimSettler finds the row in MySQL and confirms it later.
            log.debug("Could not confirm coupon {} for {} in Redis: {}", promotionId, userId, e.getMessage());
        }
        return issued;
    }

    private ClaimResult claimInRedis(CouponWindow window, String userId) {
        Long promotionId = window.promotionId();
        ClaimResult result = redisCall(() -> couponStockStore.tryClaim(promotionId, userId));
        if (result != ClaimResult.NOT_LOADED) {
            return result;
        }
        boolean loaded = redisCall(() -> couponStockStore.loadIfAbsent(promotionId, window.issueLimit()));
        return loaded ? redisCall(() -> couponStockStore.tryClaim(promotionId, userId)) : ClaimResult.NOT_LOADED;
    }

    private void undoClaim(Long promotionId, String userId, boolean forgetUser) {
        try {
            couponStockStore.undoClaim(promotionId, userId, forgetUser);
        } catch (DataAccessException e) {
            // Stock stays one short until the keys are rebuilt; never over-issues.
            log.error("Could not give back coupon {} claimed by {} in Redis", promotionId, userId, e);
        }
    }

    /** A Redis failure stops issuing (503) instead of falling back to MySQL with stale stock. */
    private static <T> T redisCall(Supplier<T> call) {
        try {
            return call.get();
        } catch (DataAccessException e) {
            log.warn("Coupon store unavailable: {}", e.getMessage());
            throw BusinessException.of(ErrorCode.COUPON_STORE_UNAVAILABLE);
        }
    }

    // ------------------------------------------------------------------
    // db
    // ------------------------------------------------------------------

    private PromotionIssueVm issueWithDatabase(Long promotionId, String userId, Instant now) {
        if (promotionRepository.claimIssueSlot(promotionId, now) == 0) {
            throw explainClosedClaim(promotionId, now);
        }
        try {
            return save(promotionId, userId, now);
        } catch (DataIntegrityViolationException ex) {
            if (isDuplicateIssue(ex)) {
                throw BusinessException.of(ErrorCode.COUPON_ALREADY_CLAIMED);
            }
            throw ex;
        }
    }

    private BusinessException explainClosedClaim(Long promotionId, Instant now) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> BusinessException.notFound("promotion.coupon.not.found", promotionId));
        if (!CouponWindow.of(promotion).isOpen(now)) {
            return BusinessException.of(ErrorCode.COUPON_NOT_OPEN);
        }
        return BusinessException.of(ErrorCode.COUPON_SOLD_OUT);
    }

    // ------------------------------------------------------------------
    // shared
    // ------------------------------------------------------------------

    private PromotionIssueVm save(Long promotionId, String userId, Instant now) {
        PromotionIssue saved = promotionIssueRepository.saveAndFlush(PromotionIssue.builder()
                .promotion(promotionRepository.getReferenceById(promotionId))
                .userId(userId)
                .issuedAt(now)
                .build());
        return new PromotionIssueVm(saved.getId(), promotionId, userId, saved.getIssuedAt());
    }

    private boolean isDuplicateIssue(DataIntegrityViolationException ex) {
        String message = ex.getMostSpecificCause().getMessage();
        return message != null && message.contains(DUPLICATE_ISSUE_CONSTRAINT);
    }
}
