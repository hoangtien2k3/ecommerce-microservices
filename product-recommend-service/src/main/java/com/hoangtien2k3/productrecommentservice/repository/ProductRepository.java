package com.hoangtien2k3.productrecommentservice.repository;

import com.hoangtien2k3.productrecommentservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
