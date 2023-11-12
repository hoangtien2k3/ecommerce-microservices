package com.hoangtien2k3.proxyclient.business.orderItem.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hoangtien2k3.proxyclient.business.orderItem.model.OrderItemDto;
import com.hoangtien2k3.proxyclient.business.orderItem.model.OrderItemId;
import com.hoangtien2k3.proxyclient.business.orderItem.model.response.OrderItemOrderItemServiceDtoCollectionResponse;
import com.hoangtien2k3.proxyclient.business.orderItem.service.OrderItemClientService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/shippings")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemClientService orderItemClientService;

    @GetMapping
    public ResponseEntity<OrderItemOrderItemServiceDtoCollectionResponse> findAll() {
        return ResponseEntity.ok(this.orderItemClientService.findAll().getBody());
    }

    @GetMapping("/{orderId}/{productId}")
    public ResponseEntity<OrderItemDto> findById(@PathVariable("orderId") final String orderId,
                                                 @PathVariable("productId") final String productId) {
        return ResponseEntity.ok(this.orderItemClientService.findById(new OrderItemId(Integer.parseInt(productId),
                Integer.parseInt(orderId))).getBody());
    }

    @GetMapping("/find")
    public ResponseEntity<OrderItemDto> findById(@RequestBody final OrderItemId orderItemId) {
        return ResponseEntity.ok(this.orderItemClientService.findById(orderItemId).getBody());
    }

    @PostMapping
    public ResponseEntity<OrderItemDto> save(@RequestBody final OrderItemDto orderItemDto) {
        return ResponseEntity.ok(this.orderItemClientService.save(orderItemDto).getBody());
    }

    @PutMapping
    public ResponseEntity<OrderItemDto> update(@RequestBody final OrderItemDto orderItemDto) {
        return ResponseEntity.ok(this.orderItemClientService.update(orderItemDto).getBody());
    }

    @DeleteMapping("/{orderId}/{productId}")
    public ResponseEntity<Boolean> deleteById(@PathVariable("orderId") final String orderId,
                                              @PathVariable("productId") final String productId) {
        this.orderItemClientService.deleteById(new OrderItemId(Integer.parseInt(orderId),
                Integer.parseInt(productId))).getBody();
        return ResponseEntity.ok(true);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> deleteById(@RequestBody final OrderItemId orderItemId) {
        this.orderItemClientService.deleteById(orderItemId).getBody();
        return ResponseEntity.ok(true);
    }

}
