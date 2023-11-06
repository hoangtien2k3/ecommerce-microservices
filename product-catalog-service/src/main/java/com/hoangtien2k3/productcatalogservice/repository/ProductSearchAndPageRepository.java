package com.hoangtien2k3.productcatalogservice.repository;

import com.hoangtien2k3.productcatalogservice.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.SortDefault;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface ProductSearchAndPageRepository extends PagingAndSortingRepository<Product, Long> {

//    Page<Product> findByNameContaining(String keyword, Pageable pageable);

//    @Query("SELECT p FROM Product p WHERE p.productName LIKE %:keyword%")
//    Page<Product> findByNameContaining(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "WHERE p.productName LIKE %:keyword% " +
            "OR p.price LIKE %:keyword% " +
            "OR p.description LIKE %:keyword% " +
            "OR p.category LIKE %:keyword% " +
            "OR :keyword IS NULL"
    )
    Page<Product> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);


    Page<Product> findByKeyword(@Param("keyword") String keyword, Pageable pageable,
                                @Param("orderBy") String orderBy,
                                @Param("orderDirection") String orderDirection
    );
}

