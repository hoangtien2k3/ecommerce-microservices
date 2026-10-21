package com.ecommerce.promotion.repository;

import com.ecommerce.promotion.model.Promotion;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    Optional<Promotion> findBySlugAndIsActiveTrue(String slug);

    @Query("SELECT p FROM Promotion p "
            + "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%',:name,'%')) "
            + "AND LOWER(p.couponCode) LIKE LOWER(CONCAT('%',:couponCode,'%')) "
            + "AND (cast(:startDate as date) is null or (p.startDate BETWEEN :startDate AND :endDate)) "
            + "AND (cast(:startDate as date) is null or (p.endDate BETWEEN :startDate AND :endDate))")
    Page<Promotion> findPromotions(@Param("name") String name,
                                   @Param("couponCode") String couponCode,
                                   @Param("startDate") Instant startDate,
                                   @Param("endDate") Instant endDate,
                                   Pageable pageable);

    /** Active first-come coupons that have not ended, or ended after {@code endedAfter}. */
    @Query("""
            SELECT p FROM Promotion p
            WHERE p.issueLimit IS NOT NULL
              AND p.isActive = true
              AND (p.endDate IS NULL OR p.endDate >= :endedAfter)
            ORDER BY p.startDate, p.id
            """)
    List<Promotion> findFirstComeCoupons(@Param("endedAfter") Instant endedAfter, Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE Promotion p
            SET p.issuedCount = p.issuedCount + 1
            WHERE p.id = :id
              AND p.issueLimit IS NOT NULL
              AND p.issuedCount < p.issueLimit
              AND p.isActive = true
              AND (p.startDate IS NULL OR p.startDate <= :now)
              AND (p.endDate IS NULL OR p.endDate >= :now)
            """)
    int claimIssueSlot(@Param("id") Long id, @Param("now") Instant now);

    /**
     * Copies the number of issued rows into {@code issued_count} for first-come coupons that
     * have not been over for long. Touches only rows whose count is off.
     */
    @Modifying
    @Query(nativeQuery = true, value = """
            UPDATE promotion p
            SET p.issued_count = (SELECT COUNT(*) FROM promotion_issue i WHERE i.promotion_id = p.id)
            WHERE p.issue_limit IS NOT NULL
              AND (p.end_date IS NULL OR p.end_date >= :endedAfter)
              AND p.issued_count <> (SELECT COUNT(*) FROM promotion_issue i WHERE i.promotion_id = p.id)
            """)
    int syncIssuedCounts(@Param("endedAfter") Instant endedAfter);

}
