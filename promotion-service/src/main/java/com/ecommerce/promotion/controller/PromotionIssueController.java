package com.ecommerce.promotion.controller;

import com.ecommerce.promotion.service.PromotionIssueService;
import com.ecommerce.promotion.viewmodel.PromotionIssueVm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PromotionIssueController {

    private final PromotionIssueService promotionIssueService;

    @PostMapping("/storefront/promotions/{promotionId}/issue")
    public ResponseEntity<PromotionIssueVm> issue(@PathVariable Long promotionId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(promotionIssueService.issue(promotionId));
    }
}
