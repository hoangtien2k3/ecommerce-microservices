package com.ecommerce.promotion.viewmodel;

import com.ecommerce.promotion.model.Promotion;
import java.time.Instant;

import lombok.Builder;

@Builder
public record PromotionVm(Long id,
                          String name,
                          String slug,
                          String couponCode,
                          Long discountPercentage,
                          Long discountAmount,
                          Boolean isActive,
                          Instant startDate,
                          Instant endDate
) {
    public static PromotionVm fromModel(Promotion promotion) {
        return PromotionVm.builder()
                .id(promotion.getId())
                .name(promotion.getName())
                .slug(promotion.getSlug())
                .discountPercentage(promotion.getDiscountPercentage())
                .discountAmount(promotion.getDiscountAmount())
                .isActive(promotion.getIsActive())
                .startDate(promotion.getStartDate())
                .endDate(promotion.getEndDate())
                .build();
    }
}
