package com.ecommerce.productservice.service.impl;

import com.ecommerce.productservice.dto.CategoryDto;
import com.ecommerce.productservice.entity.Category;
import com.ecommerce.productservice.exception.wrapper.CategoryNotFoundException;
import com.ecommerce.productservice.helper.CategoryMappingHelper;
import com.ecommerce.productservice.repository.CategoryRepository;
import com.ecommerce.productservice.repository.CategoryRepositoryPagingAndSorting;
import com.ecommerce.productservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);

    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;
    private final CategoryRepositoryPagingAndSorting categoryRepositoryPagingAndSorting;

    @Override
    public List<CategoryDto> findAll() {
        log.info("Category List Service, fetch all categories");
        return categoryRepository.findAll()
                .stream()
                .map(CategoryMappingHelper::map)
                .distinct()
                .toList();
    }

    @Override
    public Page<CategoryDto> findAllCategory(int page, int size) {
        log.info("CategoryDto List, service; fetch all categories with paging");
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        List<CategoryDto> categoryDtos = categoryPage.getContent()
                .stream()
                .map(CategoryMappingHelper::map)
                .distinct()
                .collect(Collectors.toList());
        return new PageImpl<>(categoryDtos, pageable, categoryPage.getTotalElements());
    }

    @Override
    public List<CategoryDto> getAllCategories(Integer pageNo, Integer pageSize, String sortBy) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        Page<Category> pagedResult = categoryRepositoryPagingAndSorting.findAllPagedAndSortedCategories(paging);
        if (pagedResult.hasContent()) {
            return pagedResult.getContent()
                    .stream()
                    .map(element -> modelMapper.map(element, CategoryDto.class))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
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
        try {
            return CategoryMappingHelper.map(categoryRepository.save(CategoryMappingHelper.map(categoryDto)));
        } catch (DataIntegrityViolationException e) {
            throw new CategoryNotFoundException("Bad Request", e);
        }
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto) {
        log.info("CategoryDto Service, update category");
        try {
            Category existingCategory = categoryRepository.findById(categoryDto.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + categoryDto.getCategoryId()));
            BeanUtils.copyProperties(categoryDto, existingCategory, "categoryId", "parentCategoryDto");
            if (categoryDto.getParentCategoryDto() != null) {
                existingCategory.setParentCategory(CategoryMappingHelper.map(categoryDto.getParentCategoryDto()));
            }
            return CategoryMappingHelper.map(categoryRepository.save(existingCategory));
        } catch (CategoryNotFoundException e) {
            log.error("Error updating category. Category with id [{}] not found.", categoryDto.getCategoryId());
            throw e;
        } catch (DataIntegrityViolationException e) {
            log.error("Error updating category: Data integrity violation", e);
            throw new CategoryNotFoundException("Error updating category: Data integrity violation", e);
        } catch (Exception e) {
            log.error("Error updating category", e);
            throw new CategoryNotFoundException("Error updating category", e);
        }
    }

    @Override
    public CategoryDto update(Integer categoryId, CategoryDto categoryDto) {
        log.info("CategoryDto Service: Updating category with categoryId");
        try {
            CategoryDto existingCategoryDto = findById(categoryId);
            Category existingCategory = CategoryMappingHelper.map(existingCategoryDto);
            BeanUtils.copyProperties(categoryDto, existingCategory, "categoryId", "parentCategoryDto");
            if (categoryDto.getParentCategoryDto() != null) {
                existingCategory.setParentCategory(CategoryMappingHelper.map(categoryDto.getParentCategoryDto()));
            }
            return CategoryMappingHelper.map(categoryRepository.save(existingCategory));
        } catch (CategoryNotFoundException e) {
            log.error("Error updating category. Category with id [{}] not found.", categoryId);
            throw e;
        } catch (DataIntegrityViolationException e) {
            log.error("Error updating category: Data integrity violation", e);
            throw new CategoryNotFoundException("Error updating category: Data integrity violation", e);
        } catch (Exception e) {
            log.error("Error updating category", e);
            throw new CategoryNotFoundException("Error updating category", e);
        }
    }

    @Override
    public void deleteById(Integer categoryId) {
        log.info("Void Service, delete category by id");
        try {
            categoryRepository.deleteById(categoryId);
        } catch (Exception e) {
            log.error("Error deleting category", e);
            throw new CategoryNotFoundException("Error deleting category", e);
        }
    }
}
