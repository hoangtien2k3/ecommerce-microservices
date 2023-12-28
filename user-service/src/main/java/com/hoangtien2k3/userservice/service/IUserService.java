package com.hoangtien2k3.userservice.service;

import com.hoangtien2k3.userservice.model.dto.request.SignInForm;
import com.hoangtien2k3.userservice.model.dto.request.SignUpForm;
import com.hoangtien2k3.userservice.model.dto.response.JwtResponseMessage;
import com.hoangtien2k3.userservice.model.entity.User;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    Mono<User> register(SignUpForm signUpFrom);
    Mono<JwtResponseMessage> login(SignInForm signInForm);
    Mono<User> update(Long userId, SignUpForm signUpForm);
    String delete(Long id);
    Optional<User> findById(Long userId);
    Optional<User> findByUsername(String userName);
    Optional<List<User>> findAllUser();
}