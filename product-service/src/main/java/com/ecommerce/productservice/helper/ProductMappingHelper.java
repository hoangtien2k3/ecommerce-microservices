package com.ecommerce.productservice.helper;

import com.ecommerce.productservice.entity.Category;
import com.ecommerce.productservice.entity.Product;
import com.ecommerce.productservice.dto.CategoryDto;
import com.ecommerce.productservice.dto.ProductDto;

public interface ProductMappingHelper {
    static ProductDto map(final Product product) {
        return ProductDto.builder()
                .productId(product.getProductId())
                .productTitle(product.getProductTitle())
                .imageUrl(product.getImageUrl())
                .sku(product.getSku())
                .priceUnit(product.getPriceUnit())
                .quantity(product.getQuantity())
                .categoryDto(mapCategory(product.getCategory()))
                .build();
    }

    static Product map(final ProductDto productDto) {
        return Product.builder()
                .productId(productDto.getProductId())
                .productTitle(productDto.getProductTitle())
                .imageUrl(productDto.getImageUrl())
                .sku(productDto.getSku())
                .priceUnit(productDto.getPriceUnit())
                .quantity(productDto.getQuantity())
                .category(mapCategory(productDto.getCategoryDto()))
                .build();
    }

    // products.category_id is nullable, so a product may have no category.
    private static CategoryDto mapCategory(final Category category) {
        if (category == null) return null;
        return CategoryDto.builder()
                .categoryId(category.getCategoryId())
                .categoryTitle(category.getCategoryTitle())
                .imageUrl(category.getImageUrl())
                .build();
    }

    private static Category mapCategory(final CategoryDto categoryDto) {
        if (categoryDto == null || categoryDto.getCategoryId() == null) return null;
        return Category.builder()
                .categoryId(categoryDto.getCategoryId())
                .categoryTitle(categoryDto.getCategoryTitle())
                .imageUrl(categoryDto.getImageUrl())
                .build();
    }

}
