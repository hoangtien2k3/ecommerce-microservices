package com.ecommerce.promotion.config;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "promotion.issue")
public class PromotionIssueProperties {

    /** Where first-come coupon stock is kept and decremented. */
    private Store store = Store.REDIS;

    /** How long a pod trusts its cached copy of a coupon's open window and total quantity. */
    private Duration windowCacheTtl = Duration.ofSeconds(3);

    /** How often issued_count is caught up with promotion_issue when the store is Redis. */
    private Duration countSyncInterval = Duration.ofSeconds(5);

    /**
     * Redis store only: a claim with no MySQL row after this long is given back. Must be longer
     * than a claim can take, including waiting for a database connection (Hikari: 30s).
     */
    private Duration staleClaimAfter = Duration.ofSeconds(60);

    public enum Store {
        REDIS,
        DB
    }
}
