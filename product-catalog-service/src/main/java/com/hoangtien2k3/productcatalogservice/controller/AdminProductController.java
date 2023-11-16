package com.hoangtien2k3.productcatalogservice.controller;

import com.hoangtien2k3.productcatalogservice.dto.ProductRequest;
import com.hoangtien2k3.productcatalogservice.entity.Product;
import com.hoangtien2k3.productcatalogservice.http.HeaderGenerator;
import com.hoangtien2k3.productcatalogservice.security.JwtValidate;
import com.hoangtien2k3.productcatalogservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminProductController {

    private final ProductService productService;
    private final HeaderGenerator headerGenerator;
    private final JwtValidate jwtValidate;

    @PostMapping("/products")
    public ResponseEntity<Product> addProduct(@RequestHeader(name = "Authorization") String authorizationHeader,
                                               @RequestBody ProductRequest productRequest) {

        if (!jwtValidate.validateTokenUserService(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (productRequest == null) {
            return ResponseEntity.badRequest().headers(headerGenerator.getHeadersForError()).build();
        }

        try {
            log.info("Product insert successfully in product-catalog.");
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

    @DeleteMapping(value = "/products/{id}")
    public ResponseEntity<Void> deleteProduct(@RequestHeader(name = "Authorization") String authorizationHeader,
                                               @PathVariable("id") Long id) {

        if (!jwtValidate.validateTokenUserService(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Product product = productService.getProductById(id);
        if (product == null) {
            return new ResponseEntity<>(headerGenerator.getHeadersForError(), HttpStatus.NOT_FOUND);
        }

        try {
            productService.deleteProduct(id);
            return new ResponseEntity<>(
                    headerGenerator.getHeadersForSuccessGetMethod(),
                    HttpStatus.OK);
        } catch (Exception e) {
            log.error("INTERNAL_SERVER_ERROR" + e);
            return new ResponseEntity<>(
                    headerGenerator.getHeadersForError(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}