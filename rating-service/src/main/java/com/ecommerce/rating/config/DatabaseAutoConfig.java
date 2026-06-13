package com.ecommerce.rating.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("com.ecommerce.rating.repository")
@EntityScan("com.ecommerce.rating.model")
public class DatabaseAutoConfig {
}
