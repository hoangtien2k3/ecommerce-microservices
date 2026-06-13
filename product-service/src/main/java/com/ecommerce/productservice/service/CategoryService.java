package com.ecommerce.productservice.service;

import com.ecommerce.productservice.dto.CategoryDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CategoryService {

    List<CategoryDto> findAll();

    Page<CategoryDto> findAllCategory(int page, int size);

    List<CategoryDto> getAllCategories(Integer pageNo, Integer pageSize, String sortBy);

    CategoryDto findById(final Integer categoryId);

    CategoryDto save(final CategoryDto categoryDto);

    CategoryDto update(final CategoryDto categoryDto);

    CategoryDto update(final Integer categoryId, final CategoryDto categoryDto);

    void deleteById(final Integer categoryId);
}
