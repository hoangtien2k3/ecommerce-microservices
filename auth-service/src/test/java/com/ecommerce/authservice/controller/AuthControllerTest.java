package com.ecommerce.authservice.controller;

import com.ecommerce.authservice.model.dto.request.Login;
import com.ecommerce.authservice.model.dto.request.RefreshTokenRequest;
import com.ecommerce.authservice.model.dto.request.SignUp;
import com.ecommerce.authservice.model.dto.response.JwtResponseMessage;
import com.ecommerce.authservice.model.dto.response.ResponseMessage;
import com.ecommerce.authservice.model.entity.User;
import com.ecommerce.authservice.service.UserService;
import com.ecommerce.commonlib.keycloak.KeycloakAuthClient;
import com.ecommerce.commonlib.keycloak.KeycloakTokenResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthControllerTest {

    @Test
    void loginShouldReturnAccessAndRefreshToken() {
        AuthController authController = new AuthController(new StubUserService(), new StubKeycloakAuthClient());
        Login login = new Login();
        login.setUsername("testuser");
        login.setPassword("testpass");

        ResponseEntity<JwtResponseMessage> response = authController.login(login);
        assertNotNull(response);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals("access-testuser", response.getBody().getAccessToken());
        assertEquals("refresh-testuser", response.getBody().getRefreshToken());
    }

    @Test
    void refreshShouldReturnNewTokens() {
        AuthController authController = new AuthController(new StubUserService(), new StubKeycloakAuthClient());
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("old-refresh-token");

        ResponseEntity<JwtResponseMessage> response = authController.refreshToken(request);
        assertNotNull(response);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals("new-access-token", response.getBody().getAccessToken());
        assertEquals("new-refresh-token", response.getBody().getRefreshToken());
    }

    @Test
    void logoutShouldReturnSuccessMessage() {
        AuthController authController = new AuthController(new StubUserService(), new StubKeycloakAuthClient());
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("refresh-token");

        ResponseEntity<ResponseMessage> response = authController.logout(request);
        assertNotNull(response);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals("Logout successfully.", response.getBody().getMessage());
    }

    @Test
    void registerShouldReturnSuccessMessage() {
        AuthController authController = new AuthController(new StubUserService(), new StubKeycloakAuthClient());
        SignUp signUp = new SignUp();
        signUp.setUsername("new-user");

        ResponseMessage response = authController.register(signUp);
        assertNotNull(response);
        assertEquals("Create user: new-user successfully.", response.getMessage());
    }

    private static final class StubKeycloakAuthClient extends KeycloakAuthClient {
        private StubKeycloakAuthClient() {
            super(null, null);
        }

        @Override
        public KeycloakTokenResponse login(String username, String password) {
            KeycloakTokenResponse response = new KeycloakTokenResponse();
            response.setAccessToken("access-" + username);
            response.setRefreshToken("refresh-" + username);
            return response;
        }

        @Override
        public KeycloakTokenResponse refreshToken(String refreshToken) {
            KeycloakTokenResponse response = new KeycloakTokenResponse();
            response.setAccessToken("new-access-token");
            response.setRefreshToken("new-refresh-token");
            return response;
        }

        @Override
        public void logout(String refreshToken) {
        }
    }

    private static final class StubUserService implements UserService {
        @Override
        public User register(SignUp signUp) {
            User user = new User();
            user.setUsername(signUp.getUsername());
            return user;
        }

        @Override
        public User update(Long userId, SignUp update) {
            return null;
        }

        @Override
        public String changePassword(com.ecommerce.authservice.model.dto.request.ChangePasswordRequest request) {
            return null;
        }

        @Override
        public String delete(Long id) {
            return null;
        }

        @Override
        public User findById(Long userId) {
            return null;
        }

        @Override
        public User findByUsername(String userName) {
            return null;
        }

        @Override
        public org.springframework.data.domain.Page<com.ecommerce.authservice.model.dto.request.UserDto> findAllUsers(
                int page, int size, String sortBy, String sortOrder) {
            return null;
        }
    }
}
