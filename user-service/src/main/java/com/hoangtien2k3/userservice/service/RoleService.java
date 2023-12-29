package com.hoangtien2k3.userservice.service;

import com.hoangtien2k3.userservice.model.entity.Role;
import com.hoangtien2k3.userservice.model.entity.RoleName;

import java.util.Optional;

public interface RoleService {

    Optional<Role> findByName(RoleName name);
}
