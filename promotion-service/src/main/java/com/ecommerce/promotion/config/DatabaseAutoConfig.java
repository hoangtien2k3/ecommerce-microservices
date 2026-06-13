package com.ecommerce.promotion.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("com.ecommerce.promotion.repository")
@EntityScan("com.ecommerce.promotion.model")
public class DatabaseAutoConfig {
}
