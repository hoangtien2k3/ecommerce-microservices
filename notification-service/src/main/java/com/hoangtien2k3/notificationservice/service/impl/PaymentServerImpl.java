package com.hoangtien2k3.notificationservice.service.impl;

import com.hoangtien2k3.notificationservice.dto.PaymentDto;
import com.hoangtien2k3.notificationservice.entity.Payment;
import com.hoangtien2k3.notificationservice.helper.PaymentMappingHelper;
import com.hoangtien2k3.notificationservice.repository.PaymentRepository;
import com.hoangtien2k3.notificationservice.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class PaymentServerImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentServerImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Mono<Payment> savePayment(PaymentDto paymentDto) {
        return Mono.fromSupplier(() -> paymentRepository.save(PaymentMappingHelper.map(paymentDto)))
                .onErrorResume(throwable -> {
                    log.error("Error saving payment: {}", throwable.getMessage());
                    return Mono.error(throwable);
                });
    }

    @Override
    public Mono<Payment> getPayment(Integer paymentId) {
        return Mono.fromSupplier(() -> paymentRepository.findById(paymentId)
                        .orElse(null));
    }

    @Override
    public Mono<List<Payment>> getAllPayments() {
        return Mono.fromSupplier(paymentRepository::findAll)
                .onErrorResume(throwable -> {
                    log.error("Error fetching user info: {}", throwable.getMessage());
                    return Mono.empty();
                });
    }

    @Override
    public Mono<Void> deletePayment(Integer paymentId) {
        log.info("Void, service; delete payment by id");
        return Mono.fromRunnable(() -> paymentRepository.deleteById(paymentId));
    }
}
