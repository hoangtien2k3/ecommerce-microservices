package com.ecommerce.commonlib.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Default {@link SecurityFilterChain} for every resource-server in the platform.
 *
 * <p>Behavior:</p>
 * <ul>
 *   <li>CSRF disabled (stateless JWT auth)</li>
 *   <li>{@code SessionCreationPolicy.STATELESS}</li>
 *   <li>{@link SecurityProperties#resolvedPublicPaths()} permitted</li>
 *   <li>Everything else requires authentication, verified by OAuth2 resource server</li>
 *   <li>Authorities mapped via {@link KeycloakRealmRoleConverter}</li>
 * </ul>
 *
 * <p>If a service needs custom rules (e.g. method-level scope checks), it can declare
 * its own {@code SecurityFilterChain} bean — the {@code @ConditionalOnMissingBean}
 * ensures this one steps aside.</p>
 */
public class BaseSecurityConfig {

    private final SecurityProperties properties;

    public BaseSecurityConfig(SecurityProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationConverter converter) throws Exception {
        if (properties.csrfDisabled()) {
            http.csrf(AbstractHttpConfigurer::disable);
        }
        if (properties.statelessSession()) {
            http.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        }
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(properties.resolvedPublicPaths().toArray(new String[0])).permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(converter)));
        return http.build();
    }

    @Bean
    @ConditionalOnMissingBean(JwtAuthenticationConverter.class)
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());
        return converter;
    }
}
