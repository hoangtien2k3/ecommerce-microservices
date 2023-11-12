package com.hoangtien2k3.proxyclient.business.product.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hoangtien2k3.proxyclient.business.product.model.ProductDto;
import com.hoangtien2k3.proxyclient.business.product.model.response.ProductProductServiceCollectionDtoResponse;
import com.hoangtien2k3.proxyclient.business.product.service.ProductClientService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductClientService productClientService;

    @GetMapping
    public ResponseEntity<ProductProductServiceCollectionDtoResponse> findAll() {
        return ResponseEntity.ok(this.productClientService.findAll().getBody());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> findById(@PathVariable("productId") final String productId) {
        return ResponseEntity.ok(this.productClientService.findById(productId).getBody());
    }

    @PostMapping
    public ResponseEntity<ProductDto> save(@RequestBody final ProductDto productDto) {
        return ResponseEntity.ok(this.productClientService.save(productDto).getBody());
    }

    @PutMapping
    public ResponseEntity<ProductDto> update(@RequestBody final ProductDto productDto) {
        return ResponseEntity.ok(this.productClientService.update(productDto).getBody());
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto> update(@PathVariable("productId") final String productId,
                                             @RequestBody final ProductDto productDto) {
        return ResponseEntity.ok(this.productClientService.update(productId, productDto).getBody());
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Boolean> deleteById(@PathVariable("productId") final String productId) {
        return ResponseEntity.ok(this.productClientService.deleteById(productId).getBody());
    }

}