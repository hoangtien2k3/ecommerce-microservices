package com.ecommerce.notificationservice.service;

import com.ecommerce.notificationservice.dto.PaymentDto;
import com.ecommerce.notificationservice.entity.Payment;

import java.util.List;

public interface PaymentService {
    Payment savePayment(PaymentDto paymentDto);
    Payment getPayment(Long paymentId);
    List<Payment> getAllPayments();
    void deletePayment(Long paymentId);
}
