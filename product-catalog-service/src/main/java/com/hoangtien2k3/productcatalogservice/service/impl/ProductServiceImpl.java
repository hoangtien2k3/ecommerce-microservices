package com.hoangtien2k3.productcatalogservice.service.impl;

import com.hoangtien2k3.productcatalogservice.dto.PageResult;
import com.hoangtien2k3.productcatalogservice.entity.Product;
import com.hoangtien2k3.productcatalogservice.repository.ProductRepository;
import com.hoangtien2k3.productcatalogservice.repository.ProductSearchAndPageRepository;
import com.hoangtien2k3.productcatalogservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductSearchAndPageRepository productSearchAndPageRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, ProductSearchAndPageRepository productSearchAndPageRepository) {
        this.productRepository = productRepository;
        this.productSearchAndPageRepository = productSearchAndPageRepository;
    }

    @Override
    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getAllProductByCategory(String category) {
        return productRepository.findAllByCategory(category);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.getReferenceById(id);
    }

    @Override
    public List<Product> getAllProductsByName(String name) {
        return productRepository.findAllByProductName(name);
    }

    @Override
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }

    @Override
    public Page<Product> getProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAll(pageable);
    }


    @Override
    public PageResult getAllProductsPage(int page, int size) {
        int offset = page * size;

        List<Product> products = productRepository.findProductsByPage(offset, size);
        int totalProducts = productRepository.countAllProducts();

        return new PageResult(products, totalProducts);
    }

    // search
    @Override
    public List<Product> searchProductsByKeyword(String keyword) {
        return productRepository.searchProductsByKeyword(keyword.toLowerCase());
    }

    // search and page and sort by price default asc
    @Override
    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        Sort sort = Sort.by("price").ascending();
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return productSearchAndPageRepository.searchByKeyword(keyword, pageable);
    }

}
