package com.ecommerce.promotion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    // ProductService calls absolute URLs, so no base URL is set here.
    @Bean
    public RestClient restClient(RestClient.Builder restClientBuilder) {
        return restClientBuilder.clone().build();
    }
}
