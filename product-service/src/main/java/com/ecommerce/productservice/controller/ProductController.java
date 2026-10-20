package com.ecommerce.productservice.controller;

import com.ecommerce.commonlib.viewmodel.ApiResponse;
import com.ecommerce.productservice.dto.ProductDto;
import com.ecommerce.productservice.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductDto>> findAll() {
        log.info("ProductDto List, controller; fetch all products");
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductDto>> findById(@PathVariable("productId") final Integer productId) {
        log.info("ProductDto, resource; fetch product by id");
        return ResponseEntity.ok(ApiResponse.ok(productService.findById(productId)));
    }

    @PostMapping
    public ResponseEntity<ProductDto> save(@RequestBody
                                           @NotNull(message = "Input must not be NULL!")
                                           @Valid final ProductDto productDto) {
        log.info("ProductDto, resource; save product");
        return ResponseEntity.ok(productService.save(productDto));
    }

    @PutMapping
    public ResponseEntity<ProductDto> update(@RequestBody
                                             @NotNull(message = "Input must not be NULL!")
                                             @Valid final ProductDto productDto) {
        log.info("ProductDto, resource; update product");
        return ResponseEntity.ok(productService.update(productDto));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto> update(@PathVariable("productId") final Integer productId,
                                             @RequestBody
                                             @NotNull(message = "Input must not be NULL!")
                                             @Valid final ProductDto productDto) {
        log.info("ProductDto, resource; update product with productId");
        return ResponseEntity.ok(productService.update(productId, productDto));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Boolean> deleteById(@PathVariable("productId") final Integer productId) {
        log.info("Boolean, resource; delete product by id");
        productService.deleteById(productId);
        return ResponseEntity.ok(true);
    }
}
