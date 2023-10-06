package com.hoangtien2k3.paymentservice.controller;

import com.hoangtien2k3.paymentservice.config.webclient.WebClientConfig;
import com.hoangtien2k3.paymentservice.dto.PaymentDto;
import com.hoangtien2k3.paymentservice.dto.response.collection.DtoCollectionResponse;
import com.hoangtien2k3.paymentservice.security.JwtValidate;
import com.hoangtien2k3.paymentservice.service.PaymentService;
import com.netflix.discovery.converters.Auto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private final PaymentService paymentService;
    @Autowired
    private final JwtValidate jwtValidate;

    @GetMapping
    public ResponseEntity<DtoCollectionResponse<PaymentDto>> findAll() {
        log.info("PaymentDto List, controller; fetch all payments");
        return ResponseEntity.ok(new DtoCollectionResponse<>(this.paymentService.findAll()));
    }

//    @GetMapping
//    public ResponseEntity<DtoCollectionResponse<PaymentDto>> findAll() {
//        log.info("PaymentDto List, controller; fetch all payments");
//
//        // Gọi phương thức findAll() từ PaymentService để lấy danh sách PaymentDto
//        List<PaymentDto> paymentDtos = this.paymentService.findAll();
//
//        // Trả về danh sách PaymentDto trong ResponseEntity
//        return ResponseEntity.ok(new DtoCollectionResponse<>(paymentDtos));
//    }

    @GetMapping("/inventory-test")
    @ResponseBody
    public Mono<ResponseEntity<List<String>>> callServiceB() {
        String serviceBUrl = "http://localhost:8083/api/inventory?productName=iphone_13,iphone_13_red";

        return paymentService.callServiceB(serviceBUrl)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDto> findById(@PathVariable("paymentId")
                                               @NotBlank(message = "Input must not be blank")
                                               @Valid final Integer paymentId) {
        log.info("PaymentDto, resource; fetch payment by id");
        return ResponseEntity.ok(this.paymentService.findById(paymentId));
    }

    @PostMapping
    public ResponseEntity<PaymentDto> save(@RequestHeader(name = "Authorization") String authorizationHeader,
                                           @RequestBody @NotNull(message = "Input must not be NULL")
                                           @Valid final PaymentDto paymentDto) {
        if (!jwtValidate.validateTokenUserService(authorizationHeader)) {
            ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).build();
        }
        log.info("PaymentDto, resource; save payment");
        return ResponseEntity.ok(this.paymentService.save(paymentDto));
    }

    @PutMapping
    public ResponseEntity<PaymentDto> update(@RequestHeader(name = "Authorization") String authorizationHeader,
                                             @RequestBody @NotNull(message = "Input must not be NULL")
                                             @Valid final PaymentDto paymentDto) {
        if (!jwtValidate.validateTokenUserService(authorizationHeader)) {
            ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).build();
        }
        log.info("PaymentDto, resource; update payment");
        return ResponseEntity.ok(this.paymentService.update(paymentDto));
    }

    @DeleteMapping("/{paymentId}")
    public ResponseEntity<Boolean> deleteById(@RequestHeader(name = "Authorization") String authorizationHeader,
                                              @PathVariable("paymentId") final String paymentId) {
        if (!jwtValidate.validateTokenUserService(authorizationHeader)) {
            ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).build();
        }
        log.info("Boolean, resource; delete payment by id");
        this.paymentService.deleteById(Integer.parseInt(paymentId));
        return ResponseEntity.ok(true);
    }

}
