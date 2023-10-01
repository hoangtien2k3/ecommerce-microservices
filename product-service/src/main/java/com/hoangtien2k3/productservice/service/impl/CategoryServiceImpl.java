package com.hoangtien2k3.productservice.service.impl;

import com.hoangtien2k3.productservice.dto.CategoryDto;
import com.hoangtien2k3.productservice.exception.wrapper.CategoryNotFoundException;
import com.hoangtien2k3.productservice.helper.CategoryMappingHelper;
import com.hoangtien2k3.productservice.repository.CategoryRepository;
import com.hoangtien2k3.productservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Slf4j
@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryDto> findAll() {
        log.info("Category List Service, fetch all category");
        return categoryRepository.findAll()
                .stream()
                .map(CategoryMappingHelper::map)
                .distinct() // loại bỏ phần tử trùng lặp (phần tử duy nhất)
                .toList();
    }

    @Override
    public CategoryDto findById(Integer categoryId) {
        log.info("CategoryDto Service, fetch category by id");
        return categoryRepository.findById(categoryId)
                .map(CategoryMappingHelper::map)
                .orElseThrow(() -> new CategoryNotFoundException(String.format("Category with id[%d] not found", categoryId)));
    }

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        log.info("CategoryDto, service; save category");
        return CategoryMappingHelper
                .map(categoryRepository.save(CategoryMappingHelper.map(categoryDto)));
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
