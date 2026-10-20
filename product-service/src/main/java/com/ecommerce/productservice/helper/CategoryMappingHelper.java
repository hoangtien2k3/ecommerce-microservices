package com.ecommerce.productservice.helper;

import com.ecommerce.productservice.entity.Category;
import com.ecommerce.productservice.dto.CategoryDto;


public interface CategoryMappingHelper {

    static CategoryDto map(final Category category) {
        final var parentCategory = category.getParentCategory();
        return CategoryDto.builder()
                .categoryId(category.getCategoryId())
                .categoryTitle(category.getCategoryTitle())
                .imageUrl(category.getImageUrl())
                .parentCategoryDto(parentCategory == null ? null : CategoryDto.builder()
                        .categoryId(parentCategory.getCategoryId())
                        .categoryTitle(parentCategory.getCategoryTitle())
                        .imageUrl(parentCategory.getImageUrl())
                        .build())
                .build();
    }

    // A top-level category has no parent; linking an empty parent makes Hibernate reject the save.
    static Category map(CategoryDto categoryDto) {
        final var parentCategoryDto = categoryDto.getParentCategoryDto();
        final boolean hasParent = parentCategoryDto != null && parentCategoryDto.getCategoryId() != null;
        return Category.builder()
                .categoryId(categoryDto.getCategoryId())
                .categoryTitle(categoryDto.getCategoryTitle())
                .imageUrl(categoryDto.getImageUrl())
                .parentCategory(hasParent ? Category.builder()
                        .categoryId(parentCategoryDto.getCategoryId())
                        .categoryTitle(parentCategoryDto.getCategoryTitle())
                        .imageUrl(parentCategoryDto.getImageUrl())
                        .build() : null)
                .build();
    }

}
