package com.hoangtien2k3.orderservice.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class RoleAuthorities {
    private final WebClient.Builder webClientBuilder;

    @Value("${user-service.base-url}")
    private String userServiceBaseUrl; // URL của user-service

    @Autowired
    public RoleAuthorities(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public RoleAuthorities(WebClient.Builder webClientBuilder, String userServiceBaseUrl) {
        this.webClientBuilder = webClientBuilder;
        this.userServiceBaseUrl = userServiceBaseUrl;
    }

    public List<String> hasAuthority(String authorizationToken) {
        List<String> listRoleAuthorities = webClientBuilder.baseUrl(userServiceBaseUrl + "/api/auth")
                .build()
                .get()
                .uri("/hasAuthority")
                .header(HttpHeaders.AUTHORIZATION, authorizationToken)
                .retrieve() // thực hiện HTTP request -> user-service
                .bodyToMono(List.class)
                .block();

        return listRoleAuthorities;
    }

}
