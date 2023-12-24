package com.hoangtien2k3.productservice.api;

import com.hoangtien2k3.productservice.dto.CategoryDto;
import com.hoangtien2k3.productservice.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private final CategoryService categoryService;

    // Get a list of all categories
    @GetMapping
    public ResponseEntity<Flux<List<CategoryDto>>> findAll() {
        log.info("CategoryDto List, controller; fetch all categories");
        return ResponseEntity.ok(categoryService.findAll());
    }

    // Get all list categories with paging
    @GetMapping("/paging")
    public ResponseEntity<Page<CategoryDto>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<CategoryDto> categoryPage = categoryService.findAllCategory(page, size);
        return new ResponseEntity<>(categoryPage, HttpStatus.OK);
    }

    @GetMapping("/paging-and-sorting")
    public ResponseEntity<List<CategoryDto>> getAllEmployees(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "categoryId") String sortBy) {

        List<CategoryDto> list = categoryService.getAllCategories(pageNo, pageSize, sortBy);

        return new ResponseEntity<List<CategoryDto>>(list, new HttpHeaders(), HttpStatus.OK);
    }

    // Get detailed information of a specific category:
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> findById(@PathVariable("categoryId")
                                                @NotBlank(message = "Input must not be blank")
                                                @Valid final String categoryId) {
        log.info("CategoryDto, resource; fetch category by id");
        return ResponseEntity.ok(categoryService.findById(Integer.parseInt(categoryId)));
    }

    //     Create a new category
    @PostMapping
    public ResponseEntity<Mono<CategoryDto>> save(@RequestBody @NotNull(message = "Input must not be NULL")
                                                  @Valid final CategoryDto categoryDto) {
        log.info("CategoryDto, resource; save category");
        return ResponseEntity.ok(categoryService.save(categoryDto));
    }

    // Update information of all category
    @PutMapping
    public ResponseEntity<CategoryDto> update(@RequestBody
                                              @NotNull(message = "Input must not be NULL")
                                              @Valid final CategoryDto categoryDto) {
        log.info("CategoryDto, resource; update category");
        return ResponseEntity.ok(categoryService.update(categoryDto));
    }

    // Update information of a category
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> update(@PathVariable("categoryId")
                                              @NotBlank(message = "Input must not be blank")
                                              @Valid final String categoryId,
                                              @RequestBody @NotNull(message = "Input must not be NULL")
                                              @Valid final CategoryDto categoryDto) {
        log.info("CategoryDto, resource; update category with categoryId");
        return ResponseEntity.ok(categoryService.update(Integer.parseInt(categoryId), categoryDto));
    }

    // Delete a category
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Boolean> deleteById(@PathVariable("categoryId") final String categoryId) {
        log.info("Boolean, resource; delete category by id");
        categoryService.deleteById(Integer.parseInt(categoryId));
        return ResponseEntity.ok(true);
    }

}
