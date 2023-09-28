package com.hoangtien2k3.inventoryservice.controller;

import com.hoangtien2k3.inventoryservice.dto.InventoryResponse;
import com.hoangtien2k3.inventoryservice.exception.UnauthorizedException;
import com.hoangtien2k3.inventoryservice.service.InventoryService;
import com.hoangtien2k3.inventoryservice.service.TokenValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
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
    private final TokenValidationService tokenValidationService;

    // http://localhost:8082/api/inventory/iphone-13,iphone13-red

    // http://localhost:8082/api/inventory?productName=iphone-13&productName=iphone13-red

//    @GetMapping
//    @ResponseStatus(HttpStatus.OK)
//    public List<InventoryResponse> isInStock(@RequestParam List<String> productName) {
//        log.info("Received inventory check request for skuCode: {}", productName);
//        return inventoryService.isInStock(productName);
//    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<List<InventoryResponse>> isInStock(@RequestParam List<String> productName,
                                                   @RequestHeader("Authorization") String authorizationHeader) {

        log.info("Received inventory check request for skuCode: {}", productName);

        // Trích xuất token từ header "Authorization"
        String token = extractTokenFromAuthorizationHeader(authorizationHeader);

        // Xác thực token
        return tokenValidationService.validateToken(token)
                .flatMap(valid -> {
                    if (valid) {
                        // Token hợp lệ, tiếp tục xử lý yêu cầu
                        return Mono.just(inventoryService.isInStock(productName));
                    } else {
                        // Token không hợp lệ, trả về lỗi hoặc xử lý khác tùy ý
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