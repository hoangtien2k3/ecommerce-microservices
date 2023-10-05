package com.hoangtien2k3.productcatalogservice.controller;

import javax.servlet.http.HttpServletRequest;
import com.hoangtien2k3.productcatalogservice.dto.ProductRequest;
import com.hoangtien2k3.productcatalogservice.entity.Product;
import com.hoangtien2k3.productcatalogservice.http.HeaderGenerator;
import com.hoangtien2k3.productcatalogservice.service.ProductService;
import com.hoangtien2k3.productcatalogservice.service.impl.ProductServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin")
public class AdminProductController {

    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private HeaderGenerator headerGenerator;

    @PostMapping("/products")
    private ResponseEntity<Product> addProduct(@RequestBody ProductRequest productRequest) {
        if (productRequest != null) {
            try {
                Product product = Product.builder()
                        .productName(productRequest.getProductName())
                        .price(productRequest.getPrice())
                        .description(productRequest.getDescription())
                        .category(productRequest.getCategory())
                        .availability(productRequest.getAvailability())
                        .build();
                productService.addProduct(product);

                return new ResponseEntity<>(
                        product,
                        headerGenerator.getHeadersForSuccessPostMethod(product.getId()),
                        HttpStatus.CREATED);
            } catch (Exception e) {
                log.error("Product is not add list {}", productRequest.getProductName());
                return new ResponseEntity<>(
                        headerGenerator.getHeadersForError(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(
                headerGenerator.getHeadersForError(),
                HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping(value = "/products/{id}")
    private ResponseEntity<Void> deleteProduct(@PathVariable("id") Long id) {
        Product product = productService.getProductById(id);
        if (product != null) {
            try {
                productService.deleteProduct(id);
                return new ResponseEntity<>(
                        headerGenerator.getHeadersForSuccessGetMethod(),
                        HttpStatus.OK);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(
                        headerGenerator.getHeadersForError(),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(headerGenerator.getHeadersForError(), HttpStatus.NOT_FOUND);
    }

}
