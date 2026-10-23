package com.ecommerce.promotion.controller;

import com.ecommerce.promotion.service.PromotionInternalService;
import com.ecommerce.promotion.viewmodel.PromotionIssueStateVm;
import com.ecommerce.promotion.viewmodel.PromotionReleaseRequest;
import com.ecommerce.promotion.viewmodel.PromotionUseRequest;
import com.ecommerce.promotion.viewmodel.UnusedPromotionIssueVm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/promotions")
@RequiredArgsConstructor
public class PromotionInternalController {

    private final PromotionInternalService promotionInternalService;

    @GetMapping("/{promotionId}/unused-issue")
    public UnusedPromotionIssueVm unusedIssue(@PathVariable Long promotionId) {
        return promotionInternalService.unusedIssue(promotionId);
    }

    @PostMapping("/{promotionId}/use")
    public PromotionIssueStateVm use(@PathVariable Long promotionId,
                                     @Valid @RequestBody PromotionUseRequest request) {
        return promotionInternalService.use(promotionId, request);
    }

    @PostMapping("/{promotionId}/release")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void release(@PathVariable Long promotionId,
                        @Valid @RequestBody PromotionReleaseRequest request) {
        promotionInternalService.release(promotionId, request.orderId());
    }
}
