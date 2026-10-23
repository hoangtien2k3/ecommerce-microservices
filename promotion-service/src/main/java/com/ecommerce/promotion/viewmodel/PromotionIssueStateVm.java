package com.ecommerce.promotion.viewmodel;

import com.ecommerce.promotion.model.enumeration.PromotionIssueStatus;

public record PromotionIssueStateVm(Long id, Long promotionId, Long orderId, PromotionIssueStatus status) {
}
