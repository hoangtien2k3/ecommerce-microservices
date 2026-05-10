package com.ecommerce.userservice.service;

import com.ecommerce.userservice.model.dto.request.ChangePasswordRequest;
import com.ecommerce.userservice.model.dto.request.Login;
import com.ecommerce.userservice.model.dto.request.SignUp;
import com.ecommerce.userservice.model.dto.request.UserDto;
import com.ecommerce.userservice.model.dto.response.JwtResponseMessage;
import com.ecommerce.userservice.model.entity.User;
import org.springframework.data.domain.Page;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<User> register(SignUp signUp);

    Mono<JwtResponseMessage> login(Login signInForm);

    Mono<Void> logout();

    Mono<User> update(Long userId, SignUp update);

    Mono<String> changePassword(ChangePasswordRequest request);

    // Mono<String> resetPassword(ResetPasswordRequest resetPasswordRequest);
    Mono<String> delete(Long id);

    Mono<User> findById(Long userId);

    Mono<User> findByUsername(String userName);

    Mono<Page<UserDto>> findAllUsers(int page, int size, String sortBy, String sortOrder);
}
