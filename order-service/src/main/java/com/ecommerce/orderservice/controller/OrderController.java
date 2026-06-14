package com.ecommerce.orderservice.controller;

import com.ecommerce.orderservice.dto.order.OrderDto;
import com.ecommerce.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orders")
@Tag(name = "OrderController", description = "Operations related to orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<List<OrderDto>> findAll() {
        log.info("*** OrderDto List, controller; fetch all orders *");
        return ResponseEntity.ok(orderService.findAll());
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<Page<OrderDto>> findAll(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   @RequestParam(defaultValue = "orderId") String sortBy,
                                                   @RequestParam(defaultValue = "asc") String sortOrder) {
        return ResponseEntity.ok(orderService.findAll(page, size, sortBy, sortOrder));
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<OrderDto> findById(@PathVariable("orderId")
                                              @NotBlank(message = "Input must not be blank")
                                              @Valid final String orderId) {
        log.info("*** OrderDto, resource; fetch order by id *");
        return ResponseEntity.ok(orderService.findById(Integer.parseInt(orderId)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<OrderDto> save(@RequestBody
                                          @NotNull(message = "Input must not be NULL")
                                          @Valid final OrderDto orderDto) {
        log.info("*** OrderDto, resource; save order *");
        return ResponseEntity.ok(orderService.save(orderDto));
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<OrderDto> update(@RequestBody
                                            @NotNull(message = "Input must not be NULL")
                                            @Valid final OrderDto orderDto) {
        log.info("*** OrderDto, resource; update order *");
        return ResponseEntity.ok(orderService.update(orderDto));
    }

    @PutMapping("/{orderId}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<OrderDto> update(@PathVariable("orderId")
                                            @NotBlank(message = "Input must not be blank")
                                            @Valid final String orderId,
                                            @RequestBody
                                            @NotNull(message = "Input must not be NULL")
                                            @Valid final OrderDto orderDto) {
        log.info("*** OrderDto, resource; update order with orderId *");
        return ResponseEntity.ok(orderService.update(Integer.parseInt(orderId), orderDto));
    }

    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<Boolean> deleteById(@PathVariable("orderId") final String orderId) {
        log.info("*** Boolean, resource; delete order by id *");
        orderService.deleteById(Integer.parseInt(orderId));
        return ResponseEntity.ok(true);
    }

    @GetMapping("/existOrderId")
    public ResponseEntity<Boolean> existsByOrderId(@RequestParam Integer orderId) {
        return ResponseEntity.ok(orderService.existsByOrderId(orderId));
    }
}
