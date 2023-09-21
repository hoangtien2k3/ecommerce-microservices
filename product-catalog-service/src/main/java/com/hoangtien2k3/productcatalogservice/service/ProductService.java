package com.hoangtien2k3.productcatalogservice.service;

import com.hoangtien2k3.productcatalogservice.entity.Product;

import java.util.List;


public interface ProductService {
    List<Product> getAllProduct();
    List<Product> getAllProductByCategory(String category);
    Product getProductById(Long id);
    List<Product> getAllProductsByName(String name);
    Product addProduct(Product product);
    void deleteProduct(Long productId);
}
