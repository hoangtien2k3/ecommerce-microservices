package com.hoangtien2k3.inventoryservice.controller;

import com.hoangtien2k3.inventoryservice.dto.response.InventoryResponse;
import com.hoangtien2k3.inventoryservice.exception.UnauthorizedException;
import com.hoangtien2k3.inventoryservice.security.JwtUtil;
import com.hoangtien2k3.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

    @Autowired
    private final InventoryService inventoryService;

    @Autowired
    private final WebClient.Builder webClientBuilder;

    @Value("${user-service.base-url}")
    private String userServiceBaseUrl; // URL của user-service


    // http://localhost:8082/api/inventory/iphone-13,iphone13-red

    // http://localhost:8082/api/inventory?productName=iphone-13&productName=iphone13-red

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStockNoAccessToken(@RequestParam List<String> productName) {
        log.info("Received inventory check request for skuCode: {}", productName);
        return inventoryService.isInStockNoAccessToken(productName);
    }


    @GetMapping("/get-order-details")
    public String getOrderDetails(@RequestHeader(name = "Authorization") String authorizationHeader) {
        // Sử dụng JWT từ tiêu đề "Authorization" của yêu cầu gọi API
        String jwtToken = authorizationHeader.replace("Bearer ", "");

        // Kiểm tra tính hợp lệ của token trước khi gọi API
        if (JwtUtil.validateToken(jwtToken)) {
            // Token hợp lệ, tiếp tục gọi API từ user-service
            String apiEndpoint = userServiceBaseUrl + "/api/manager"; // URL của API trong user-service
            String response = webClientBuilder.baseUrl(apiEndpoint)
                    .build()
                    .get()
                    .uri("/token")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return response;
        } else {
            // Token không hợp lệ, trả về lỗi hoặc xử lý khác
            return "Unauthorized access";
        }
    }



    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public Mono<List<InventoryResponse>> isInStockAccessToken(@RequestParam List<String> productName,
                                                              @RequestHeader("Authorization") String authorizationHeader) {

        log.info("Received inventory check request for skuCode: {}", productName);

        // Trích xuất token từ header "Authorization"
        String token = extractTokenFromAuthorizationHeader(authorizationHeader);

        // Gửi access token đến user-service để xác thực
        return inventoryService.requestTokenValidation(token)
                .flatMap(validationMessage -> {
                    if ("Valid token".equals(validationMessage)) {
                        // Access token hợp lệ, tiếp tục xử lý yêu cầu
                        return Mono.just(inventoryService.isInStock(productName, token));
                    } else {
                        // Access token không hợp lệ, trả về lỗi hoặc xử lý khác tùy ý
                        return Mono.error(new UnauthorizedException("Invalid token"));
                    }
                });
    }

    // Hàm để trích xuất token từ header "Authorization"
    private String extractTokenFromAuthorizationHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7); // Loại bỏ "Bearer " để lấy token
        }
        return null;
    }

}