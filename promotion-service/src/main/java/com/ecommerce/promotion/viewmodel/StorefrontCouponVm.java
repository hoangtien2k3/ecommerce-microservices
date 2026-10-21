package com.ecommerce.promotion.viewmodel;

import com.ecommerce.promotion.model.Promotion;
import com.ecommerce.promotion.model.enumeration.DiscountType;
import java.time.Instant;

/**
 * One card on the storefront first-come coupon page. Field names match the frontend's
 * {@code CouponEvent}; {@code claimed} is false for anonymous callers.
 */
public record StorefrontCouponVm(Long promotionId,
                                 String name,
                                 String description,
                                 DiscountType discountType,
                                 Long discountAmount,
                                 Long discountPercentage,
                                 Long minimumOrderPurchaseAmount,
                                 int issueLimit,
                                 int issuedCount,
                                 Instant startDate,
                                 Instant endDate,
                                 boolean claimed) {

    public static StorefrontCouponVm of(Promotion promotion, int issuedCount, boolean claimed) {
        return new StorefrontCouponVm(promotion.getId(), promotion.getName(), promotion.getDescription(),
                promotion.getDiscountType(), promotion.getDiscountAmount(), promotion.getDiscountPercentage(),
                promotion.getMinimumOrderPurchaseAmount(), promotion.getIssueLimit(), issuedCount,
                promotion.getStartDate(), promotion.getEndDate(), claimed);
    }
}
