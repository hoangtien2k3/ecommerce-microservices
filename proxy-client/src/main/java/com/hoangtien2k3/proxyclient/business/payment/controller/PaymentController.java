package com.hoangtien2k3.proxyclient.business.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hoangtien2k3.proxyclient.business.payment.model.PaymentDto;
import com.hoangtien2k3.proxyclient.business.payment.model.response.PaymentPaymentServiceDtoCollectionResponse;
import com.hoangtien2k3.proxyclient.business.payment.service.PaymentClientService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentClientService paymentClientService;

    @GetMapping
    public ResponseEntity<PaymentPaymentServiceDtoCollectionResponse> findAll() {
        return ResponseEntity.ok(this.paymentClientService.findAll().getBody());
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDto> findById(@PathVariable("paymentId") final String paymentId) {
        return ResponseEntity.ok(this.paymentClientService.findById(paymentId).getBody());
    }

    @PostMapping
    public ResponseEntity<PaymentDto> save(@RequestBody final PaymentDto paymentDto) {
        return ResponseEntity.ok(this.paymentClientService.save(paymentDto).getBody());
    }

    @PutMapping
    public ResponseEntity<PaymentDto> update(@RequestBody final PaymentDto paymentDto) {
        return ResponseEntity.ok(this.paymentClientService.update(paymentDto).getBody());
    }

    @DeleteMapping("/{paymentId}")
    public ResponseEntity<Boolean> deleteById(@PathVariable("paymentId") final String paymentId) {
        return ResponseEntity.ok(this.paymentClientService.deleteById(paymentId).getBody());
    }

}