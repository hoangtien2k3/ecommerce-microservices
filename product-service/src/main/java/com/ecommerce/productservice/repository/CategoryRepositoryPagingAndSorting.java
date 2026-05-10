package com.ecommerce.productservice.repository;

import com.ecommerce.productservice.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CategoryRepositoryPagingAndSorting extends PagingAndSortingRepository<Category, Integer> {

    @Query("SELECT c FROM Category c")
    Page<Category> findAllPagedAndSortedCategories(Pageable pageable);

}
