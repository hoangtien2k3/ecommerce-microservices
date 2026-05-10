package com.ecommerce.userservice.controller;

import com.ecommerce.userservice.model.dto.request.Login;
import com.ecommerce.userservice.model.dto.request.SignUp;
import com.ecommerce.userservice.model.dto.response.JwtResponseMessage;
import com.ecommerce.userservice.model.dto.response.ResponseMessage;
import com.ecommerce.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserAuth {

    private final UserService userService;

    @PostMapping({ "/signup", "/register" })
    public Mono<ResponseMessage> register(@Valid @RequestBody SignUp signUp) {
        return userService.register(signUp)
                .map(user -> new ResponseMessage("Create user: " + signUp.getUsername() + " successfully."))
                .onErrorResume(error -> Mono.just(new ResponseMessage("Error occurred while creating the account.")));
    }

    @PostMapping({ "/signin", "/login" })
    public Mono<ResponseEntity<JwtResponseMessage>> login(@Valid @RequestBody Login signInForm) {
        log.warn("Legacy login endpoint is disabled. Use Keycloak to obtain tokens.");
        return Mono.just(ResponseEntity.status(HttpStatus.GONE)
                .body(new JwtResponseMessage()));
    }

}
