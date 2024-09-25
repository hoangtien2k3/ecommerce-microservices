package com.hoangtien2k3.orderservice.service;

import com.hoangtien2k3.orderservice.dto.product.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "productClient", url = "http://localhost:8086")
public interface ProductClient {

    @GetMapping("/api/products/{productId}")
    ProductDto getProductById(@PathVariable("productId") Integer productId);
}