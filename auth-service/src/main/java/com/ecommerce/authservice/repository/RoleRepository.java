package com.ecommerce.authservice.repository;

import com.ecommerce.authservice.model.entity.Role;
import com.ecommerce.authservice.model.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query("SELECT r FROM Role r WHERE r.name = :name")
    Optional<Role> findByName(@Param("name") RoleName name);

    @Query("SELECT u.roles FROM User u WHERE u.id = :id")
    List<Role> findByUserId(@Param("id") Long id);

}
