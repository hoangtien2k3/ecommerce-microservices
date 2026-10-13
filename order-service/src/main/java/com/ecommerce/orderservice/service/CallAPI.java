package com.ecommerce.orderservice.service;

import com.ecommerce.orderservice.dto.inventory.InventoryStockDto;
import com.ecommerce.orderservice.dto.product.ProductDto;
import com.ecommerce.orderservice.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CallAPI {

    private final RestClient.Builder restClientBuilder;

    public UserDto receiverUserDto(Long userId, String token) {
        return restClientBuilder.baseUrl("http://auth-service:8088").build()
                .get()
                .uri("/api/manager/user/{id}", userId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .body(UserDto.class);
    }

    public ProductDto receiverProductDto(Integer productId) {
        return restClientBuilder.baseUrl("http://product-service:8086").build()
                .get()
                .uri("/api/products/{id}", productId)
                .retrieve()
                .body(ProductDto.class);
    }

    public ProductDto receiverProductDto(Integer productId, String authorization) {
        return restClientBuilder.baseUrl("http://product-service:8086").build()
                .get()
                .uri("/product/api/products/{id}", productId)
                .headers(headers -> applyAuthorization(headers, authorization))
                .retrieve()
                .body(ProductDto.class);
    }

    public List<InventoryStockDto> receiverInventory(String productName, String authorization) {
        List<InventoryStockDto> stocks = restClientBuilder.baseUrl("http://inventory-service:8082").build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/inventory/api/inventory")
                        .queryParam("productName", productName)
                        .build())
                .headers(headers -> applyAuthorization(headers, authorization))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
        return stocks == null ? List.of() : stocks;
    }

    private static void applyAuthorization(HttpHeaders headers, String authorization) {
        if (authorization != null && !authorization.isBlank()) {
            headers.set(HttpHeaders.AUTHORIZATION, authorization);
        }
    }
}
