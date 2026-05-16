package com.ecommerce.authservice.service;

import com.ecommerce.authservice.dto.request.ChangePasswordRequest;
import com.ecommerce.authservice.dto.request.RegisterRequest;
import com.ecommerce.authservice.dto.request.UpdateUserRequest;
import com.ecommerce.authservice.dto.response.UserResponse;
import com.ecommerce.authservice.entity.User;
import org.springframework.data.domain.Page;

public interface UserService {
    User register(RegisterRequest request);

    UserResponse update(Long userId, UpdateUserRequest request);

    String changePassword(ChangePasswordRequest request);

    String delete(Long id);

    User findById(Long userId);

    User findByUsername(String userName);

    Page<UserResponse> findAllUsers(int page, int size, String sortBy, String sortOrder);
}
