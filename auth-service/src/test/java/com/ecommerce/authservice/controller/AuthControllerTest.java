package com.ecommerce.authservice.controller;

import com.ecommerce.authservice.config.SsoProperties;
import com.ecommerce.authservice.dto.request.RefreshTokenRequest;
import com.ecommerce.authservice.dto.request.RegisterRequest;
import com.ecommerce.authservice.service.SsoSessionStore;
import com.ecommerce.authservice.service.UserService;
import com.ecommerce.commonlib.keycloak.KeycloakAuthClient;
import com.ecommerce.commonlib.keycloak.KeycloakClientProperties;
import com.ecommerce.commonlib.viewmodel.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.net.URI;

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

    private KeycloakClientProperties keycloakProperties;
    private SsoProperties ssoProperties;
    private KeycloakAuthClient keycloakAuthClient;
    private SsoSessionStore ssoSessionStore;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        keycloakProperties = new KeycloakClientProperties();
        keycloakProperties.setServerUrl("http://localhost:8080");
        keycloakProperties.setRealm("test");
        keycloakProperties.setClientId("test-client");
        keycloakProperties.setAdminUsername("admin");
        keycloakProperties.setAdminPassword("admin");

        ssoProperties = new SsoProperties();
        ssoSessionStore = new SsoSessionStore(ssoProperties);
        keycloakAuthClient = new KeycloakAuthClient(RestClient.builder(), keycloakProperties);
        authController = new AuthController(
                userService, keycloakAuthClient, keycloakProperties, ssoProperties, ssoSessionStore);
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
    void ssoLoginShouldRedirectToKeycloak() {
        ResponseEntity<Void> response = authController.ssoLogin(null);

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        URI location = response.getHeaders().getLocation();
        assertNotNull(location);
        assertTrue(location.toString().startsWith(
                "http://localhost:8080/realms/test/protocol/openid-connect/auth"));
        assertTrue(location.toString().contains("client_id=test-client"));
        assertTrue(location.toString().contains("response_type=code"));
    }

    @Test
    void ssoSessionShouldRejectUnknownTicket() {
        assertThrows(Exception.class, () -> authController.ssoSession("bogus-ticket"));
    }

    @Test
    void logoutShouldUseKeycloakAuthClient() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("token");

        assertThrows(Exception.class, () -> authController.logout(request));
    }
}
