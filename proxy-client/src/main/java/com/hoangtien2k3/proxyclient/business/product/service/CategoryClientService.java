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

import com.hoangtien2k3.proxyclient.business.product.model.CategoryDto;
import com.hoangtien2k3.proxyclient.business.product.model.response.CategoryProductServiceCollectionDtoResponse;

@FeignClient(name = "PRODUCT-SERVICE",
        contextId = "categoryClientService",
        path = "/product-service/api/categories"
)
@Service
public interface CategoryClientService {

    @GetMapping
    ResponseEntity<CategoryProductServiceCollectionDtoResponse> findAll();

    @GetMapping("/{categoryId}")
    ResponseEntity<CategoryDto> findById(@PathVariable("categoryId")
                                         @NotBlank(message = "Input must not be blank!")
                                         @Valid final String categoryId);

    @PostMapping
    ResponseEntity<CategoryDto> save(@RequestBody
                                     @NotNull(message = "Input must not be NULL!")
                                     @Valid final CategoryDto categoryDto);

    @PutMapping
    ResponseEntity<CategoryDto> update(@RequestBody
                                       @NotNull(message = "Input must not be NULL!")
                                       @Valid final CategoryDto categoryDto);

    @PutMapping("/{categoryId}")
    ResponseEntity<CategoryDto> update(@PathVariable("categoryId")
                                       @NotBlank(message = "Input must not be blank!")
                                       @Valid final String categoryId,
                                       @RequestBody
                                       @NotNull(message = "Input must not be NULL!")
                                       @Valid final CategoryDto categoryDto);

    @DeleteMapping("/{categoryId}")
    ResponseEntity<Boolean> deleteById(@PathVariable("categoryId") final String categoryId);

}