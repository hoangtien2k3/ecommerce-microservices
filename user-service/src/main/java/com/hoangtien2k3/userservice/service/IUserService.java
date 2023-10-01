package com.hoangtien2k3.userservice.service;

import com.hoangtien2k3.userservice.dto.request.SignInForm;
import com.hoangtien2k3.userservice.dto.request.SignUpForm;
import com.hoangtien2k3.userservice.dto.response.JwtResponse;
import com.hoangtien2k3.userservice.entity.User;
import reactor.core.publisher.Mono;

public interface IUserService {
    Mono<User> registerUser(SignUpForm signUpFrom);

    Mono<JwtResponse> login(SignInForm signInForm);
}