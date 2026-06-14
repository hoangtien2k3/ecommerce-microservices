package com.ecommerce.productservice.controller;

import com.ecommerce.productservice.dto.CategoryDto;
import com.ecommerce.productservice.service.CategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> findAll() {
        log.info("CategoryDto List, controller; fetch all categories");
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("/paging")
    public ResponseEntity<Page<CategoryDto>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<>(categoryService.findAllCategory(page, size), HttpStatus.OK);
    }

    @GetMapping("/paging-and-sorting")
    public ResponseEntity<List<CategoryDto>> getAllEmployees(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "categoryId") String sortBy) {
        return new ResponseEntity<>(categoryService.getAllCategories(pageNo, pageSize, sortBy), new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> findById(@PathVariable("categoryId")
                                                @NotBlank(message = "Input must not be blank")
                                                @Valid final String categoryId) {
        log.info("CategoryDto, resource; fetch category by id");
        return ResponseEntity.ok(categoryService.findById(Integer.parseInt(categoryId)));
    }

    @PostMapping
    public ResponseEntity<CategoryDto> save(@RequestBody
                                            @NotNull(message = "Input must not be NULL")
                                            @Valid final CategoryDto categoryDto) {
        log.info("CategoryDto, resource; save category");
        return ResponseEntity.ok(categoryService.save(categoryDto));
    }

    @PutMapping
    public ResponseEntity<CategoryDto> update(@RequestBody
                                              @NotNull(message = "Input must not be NULL")
                                              @Valid final CategoryDto categoryDto) {
        log.info("CategoryDto, resource; update category");
        return ResponseEntity.ok(categoryService.update(categoryDto));
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> update(@PathVariable("categoryId")
                                              @NotBlank(message = "Input must not be blank")
                                              @Valid final String categoryId,
                                              @RequestBody
                                              @NotNull(message = "Input must not be NULL")
                                              @Valid final CategoryDto categoryDto) {
        log.info("CategoryDto, resource; update category with categoryId");
        return ResponseEntity.ok(categoryService.update(Integer.parseInt(categoryId), categoryDto));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Boolean> deleteById(@PathVariable("categoryId") final String categoryId) {
        log.info("Boolean, resource; delete category by id");
        categoryService.deleteById(Integer.parseInt(categoryId));
        return ResponseEntity.ok(true);
    }
}
