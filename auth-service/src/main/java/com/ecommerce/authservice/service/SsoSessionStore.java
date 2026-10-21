package com.ecommerce.authservice.service;

import com.ecommerce.authservice.config.SsoProperties;
import com.ecommerce.authservice.entity.SsoHandshake;
import com.ecommerce.authservice.repository.SsoHandshakeRepository;
import com.ecommerce.commonlib.exception.BusinessException;
import com.ecommerce.commonlib.keycloak.KeycloakTokenResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

/**
 * Store for the two short-lived secrets of the backend-mediated SSO handshake:
 *
 * <ol>
 *   <li><b>state</b> — random value tying the {@code /login} redirect to its
 *       {@code /callback}, carrying the frontend page to bounce back to (CSRF guard).</li>
 *   <li><b>ticket</b> — single-use handle the frontend swaps for the real tokens at
 *       {@code /session}, so tokens never travel in a URL.</li>
 * </ol>
 *
 * <p>Entries live in the auth-service database rather than in memory, so {@code /login},
 * {@code /callback} and {@code /session} may each land on a different replica (or cloud).
 * Consuming takes a row lock and deletes the row, so each handle works exactly once.</p>
 */
@Component
public class SsoSessionStore {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder().withoutPadding();

    private final SsoProperties properties;
    private final SsoHandshakeRepository repository;
    private final ObjectMapper objectMapper;

    public SsoSessionStore(SsoProperties properties, SsoHandshakeRepository repository, ObjectMapper objectMapper) {
        this.properties = properties;
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    /** Mints a {@code state} bound to the frontend page we should return to. */
    @Transactional
    public String createLoginState(String frontendRedirect) {
        return create(SsoHandshake.Kind.STATE, frontendRedirect, properties.getStateTtlSeconds());
    }

    /** Validates + consumes a {@code state}, returning its frontend redirect. */
    @Transactional
    public String consumeLoginState(String state) {
        return consume(SsoHandshake.Kind.STATE, state, "sso.state.invalid");
    }

    /** Stores freshly-minted tokens behind a single-use ticket. */
    @Transactional
    public String storeTokens(KeycloakTokenResponse tokens) {
        return create(SsoHandshake.Kind.TICKET, toJson(tokens), properties.getTicketTtlSeconds());
    }

    /** Validates + consumes a ticket, returning the tokens exactly once. */
    @Transactional
    public KeycloakTokenResponse consumeTokens(String ticket) {
        return fromJson(consume(SsoHandshake.Kind.TICKET, ticket, "sso.ticket.invalid"));
    }

    private String create(SsoHandshake.Kind kind, String payload, long ttlSeconds) {
        Instant now = Instant.now();
        repository.deleteExpired(now);
        String handle = randomToken();
        repository.save(new SsoHandshake(handle, kind, payload, now.plusSeconds(ttlSeconds)));
        return handle;
    }

    private String consume(SsoHandshake.Kind kind, String handle, String invalidKey) {
        SsoHandshake entry = handle == null ? null : repository.findByHandleAndKind(handle, kind).orElse(null);
        if (entry == null) {
            throw BusinessException.unauthorized(invalidKey);
        }
        repository.delete(entry);
        if (entry.getExpiresAt().isBefore(Instant.now())) {
            throw BusinessException.unauthorized(invalidKey);
        }
        return entry.getPayload();
    }

    private String toJson(KeycloakTokenResponse tokens) {
        try {
            return objectMapper.writeValueAsString(tokens);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not serialize SSO tokens", e);
        }
    }

    private KeycloakTokenResponse fromJson(String payload) {
        try {
            return objectMapper.readValue(payload, KeycloakTokenResponse.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not read SSO tokens", e);
        }
    }

    private static String randomToken() {
        byte[] buf = new byte[32];
        RANDOM.nextBytes(buf);
        return URL_ENCODER.encodeToString(buf);
    }
}
