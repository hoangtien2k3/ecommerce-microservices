package com.ecommerce.commonlib.web.cors;

import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Translates the validated {@link CorsProperties} into an MVC {@link CorsRegistry}.
 * Intentionally a {@link WebMvcConfigurer} (not a global {@code CorsConfigurationSource})
 * so it integrates with controller-level {@code @CrossOrigin} rather than overriding it.
 */
public record CorsAutoConfigurer(CorsProperties props) implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        if (!props.enabled()) {
            return;
        }
        CorsRegistration mapping = registry.addMapping("/**")
                .allowedMethods(toArray(props.allowedMethods()))
                .allowedHeaders(toArray(props.allowedHeaders()))
                .exposedHeaders(toArray(props.exposedHeaders()))
                .maxAge(props.maxAgeSeconds());

        if (props.allowedOrigins() != null && !props.allowedOrigins().isEmpty()) {
            mapping.allowedOrigins(toArray(props.allowedOrigins()));
        }
        if (props.allowedOriginPatterns() != null && !props.allowedOriginPatterns().isEmpty()) {
            mapping.allowedOriginPatterns(toArray(props.allowedOriginPatterns()));
        }
        if (props.allowCredentials() != null) {
            mapping.allowCredentials(props.allowCredentials());
        }
    }

    private static String[] toArray(List<String> values) {
        return values == null ? new String[0] : values.toArray(new String[0]);
    }
}
