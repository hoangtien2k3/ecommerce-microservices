package com.ecommerce.promotion.viewmodel;

import jakarta.validation.constraints.NotNull;

public record PromotionReleaseRequest(@NotNull Long orderId) {
}
