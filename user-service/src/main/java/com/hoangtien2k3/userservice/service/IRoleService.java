package com.hoangtien2k3.userservice.service;

import com.hoangtien2k3.userservice.entity.Role;
import com.hoangtien2k3.userservice.entity.RoleName;

import java.util.Optional;

public interface IRoleService {
    Optional<Role> findByName(RoleName name);
}
