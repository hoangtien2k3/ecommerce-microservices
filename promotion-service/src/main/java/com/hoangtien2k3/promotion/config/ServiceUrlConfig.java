package com.hoangtien2k3.promotion.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ecommerce.services")
public record ServiceUrlConfig(String product) {
}
