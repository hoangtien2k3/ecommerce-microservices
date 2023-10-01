package com.hoangtien2k3.productservice.repository;

import com.hoangtien2k3.productservice.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
