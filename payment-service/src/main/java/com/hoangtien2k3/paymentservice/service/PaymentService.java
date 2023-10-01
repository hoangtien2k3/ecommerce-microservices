package com.hoangtien2k3.paymentservice.service;

import com.hoangtien2k3.paymentservice.dto.PaymentDto;

import java.util.List;

public interface PaymentService {
    List<PaymentDto> findAll();
    PaymentDto findById(final Integer paymentId);
    PaymentDto save(final PaymentDto paymentDto);
    PaymentDto update(final PaymentDto paymentDto);
    void deleteById(final Integer paymentId);
}
