package com.ecommerce.authservice.controller;

import com.ecommerce.authservice.dto.request.LoginRequest;
import com.ecommerce.authservice.dto.request.RefreshTokenRequest;
import com.ecommerce.authservice.dto.request.RegisterRequest;
import com.ecommerce.authservice.dto.response.ResponseMessage;
import com.ecommerce.authservice.service.UserService;
import com.ecommerce.commonlib.keycloak.KeycloakAuthClient;
import com.ecommerce.commonlib.keycloak.KeycloakTokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final KeycloakAuthClient keycloakAuthClient;

    @PostMapping({"/signup", "/register"})
    public ResponseMessage register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return new ResponseMessage("Create user: " + request.getUsername() + " successfully.");
    }

    @PostMapping({"/signin", "/login"})
    public ResponseEntity<KeycloakTokenResponse> login(@Valid @RequestBody LoginRequest request) {
        KeycloakTokenResponse tokenResponse = keycloakAuthClient.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping({"/refresh", "/refresh-token"})
    public ResponseEntity<KeycloakTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        KeycloakTokenResponse tokenResponse = keycloakAuthClient.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseMessage> logout(@Valid @RequestBody RefreshTokenRequest request) {
        keycloakAuthClient.logout(request.getRefreshToken());
        return ResponseEntity.ok(new ResponseMessage("Logout successfully."));
    }
}
