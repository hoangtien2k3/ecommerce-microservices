package com.ecommerce.orderservice.service.impl;

import com.ecommerce.orderservice.dto.payment.PaymentDto;
import com.ecommerce.orderservice.entity.Payment;
import com.ecommerce.orderservice.entity.PaymentStatus;
import com.ecommerce.orderservice.helper.PaymentMappingHelper;
import com.ecommerce.orderservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentStore {

    private final PaymentRepository paymentRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PaymentDto saveUnpaid(PaymentDto paymentDto) {
        Payment payment = unpaidOrNew(paymentDto);
        payment.setIsPayed(false);
        payment.setPaymentStatus(PaymentStatus.NOT_STARTED);
        return PaymentMappingHelper.map(paymentRepository.save(payment));
    }

    @Transactional
    public PaymentDto saveCompleted(PaymentDto paymentDto) {
        Payment payment = unpaidOrNew(paymentDto);
        payment.setIsPayed(true);
        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        return PaymentMappingHelper.map(paymentRepository.save(payment));
    }

    private Payment unpaidOrNew(PaymentDto paymentDto) {
        if (paymentDto.getOrderId() != null) {
            List<Payment> unpaid = paymentRepository.findUnpaidByOrderId(paymentDto.getOrderId());
            if (!unpaid.isEmpty()) {
                Payment existing = unpaid.get(0);
                existing.setUserId(paymentDto.getUserId());
                return existing;
            }
        }
        Payment created = PaymentMappingHelper.map(paymentDto);
        created.setPaymentId(null);
        return created;
    }

}
