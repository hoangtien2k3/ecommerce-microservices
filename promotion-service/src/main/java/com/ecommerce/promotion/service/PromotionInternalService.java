package com.ecommerce.promotion.service;

import com.ecommerce.commonlib.exception.BusinessException;
import com.ecommerce.commonlib.security.AuthenticationUtils;
import com.ecommerce.promotion.model.Promotion;
import com.ecommerce.promotion.model.PromotionIssue;
import com.ecommerce.promotion.model.PromotionUsage;
import com.ecommerce.promotion.model.enumeration.PromotionIssueStatus;
import com.ecommerce.promotion.repository.PromotionIssueRepository;
import com.ecommerce.promotion.repository.PromotionRepository;
import com.ecommerce.promotion.repository.PromotionUsageRepository;
import com.ecommerce.promotion.viewmodel.PromotionIssueStateVm;
import com.ecommerce.promotion.viewmodel.PromotionUseRequest;
import com.ecommerce.promotion.viewmodel.UnusedPromotionIssueVm;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PromotionInternalService {

    private final PromotionIssueRepository promotionIssueRepository;
    private final PromotionUsageRepository promotionUsageRepository;
    private final PromotionRepository promotionRepository;

    @Transactional(readOnly = true)
    public UnusedPromotionIssueVm unusedIssue(Long promotionId) {
        PromotionIssue issue = requireIssue(promotionId, AuthenticationUtils.requireUserId());
        if (issue.getStatus() == PromotionIssueStatus.USED) {
            throw BusinessException.conflict("promotion.coupon.already.used");
        }
        if (!isOpen(issue.getPromotion())) {
            throw BusinessException.badRequest("promotion.coupon.not.open");
        }
        return toUnusedVm(issue);
    }

    @Transactional
    public PromotionIssueStateVm use(Long promotionId, PromotionUseRequest request) {
        String userId = AuthenticationUtils.requireUserId();
        Instant now = Instant.now();
        int updated = promotionIssueRepository.markUsed(
                promotionId, userId, request.orderId(), now,
                PromotionIssueStatus.USED, PromotionIssueStatus.ISSUED);
        if (updated == 0) {
            throw explainUnavailable(promotionId, userId);
        }
        promotionUsageRepository.save(PromotionUsage.builder()
                .promotion(promotionRepository.getReferenceById(promotionId))
                .userId(userId)
                .orderId(request.orderId())
                .productId(request.productId())
                .build());
        PromotionIssue issue = requireIssue(promotionId, userId);
        return new PromotionIssueStateVm(issue.getId(), promotionId, request.orderId(), issue.getStatus());
    }

    @Transactional
    public void release(Long promotionId, Long orderId) {
        String userId = AuthenticationUtils.requireUserId();
        int updated = promotionIssueRepository.release(
                promotionId, userId, orderId,
                PromotionIssueStatus.ISSUED, PromotionIssueStatus.USED);
        if (updated == 0) {
            throw BusinessException.conflict("promotion.coupon.release.mismatch");
        }
        promotionUsageRepository.deleteByPromotion_IdAndUserIdAndOrderId(promotionId, userId, orderId);
    }

    private PromotionIssue requireIssue(Long promotionId, String userId) {
        return promotionIssueRepository.findByPromotion_IdAndUserId(promotionId, userId)
                .orElseThrow(() -> BusinessException.notFound("promotion.coupon.not.claimed"));
    }

    private BusinessException explainUnavailable(Long promotionId, String userId) {
        PromotionIssue issue = promotionIssueRepository.findByPromotion_IdAndUserId(promotionId, userId)
                .orElseThrow(() -> BusinessException.notFound("promotion.coupon.not.claimed"));
        if (issue.getStatus() == PromotionIssueStatus.USED) {
            return BusinessException.conflict("promotion.coupon.already.used");
        }
        return BusinessException.badRequest("promotion.coupon.not.open");
    }

    private boolean isOpen(Promotion promotion) {
        Instant now = Instant.now();
        return Boolean.TRUE.equals(promotion.getIsActive())
                && (promotion.getStartDate() == null || !promotion.getStartDate().isAfter(now))
                && (promotion.getEndDate() == null || !promotion.getEndDate().isBefore(now));
    }

    private UnusedPromotionIssueVm toUnusedVm(PromotionIssue issue) {
        Promotion promotion = issue.getPromotion();
        return new UnusedPromotionIssueVm(
                issue.getId(),
                promotion.getId(),
                issue.getUserId(),
                promotion.getDiscountType(),
                promotion.getDiscountAmount(),
                promotion.getDiscountPercentage());
    }
}
