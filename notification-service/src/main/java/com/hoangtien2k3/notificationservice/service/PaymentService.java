package com.hoangtien2k3.notificationservice.service;

import com.hoangtien2k3.notificationservice.dto.PaymentDto;
import com.hoangtien2k3.notificationservice.entity.Payment;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PaymentService {
    Mono<Payment> savePayment(PaymentDto paymentDto);
    Mono<Payment> getPayment(Integer paymentId);
    Mono<List<Payment>> getAllPayments();
    Mono<Void> deletePayment(Integer paymentId);
}
