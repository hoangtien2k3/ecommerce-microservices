package com.ecommerce.authservice.service;

import com.ecommerce.authservice.model.dto.request.ChangePasswordRequest;
import com.ecommerce.authservice.model.dto.request.SignUp;
import com.ecommerce.authservice.model.dto.request.UserDto;
import com.ecommerce.authservice.model.entity.User;
import org.springframework.data.domain.Page;

public interface UserService {
    User register(SignUp signUp);

    User update(Long userId, SignUp update);

    String changePassword(ChangePasswordRequest request);

    String delete(Long id);

    User findById(Long userId);

    User findByUsername(String userName);

    Page<UserDto> findAllUsers(int page, int size, String sortBy, String sortOrder);
}
