package com.hoangtien2k3.orderservice.controller;

import com.hoangtien2k3.orderservice.dto.OrderDto;
import com.hoangtien2k3.orderservice.dto.response.collection.DtoCollectionResponse;
import com.hoangtien2k3.orderservice.service.OrderService;
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
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<DtoCollectionResponse<OrderDto>> findAll() {
        log.info("OrderDto List, controller; fetch all orders");
        return ResponseEntity.ok(new DtoCollectionResponse<>(this.orderService.findAll()));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> findById(@PathVariable("orderId")
                                             @NotBlank(message = "Input must not be blank")
                                             @Valid final String orderId) {
        log.info("OrderDto, resource; fetch order by id");
        return ResponseEntity.ok(this.orderService.findById(Integer.parseInt(orderId)));
    }

    @PostMapping
    public ResponseEntity<OrderDto> save(@RequestBody
                                         @NotNull(message = "Input must not be NULL")
                                         @Valid final OrderDto orderDto) {
        log.info("OrderDto, resource; save order");
        return ResponseEntity.ok(this.orderService.save(orderDto));
    }

    @PutMapping
    public ResponseEntity<OrderDto> update(@RequestBody
                                           @NotNull(message = "Input must not be NULL")
                                           @Valid final OrderDto orderDto) {
        log.info("OrderDto, resource; update order");
        return ResponseEntity.ok(this.orderService.update(orderDto));
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<OrderDto> update(@PathVariable("orderId")
                                           @NotBlank(message = "Input must not be blank")
                                           @Valid final String orderId,
                                           @RequestBody
                                           @NotNull(message = "Input must not be NULL")
                                           @Valid final OrderDto orderDto) {
        log.info("OrderDto, resource; update order with orderId");
        return ResponseEntity.ok(this.orderService.update(Integer.parseInt(orderId), orderDto));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Boolean> deleteById(@PathVariable("orderId") final String orderId) {
        log.info("Boolean, resource; delete order by id");
        this.orderService.deleteById(Integer.parseInt(orderId));
        return ResponseEntity.ok(true);
    }

}
