package com.ecommerce.promotion.repository;

import com.ecommerce.promotion.model.PromotionIssue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionIssueRepository extends JpaRepository<PromotionIssue, Long> {
}
