package com.ecommerce.promotion.service;

import com.ecommerce.promotion.config.PromotionIssueProperties;
import com.ecommerce.promotion.repository.PromotionRepository;
import java.time.Duration;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * With the Redis store a claim no longer updates the promotion row (that row lock was the
 * bottleneck), so {@code issued_count} — what the backoffice and the coupon list show — is
 * caught up from {@code promotion_issue} every few seconds instead.
 *
 * <p>Registered through {@link SchedulingConfigurer} rather than {@code @Scheduled} so the
 * interval can be written as {@code 5s}: {@code fixedDelayString} only takes milliseconds or
 * ISO-8601 here.</p>
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "promotion.issue", name = "store", havingValue = "redis", matchIfMissing = true)
public class IssuedCountSynchronizer implements SchedulingConfigurer {

    // Coupons that ended within this window still get their final count written.
    private static final Duration ENDED_GRACE = Duration.ofDays(1);

    private final PromotionRepository promotionRepository;
    private final PromotionIssueProperties properties;
    private final TransactionTemplate transactionTemplate;

    public IssuedCountSynchronizer(PromotionRepository promotionRepository,
                                   PromotionIssueProperties properties,
                                   PlatformTransactionManager transactionManager) {
        this.promotionRepository = promotionRepository;
        this.properties = properties;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar registrar) {
        registrar.addFixedDelayTask(this::sync, properties.getCountSyncInterval());
    }

    public void sync() {
        Integer updated = transactionTemplate.execute(status ->
                promotionRepository.syncIssuedCounts(Instant.now().minus(ENDED_GRACE)));
        if (updated != null && updated > 0) {
            log.debug("Synced issued_count of {} promotions", updated);
        }
    }
}
