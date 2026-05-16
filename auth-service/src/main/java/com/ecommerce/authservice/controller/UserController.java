package com.ecommerce.authservice.controller;

import com.ecommerce.authservice.dto.request.UpdateUserRequest;
import com.ecommerce.authservice.dto.response.ResponseMessage;
import com.ecommerce.authservice.dto.response.UserResponse;
import com.ecommerce.authservice.mapper.UserMapper;
import com.ecommerce.authservice.service.UserService;
import com.ecommerce.commonlib.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/manager")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PutMapping("update/{id}")
    @PreAuthorize("isAuthenticated() and hasAuthority('USER')")
    public ResponseEntity<ResponseMessage> update(@PathVariable("id") Long id,
                                                  @RequestBody UpdateUserRequest request) {
        userService.update(id, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseMessage.builder()
                        .message("Update user successfully.")
                        .build());
    }

    @PutMapping("/change-password")
    @PreAuthorize("isAuthenticated() and hasAuthority('USER')")
    public String changePassword(@RequestBody com.ecommerce.authservice.dto.request.ChangePasswordRequest request) {
        return userService.changePassword(request);
    }

    @DeleteMapping("delete/{id}")
    @PreAuthorize("isAuthenticated() and (hasAuthority('USER') or hasAuthority('ADMIN'))")
    public String delete(@PathVariable("id") Long id) {
        return userService.delete(id);
    }

    @GetMapping("/user")
    @PreAuthorize("isAuthenticated() and (hasAuthority('USER') or hasAuthority('ADMIN'))")
    public ResponseEntity<UserResponse> getUserByUsername(@RequestParam(value = "username") String username) {
        return ResponseEntity.ok(userMapper.toResponse(userService.findByUsername(username)));
    }

    @GetMapping("/user/{id}")
    @PreAuthorize("isAuthenticated() and (hasAuthority('USER') or hasAuthority('ADMIN'))")
    public ResponseEntity<UserResponse> getUserById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userMapper.toResponse(userService.findById(id)));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size,
                                                          @RequestParam(defaultValue = "id") String sortBy,
                                                          @RequestParam(defaultValue = "ASC") String sortOrder) {
        return ResponseEntity.ok(userService.findAllUsers(page, size, sortBy, sortOrder));
    }

    @GetMapping("/info")
    public ResponseEntity<UserResponse> getUserInfo(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            throw BusinessException.unauthorized("auth.token.error.or.timeout");
        }
        String username = jwt.getSubject();
        return ResponseEntity.ok(userMapper.toResponse(userService.findByUsername(username)));
    }
}
