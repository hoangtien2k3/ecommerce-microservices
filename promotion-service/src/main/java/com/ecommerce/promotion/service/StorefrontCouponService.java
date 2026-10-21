package com.ecommerce.promotion.service;

import com.ecommerce.commonlib.security.AuthenticationUtils;
import com.ecommerce.promotion.config.PromotionIssueProperties;
import com.ecommerce.promotion.model.Promotion;
import com.ecommerce.promotion.repository.PromotionIssueRepository;
import com.ecommerce.promotion.repository.PromotionRepository;
import com.ecommerce.promotion.viewmodel.StorefrontCouponVm;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** The storefront coupon page: active first-come coupons, upcoming, open, or just ended. */
@Slf4j
@Service
@RequiredArgsConstructor
public class StorefrontCouponService {

    // Ended coupons stay listed this long so the page can show them as ended.
    private static final Duration ENDED_VISIBLE = Duration.ofDays(1);
    private static final int MAX_COUPONS = 100;

    private final PromotionRepository promotionRepository;
    private final PromotionIssueRepository promotionIssueRepository;
    private final CouponStockStore couponStockStore;
    private final PromotionIssueProperties properties;

    @Transactional(readOnly = true)
    public List<StorefrontCouponVm> listCoupons() {
        List<Promotion> coupons = promotionRepository.findFirstComeCoupons(
                Instant.now().minus(ENDED_VISIBLE), PageRequest.of(0, MAX_COUPONS));
        if (coupons.isEmpty()) {
            return List.of();
        }
        List<Long> ids = coupons.stream().map(Promotion::getId).toList();
        Map<Long, Integer> liveCounts = liveIssuedCounts(coupons);
        Set<Long> claimed = AuthenticationUtils.currentUserId()
                .<Set<Long>>map(userId -> new HashSet<>(promotionIssueRepository.findClaimedPromotionIds(userId, ids)))
                .orElse(Set.of());
        return coupons.stream()
                .map(p -> StorefrontCouponVm.of(p,
                        liveCounts.getOrDefault(p.getId(), p.getIssuedCount()),
                        claimed.contains(p.getId())))
                .toList();
    }

    /**
     * With the Redis store, issued counts come from the live stock rather than issued_count,
     * which trails by a few seconds. Falls back to issued_count when Redis is unavailable:
     * the list is only a display, unlike claiming.
     */
    private Map<Long, Integer> liveIssuedCounts(List<Promotion> coupons) {
        if (properties.getStore() != PromotionIssueProperties.Store.REDIS) {
            return Map.of();
        }
        try {
            return couponStockStore.issuedCounts(coupons.stream()
                    .collect(Collectors.toMap(Promotion::getId, Promotion::getIssueLimit)));
        } catch (DataAccessException e) {
            log.warn("Coupon store unavailable, listing stored issued counts: {}", e.getMessage());
            return Map.of();
        }
    }
}
