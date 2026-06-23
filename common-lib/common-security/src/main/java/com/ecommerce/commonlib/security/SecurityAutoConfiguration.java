package com.ecommerce.commonlib.security;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;

/**
 * Autoconfigures the resource-server security stack for every servlet service
 * that has {@code spring-security} on the classpath.
 *
 * <p>{@code @Import(BaseSecurityConfig.class)} is intentional — it tells Spring to
 * process {@code BaseSecurityConfig} as a {@code @Configuration}, so its
 * {@code @Bean} methods ({@code securityFilterChain}, {@code jwtAuthenticationConverter})
 * are discovered and registered. A plain {@code @Bean} factory method would only
 * create the instance without processing its inner {@code @Bean} methods.</p>
 *
 * <p>Disable by setting {@code ecommerce.security.enabled=false}.</p>
 */
@AutoConfiguration
@ConditionalOnClass({HttpSecurity.class, JwtDecoder.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "ecommerce.security", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(SecurityProperties.class)
@EnableMethodSecurity
@Import(BaseSecurityConfig.class)
public class SecurityAutoConfiguration {
}
