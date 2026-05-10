package com.ecommerce.paymentservice.api;

import com.ecommerce.paymentservice.dto.OrderDto;
import com.ecommerce.paymentservice.dto.PaymentDto;
import com.ecommerce.paymentservice.http.HeaderGenerator;
import com.ecommerce.paymentservice.service.PaymentService;
import com.ecommerce.paymentservice.service.impl.PaymentServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@Slf4j
@RequiredArgsConstructor
public class PaymentController {

    @Autowired
    private final PaymentService paymentService;

    @Autowired
    private final PaymentServiceImpl paymentServiceImpl;

    @Autowired
    private final HeaderGenerator headerGenerator;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<ResponseEntity<List<PaymentDto>>> findAll() {
        log.info("*** PaymentDto List, controller; fetch all categories *");
        return paymentService.findAll()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.ok(Collections.emptyList()));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<ResponseEntity<Page<PaymentDto>>> findAll(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size,
                                                          @RequestParam(defaultValue = "paymentId") String sortBy,
                                                          @RequestParam(defaultValue = "asc") String sortOrder) {
        return paymentService.findAll(page, size, sortBy, sortOrder)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{paymentId}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<Mono<PaymentDto>> findById(@PathVariable("paymentId")
                                                     @NotBlank(message = "Input must not be blank")
                                                     @Valid final String paymentId) {
        log.info("*** PaymentDto, resource; fetch cart by id *");
        return ResponseEntity.ok(paymentService.findById(Integer.parseInt(paymentId)));
    }


    @GetMapping("/getOrder/{orderId}")
    public ResponseEntity<Mono<OrderDto>> getOrderDto(@PathVariable("orderId") final Integer orderId) {
        return ResponseEntity.ok(paymentServiceImpl.getOrderDto(orderId));
    }


    @PostMapping
    @PreAuthorize("hasAuthority('USER')")
    public Mono<ResponseEntity<PaymentDto>> save(@RequestBody
                                                 @NotNull(message = "Input must not be NULL!")
                                                 @Valid final PaymentDto paymentDto) {
        log.info("*** PaymentDto, resource; save payments *");
        return paymentService.save(paymentDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<ResponseEntity<PaymentDto>> update(@RequestBody
                                                   @NotNull(message = "Input must not be NULL")
                                                   @Valid final PaymentDto paymentDto) {
        log.info("*** CartDto, resource; update cart *");
        return paymentService.update(paymentDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }


    @PutMapping("/{paymentId}")
    @PreAuthorize("hasAuthority('USER')")
    public Mono<ResponseEntity<PaymentDto>> update(@PathVariable("paymentId")
                                                   @NotBlank(message = "Input must not be blank")
                                                   @Valid final Integer paymentId,
                                                   @RequestBody
                                                   @NotNull(message = "Input must not be NULL")
                                                   @Valid final PaymentDto paymentDto) {
        log.info("*** PaymentDto, resource; update cart with paymentId *");
        return paymentService.update(paymentId, paymentDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{paymentId}")
    @PreAuthorize("hasAuthority('USER')")
    public Mono<ResponseEntity<Boolean>> deleteById(@PathVariable("paymentId") final Integer paymentId) {
        log.info("*** Boolean, resource; delete payment by id *");
        return paymentService.deleteById(paymentId)
                .thenReturn(ResponseEntity.ok(true))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).body(false));
    }

}
