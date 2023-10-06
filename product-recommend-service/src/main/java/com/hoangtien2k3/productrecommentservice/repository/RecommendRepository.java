package com.hoangtien2k3.productrecommentservice.repository;

import com.hoangtien2k3.productrecommentservice.model.Recommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface RecommendRepository extends JpaRepository<Recommend, Long> {

    @Query("SELECT r FROM Recommend r WHERE r.product.productName = :productName")
    public List<Recommend> findAllRatingByProductName(@Param("productName") String productName);

}
