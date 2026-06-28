package com.ecommerce.authservice.service;

import com.ecommerce.authservice.config.SsoProperties;
import com.ecommerce.commonlib.exception.BusinessException;
import com.ecommerce.commonlib.keycloak.KeycloakTokenResponse;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tiny in-memory store for the two short-lived secrets of the backend-mediated
 * SSO handshake:
 *
 * <ol>
 *   <li><b>state</b> — random value tying the {@code /login} redirect to its
 *       {@code /callback}, carrying the frontend page to bounce back to (CSRF guard).</li>
 *   <li><b>ticket</b> — single-use handle the frontend swaps for the real tokens at
 *       {@code /session}, so tokens never travel in a URL.</li>
 * </ol>
 *
 * <p>Both entries are tiny and live for seconds, so an in-process map is sufficient.
 * NOTE: this assumes a single auth-service replica; scale-out would need Redis (already
 * in the cluster) behind the same interface.</p>
 */
@Component
public class SsoSessionStore {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder().withoutPadding();

    private final SsoProperties properties;
    private final ConcurrentHashMap<String, Entry<String>> loginStates = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Entry<KeycloakTokenResponse>> tickets = new ConcurrentHashMap<>();

    public SsoSessionStore(SsoProperties properties) {
        this.properties = properties;
    }

    /** Mints a {@code state} bound to the frontend page we should return to. */
    public String createLoginState(String frontendRedirect) {
        purgeExpired();
        String state = randomToken();
        loginStates.put(state, new Entry<>(frontendRedirect, expiry(properties.getStateTtlSeconds())));
        return state;
    }

    /** Validates + consumes a {@code state}, returning its frontend redirect. */
    public String consumeLoginState(String state) {
        Entry<String> entry = state == null ? null : loginStates.remove(state);
        if (entry == null || entry.expired()) {
            throw BusinessException.unauthorized("sso.state.invalid");
        }
        return entry.value();
    }

    /** Stores freshly-minted tokens behind a single-use ticket. */
    public String storeTokens(KeycloakTokenResponse tokens) {
        purgeExpired();
        String ticket = randomToken();
        tickets.put(ticket, new Entry<>(tokens, expiry(properties.getTicketTtlSeconds())));
        return ticket;
    }

    /** Validates + consumes a ticket, returning the tokens exactly once. */
    public KeycloakTokenResponse consumeTokens(String ticket) {
        Entry<KeycloakTokenResponse> entry = ticket == null ? null : tickets.remove(ticket);
        if (entry == null || entry.expired()) {
            throw BusinessException.unauthorized("sso.ticket.invalid");
        }
        return entry.value();
    }

    private static String randomToken() {
        byte[] buf = new byte[32];
        RANDOM.nextBytes(buf);
        return URL_ENCODER.encodeToString(buf);
    }

    private static Instant expiry(long seconds) {
        return Instant.now().plusSeconds(seconds);
    }

    private void purgeExpired() {
        Instant now = Instant.now();
        loginStates.values().removeIf(e -> e.expiresAt().isBefore(now));
        tickets.values().removeIf(e -> e.expiresAt().isBefore(now));
    }

    private record Entry<T>(T value, Instant expiresAt) {
        boolean expired() {
            return expiresAt.isBefore(Instant.now());
        }
    }
}
