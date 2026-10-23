package com.ecommerce.orderservice.dto.promotion;

public record UnusedPromotionIssueResponse(
        Long id,
        Long promotionId,
        String userId,
        String discountType,
        Long discountAmount,
        Long discountPercentage
) {
}
