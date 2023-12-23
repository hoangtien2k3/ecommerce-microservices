package com.hoangtien2k3.productservice.service.impl;

import com.hoangtien2k3.productservice.dto.CategoryDto;
import com.hoangtien2k3.productservice.exception.wrapper.CategoryNotFoundException;
import com.hoangtien2k3.productservice.helper.CategoryMappingHelper;
import com.hoangtien2k3.productservice.repository.CategoryRepository;
import com.hoangtien2k3.productservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;

@Transactional
@Slf4j
@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private final CategoryRepository categoryRepository;

    @Override
    public Flux<List<CategoryDto>> findAll() {
        log.info("Category List Service, fetch all category");
        return Flux.just(categoryRepository.findAll())
                .flatMap(categories ->
                        Flux.fromIterable(categories)
                                .map(CategoryMappingHelper::map)
                                .distinct()
                                .collectList()
                )
                .map(categoryDtos -> {
                    log.info("Categories fetched successfully");
                    return categoryDtos;
                })
                .onErrorResume(throwable -> {
                    log.error("Error while fetching categories: " + throwable.getMessage());
                    return Mono.just(Collections.emptyList());
                });
    }


    @Override
    public CategoryDto findById(Integer categoryId) {
        log.info("CategoryDto Service, fetch category by id");
        return categoryRepository.findById(categoryId)
                .map(CategoryMappingHelper::map)
                .orElseThrow(() -> new CategoryNotFoundException(String.format("Category with id[%d] not found", categoryId)));
    }

    @Override
    public Mono<CategoryDto> save(CategoryDto categoryDto) {
        log.info("CategoryDto, service; save category");
        return Mono.just(categoryDto)
                .map(CategoryMappingHelper::map)
                .flatMap(category ->
                        Mono.fromCallable(() -> CategoryMappingHelper.map(categoryRepository.save(category)))
                                .onErrorMap(DataIntegrityViolationException.class, e -> new CategoryNotFoundException("Bad Request", e))
                );
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto) {
        log.info("CategoryDto Service, update category");
        return CategoryMappingHelper
                .map(categoryRepository.save(CategoryMappingHelper.map(categoryDto)));
    }

    @Override
    public CategoryDto update(Integer categoryId, CategoryDto categoryDto) {
        log.info("CategoryDto Service, update category with categoryId");
        return CategoryMappingHelper
                .map(categoryRepository.save(CategoryMappingHelper.map(this.findById(categoryId))));
    }

    @Override
    public void deleteById(Integer categoryId) {
        log.info("Void Service, delete category by id");
        categoryRepository.deleteById(categoryId);
    }
}
