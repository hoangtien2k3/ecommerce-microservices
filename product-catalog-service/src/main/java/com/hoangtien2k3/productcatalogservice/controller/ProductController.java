package com.hoangtien2k3.productcatalogservice.controller;

import com.hoangtien2k3.productcatalogservice.dto.PageResult;
import com.hoangtien2k3.productcatalogservice.entity.Product;
import com.hoangtien2k3.productcatalogservice.http.HeaderGenerator;
import com.hoangtien2k3.productcatalogservice.service.ProductService;
import com.hoangtien2k3.productcatalogservice.service.impl.ProductServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;
    private final HeaderGenerator headerGenerator;

    @GetMapping(value = "/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProduct();
        if (!products.isEmpty()) {
            return new ResponseEntity<>(
                    products,
                    headerGenerator.getHeadersForSuccessGetMethod(),
                    HttpStatus.OK);
        }
        return new ResponseEntity<>(
                headerGenerator.getHeadersForError(),
                HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = {"/products", "/product"}, params = "category")
    public ResponseEntity<List<Product>> getAllProductByCategory(@RequestParam("category") String category) {
        List<Product> products = productService.getAllProductByCategory(category);
        if (!products.isEmpty()) {
            return new ResponseEntity<List<Product>>(
                    products,
                    headerGenerator.getHeadersForSuccessGetMethod(),
                    HttpStatus.OK);
        }
        return new ResponseEntity<List<Product>>(
                headerGenerator.getHeadersForError(),
                HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/products/{id}")
    public ResponseEntity<Product> getOneProductById(@PathVariable("id") long id) {
        Product product = productService.getProductById(id);
        if (product != null) {
            return new ResponseEntity<>(
                    product,
                    headerGenerator.getHeadersForSuccessGetMethod(),
                    HttpStatus.OK);
        }
        return new ResponseEntity<>(
                headerGenerator.getHeadersForError(),
                HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/products", params = "name")
    public ResponseEntity<List<Product>> getAllProductsByName(@RequestParam("name") String name) {
        List<Product> products = productService.getAllProductsByName(name);
        if (!products.isEmpty()) {
            return new ResponseEntity<>(
                    products,
                    headerGenerator.getHeadersForSuccessGetMethod(),
                    HttpStatus.OK);
        }
        return new ResponseEntity<>(
                headerGenerator.getHeadersForError(),
                HttpStatus.NOT_FOUND);
    }


    @GetMapping
    public ResponseEntity<Page<Product>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Product> products = productService.getProducts(page, size);
        return ResponseEntity.ok(products);
    }


    // thủ công
    @GetMapping("/page")
    public ResponseEntity<PageResult> getAllProductsPage(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size) {

        PageResult pageResult = productService.getAllProductsPage(page, size);
        return ResponseEntity.ok(pageResult);
    }


    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProductsByKeyword(@RequestParam String keyword) {

        List<Product> products = productService.searchProductsByKeyword(keyword);

        return ResponseEntity.ok(products);
    }

    @GetMapping("/search-and-page")
    public ResponseEntity<Page<Product>> searchProducts(@RequestParam(value = "keyword", required = false) String keyword,
                                                        @RequestParam(value = "page", defaultValue = "0") int page,
                                                        @RequestParam(value = "size", defaultValue = "10") int size) {
        if (keyword == null) {
            // keyword is null
            return ResponseEntity.notFound().build();
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.searchProducts(keyword, pageable);
        return ResponseEntity.ok(products);

    }


//    @GetMapping("/search-and-page")
//    public ResponseEntity<Page<Product>> searchProducts(@RequestParam(value = "keyword", required = false) String keyword,
//                                                        @PageableDefault(size = 10, sort = "price", direction = Sort.Direction.ASC) Pageable pageable) {
//
//        if (keyword == null) {
//            // keyword is null
//            return ResponseEntity.notFound().build();
//        }
//
//        Page<Product> products = productService.searchProducts(keyword, pageable);
//        return ResponseEntity.ok(products);
//    }

}


