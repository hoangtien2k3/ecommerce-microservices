package com.hoangtien2k3.productrecommentservice.repository;

import com.hoangtien2k3.productrecommentservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}