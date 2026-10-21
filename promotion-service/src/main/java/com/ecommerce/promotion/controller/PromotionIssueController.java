package com.ecommerce.promotion.controller;

import com.ecommerce.commonlib.viewmodel.ApiResponse;
import com.ecommerce.promotion.service.PromotionIssueService;
import com.ecommerce.promotion.service.StorefrontCouponService;
import com.ecommerce.promotion.viewmodel.PromotionIssueVm;
import com.ecommerce.promotion.viewmodel.StorefrontCouponVm;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PromotionIssueController {

    private final PromotionIssueService promotionIssueService;
    private final StorefrontCouponService storefrontCouponService;

    /** Coupon page cards. Works without a token; with one, {@code claimed} is filled in. */
    @GetMapping("/storefront/promotions")
    public ApiResponse<List<StorefrontCouponVm>> listCoupons() {
        return ApiResponse.ok(storefrontCouponService.listCoupons());
    }

    @PostMapping("/storefront/promotions/{promotionId}/issue")
    public ResponseEntity<PromotionIssueVm> issue(@PathVariable Long promotionId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(promotionIssueService.issue(promotionId));
    }
}
