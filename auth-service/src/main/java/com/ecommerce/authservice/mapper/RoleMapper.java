package com.ecommerce.authservice.mapper;

import com.ecommerce.authservice.entity.RoleName;
import com.ecommerce.commonlib.exception.BusinessException;

public final class RoleMapper {

    private RoleMapper() {
    }

    public static RoleName toRoleName(String roleName) {
        try {
            return RoleName.valueOf(roleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw BusinessException.badRequest("auth.unsupported.role", roleName);
        }
    }
}
