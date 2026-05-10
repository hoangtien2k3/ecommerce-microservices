package com.ecommerce.commonlib.keycloak;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(KeycloakClientProperties.class)
public class KeycloakClientConfig {
}
