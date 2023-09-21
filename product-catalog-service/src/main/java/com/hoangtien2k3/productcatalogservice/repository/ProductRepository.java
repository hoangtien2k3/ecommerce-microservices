package com.hoangtien2k3.productcatalogservice.repository;

import com.hoangtien2k3.productcatalogservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByCategory(String category);
    List<Product> findAllByProductName(String name);
}