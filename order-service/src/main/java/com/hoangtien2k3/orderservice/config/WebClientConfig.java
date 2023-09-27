package com.hoangtien2k3.orderservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    /*
    Có 2 cách giao tiếp đồng bộ giữa 2 service thông quá HTTP request:
            + RestTemplate
            + WebClient
    */

    // sử dụng WebClient từ Spring WebFlux từ Spring 5 trở lên
    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }

}
