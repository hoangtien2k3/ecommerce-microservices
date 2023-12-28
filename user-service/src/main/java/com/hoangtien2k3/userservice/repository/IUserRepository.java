package com.hoangtien2k3.userservice.repository;

import com.hoangtien2k3.userservice.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);

    Optional<User> findByEmail(String name);
//    Optional<User> findById(Long id);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    User getUserByUsername(String username);
    List<User> findAll();
}
