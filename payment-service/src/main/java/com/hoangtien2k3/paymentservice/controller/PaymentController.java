package com.hoangtien2k3.paymentservice.controller;

import com.hoangtien2k3.paymentservice.dto.PaymentDto;
import com.hoangtien2k3.paymentservice.dto.response.collection.DtoCollectionResponse;
import com.hoangtien2k3.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<DtoCollectionResponse<PaymentDto>> findAll() {
        log.info("PaymentDto List, controller; fetch all payments");
        return ResponseEntity.ok(new DtoCollectionResponse<>(this.paymentService.findAll()));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDto> findById(@PathVariable("paymentId")
                                               @NotBlank(message = "Input must not be blank")
                                               @Valid final String paymentId) {
        log.info("*** PaymentDto, resource; fetch payment by id *");
        return ResponseEntity.ok(this.paymentService.findById(Integer.parseInt(paymentId)));
    }


    @PostMapping
    public ResponseEntity<PaymentDto> save(@RequestBody
                                           @NotNull(message = "Input must not be NULL")
                                           @Valid final PaymentDto paymentDto) {
        log.info("PaymentDto, resource; save payment");
        return ResponseEntity.ok(this.paymentService.save(paymentDto));
    }

    @PutMapping
    public ResponseEntity<PaymentDto> update(@RequestBody
                                             @NotNull(message = "Input must not be NULL")
                                             @Valid final PaymentDto paymentDto) {
        log.info("PaymentDto, resource; update payment");
        return ResponseEntity.ok(this.paymentService.update(paymentDto));
    }

    @DeleteMapping("/{paymentId}")
    public ResponseEntity<Boolean> deleteById(@PathVariable("paymentId") final String paymentId) {
        log.info("Boolean, resource; delete payment by id");
        this.paymentService.deleteById(Integer.parseInt(paymentId));
        return ResponseEntity.ok(true);
    }

}
