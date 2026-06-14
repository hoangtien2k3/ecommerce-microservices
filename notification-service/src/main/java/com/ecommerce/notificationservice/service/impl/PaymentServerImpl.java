package com.ecommerce.notificationservice.service.impl;

import com.ecommerce.notificationservice.dto.PaymentDto;
import com.ecommerce.notificationservice.entity.Payment;
import com.ecommerce.notificationservice.helper.PaymentMappingHelper;
import com.ecommerce.notificationservice.repository.PaymentRepository;
import com.ecommerce.notificationservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServerImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServerImpl.class);

    private final PaymentRepository paymentRepository;

    @Override
    public Payment savePayment(PaymentDto paymentDto) {
        try {
            return paymentRepository.save(PaymentMappingHelper.map(paymentDto));
        } catch (Exception e) {
            log.error("Error saving payment: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Payment getPayment(Long paymentId) {
        return paymentRepository.findById(paymentId).orElse(null);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public void deletePayment(Long paymentId) {
        paymentRepository.deleteById(paymentId);
    }
}
