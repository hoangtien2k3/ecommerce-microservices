package com.ecommerce.authservice.controller;

import com.ecommerce.authservice.model.dto.request.Login;
import com.ecommerce.authservice.model.dto.request.RefreshTokenRequest;
import com.ecommerce.authservice.model.dto.request.SignUp;
import com.ecommerce.authservice.model.dto.response.ResponseMessage;
import com.ecommerce.authservice.service.UserService;
import com.ecommerce.commonlib.keycloak.KeycloakAuthClient;
import com.ecommerce.commonlib.keycloak.KeycloakTokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final KeycloakAuthClient keycloakAuthClient;

    @PostMapping({ "/signup", "/register" })
    public ResponseMessage register(@Valid @RequestBody SignUp signUp) {
        userService.register(signUp);
        return new ResponseMessage("Create user: " + signUp.getUsername() + " successfully.");
    }

    @PostMapping({ "/signin", "/login" })
    public ResponseEntity<KeycloakTokenResponse> login(@Valid @RequestBody Login signInForm) {
        KeycloakTokenResponse tokenResponse = keycloakAuthClient.login(signInForm.getUsername(), signInForm.getPassword());
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
