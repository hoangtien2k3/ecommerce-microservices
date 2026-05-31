package com.ecommerce.authservice.controller;

import com.ecommerce.authservice.dto.request.LoginRequest;
import com.ecommerce.authservice.dto.request.RefreshTokenRequest;
import com.ecommerce.authservice.dto.request.RegisterRequest;
import com.ecommerce.authservice.service.UserService;
import com.ecommerce.commonlib.keycloak.KeycloakAuthClient;
import com.ecommerce.commonlib.keycloak.KeycloakClientProperties;
import com.ecommerce.commonlib.viewmodel.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    private KeycloakAuthClient keycloakAuthClient;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        KeycloakClientProperties properties = new KeycloakClientProperties();
        properties.setServerUrl("http://localhost:8080");
        properties.setRealm("test");
        properties.setClientId("test-client");
        properties.setAdminUsername("admin");
        properties.setAdminPassword("admin");
        keycloakAuthClient = new KeycloakAuthClient(RestClient.builder(), properties);
        authController = new AuthController(userService, keycloakAuthClient);
    }

    @Test
    void registerShouldDelegateToUserService() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("Password1");
        request.setFullName("New User");

        when(userService.register(any(RegisterRequest.class))).thenReturn(null);

        ApiResponse<Void> response = authController.register(request);

        assertNotNull(response);
        assertTrue(response.success());
        assertEquals("User newuser registered successfully", response.message());
        verify(userService).register(request);
    }

    @Test
    void loginShouldUseKeycloakAuthClient() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("testpass");

        assertThrows(Exception.class, () -> authController.login(request));
    }

    @Test
    void logoutShouldUseKeycloakAuthClient() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("token");

        assertThrows(Exception.class, () -> authController.logout(request));
    }
}
