package com.ecommerce.paymentservice.service;

import com.ecommerce.paymentservice.dto.PaymentDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PaymentService {
    List<PaymentDto> findAll();
    Page<PaymentDto> findAll(int page, int size, String sortBy, String sortOrder);
    PaymentDto findById(Integer paymentId);
    PaymentDto save(PaymentDto paymentDto);
    PaymentDto update(PaymentDto paymentDto);
    PaymentDto update(Integer paymentId, PaymentDto paymentDto);
    void deleteById(Integer paymentId);
}
