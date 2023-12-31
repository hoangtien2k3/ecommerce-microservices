package com.hoangtien2k3.userservice.service;

import com.hoangtien2k3.userservice.model.dto.request.EmailDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class EmailService {
    private final WebClient.Builder webClientBuilder;

    @Value("${api.gateway.url}")
    private String baseUrl; // http://localhost:8080

    public EmailService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public String sendMail(EmailDetails emailDetails) {
        // Use block() to wait until completion and get the result value
        return webClientBuilder.baseUrl(baseUrl).build()
                .post()
                .uri("/api/email/sendMail")
                .bodyValue(emailDetails)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}

