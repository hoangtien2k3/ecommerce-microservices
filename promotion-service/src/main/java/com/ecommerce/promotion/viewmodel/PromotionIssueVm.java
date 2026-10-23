package com.ecommerce.promotion.viewmodel;

import java.time.Instant;

public record PromotionIssueVm(Long id, Long promotionId, String userId, Instant issuedAt) {
}
