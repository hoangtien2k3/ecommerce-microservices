package com.ecommerce.commonlib.autoconfigure;

import com.ecommerce.commonlib.keycloak.KeycloakAuthClient;
import com.ecommerce.commonlib.keycloak.KeycloakClientProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

/**
 * Auto-config for the Keycloak admin/auth client.
 * Only activates when {@code keycloak.client.admin-username} is set — most services
 * do not need to talk to the admin API, so we keep the bean off the context for them.
 */
@AutoConfiguration
@ConditionalOnClass(RestClient.class)
@ConditionalOnProperty(prefix = "keycloak.client", name = "admin-username")
@EnableConfigurationProperties(KeycloakClientProperties.class)
public class KeycloakAdminAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(KeycloakAuthClient.class)
    public KeycloakAuthClient keycloakAuthClient(RestClient.Builder builder,
                                                 KeycloakClientProperties properties) {
        return new KeycloakAuthClient(builder, properties);
    }

    @Bean
    @ConditionalOnMissingBean(RestClient.Builder.class)
    public RestClient.Builder keycloakRestClientBuilder() {
        return RestClient.builder();
    }
}
