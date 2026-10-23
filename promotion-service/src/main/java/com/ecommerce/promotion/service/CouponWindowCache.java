package com.ecommerce.promotion.service;

import com.ecommerce.promotion.config.PromotionIssueProperties;
import com.ecommerce.promotion.model.Promotion;
import com.ecommerce.promotion.repository.PromotionRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Per-pod cache of what a claim needs to know about a coupon (open or not, how many in total),
 * so a burst of claims does not read the promotion row on every request. Entries live
 * {@code promotion.issue.window-cache-ttl}; edits made on another pod show up after that.
 */
@Component
@RequiredArgsConstructor
public class CouponWindowCache {

    private final PromotionRepository promotionRepository;
    private final PromotionIssueProperties properties;
    private final ConcurrentHashMap<Long, Entry> entries = new ConcurrentHashMap<>();

    public record CouponWindow(Long promotionId, boolean active, Integer issueLimit,
                               Instant startDate, Instant endDate) {

        static CouponWindow of(Promotion promotion) {
            return new CouponWindow(promotion.getId(), Boolean.TRUE.equals(promotion.getIsActive()),
                    promotion.getIssueLimit(), promotion.getStartDate(), promotion.getEndDate());
        }

        public boolean isOpen(Instant now) {
            return active
                    && issueLimit != null
                    && (startDate == null || !startDate.isAfter(now))
                    && (endDate == null || !endDate.isBefore(now));
        }
    }

    private record Entry(Optional<CouponWindow> window, long expiresAtNanos) {
    }

    /** The coupon's window, or empty if no such promotion exists. */
    public Optional<CouponWindow> get(Long promotionId) {
        long now = System.nanoTime();
        Entry entry = entries.get(promotionId);
        if (entry == null || entry.expiresAtNanos() - now <= 0) {
            entry = new Entry(promotionRepository.findById(promotionId).map(CouponWindow::of),
                    now + properties.getWindowCacheTtl().toNanos());
            entries.put(promotionId, entry);
        }
        return entry.window();
    }

    public void evict(Long promotionId) {
        entries.remove(promotionId);
    }
}
