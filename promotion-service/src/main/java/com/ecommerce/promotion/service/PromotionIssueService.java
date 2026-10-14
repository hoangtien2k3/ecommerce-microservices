package com.ecommerce.promotion.service;

import com.ecommerce.commonlib.exception.BusinessException;
import com.ecommerce.commonlib.security.AuthenticationUtils;
import com.ecommerce.promotion.model.Promotion;
import com.ecommerce.promotion.model.PromotionIssue;
import com.ecommerce.promotion.repository.PromotionIssueRepository;
import com.ecommerce.promotion.repository.PromotionRepository;
import com.ecommerce.promotion.viewmodel.PromotionIssueVm;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PromotionIssueService {

    private static final String DUPLICATE_ISSUE_CONSTRAINT = "uk_promotion_issue_promotion_user";

    private final PromotionRepository promotionRepository;
    private final PromotionIssueRepository promotionIssueRepository;

    @Transactional
    public PromotionIssueVm issue(Long promotionId) {
        String userId = AuthenticationUtils.requireUserId();
        Instant now = Instant.now();
        if (promotionRepository.claimIssueSlot(promotionId, now) == 0) {
            throw explainClosedClaim(promotionId, now);
        }
        try {
            PromotionIssue saved = promotionIssueRepository.saveAndFlush(PromotionIssue.builder()
                    .promotion(promotionRepository.getReferenceById(promotionId))
                    .userId(userId)
                    .issuedAt(now)
                    .build());
            return new PromotionIssueVm(saved.getId(), promotionId, userId, saved.getIssuedAt());
        } catch (DataIntegrityViolationException ex) {
            if (isDuplicateIssue(ex)) {
                throw BusinessException.conflict("promotion.coupon.already.claimed", ex);
            }
            throw ex;
        }
    }

    private BusinessException explainClosedClaim(Long promotionId, Instant now) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> BusinessException.notFound("promotion.coupon.not.found", promotionId));
        if (!Boolean.TRUE.equals(promotion.getIsActive())
                || !withinWindow(promotion, now)
                || promotion.getIssueLimit() == null) {
            return BusinessException.badRequest("promotion.coupon.not.open");
        }
        return BusinessException.conflict("promotion.coupon.sold.out");
    }

    private boolean withinWindow(Promotion promotion, Instant now) {
        return (promotion.getStartDate() == null || !promotion.getStartDate().isAfter(now))
                && (promotion.getEndDate() == null || !promotion.getEndDate().isBefore(now));
    }

    private boolean isDuplicateIssue(DataIntegrityViolationException ex) {
        String message = ex.getMostSpecificCause().getMessage();
        return message != null && message.contains(DUPLICATE_ISSUE_CONSTRAINT);
    }
}
