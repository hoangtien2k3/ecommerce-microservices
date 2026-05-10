package com.ecommerce.paymentservice.repository;

import com.ecommerce.paymentservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    @Query("SELECT CASE " +
            "WHEN COUNT(p) > 0 THEN TRUE " +
            "ELSE FALSE " +
            "END " +
            "FROM Payment p " +
            "WHERE p.orderId = :orderId " +
            "AND p.isPayed = TRUE")
    boolean existsByOrderIdAndIsPayed(Integer orderId);
}
