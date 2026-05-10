package com.ecommerce.paymentservice.helper;

import com.ecommerce.paymentservice.dto.OrderDto;
import com.ecommerce.paymentservice.dto.PaymentDto;
import com.ecommerce.paymentservice.dto.UserDto;
import com.ecommerce.paymentservice.entity.Payment;

public interface PaymentMappingHelper {
    static PaymentDto map(final Payment payment) {
        return PaymentDto.builder()
                .paymentId(payment.getPaymentId())
                .isPayed(payment.getIsPayed())
                .paymentStatus(payment.getPaymentStatus())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .orderDto(
                        OrderDto.builder()
                                .orderId(payment.getOrderId())
                                .build())
                .userDto(UserDto.builder()
                        .id(payment.getUserId())
                        .build())
                .build();
    }

    static Payment map(final PaymentDto paymentDto) {
        return Payment.builder()
                .paymentId(paymentDto.getPaymentId())
                .orderId(paymentDto.getOrderId())
                .userId(paymentDto.getUserId())
                .isPayed(paymentDto.getIsPayed())
                .paymentStatus(paymentDto.getPaymentStatus())
                .build();
    }
}
