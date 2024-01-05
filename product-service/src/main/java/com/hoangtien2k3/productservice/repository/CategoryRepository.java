package com.hoangtien2k3.productservice.repository;

import com.hoangtien2k3.productservice.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import reactor.core.publisher.Flux;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Page<Category> findAll(Pageable pageable);

    // Thêm các phương thức tìm kiếm hoặc lọc tại đây
    Page<Category> findByCategoryTitleContaining(String categoryTitle, Pageable pageable);

}
