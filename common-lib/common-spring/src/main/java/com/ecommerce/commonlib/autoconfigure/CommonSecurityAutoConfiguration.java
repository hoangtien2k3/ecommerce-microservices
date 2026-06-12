package com.ecommerce.commonlib.autoconfigure;

import com.ecommerce.commonlib.security.BaseSecurityConfig;
import com.ecommerce.commonlib.security.SecurityProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;

/**
 * Auto-configures the resource-server security stack for every servlet service
 * that has {@code spring-security} on the classpath.
 *
 * <p>Disable by setting {@code ecommerce.security.enabled=false} (used by services
 * that intentionally have no auth, e.g. the discovery server).</p>
 */
@AutoConfiguration
@ConditionalOnClass({HttpSecurity.class, JwtDecoder.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "ecommerce.security", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(SecurityProperties.class)
@EnableMethodSecurity
@Import(BaseSecurityConfig.class)
public class CommonSecurityAutoConfiguration {

    @Bean
    public BaseSecurityConfig baseSecurityConfig(SecurityProperties properties) {
        return new BaseSecurityConfig(properties);
    }
}
