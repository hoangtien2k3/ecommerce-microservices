package com.ecommerce.authservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Backend-mediated SSO settings, bound from {@code keycloak.sso.*}.
 *
 * <p>The browser logs in at Keycloak, Keycloak redirects to {@link #backendCallbackUrl}
 * (this service), we exchange the code for tokens and then bounce the browser back to
 * one of the {@link #allowedRedirectUris} carrying a single-use {@code ticket} that the
 * frontend swaps for the tokens via {@code GET /api/v1/auth/session}.</p>
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "keycloak.sso")
public class SsoProperties {

    /**
     * Browser-facing URL Keycloak redirects to after login. MUST be registered as a
     * Valid Redirect URI on the Keycloak client and be reachable by the browser.
     */
    private String backendCallbackUrl = "http://api.ecommerce.local/api/v1/auth/callback";

    /** Frontend callback pages allowed to receive the login ticket (anti open-redirect). */
    private List<String> allowedRedirectUris = List.of(
            "http://ecommerce.local/auth/callback",
            "http://admin.ecommerce.local/auth/callback",
            "http://localhost:3000/auth/callback",
            "http://localhost:3001/auth/callback"
    );

    /** Used when the caller supplies no (or a disallowed) {@code redirect_uri}. */
    private String defaultFrontendRedirect = "http://ecommerce.local/auth/callback";

    /** OIDC scopes requested during the authorization redirect. */
    private String scope = "openid profile email";

    /** How long an in-flight login {@code state} stays valid. */
    private long stateTtlSeconds = 300;

    /** How long a post-login {@code ticket} stays valid before the frontend swaps it. */
    private long ticketTtlSeconds = 60;
}
