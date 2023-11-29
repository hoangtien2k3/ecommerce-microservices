package com.hoangtien2k3.userservice.service;

import com.hoangtien2k3.userservice.model.dto.request.SignInForm;
import com.hoangtien2k3.userservice.model.dto.request.SignUpForm;
import com.hoangtien2k3.userservice.model.dto.response.JwtResponseMessage;
import com.hoangtien2k3.userservice.model.entity.User;
import reactor.core.publisher.Mono;

public interface IUserService {
    Mono<User> registerUser(SignUpForm signUpFrom);

    Mono<JwtResponseMessage> login(SignInForm signInForm);
}