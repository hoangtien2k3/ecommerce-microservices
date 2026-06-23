package com.ecommerce.commonlib.keycloak;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * Auto-config for the Keycloak admin/auth client.
 *
 * <p>Intentionally injects {@link CloseableHttpClient} directly — NOT the
 * {@code RestClient.Builder} bean — so that Keycloak calls always go to the
 * real Keycloak server and are never routed through the Eureka load balancer
 * (which services expose via their own {@code @LoadBalanced RestClient.Builder}).</p>
 *
 * <p>Only activates when {@code keycloak.client.admin-username} is set.</p>
 */
@AutoConfiguration(afterName = "com.ecommerce.commonlib.autoconfigure.RestClientAutoConfiguration")
@ConditionalOnClass({RestClient.class, CloseableHttpClient.class})
@ConditionalOnProperty(prefix = "keycloak.client", name = "admin-username")
@EnableConfigurationProperties(KeycloakClientProperties.class)
public class KeycloakAdminAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(KeycloakAuthClient.class)
    @ConditionalOnBean(CloseableHttpClient.class)
    public KeycloakAuthClient keycloakAuthClient(CloseableHttpClient httpClient,
                                                 KeycloakClientProperties properties) {
        RestClient.Builder builder = RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
        return new KeycloakAuthClient(builder, properties);
    }
}
