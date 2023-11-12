package com.hoangtien2k3.proxyclient.business.product.service;

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

import com.hoangtien2k3.proxyclient.business.product.model.ProductDto;
import com.hoangtien2k3.proxyclient.business.product.model.response.ProductProductServiceCollectionDtoResponse;

@FeignClient(name = "PRODUCT-SERVICE",
        contextId = "productClientService",
        path = "/product-service/api/products"
)
@Service
public interface ProductClientService {

    @GetMapping
    ResponseEntity<ProductProductServiceCollectionDtoResponse> findAll();

    @GetMapping("/{productId}")
    ResponseEntity<ProductDto> findById(@PathVariable("productId")
                                        @NotBlank(message = "Input must not be blank!")
                                        @Valid final String productId);

    @PostMapping
    ResponseEntity<ProductDto> save(@RequestBody
                                    @NotNull(message = "Input must not be NULL!")
                                    @Valid final ProductDto productDto);

    @PutMapping
    ResponseEntity<ProductDto> update(@RequestBody
                                      @NotNull(message = "Input must not be NULL!")
                                      @Valid final ProductDto productDto);

    @PutMapping("/{productId}")
    ResponseEntity<ProductDto> update(@PathVariable("productId")
                                      @NotBlank(message = "Input must not be blank!")
                                      @Valid final String productId,
                                      @RequestBody
                                      @NotNull(message = "Input must not be NULL!")
                                      @Valid final ProductDto productDto);

    @DeleteMapping("/{productId}")
    ResponseEntity<Boolean> deleteById(@PathVariable("productId") final String productId);

}
