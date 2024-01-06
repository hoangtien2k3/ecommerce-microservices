package com.hoangtien2k3.orderservice.service;

import com.hoangtien2k3.orderservice.dto.product.ProductDto;
import com.hoangtien2k3.orderservice.dto.user.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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

    public Mono<UserDto> receiverUserDto(Long userId, String token) {
        return webClientBuilder.baseUrl("http://localhost:8088").build()
                .get()
                .uri("/api/manager/user/" + userId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(UserDto.class);
    }

    public Mono<ProductDto> receiverProductDto(Integer productId) {
        return webClientBuilder.baseUrl("http://localhost:8086").build()
                .get()
                .uri("/api/products/" + productId)
                .retrieve()
                .bodyToMono(ProductDto.class);
    }

}
