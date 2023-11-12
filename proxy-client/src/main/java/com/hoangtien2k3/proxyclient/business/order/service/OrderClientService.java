package com.hoangtien2k3.proxyclient.business.order.service;

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

import com.hoangtien2k3.proxyclient.business.order.model.OrderDto;
import com.hoangtien2k3.proxyclient.business.order.model.response.OrderOrderServiceDtoCollectionResponse;

@FeignClient(name = "ORDER-SERVICE",
        contextId = "orderClientService",
        path = "/order-service/api/orders"
)
@Service
public interface OrderClientService {

    @GetMapping
    public ResponseEntity<OrderOrderServiceDtoCollectionResponse> findAll();

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> findById(@PathVariable("orderId")
                                             @NotBlank(message = "Input must not be blank!")
                                             @Valid final String orderId);

    @PostMapping
    public ResponseEntity<OrderDto> save(@RequestBody
                                         @NotNull(message = "Input must not be NULL!")
                                         @Valid final OrderDto orderDto);

    @PutMapping
    public ResponseEntity<OrderDto> update(@RequestBody
                                           @NotNull(message = "Input must not be NULL!")
                                           @Valid final OrderDto orderDto);

    @PutMapping("/{orderId}")
    public ResponseEntity<OrderDto> update(@PathVariable("orderId")
                                           @NotBlank(message = "Input must not be blank!")
                                           @Valid final String orderId,
                                           @RequestBody
                                           @NotNull(message = "Input must not be NULL!")
                                           @Valid final OrderDto orderDto);

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Boolean> deleteById(@PathVariable("orderId") final String orderId);

}