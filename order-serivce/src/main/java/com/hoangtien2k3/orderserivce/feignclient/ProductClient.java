package com.hoangtien2k3.orderserivce.feignclient;

import com.hoangtien2k3.orderserivce.entity.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-catalog-service", url = "http://localhost:8085/")
public interface ProductClient {

    @GetMapping(value = "/products/{id}")
    Product getProductById(@PathVariable(value = "id") Long productId);

}
