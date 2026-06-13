package com.ecommerce.commonlib.web.cors;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * CORS configuration bound from {@code ecommerce.cors.*}.
 * <p>
 * Defaults are restrictive on purpose: no allowed origin is set, so cross-origin
 * requests are denied until the operator explicitly opens them per environment.
 */
@ConfigurationProperties(prefix = "ecommerce.cors")
public record CorsProperties(
        boolean enabled,
        List<String> allowedOrigins,
        List<String> allowedOriginPatterns,
        List<String> allowedMethods,
        List<String> allowedHeaders,
        List<String> exposedHeaders,
        Boolean allowCredentials,
        Long maxAgeSeconds
) {
    public CorsProperties {
        if (allowedMethods == null || allowedMethods.isEmpty()) {
            allowedMethods = List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
        }
        if (allowedHeaders == null || allowedHeaders.isEmpty()) {
            allowedHeaders = List.of("*");
        }
        if (exposedHeaders == null) {
            exposedHeaders = List.of("X-Correlation-Id", "X-Request-Id");
        }
        if (maxAgeSeconds == null) {
            maxAgeSeconds = 3600L;
        }
    }
}
