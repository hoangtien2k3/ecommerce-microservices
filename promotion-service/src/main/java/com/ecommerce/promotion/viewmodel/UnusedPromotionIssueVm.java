package com.ecommerce.promotion.viewmodel;

import com.ecommerce.promotion.model.enumeration.DiscountType;

public record UnusedPromotionIssueVm(
        Long id,
        Long promotionId,
        String userId,
        DiscountType discountType,
        Long discountAmount,
        Long discountPercentage
) {
}
