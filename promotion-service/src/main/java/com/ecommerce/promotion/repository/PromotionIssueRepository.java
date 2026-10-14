package com.ecommerce.promotion.repository;

import com.ecommerce.promotion.model.PromotionIssue;
import com.ecommerce.promotion.model.enumeration.PromotionIssueStatus;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PromotionIssueRepository extends JpaRepository<PromotionIssue, Long> {

    Optional<PromotionIssue> findByPromotion_IdAndUserId(Long promotionId, String userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE PromotionIssue i
            SET i.status = :used, i.usedAt = :now, i.orderId = :orderId
            WHERE i.promotion.id = :promotionId
              AND i.userId = :userId
              AND i.status = :issued
            """)
    int markUsed(@Param("promotionId") Long promotionId,
                 @Param("userId") String userId,
                 @Param("orderId") Long orderId,
                 @Param("now") Instant now,
                 @Param("used") PromotionIssueStatus used,
                 @Param("issued") PromotionIssueStatus issued);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE PromotionIssue i
            SET i.status = :issued, i.usedAt = NULL, i.orderId = NULL
            WHERE i.promotion.id = :promotionId
              AND i.userId = :userId
              AND i.status = :used
              AND i.orderId = :orderId
            """)
    int release(@Param("promotionId") Long promotionId,
                @Param("userId") String userId,
                @Param("orderId") Long orderId,
                @Param("issued") PromotionIssueStatus issued,
                @Param("used") PromotionIssueStatus used);
}
