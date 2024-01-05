package com.hoangtien2k3.orderservice.service;

import com.hoangtien2k3.orderservice.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class CallAPI {
    private final WebClient.Builder webClientBuilder;

    @Autowired
    public CallAPI(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public Mono<UserDto> receiverUserDto(Long userId) {
        return webClientBuilder.baseUrl("http://localhost:8080").build()
                .get()
                .uri("/api/manager/user/" + userId)
                .retrieve()
                .bodyToMono(UserDto.class);
    }

}
