package com.ecommerce.authservice.controller;

import com.ecommerce.authservice.dto.request.ChangePasswordRequest;
import com.ecommerce.authservice.dto.request.UpdateUserRequest;
import com.ecommerce.authservice.dto.response.UserResponse;
import com.ecommerce.authservice.mapper.UserMapper;
import com.ecommerce.authservice.service.UserService;
import com.ecommerce.commonlib.constants.ApiPaths;
import com.ecommerce.commonlib.exception.BusinessException;
import com.ecommerce.commonlib.exception.ErrorCode;
import com.ecommerce.commonlib.viewmodel.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping(ApiPaths.USERS)
@RequiredArgsConstructor
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final UserMapper userMapper;

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated() and hasAuthority('USER')")
    public ApiResponse<UserResponse> update(@PathVariable Long id,
                                            @Valid @RequestBody UpdateUserRequest request) {
        return ApiResponse.ok(userService.update(id, request), "User updated successfully");
    }

    @PutMapping("/me/password")
    @PreAuthorize("isAuthenticated() and hasAuthority('USER')")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return ApiResponse.message("Password change request processed");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated() and (hasAuthority('USER') or hasAuthority('ADMIN'))")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ApiResponse.message("User " + id + " deleted successfully");
    }

    @GetMapping
    @PreAuthorize("isAuthenticated() and (hasAuthority('USER') or hasAuthority('ADMIN'))")
    public ApiResponse<UserResponse> getUserByUsername(@RequestParam String username) {
        return ApiResponse.ok(userMapper.toResponse(userService.findByUsername(username)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated() and (hasAuthority('USER') or hasAuthority('ADMIN'))")
    public ApiResponse<UserResponse> getUserById(@PathVariable Long id) {
        return ApiResponse.ok(userMapper.toResponse(userService.findById(id)));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse<Page<UserResponse>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size,
                                                       @RequestParam(defaultValue = "id") String sortBy,
                                                       @RequestParam(defaultValue = "ASC") String sortOrder) {
        return ApiResponse.ok(userService.findAllUsers(page, size, sortBy, sortOrder));
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            throw BusinessException.of(ErrorCode.AUTH_TOKEN_INVALID);
        }
        return ApiResponse.ok(userMapper.toResponse(userService.findByUsername(jwt.getSubject())));
    }
}
