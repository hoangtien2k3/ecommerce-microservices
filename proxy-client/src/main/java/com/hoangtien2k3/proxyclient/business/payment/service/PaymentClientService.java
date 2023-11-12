package com.hoangtien2k3.proxyclient.business.payment.service;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.hoangtien2k3.proxyclient.business.payment.model.PaymentDto;
import com.hoangtien2k3.proxyclient.business.payment.model.response.PaymentPaymentServiceDtoCollectionResponse;

@FeignClient(name = "PAYMENT-SERVICE",
        contextId = "paymentClientService",
        path = "/payment-service/api/payments"
)
@Service
public interface PaymentClientService {

    @GetMapping
    public ResponseEntity<PaymentPaymentServiceDtoCollectionResponse> findAll();

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDto> findById(@PathVariable("paymentId")
                                               @NotBlank(message = "Input must not be blank!")
                                               @Valid final String paymentId);

    @PostMapping
    public ResponseEntity<PaymentDto> save(@RequestBody
                                           @NotNull(message = "Input must not be NULL!")
                                           @Valid final PaymentDto paymentDto);

    @PutMapping
    public ResponseEntity<PaymentDto> update(@RequestBody
                                             @NotNull(message = "Input must not be NULL!")
                                             @Valid final PaymentDto paymentDto);

    @DeleteMapping("/{paymentId}")
    public ResponseEntity<Boolean> deleteById(@PathVariable("paymentId") final String paymentId);

}