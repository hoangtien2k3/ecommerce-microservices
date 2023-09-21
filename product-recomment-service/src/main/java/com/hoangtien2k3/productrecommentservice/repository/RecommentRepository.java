package com.hoangtien2k3.productrecommentservice.repository;

import com.hoangtien2k3.productrecommentservice.model.Recomment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface RecommentRepository extends JpaRepository<Recomment, Long> {

    @Query("SELECT r FROM Recomment r WHERE r.product.productName = :productName")
    public List<Recomment> findAllRatingByProductName(@Param("productName") String productName);

}
