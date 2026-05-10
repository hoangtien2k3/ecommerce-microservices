package com.ecommerce.notificationservice.helper;

import com.ecommerce.notificationservice.dto.PaymentDto;
import com.ecommerce.notificationservice.entity.Payment;

public interface PaymentMappingHelper {

    static PaymentDto map(Payment payment) {
        if (payment == null) return null;
        return PaymentDto.builder()
                .paymentId(payment.getPaymentId())
                .isPayed(payment.getIsPayed())
                .paymentStatus(payment.getPaymentStatus())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .build();
    }

    static Payment map(PaymentDto paymentDto) {
        if (paymentDto == null) return null;
        return Payment.builder()
                .paymentId(paymentDto.getPaymentId())
                .isPayed(paymentDto.getIsPayed())
                .paymentStatus(paymentDto.getPaymentStatus())
                .orderId(paymentDto.getOrderId())
                .userId(paymentDto.getUserId())
                .build();
    }

}
