package com.hoangtien2k3.inventoryservice.service;


import com.hoangtien2k3.inventoryservice.dto.request.TokenValidationRequest;
import com.hoangtien2k3.inventoryservice.dto.response.InventoryResponse;
import com.hoangtien2k3.inventoryservice.dto.response.TokenValidationResponse;
import com.hoangtien2k3.inventoryservice.exception.UnauthorizedException;
import com.hoangtien2k3.inventoryservice.repository.InventoryRepository;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    @Autowired
    private final InventoryRepository inventoryRepository;

    @Autowired
    private final WebClient.Builder webClientBuilder;

    // get Token in -> user-service
    public Mono<String> getTokenFromUserService() {
        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8080/api/manager/token")
                .retrieve()
                .bodyToMono(String.class);
    }


    public Mono<String> requestTokenValidation(String accessToken) {
        return webClientBuilder.build()
                .post()
                .uri("http://localhost:8080/api/auth/validateToken")
                .body(BodyInserters.fromValue(new TokenValidationRequest(accessToken)))
                .retrieve()
                .bodyToMono(TokenValidationResponse.class)
                .map(TokenValidationResponse::getMessage);
    }


    @Transactional(readOnly = true)
    @SneakyThrows
    public List<InventoryResponse> isInStockNoAccessToken(List<String> productName) {
        log.info("Checking Inventory"); // còn hàng hay không
        return inventoryRepository.findByProductNameIn(productName)
                .stream()
                .map(inventory ->
                        InventoryResponse.builder()
                                .productName(inventory.getProductName())
                                .isInStock(inventory.getQuantity() > 0)
                                .build()
                ).toList();
    }


    @Transactional(readOnly = true)
    @SneakyThrows
    public List<InventoryResponse> isInStock(List<String> productName, String accessToken) {
        log.info("Checking Inventory");

        // Xác thực access token trước khi thực hiện thao tác
        return requestTokenValidation(accessToken)
                .flatMap(validationMessage -> {
                    if ("Valid token".equals(validationMessage)) {
                        // Access token hợp lệ, tiếp tục thực hiện các thao tác
                        return Mono.just(
                                inventoryRepository.findByProductNameIn(productName)
                                        .stream()
                                        .map(inventory ->
                                                InventoryResponse.builder()
                                                        .productName(inventory.getProductName())
                                                        .isInStock(inventory.getQuantity() > 0)
                                                        .build()
                                        )
                                        .toList()
                        );
                    } else {
                        // Access token không hợp lệ hoặc hết hạn
                        return Mono.error(new UnauthorizedException("Invalid token"));
                    }
                })
                .block(); // Chờ cho đến khi kết quả hoàn thành (lưu ý: block() chỉ được sử dụng trong các tình huống đồng bộ)
    }


}
