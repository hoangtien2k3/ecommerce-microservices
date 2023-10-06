package com.hoangtien2k3.shippingservice.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class JwtValidate {

    private final WebClient.Builder webClientBuilder;

    @Value("${user-service.base-url}")
    private String userServiceBaseUrl; // URL của user-service

    @Autowired
    public JwtValidate(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public Boolean validateTokenUserService(String authorizationHeader) {

        // Sử dụng JWT từ tiêu đề "Authorization" của yêu cầu gọi API
        String jwtToken = authorizationHeader.replace("Bearer ", "");

        // Token hợp lệ, tiếp tục gọi API từ user-service
        Boolean responseToken = webClientBuilder.baseUrl(userServiceBaseUrl + "/api/auth")
                .build()
                .get()
                .uri("/validateToken")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        return responseToken;

    }

}

