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

import com.hoangtien2k3.proxyclient.business.product.model.CategoryDto;
import com.hoangtien2k3.proxyclient.business.product.model.response.CategoryProductServiceCollectionDtoResponse;
import com.hoangtien2k3.proxyclient.business.product.service.CategoryClientService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryClientService categoryClientService;

    @GetMapping
    public ResponseEntity<CategoryProductServiceCollectionDtoResponse> findAll() {
        return ResponseEntity.ok(this.categoryClientService.findAll().getBody());
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> findById(@PathVariable("categoryId") final String categoryId) {
        return ResponseEntity.ok(this.categoryClientService.findById(categoryId).getBody());
    }

    @PostMapping
    public ResponseEntity<CategoryDto> save(@RequestBody final CategoryDto categoryDto) {
        return ResponseEntity.ok(this.categoryClientService.save(categoryDto).getBody());
    }

    @PutMapping
    public ResponseEntity<CategoryDto> update(@RequestBody final CategoryDto categoryDto) {
        return ResponseEntity.ok(this.categoryClientService.update(categoryDto).getBody());
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> update(@PathVariable("categoryId") final String categoryId,
                                              @RequestBody final CategoryDto categoryDto) {
        return ResponseEntity.ok(this.categoryClientService.update(categoryId, categoryDto).getBody());
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Boolean> deleteById(@PathVariable("categoryId") final String categoryId) {
        return ResponseEntity.ok(this.categoryClientService.deleteById(categoryId).getBody());
    }

}