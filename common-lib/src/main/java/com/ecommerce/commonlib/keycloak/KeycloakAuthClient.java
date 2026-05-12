package com.ecommerce.commonlib.keycloak;

import com.ecommerce.commonlib.exception.BusinessException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.*;

@Component
@RequiredArgsConstructor
public class KeycloakAuthClient {
    private static final String BEARER_SCHEME = "Bearer";
    
    private final RestClient.Builder restClientBuilder;
    private final KeycloakClientProperties properties;

    public KeycloakTokenResponse login(String username, String password) {
        MultiValueMap<String, String> form = baseClientForm();
        form.add(GRANT_TYPE, PASSWORD);
        form.add(USERNAME, username);
        form.add(PASSWORD, password);
        return postToken(form);
    }

    public KeycloakTokenResponse refreshToken(String refreshToken) {
        MultiValueMap<String, String> form = baseClientForm();
        form.add(GRANT_TYPE, REFRESH_TOKEN);
        form.add(REFRESH_TOKEN, refreshToken);
        return postToken(form);
    }

    public void logout(String refreshToken) {
        MultiValueMap<String, String> form = baseClientForm();
        form.add(REFRESH_TOKEN, refreshToken);

        execute(() -> restClientBuilder.build()
            .post()
            .uri(properties.logoutEndpoint())
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(form)
            .retrieve()
            .toBodilessEntity());
    }

    public String createUser(
        @NonNull String username,
        @NonNull String email,
        String fullName,
        @NonNull String password,
        List<String> realmRoles
    ) {
        String adminToken = fetchAdminAccessToken();

        Map<String, Object> payload = new HashMap<>();
        payload.put("enabled", true);
        payload.put("username", username);
        payload.put("email", email);
        payload.put("emailVerified", true);
        payload.put("credentials", List.of(Map.of(
            "type", "password",
            "value", password,
            "temporary", false
        )));
        if (fullName != null && !fullName.isBlank()) {
            String normalized = fullName.trim().replaceAll("\\s+", " ");
            int idx = normalized.lastIndexOf(' ');
            if (idx > 0) {
                payload.put("firstName", normalized.substring(0, idx));
                payload.put("lastName", normalized.substring(idx + 1));
            } else {
                payload.put("firstName", normalized);
                payload.put("lastName", normalized);
            }
        }

        URI location = execute(() -> restClientBuilder.build()
            .post()
            .uri(properties.adminUsersEndpoint())
            .header(HttpHeaders.AUTHORIZATION, BEARER_SCHEME + " " + adminToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(payload)
            .retrieve()
            .toBodilessEntity())
            .getHeaders()
            .getLocation();

        if (location == null) {
            throw BusinessException.badRequest("keycloak.create.user.failed");
        }

        String keycloakUserId = extractUserIdFromLocation(location.toString());
        assignRealmRoles(adminToken, keycloakUserId, realmRoles);
        return keycloakUserId;
    }

    public void deleteUser(String keycloakUserId) {
        String adminToken = fetchAdminAccessToken();
        execute(() -> restClientBuilder.build()
            .delete()
            .uri(properties.adminUserByIdEndpoint(keycloakUserId))
            .header(HttpHeaders.AUTHORIZATION, BEARER_SCHEME + " " + adminToken)
            .retrieve()
            .toBodilessEntity());
    }

    private KeycloakTokenResponse postToken(MultiValueMap<String, String> form) {
        return execute(() -> restClientBuilder.build()
            .post()
            .uri(properties.tokenEndpoint())
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(form)
            .retrieve()
            .body(KeycloakTokenResponse.class));
    }

    private MultiValueMap<String, String> baseClientForm() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add(CLIENT_ID, properties.getClientId());
        if (properties.getClientSecret() != null && !properties.getClientSecret().isBlank()) {
            form.add(CLIENT_SECRET, properties.getClientSecret());
        }
        return form;
    }

    private String fetchAdminAccessToken() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add(CLIENT_ID, properties.getAdminClientId());
        form.add(USERNAME, properties.getAdminUsername());
        form.add(PASSWORD, properties.getAdminPassword());
        form.add(GRANT_TYPE, PASSWORD);

        KeycloakTokenResponse tokenResponse = execute(() -> restClientBuilder.build()
            .post()
            .uri(properties.adminTokenEndpoint())
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(form)
            .retrieve()
            .body(KeycloakTokenResponse.class));

        if (tokenResponse == null || tokenResponse.getAccessToken() == null || tokenResponse.getAccessToken().isBlank()) {
            throw BusinessException.unauthorized("keycloak.admin.token.failed");
        }
        return tokenResponse.getAccessToken();
    }

    private void assignRealmRoles(String adminToken, String keycloakUserId, List<String> realmRoles) {
        if (realmRoles == null || realmRoles.isEmpty()) {
            return;
        }

        List<Map<String, Object>> roleRepresentations = new ArrayList<>();
        for (String roleName : realmRoles) {
            Map<String, Object> roleRepresentation = execute(() -> restClientBuilder.build()
                .get()
                .uri(properties.adminRoleByNameEndpoint(roleName))
                .header(HttpHeaders.AUTHORIZATION, BEARER_SCHEME + " " + adminToken)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                }));
            roleRepresentations.add(roleRepresentation);
        }

        execute(() -> restClientBuilder.build()
            .post()
            .uri(properties.adminUserRealmRoleMappingEndpoint(keycloakUserId))
            .header(HttpHeaders.AUTHORIZATION, BEARER_SCHEME + " " + adminToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(roleRepresentations)
            .retrieve()
            .toBodilessEntity());
    }

    private String extractUserIdFromLocation(String location) {
        int idx = location.lastIndexOf('/');
        if (idx < 0 || idx == location.length() - 1) {
            throw BusinessException.badRequest("keycloak.invalid.user.location");
        }
        return location.substring(idx + 1);
    }

    private <T> T execute(CheckedSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (RestClientResponseException ex) {
            int status = ex.getStatusCode().value();
            if (status == 400) {
                throw BusinessException.badRequest("keycloak.bad.request", ex.getResponseBodyAsString());
            }
            if (status == 401) {
                throw BusinessException.unauthorized("keycloak.unauthorized");
            }
            if (status == 403) {
                throw BusinessException.forbidden("keycloak.forbidden");
            }
            if (status == 404) {
                throw BusinessException.notFound("keycloak.not.found");
            }
            if (status == 409) {
                throw BusinessException.conflict("keycloak.conflict");
            }
            throw BusinessException.badRequest("keycloak.request.failed", ex.getMessage());
        }
    }

    @FunctionalInterface
    private interface CheckedSupplier<T> {
        T get();
    }
}
