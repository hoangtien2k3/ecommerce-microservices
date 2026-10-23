package com.ecommerce.promotion.service;

import com.ecommerce.promotion.config.PromotionIssueProperties;
import com.ecommerce.promotion.repository.PromotionIssueRepository;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

/**
 * Gives back Redis claims that never got their MySQL row. Without this, a request that timed
 * out on Redis while the claim script still ran leaves the user marked as claimed with no
 * coupon, and one coupon short. Runs on every pod; giving back is idempotent.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "promotion.issue", name = "store", havingValue = "redis", matchIfMissing = true)
public class StaleClaimSettler implements SchedulingConfigurer {

    private static final Duration INTERVAL = Duration.ofSeconds(10);

    private final CouponStockStore couponStockStore;
    private final PromotionIssueRepository promotionIssueRepository;
    private final PromotionIssueProperties properties;

    @Override
    public void configureTasks(ScheduledTaskRegistrar registrar) {
        registrar.addFixedDelayTask(this::settle, INTERVAL);
    }

    public int settle() {
        try {
            return couponStockStore.settleStaleClaims(properties.getStaleClaimAfter(),
                    (promotionId, userId) ->
                            promotionIssueRepository.findByPromotion_IdAndUserId(promotionId, userId).isPresent());
        } catch (DataAccessException e) {
            log.warn("Could not settle pending coupon claims: {}", e.getMessage());
            return 0;
        }
    }
}
