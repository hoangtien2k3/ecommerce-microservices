package com.ecommerce.notificationservice.service;

import com.ecommerce.notificationservice.dto.PaymentDto;
import com.ecommerce.notificationservice.entity.Payment;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PaymentService {
    Mono<Payment> savePayment(PaymentDto paymentDto);
    Mono<Payment> getPayment(Long paymentId);
    Mono<List<Payment>> getAllPayments();
    Mono<Void> deletePayment(Long paymentId);
}
