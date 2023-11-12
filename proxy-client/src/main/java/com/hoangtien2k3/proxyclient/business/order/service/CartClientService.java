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

import com.hoangtien2k3.proxyclient.business.order.model.CartDto;
import com.hoangtien2k3.proxyclient.business.order.model.response.CartOrderServiceDtoCollectionResponse;

@FeignClient(name = "ORDER-SERVICE",
        contextId = "cartClientService",
        path = "/order-service/api/carts"
)
@Service
public interface CartClientService {

    @GetMapping
    public ResponseEntity<CartOrderServiceDtoCollectionResponse> findAll();

    @GetMapping("/{cartId}")
    public ResponseEntity<CartDto> findById(@PathVariable("cartId")
                                            @NotBlank(message = "Input must not be blank!")
                                            @Valid final String cartId);

    @PostMapping
    public ResponseEntity<CartDto> save(@RequestBody
                                        @NotNull(message = "Input must not be NULL!")
                                        @Valid final CartDto cartDto);

    @PutMapping
    public ResponseEntity<CartDto> update(@RequestBody
                                          @NotNull(message = "Input must not be NULL!")
                                          @Valid final CartDto cartDto);

    @PutMapping("/{cartId}")
    public ResponseEntity<CartDto> update(@PathVariable("cartId")
                                          @NotBlank(message = "Input must not be blank!")
                                          @Valid final String cartId,
                                          @RequestBody
                                          @NotNull(message = "Input must not be NULL!")
                                          @Valid final CartDto cartDto);

    @DeleteMapping("/{cartId}")
    public ResponseEntity<Boolean> deleteById(@PathVariable("cartId") final String cartId);

}
