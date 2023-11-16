package com.hoangtien2k3.productcatalogservice.repository;

import com.hoangtien2k3.productcatalogservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>  {
    // @Query(value = "SELECT p FROM Product p where (:category IS NULL or p.category =:category)")
    List<Product> findAllByCategory(String category);
    List<Product> findAllByProductName(String name);

    // get list product the page and size
    @Query(value = "SELECT * FROM products LIMIT :offset, :size", nativeQuery = true)
    List<Product> findProductsByPage(@Param("offset") int offset, @Param("size") int size);

    // total products
    @Query(value = "SELECT COUNT(*) FROM products", nativeQuery = true)
    int countAllProducts();

    // search
    @Query("SELECT p FROM Product p WHERE LOWER(p.productName) LIKE %:keyword%")
    List<Product> searchProductsByKeyword(@Param("keyword") String keyword);

}