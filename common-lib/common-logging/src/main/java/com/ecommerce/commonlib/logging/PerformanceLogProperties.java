package com.ecommerce.commonlib.logging;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration for the AOP performance logging aspect ({@link LogPerformance}).
 *
 * <p>Bound from {@code ecommerce.web.logging.performance.*} — the prefix is kept
 * unchanged from when this lived in {@code common-spring} so existing service
 * configuration keeps working after the module split.
 *
 * <pre>{@code
 * ecommerce:
 *   web:
 *     logging:
 *       performance:
 *         enabled: true
 *         threshold-ms: 50
 * }</pre>
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "ecommerce.web.logging.performance")
public class PerformanceLogProperties {

    /** Enable the {@link LogPerformance} / {@link Loggable} aspect. */
    private boolean enabled = true;

    /** Only log when execution exceeds this threshold. Fast calls stay silent. */
    private long thresholdMs = 50;
}
