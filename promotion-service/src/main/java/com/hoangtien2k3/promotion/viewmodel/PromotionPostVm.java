package com.hoangtien2k3.promotion.viewmodel;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hoangtien2k3.promotion.model.Promotion;
import com.hoangtien2k3.promotion.model.PromotionApply;
import com.hoangtien2k3.promotion.validation.PromotionConstraint;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@PromotionConstraint
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize
public class PromotionPostVm extends PromotionDto {
    @Size(min = 1, max = 450)
    private String name;
    @NotBlank
    private String slug;
    private String description;
    @NotBlank
    private String couponCode;
    Long minimumOrderPurchaseAmount;
    boolean isActive;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date startDate;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date endDate;

    public static List<PromotionApply> createPromotionApplies(PromotionPostVm promotionPostVm, Promotion promotion) {
        return switch (promotion.getApplyTo()) {
            case PRODUCT -> promotionPostVm.getProductIds().stream()
                    .map(productId -> PromotionApply.builder().productId(productId).promotion(promotion)
                            .build())
                    .toList();

            case BRAND -> promotionPostVm.getBrandIds().stream()
                    .map(brandId -> PromotionApply.builder().brandId(brandId).promotion(promotion)
                            .build())
                    .toList();

            case CATEGORY -> promotionPostVm.getCategoryIds().stream()
                    .map(categoryId -> PromotionApply.builder().categoryId(categoryId).promotion(promotion)
                            .build())
                    .toList();

        };
    }
}
