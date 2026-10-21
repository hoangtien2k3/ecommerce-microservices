package com.ecommerce.promotion.service;

import com.ecommerce.promotion.config.PromotionIssueProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Keeps the coupon caches in step with backoffice changes, after the change commits.
 * A Redis failure here never fails the backoffice call: missing keys are rebuilt from
 * MySQL on the next claim.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CouponStoreSync {

    private final CouponStockStore couponStockStore;
    private final CouponWindowCache couponWindowCache;
    private final PromotionIssueProperties properties;

    /** A new first-come coupon: seed its stock so the first claims need no rebuild. */
    public void created(Long promotionId, Integer issueLimit) {
        if (issueLimit == null) {
            return;
        }
        afterCommit(() -> {
            if (usesRedis()) {
                couponStockStore.prime(promotionId, issueLimit);
            }
        });
    }

    /** Open window or active flag may have changed: drop this pod's cached copy. */
    public void updated(Long promotionId) {
        afterCommit(() -> couponWindowCache.evict(promotionId));
    }

    public void deleted(Long promotionId) {
        afterCommit(() -> {
            couponWindowCache.evict(promotionId);
            if (usesRedis()) {
                couponStockStore.evict(promotionId);
            }
        });
    }

    private boolean usesRedis() {
        return properties.getStore() == PromotionIssueProperties.Store.REDIS;
    }

    private static void afterCommit(Runnable action) {
        Runnable safe = () -> {
            try {
                action.run();
            } catch (DataAccessException e) {
                log.warn("Could not update the coupon store: {}", e.getMessage());
            }
        };
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            safe.run();
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                safe.run();
            }
        });
    }
}
