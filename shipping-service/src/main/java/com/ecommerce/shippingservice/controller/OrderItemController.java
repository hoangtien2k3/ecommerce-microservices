package com.ecommerce.shippingservice.controller;

import com.ecommerce.shippingservice.domain.id.OrderItemId;
import com.ecommerce.shippingservice.dto.OrderItemDto;
import com.ecommerce.shippingservice.dto.response.collection.DtoCollectionResponse;
import com.ecommerce.shippingservice.service.OrderItemService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/shippings")
public class OrderItemController {

    private final OrderItemService orderItemService;

    @GetMapping
    public ResponseEntity<DtoCollectionResponse<OrderItemDto>> findAll() {
        log.info("OrderItemDto List, controller; fetch all orderItems");
        return ResponseEntity.ok(new DtoCollectionResponse<>(this.orderItemService.findAll()));
    }

    @GetMapping("/{orderId}/{productId}")
    public ResponseEntity<OrderItemDto> findById(@PathVariable("orderId") final String orderId,
                                                 @PathVariable("productId") final String productId) {
        log.info("OrderItemDto, resource; fetch orderItem by id");
        return ResponseEntity.ok(this.orderItemService.findById(
                new OrderItemId(Integer.parseInt(orderId), Integer.parseInt(productId))));
    }

    @GetMapping("/find")
    public ResponseEntity<OrderItemDto> findById(@RequestBody
                                                 @NotNull(message = "Input must not be NULL")
                                                 @Valid final OrderItemId orderItemId) {
        log.info("OrderItemDto, resource; fetch orderItem by id");
        return ResponseEntity.ok(this.orderItemService.findById(orderItemId));
    }

    @PostMapping
    public ResponseEntity<OrderItemDto> save(@RequestBody @NotNull(message = "Input must not be NULL")
                                             @Valid final OrderItemDto orderItemDto) {
        log.info("OrderItemDto, resource; save orderItem");
        return ResponseEntity.ok(this.orderItemService.save(orderItemDto));
    }

    @PutMapping
    public ResponseEntity<OrderItemDto> update(@RequestBody
                                               @NotNull(message = "Input must not be NULL")
                                               @Valid final OrderItemDto orderItemDto) {
        log.info("OrderItemDto, resource; update orderItem");
        return ResponseEntity.ok(this.orderItemService.update(orderItemDto));
    }

    @DeleteMapping("/{orderId}/{productId}")
    public ResponseEntity<Boolean> deleteById(@PathVariable("orderId") final String orderId,
                                              @PathVariable("productId") final String productId) {
        log.info("Boolean, resource; delete orderItem by id");
        this.orderItemService.deleteById(new OrderItemId(Integer.parseInt(orderId), Integer.parseInt(productId)));
        return ResponseEntity.ok(true);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> deleteById(@RequestBody
                                              @NotNull(message = "Input must not be NULL")
                                              @Valid final OrderItemId orderItemId) {
        log.info("Boolean, resource; delete orderItem by id");
        this.orderItemService.deleteById(orderItemId);
        return ResponseEntity.ok(true);
    }

}
