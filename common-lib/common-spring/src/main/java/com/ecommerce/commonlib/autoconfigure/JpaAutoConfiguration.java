package com.ecommerce.commonlib.autoconfigure;

import com.ecommerce.commonlib.data.audit.AuditorAwareImpl;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Enables Spring Data JPA auditing and wires the platform {@code AuditorAware} so
 * {@code @CreatedBy} / {@code @LastModifiedBy} columns get populated with the JWT
 * subject (or {@code "system"} when there's no security context).
 *
 * <p>Only activates when there's an {@link EntityManagerFactory} bean — services that
 * don't talk to a relational DB skip this config silently.</p>
 */
@AutoConfiguration
@ConditionalOnClass(EntityManagerFactory.class)
@ConditionalOnBean(EntityManagerFactory.class)
@EnableJpaAuditing(auditorAwareRef = "platformAuditorAware")
public class JpaAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "platformAuditorAware")
    public AuditorAware<String> platformAuditorAware() {
        return new AuditorAwareImpl();
    }
}
