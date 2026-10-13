package com.ecommerce.orderservice.repository;

import com.ecommerce.orderservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    @Query("SELECT CASE " +
            "WHEN COUNT(p) > 0 THEN TRUE " +
            "ELSE FALSE " +
            "END " +
            "FROM Payment p " +
            "WHERE p.orderId = :orderId " +
            "AND p.isPayed = TRUE")
    boolean existsByOrderIdAndIsPayed(@Param("orderId") Integer orderId);

    @Query("SELECT p FROM Payment p WHERE p.orderId = :orderId AND p.isPayed = FALSE ORDER BY p.paymentId ASC")
    List<Payment> findUnpaidByOrderId(@Param("orderId") Integer orderId);

}
