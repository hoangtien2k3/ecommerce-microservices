package com.ecommerce.authservice.service;

import com.ecommerce.authservice.entity.Role;
import com.ecommerce.authservice.entity.RoleName;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    Optional<Role> findByName(RoleName name);

    boolean assignRole(Long id, String roleName);

    boolean revokeRole(Long id, String roleName);

    List<String> getUserRoles(Long id);
}
