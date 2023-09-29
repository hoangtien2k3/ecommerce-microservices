package com.hoangtien2k3.userservice.repository;

import com.hoangtien2k3.userservice.entity.Role;
import com.hoangtien2k3.userservice.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
