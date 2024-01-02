package com.hoangtien2k3.userservice.service;

import com.hoangtien2k3.userservice.model.dto.request.ChangePasswordRequest;
import com.hoangtien2k3.userservice.model.dto.request.LoginDTO;
import com.hoangtien2k3.userservice.model.dto.request.UserDTO;
import com.hoangtien2k3.userservice.model.dto.response.JwtResponseMessage;
import com.hoangtien2k3.userservice.model.entity.User;
import org.springframework.data.domain.Page;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Mono<User> register(UserDTO signUpFrom);
    Mono<JwtResponseMessage> login(LoginDTO signInForm);
    Mono<Void> logout();
    Mono<User> update(Long userId, UserDTO signUpForm);
    Mono<String> changePassword(ChangePasswordRequest request);
//    Mono<String> resetPassword(ResetPasswordRequest resetPasswordRequest);
    String delete(Long id);
    Optional<UserDTO> findById(Long userId);
    Optional<UserDTO> findByUsername(String userName);
    Page<UserDTO> findAllUsers(int page, int size, String sortBy, String sortOrder);
}