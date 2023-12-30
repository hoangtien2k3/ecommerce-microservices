package com.hoangtien2k3.userservice.service;

import com.hoangtien2k3.userservice.model.dto.request.EmailDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class EmailService {

    private final WebClient.Builder webClientBuilder;

    @Value("${apigateway.url}")
    private String baseUrl; // http://localhost:8080

    public EmailService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public String sendMail(EmailDetails emailDetails) {
        // Sử dụng block() để đợi đến khi hoàn tất và lấy giá trị kết quả
        return webClientBuilder.baseUrl("http://localhost:8080").build()
                .post()
                .uri("/api/email/sendMail")
                .bodyValue(emailDetails)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

}

