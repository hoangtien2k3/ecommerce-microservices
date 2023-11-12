package com.hoangtien2k3.proxyclient.config.template;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TemplateConfig {
    @LoadBalanced
    @Bean
    public RestTemplate restTemplateBean() {
        return new RestTemplate();
    }
}