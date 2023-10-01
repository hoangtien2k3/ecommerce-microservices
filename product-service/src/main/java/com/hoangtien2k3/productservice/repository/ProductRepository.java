package com.hoangtien2k3.productservice.repository;

import com.hoangtien2k3.productservice.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
