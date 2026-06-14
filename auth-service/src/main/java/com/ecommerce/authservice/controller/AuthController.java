package com.ecommerce.authservice.controller;

import com.ecommerce.authservice.dto.request.LoginRequest;
import com.ecommerce.authservice.dto.request.RefreshTokenRequest;
import com.ecommerce.authservice.dto.request.RegisterRequest;
import com.ecommerce.authservice.service.UserService;
import com.ecommerce.commonlib.keycloak.KeycloakAuthClient;
import com.ecommerce.commonlib.keycloak.KeycloakTokenResponse;
import com.ecommerce.commonlib.viewmodel.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final KeycloakAuthClient keycloakAuthClient;

    @PostMapping("/signup")
    public ApiResponse<Void> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return ApiResponse.message("User " + request.getUsername() + " registered successfully");
    }

    @PostMapping("/signin")
    public ApiResponse<KeycloakTokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(keycloakAuthClient.login(request.getUsername(), request.getPassword()));
    }

    @PostMapping("/refresh")
    public ApiResponse<KeycloakTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.ok(keycloakAuthClient.refreshToken(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        keycloakAuthClient.logout(request.getRefreshToken());
        return ApiResponse.message("Logout successful");
    }
}
