package com.ecommerce.authservice.controller;

import com.ecommerce.authservice.service.RoleService;
import com.ecommerce.commonlib.constants.ApiPaths;
import com.ecommerce.commonlib.exception.BusinessException;
import com.ecommerce.commonlib.exception.ErrorCode;
import com.ecommerce.commonlib.viewmodel.ApiResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@RestController
@RequestMapping(ApiPaths.ROLES)
@RequiredArgsConstructor
@Validated
public class RoleController {

    private static final Logger log = LoggerFactory.getLogger(RoleController.class);

    private final RoleService roleService;

    @PostMapping("/users/{userId}/assign")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse<Void> assignRoles(@PathVariable Long userId,
                                         @RequestBody @NotBlank String roleNames) {
        if (!roleService.assignRole(userId, roleNames)) {
            throw BusinessException.of(ErrorCode.CONFLICT);
        }
        return ApiResponse.message("Roles assigned to user " + userId);
    }

    @PostMapping("/users/{userId}/revoke")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse<Void> revokeRoles(@PathVariable Long userId,
                                         @RequestBody @NotBlank String roleNames) {
        if (!roleService.revokeRole(userId, roleNames)) {
            throw BusinessException.of(ErrorCode.CONFLICT);
        }
        return ApiResponse.message("Roles revoked from user " + userId);
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse<List<String>> getUserRoles(@PathVariable Long userId) {
        return ApiResponse.ok(roleService.getUserRoles(userId));
    }
}
