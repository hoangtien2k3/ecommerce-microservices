package com.hoangtien2k3.media.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "hoangtien2k3")
public record YasConfig(String publicUrl) {
}
