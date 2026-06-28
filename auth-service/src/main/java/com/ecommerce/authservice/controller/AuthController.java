package com.ecommerce.authservice.controller;

import com.ecommerce.authservice.config.SsoProperties;
import com.ecommerce.authservice.dto.request.RefreshTokenRequest;
import com.ecommerce.authservice.dto.request.RegisterRequest;
import com.ecommerce.authservice.service.SsoSessionStore;
import com.ecommerce.authservice.service.UserService;
import com.ecommerce.commonlib.keycloak.KeycloakAuthClient;
import com.ecommerce.commonlib.keycloak.KeycloakClientProperties;
import com.ecommerce.commonlib.keycloak.KeycloakTokenResponse;
import com.ecommerce.commonlib.viewmodel.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final KeycloakAuthClient keycloakAuthClient;
    private final KeycloakClientProperties keycloakProperties;
    private final SsoProperties ssoProperties;
    private final SsoSessionStore ssoSessionStore;

    // ------------------------------------------------------------------
    // Self-service registration (creates the user in Keycloak via admin API)
    // ------------------------------------------------------------------

    @PostMapping("/signup")
    public ApiResponse<Void> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return ApiResponse.message("User " + request.getUsername() + " registered successfully");
    }

    // ------------------------------------------------------------------
    // SSO — Authorization Code flow, mediated by this backend
    // ------------------------------------------------------------------

    /**
     * Step 1. Browser hits this; we 302 to the Keycloak login page. The frontend
     * page to return to is remembered server-side against a random {@code state}.
     */
    @GetMapping("/login")
    public ResponseEntity<Void> ssoLogin(
            @RequestParam(value = "redirect_uri", required = false) String redirectUri) {
        String frontendRedirect = resolveFrontendRedirect(redirectUri);
        String state = ssoSessionStore.createLoginState(frontendRedirect);
        String authorizeUrl = UriComponentsBuilder.fromUriString(keycloakProperties.authorizationEndpoint())
                .queryParam("client_id", keycloakProperties.getClientId())
                .queryParam("response_type", "code")
                .queryParam("scope", ssoProperties.getScope())
                .queryParam("redirect_uri", ssoProperties.getBackendCallbackUrl())
                .queryParam("state", state)
                .build()
                .encode()
                .toUriString();
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(authorizeUrl)).build();
    }

    /**
     * Step 2. Keycloak redirects here with {@code code}. We swap it for tokens, stash
     * them behind a single-use ticket and bounce the browser back to the frontend.
     */
    @GetMapping("/callback")
    public ResponseEntity<Void> ssoCallback(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "error", required = false) String error) {

        // User denied consent or Keycloak returned an error → bounce back with the reason.
        if (StringUtils.hasText(error)) {
            return redirectTo(appendQuery(ssoProperties.getDefaultFrontendRedirect(), "error", error));
        }

        String frontendRedirect = ssoSessionStore.consumeLoginState(state);
        KeycloakTokenResponse tokens = keycloakAuthClient.exchangeAuthorizationCode(code, ssoProperties.getBackendCallbackUrl());
        String ticket = ssoSessionStore.storeTokens(tokens);

        return redirectTo(appendQuery(frontendRedirect, "ticket", ticket));
    }

    /**
     * Step 3. Frontend swaps the single-use ticket for the actual tokens (JSON).
     */
    @GetMapping("/session")
    public ApiResponse<KeycloakTokenResponse> ssoSession(@RequestParam("ticket") String ticket) {
        return ApiResponse.ok(ssoSessionStore.consumeTokens(ticket));
    }

    // ------------------------------------------------------------------
    // Token lifecycle (still used by the frontend interceptor / logout)
    // ------------------------------------------------------------------

    @PostMapping("/refresh")
    public ApiResponse<KeycloakTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.ok(keycloakAuthClient.refreshToken(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        keycloakAuthClient.logout(request.getRefreshToken());
        return ApiResponse.message("Logout successful");
    }

    // ------------------------------------------------------------------
    // Internals
    // ------------------------------------------------------------------

    /** Anti open-redirect: only bounce to a pre-registered frontend callback. */
    private String resolveFrontendRedirect(String requested) {
        if (StringUtils.hasText(requested) && ssoProperties.getAllowedRedirectUris().contains(requested)) {
            return requested;
        }
        return ssoProperties.getDefaultFrontendRedirect();
    }

    private static ResponseEntity<Void> redirectTo(String url) {
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(url)).build();
    }

    private static String appendQuery(String url, String key, String value) {
        return UriComponentsBuilder.fromUriString(url).queryParam(key, value).build().encode().toUriString();
    }
}
