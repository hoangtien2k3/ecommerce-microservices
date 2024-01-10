package com.hoangtien2k3.paymentservice.api;

import com.hoangtien2k3.paymentservice.dto.OrderDto;
import com.hoangtien2k3.paymentservice.dto.PaymentDto;
import com.hoangtien2k3.paymentservice.http.HeaderGenerator;
import com.hoangtien2k3.paymentservice.service.PaymentService;
import com.hoangtien2k3.paymentservice.service.impl.PaymentServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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

    @ApiOperation(value = "Get all payment", notes = "Retrieve a list of all payment.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Payments retrieved successfully", response = List.class),
            @ApiResponse(code = 204, message = "No content", response = ResponseEntity.class)
    })
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<ResponseEntity<List<PaymentDto>>> findAll() {
        log.info("*** PaymentDto List, controller; fetch all categories *");
        return paymentService.findAll()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.ok(Collections.emptyList()));
    }

    @ApiOperation(value = "Get all payments with paging", notes = "Retrieve a paginated list of all payments.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Payments retrieved successfully", response = Page.class),
            @ApiResponse(code = 204, message = "No content", response = ResponseEntity.class)
    })
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

    @ApiOperation(value = "Get payment by ID", notes = "Retrieve cart information based on the provided ID.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Payment retrieved successfully", response = PaymentDto.class),
            @ApiResponse(code = 404, message = "Payment not found", response = ResponseEntity.class)
    })
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


    @ApiOperation(value = "Save payment", notes = "Save a new payment.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Payment saved successfully", response = PaymentDto.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ResponseEntity.class)
    })
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

    @ApiOperation(value = "Update payment", notes = "Update an existing payment.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Payment updated successfully", response = PaymentDto.class),
            @ApiResponse(code = 404, message = "Payment not found", response = ResponseEntity.class)
    })
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


    @ApiOperation(value = "Update payment by ID", notes = "Update an existing cart based on the provided ID.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Payment updated successfully", response = PaymentDto.class),
            @ApiResponse(code = 404, message = "Payment not found", response = ResponseEntity.class)
    })
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

    @ApiOperation(value = "Delete payment by ID", notes = "Delete a cart based on the provided ID.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Payment deleted successfully", response = Boolean.class),
            @ApiResponse(code = 404, message = "Payment not found", response = ResponseEntity.class)
    })
    @DeleteMapping("/{paymentId}")
    @PreAuthorize("hasAuthority('USER')")
    public Mono<ResponseEntity<Boolean>> deleteById(@PathVariable("paymentId") final Integer paymentId) {
        log.info("*** Boolean, resource; delete payment by id *");
        return paymentService.deleteById(paymentId)
                .thenReturn(ResponseEntity.ok(true))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).body(false));
    }

}