package com.ecommerce.commonlib.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Per-service security configuration bound from {@code ecommerce.security.*}.
 *
 * <p>Endpoint allow-lists live here (not hardcoded inside {@code BaseSecurityConfig})
 * so each service decides which routes are public. The defaults open the actuator
 * health/info endpoints and OpenAPI documentation only — everything else is denied.</p>
 *
 * <pre>{@code
 * ecommerce:
 *   security:
 *     public-paths:
 *       - /api/v1/auth/login
 *       - /api/v1/auth/refresh
 *     public-method-paths:
 *       GET:
 *         - /api/v1/products/**
 * }</pre>
 */
@ConfigurationProperties(prefix = "ecommerce.security")
public record SecurityProperties(
        boolean enabled,
        List<String> publicPaths,
        boolean csrfDisabled,
        boolean statelessSession
) {

    private static final List<String> ALWAYS_PUBLIC = List.of(
            "/actuator/health",
            "/actuator/health/**",
            "/actuator/info",
            "/actuator/prometheus",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-resources/**"
    );

    public SecurityProperties {
        if (publicPaths == null) {
            publicPaths = List.of();
        }
    }

    /**
     * Effective allow-list = service-provided paths ∪ platform-wide unconditionally public paths.
     */
    public List<String> resolvedPublicPaths() {
        return java.util.stream.Stream
                .concat(ALWAYS_PUBLIC.stream(), publicPaths.stream())
                .distinct()
                .toList();
    }
}
