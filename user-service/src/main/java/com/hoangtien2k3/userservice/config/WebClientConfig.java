package com.hoangtien2k3.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(HttpClient.newConnection()
                        .compress(true) // (Optional) Enable compression
                        .port(8080) // Port of the service you want to connect to
                        .keepAlive(true))); // (Optional) Configure other settings as needed
    }
}