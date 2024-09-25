package com.hoangtien2k3.promotion.repository;

import com.hoangtien2k3.promotion.model.PromotionUsage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionUsageRepository extends JpaRepository<PromotionUsage, Long> {
    boolean existsByPromotionId(Long promotionId);
}
