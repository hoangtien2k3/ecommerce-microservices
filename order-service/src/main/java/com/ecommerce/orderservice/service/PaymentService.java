package com.ecommerce.orderservice.service;

import com.ecommerce.orderservice.dto.payment.PaymentDto;

public interface PaymentService {

    PaymentDto save(PaymentDto paymentDto);

}
