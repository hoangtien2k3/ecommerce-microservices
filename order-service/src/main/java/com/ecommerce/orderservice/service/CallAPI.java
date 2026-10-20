package com.ecommerce.orderservice.service;

import com.ecommerce.commonlib.exception.BusinessException;
import com.ecommerce.commonlib.viewmodel.ApiResponse;
import com.ecommerce.orderservice.dto.product.ProductDto;
import com.ecommerce.orderservice.dto.promotion.PromotionReleaseCall;
import com.ecommerce.orderservice.dto.promotion.PromotionUseCall;
import com.ecommerce.orderservice.dto.promotion.UnusedPromotionIssueResponse;
import com.ecommerce.orderservice.dto.user.UserDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CallAPI {

    private final RestClient authClient;
    private final RestClient productClient;
    private final RestClient promotionClient;

    // The injected builder is a shared singleton; clone it so each client keeps its own base URL.
    public CallAPI(RestClient.Builder restClientBuilder) {
        this.authClient = restClientBuilder.clone().baseUrl("http://auth-service:8088").build();
        this.productClient = restClientBuilder.clone().baseUrl("http://product-service:8086").build();
        this.promotionClient = restClientBuilder.clone().baseUrl("http://promotion-service:8093").build();
    }

    public UserDto receiverUserDto(Long userId, String token) {
        return authClient
                .get()
                .uri("/api/manager/user/{id}", userId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .body(UserDto.class);
    }

    public ProductDto receiverProductDto(Integer productId) {
        return receiverProductDto(productId, null);
    }

    public ProductDto receiverProductDto(Integer productId, String authorization) {
        ApiResponse<ProductDto> response = productClient
                .get()
                .uri("/product/api/products/{id}", productId)
                .headers(headers -> applyAuthorization(headers, authorization))
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<ProductDto>>() {
                });
        return response == null ? null : response.data();
    }

    public UnusedPromotionIssueResponse unusedPromotionIssue(Long promotionId, String authorization) {
        return promotionClient
                .get()
                .uri("/promotion/internal/promotions/{promotionId}/unused-issue", promotionId)
                .headers(headers -> applyAuthorization(headers, authorization))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw couponError(response.getStatusCode(), "promotion.coupon.already.used");
                })
                .body(UnusedPromotionIssueResponse.class);
    }

    public void usePromotion(Long promotionId, Long orderId, Long productId, String authorization) {
        promotionClient
                .post()
                .uri("/promotion/internal/promotions/{promotionId}/use", promotionId)
                .headers(headers -> applyAuthorization(headers, authorization))
                .body(new PromotionUseCall(orderId, productId))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw couponError(response.getStatusCode(), "promotion.coupon.already.used");
                })
                .toBodilessEntity();
    }

    public void releasePromotion(Long promotionId, Long orderId, String authorization) {
        promotionClient
                .post()
                .uri("/promotion/internal/promotions/{promotionId}/release", promotionId)
                .headers(headers -> applyAuthorization(headers, authorization))
                .body(new PromotionReleaseCall(orderId))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw couponError(response.getStatusCode(), "promotion.coupon.release.mismatch");
                })
                .toBodilessEntity();
    }

    private static BusinessException couponError(HttpStatusCode status, String conflictKey) {
        return switch (status.value()) {
            case 404 -> BusinessException.notFound("promotion.coupon.not.claimed");
            case 409 -> BusinessException.conflict(conflictKey);
            case 401 -> BusinessException.unauthorized("error.unauthorized");
            default -> BusinessException.badRequest("promotion.coupon.not.open");
        };
    }

    private static void applyAuthorization(HttpHeaders headers, String authorization) {
        if (authorization != null && !authorization.isBlank()) {
            headers.set(HttpHeaders.AUTHORIZATION, authorization);
        }
    }
}
