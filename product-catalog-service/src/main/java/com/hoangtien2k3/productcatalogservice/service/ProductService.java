package com.hoangtien2k3.productcatalogservice.service;

import com.hoangtien2k3.productcatalogservice.dto.PageResult;
import com.hoangtien2k3.productcatalogservice.entity.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    List<Product> getAllProduct();
    List<Product> getAllProductByCategory(String category);
    Product getProductById(Long id);
    List<Product> getAllProductsByName(String name);
    Product addProduct(Product product);
    void deleteProduct(Long productId);
    Page<Product> getProducts(int page, int size);

//    List<Product> getAllProductsPage(int page, int size);
    PageResult getAllProductsPage(int page, int size);
}
