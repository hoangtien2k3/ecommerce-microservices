package com.ecommerce.orderservice.helper;

import com.ecommerce.orderservice.dto.order.OrderDto;
import com.ecommerce.orderservice.dto.payment.PaymentDto;
import com.ecommerce.orderservice.dto.user.UserDto;
import com.ecommerce.orderservice.entity.Payment;

public interface PaymentMappingHelper {

    static PaymentDto map(final Payment payment) {
        if (payment == null) {
            return null;
        }
        return PaymentDto.builder()
                .paymentId(payment.getPaymentId())
                .isPayed(payment.getIsPayed())
                .paymentStatus(payment.getPaymentStatus())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .orderDto(OrderDto.builder()
                        .orderId(payment.getOrderId())
                        .build())
                .userDto(UserDto.builder()
                        .id(payment.getUserId())
                        .build())
                .build();
    }

    static Payment map(final PaymentDto paymentDto) {
        if (paymentDto == null) {
            return null;
        }
        return Payment.builder()
                .paymentId(paymentDto.getPaymentId())
                .orderId(paymentDto.getOrderId())
                .userId(paymentDto.getUserId())
                .isPayed(paymentDto.getIsPayed())
                .paymentStatus(paymentDto.getPaymentStatus())
                .build();
    }

}
