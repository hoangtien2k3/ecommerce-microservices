package com.ecommerce.productservice.service.impl;

import com.ecommerce.productservice.dto.ProductDto;
import com.ecommerce.productservice.entity.Product;
import com.ecommerce.productservice.exception.wrapper.ProductNotFoundException;
import com.ecommerce.productservice.helper.CategoryMappingHelper;
import com.ecommerce.productservice.helper.ProductMappingHelper;
import com.ecommerce.productservice.repository.ProductRepository;
import com.ecommerce.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;

    @Override
    public List<ProductDto> findAll() {
        log.info("ProductDto List, service; fetch all products");
        return productRepository.findAll()
                .stream()
                .map(ProductMappingHelper::map)
                .distinct()
                .toList();
    }

    @Override
    public ProductDto findById(Integer productId) {
        log.info("ProductDto, service; fetch product by id");
        return productRepository.findById(productId)
                .map(ProductMappingHelper::map)
                .orElseThrow(() -> new ProductNotFoundException(String.format("Product with id[%d] not found", productId)));
    }

    @Override
    public ProductDto save(ProductDto productDto) {
        log.info("ProductDto, service; save product");
        try {
            return ProductMappingHelper.map(productRepository.save(ProductMappingHelper.map(productDto)));
        } catch (DataIntegrityViolationException e) {
            log.error("Error saving product: Data integrity violation", e);
            throw new ProductNotFoundException("Error saving product: Data integrity violation", e);
        } catch (Exception e) {
            log.error("Error saving product", e);
            throw new ProductNotFoundException("Error saving product", e);
        }
    }

    @Override
    public ProductDto update(ProductDto productDto) {
        log.info("ProductDto, service; update product");
        Product existingProduct = productRepository.findById(productDto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productDto.getProductId()));
        BeanUtils.copyProperties(productDto, existingProduct, "productId", "categoryDto");
        if (productDto.getCategoryDto() != null) {
            existingProduct.setCategory(CategoryMappingHelper.map(productDto.getCategoryDto()));
        }
        return ProductMappingHelper.map(productRepository.save(existingProduct));
    }

    @Override
    public ProductDto update(Integer productId, ProductDto productDto) {
        log.info("ProductDto, service; update product with productId");
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
        BeanUtils.copyProperties(productDto, existingProduct, "productId", "category");
        if (productDto.getCategoryDto() != null) {
            existingProduct.setCategory(CategoryMappingHelper.map(productDto.getCategoryDto()));
        }
        return ProductMappingHelper.map(productRepository.save(existingProduct));
    }

    @Override
    public void deleteById(Integer productId) {
        log.info("Void, service; delete product by id");
        productRepository.delete(ProductMappingHelper.map(findById(productId)));
    }
}
