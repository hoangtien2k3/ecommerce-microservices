package com.ecommerce.commonlib.openapi;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * OpenAPI metadata bound from {@code ecommerce.openapi.*}.
 * Defaults are platform-wide; each service should override at least {@code title}.
 */
@ConfigurationProperties(prefix = "ecommerce.openapi")
public record OpenApiProperties(
        String title,
        String version,
        String description
) {

    public OpenApiProperties {
        if (title == null || title.isBlank()) {
            title = "Ecommerce Microservice API";
        }
        if (version == null || version.isBlank()) {
            version = "v1";
        }
        if (description == null) {
            description = "API documentation";
        }
    }
}
