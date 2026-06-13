package com.ecommerce.tax.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("com.ecommerce.tax.repository")
@EntityScan("com.ecommerce.tax.model")
public class DatabaseAutoConfig {
}
