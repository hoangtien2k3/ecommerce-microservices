package com.ecommerce.commonlib.autoconfigure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Tuning knobs for the shared {@link org.springframework.web.client.RestClient}.
 *
 * <pre>
 * ecommerce:
 *   rest-client:
 *     connect-timeout: 5s
 *     read-timeout: 30s
 *     pool:
 *       max-total: 200
 *       max-per-route: 50
 *       keep-alive: 60s
 *       evict-idle-after: 30s
 * </pre>
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "ecommerce.rest-client")
public class RestClientProperties {

    /** TCP connection establishment timeout. */
    private Duration connectTimeout = Duration.ofSeconds(5);

    /** Socket read timeout (waiting for server response). */
    private Duration readTimeout = Duration.ofSeconds(30);

    private Pool pool = new Pool();

    @Setter
    @Getter
    public static class Pool {
        /** Maximum total connections in the pool across all routes. */
        private int maxTotal = 200;

        /** Maximum connections per single target host. */
        private int maxPerRoute = 50;

        /** How long an idle persistent connection is kept alive. */
        private Duration keepAlive = Duration.ofSeconds(60);

        /** Evict idle connections after this period of inactivity. */
        private Duration evictIdleAfter = Duration.ofSeconds(30);
    }
}
