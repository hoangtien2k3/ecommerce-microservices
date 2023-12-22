package com.hoangtien2k3.productservice.service.impl;

import com.hoangtien2k3.productservice.dto.ProductDto;
import com.hoangtien2k3.productservice.exception.wrapper.ProductNotFoundException;
import com.hoangtien2k3.productservice.helper.ProductMappingHelper;
import com.hoangtien2k3.productservice.repository.ProductRepository;
import com.hoangtien2k3.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Slf4j
@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private final ProductRepository productRepository;

    @Override
    public Flux<List<ProductDto>> findAll() {
        log.info("ProductDto List, service, fetch all products");
        return Flux.defer(() -> {
                    List<ProductDto> productDtos = productRepository.findAll()
                            .stream()
                            .map(ProductMappingHelper::map)
                            .distinct()
                            .toList();
                    return Flux.just(productDtos);
                })
                .onErrorResume(throwable -> {
                    log.error("Error while fetching products: " + throwable.getMessage());
                    return Flux.empty();
                });
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
        return ProductMappingHelper.map(productRepository.save(ProductMappingHelper.map(productDto)));
    }

    @Override
    public ProductDto update(ProductDto productDto) {
        log.info("ProductDto, service; update product");
        return ProductMappingHelper.map(productRepository.save(ProductMappingHelper.map(productDto)));
    }

    @Override
    public ProductDto update(Integer productId, ProductDto productDto) {
        log.info("ProductDto, service; update product with productId");
        return ProductMappingHelper.map(productRepository.save(ProductMappingHelper.map(this.findById(productId))));
    }

    @Override
    public void deleteById(Integer productId) {
        log.info("Void, service; delete product by id");
        this.productRepository.delete(ProductMappingHelper.map(this.findById(productId)));
    }

}
