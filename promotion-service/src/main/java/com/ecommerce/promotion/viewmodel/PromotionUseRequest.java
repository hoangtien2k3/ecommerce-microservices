package com.ecommerce.promotion.viewmodel;

import jakarta.validation.constraints.NotNull;

public record PromotionUseRequest(@NotNull Long orderId, Long productId) {
}
