package com.ecommerce.promotion.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Shows whether the coupon store (Redis) answers, without failing the service health:
 * UNKNOWN does not lower the aggregate status, so probes keep the pod in rotation and
 * only coupon issuing is refused while Redis is down.
 */
@Component("couponStore")
@RequiredArgsConstructor
public class CouponStoreHealthIndicator implements HealthIndicator {

    private final StringRedisTemplate redisTemplate;
    private final PromotionIssueProperties properties;

    @Override
    public Health health() {
        if (properties.getStore() != PromotionIssueProperties.Store.REDIS) {
            return Health.up().withDetail("store", "db").build();
        }
        try {
            String pong = redisTemplate.execute((RedisCallback<String>) connection -> connection.ping());
            return Health.up().withDetail("store", "redis").withDetail("ping", pong).build();
        } catch (RuntimeException e) {
            return Health.unknown().withDetail("store", "redis").withDetail("error", e.getMessage()).build();
        }
    }
}
