package com.ecommerce.commonlib.keycloak;

import com.ecommerce.commonlib.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.CLIENT_ID;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.CLIENT_SECRET;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.GRANT_TYPE;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.PASSWORD;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.REFRESH_TOKEN;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.USERNAME;

/**
 * Adapter over Keycloak's admin + token endpoints.
 *
 * <h3>Design notes</h3>
 * <ul>
 *   <li><strong>Reuses one {@link RestClient}</strong> built at construction time —
 *       the previous implementation rebuilt the client on every call.</li>
 *   <li>Bubbles up vendor errors as {@link BusinessException} with i18n keys so the
 *       global {@code ApiExceptionHandler} renders a consistent envelope.</li>
 *   <li>Pure adapter — keeps no in-memory state; if a service needs admin token caching
 *       it should wrap this with its own decorator.</li>
 * </ul>
 */
public class KeycloakAuthClient {

    private static final Logger log = LoggerFactory.getLogger(KeycloakAuthClient.class);

    private static final String BEARER_SCHEME = "Bearer ";
    private static final ParameterizedTypeReference<Map<String, Object>> MAP_TYPE =
            new ParameterizedTypeReference<>() {};

    private final RestClient restClient;
    private final KeycloakClientProperties properties;

    public KeycloakAuthClient(RestClient.Builder restClientBuilder, KeycloakClientProperties properties) {
        this.restClient = restClientBuilder.build();
        this.properties = properties;
    }

    // ------------------------------------------------------------------
    // Token endpoints
    // ------------------------------------------------------------------

    public KeycloakTokenResponse login(String username, String password) {
        MultiValueMap<String, String> form = baseClientForm();
        form.add(GRANT_TYPE, PASSWORD);
        form.add(USERNAME, Objects.requireNonNull(username, "username"));
        form.add(PASSWORD, Objects.requireNonNull(password, "password"));
        return postToken(form);
    }

    public KeycloakTokenResponse refreshToken(String refreshToken) {
        MultiValueMap<String, String> form = baseClientForm();
        form.add(GRANT_TYPE, REFRESH_TOKEN);
        form.add(REFRESH_TOKEN, Objects.requireNonNull(refreshToken, "refreshToken"));
        return postToken(form);
    }

    public void logout(String refreshToken) {
        MultiValueMap<String, String> form = baseClientForm();
        form.add(REFRESH_TOKEN, Objects.requireNonNull(refreshToken, "refreshToken"));

        execute(() -> restClient.post()
                .uri(properties.logoutEndpoint())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .toBodilessEntity());
    }

    // ------------------------------------------------------------------
    // Admin operations
    // ------------------------------------------------------------------

    public String createUser(String username,
                             String email,
                             String fullName,
                             String password,
                             List<String> realmRoles) {
        Objects.requireNonNull(username, "username");
        Objects.requireNonNull(email, "email");
        Objects.requireNonNull(password, "password");

        String adminToken = fetchAdminAccessToken();

        Map<String, Object> payload = buildUserPayload(username, email, fullName, password);

        URI location = execute(() -> restClient.post()
                .uri(properties.adminUsersEndpoint())
                .header(HttpHeaders.AUTHORIZATION, BEARER_SCHEME + adminToken)
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
        execute(() -> restClient.delete()
                .uri(properties.adminUserByIdEndpoint(keycloakUserId))
                .header(HttpHeaders.AUTHORIZATION, BEARER_SCHEME + adminToken)
                .retrieve()
                .toBodilessEntity());
    }

    // ------------------------------------------------------------------
    // Internals
    // ------------------------------------------------------------------

    private KeycloakTokenResponse postToken(MultiValueMap<String, String> form) {
        return execute(() -> restClient.post()
                .uri(properties.tokenEndpoint())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(KeycloakTokenResponse.class));
    }

    private MultiValueMap<String, String> baseClientForm() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add(CLIENT_ID, properties.getClientId());
        if (StringUtils.hasText(properties.getClientSecret())) {
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

        KeycloakTokenResponse response = execute(() -> restClient.post()
                .uri(properties.adminTokenEndpoint())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(KeycloakTokenResponse.class));

        if (response == null || !StringUtils.hasText(response.accessToken())) {
            throw BusinessException.unauthorized("keycloak.admin.token.failed");
        }
        return response.accessToken();
    }

    private void assignRealmRoles(String adminToken, String keycloakUserId, List<String> realmRoles) {
        if (realmRoles == null || realmRoles.isEmpty()) {
            return;
        }

        List<Map<String, Object>> reps = new ArrayList<>(realmRoles.size());
        for (String roleName : realmRoles) {
            Map<String, Object> rep = execute(() -> restClient.get()
                    .uri(properties.adminRoleByNameEndpoint(roleName))
                    .header(HttpHeaders.AUTHORIZATION, BEARER_SCHEME + adminToken)
                    .retrieve()
                    .body(MAP_TYPE));
            if (rep != null) {
                reps.add(rep);
            }
        }

        execute(() -> restClient.post()
                .uri(properties.adminUserRealmRoleMappingEndpoint(keycloakUserId))
                .header(HttpHeaders.AUTHORIZATION, BEARER_SCHEME + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(reps)
                .retrieve()
                .toBodilessEntity());
    }

    private static Map<String, Object> buildUserPayload(String username, String email,
                                                        String fullName, String password) {
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
        if (StringUtils.hasText(fullName)) {
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
        return payload;
    }

    private static String extractUserIdFromLocation(String location) {
        int idx = location.lastIndexOf('/');
        if (idx < 0 || idx == location.length() - 1) {
            throw BusinessException.badRequest("keycloak.invalid.user.location");
        }
        return location.substring(idx + 1);
    }

    private static <T> T execute(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (RestClientResponseException ex) {
            throw translate(ex);
        }
    }

    private static BusinessException translate(RestClientResponseException ex) {
        log.warn("Keycloak HTTP {} — body: {}", ex.getStatusCode().value(), ex.getResponseBodyAsString());
        return switch (ex.getStatusCode().value()) {
            case 400 -> BusinessException.badRequest("keycloak.bad.request", ex, ex.getResponseBodyAsString());
            case 401 -> BusinessException.unauthorized("keycloak.unauthorized", ex);
            case 403 -> BusinessException.forbidden("keycloak.forbidden", ex);
            case 404 -> BusinessException.notFound("keycloak.not.found", ex);
            case 409 -> BusinessException.conflict("keycloak.conflict", ex);
            default  -> BusinessException.badRequest("keycloak.request.failed", ex, ex.getMessage());
        };
    }
}
