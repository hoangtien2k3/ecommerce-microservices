package com.ecommerce.authservice.service;

import com.ecommerce.authservice.config.SsoProperties;
import com.ecommerce.authservice.entity.SsoHandshake;
import com.ecommerce.authservice.repository.SsoHandshakeRepository;
import com.ecommerce.commonlib.exception.BusinessException;
import com.ecommerce.commonlib.keycloak.KeycloakTokenResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SsoSessionStoreTest {

    @Mock
    private SsoHandshakeRepository repository;

    private final Map<String, SsoHandshake> rows = new HashMap<>();
    private SsoProperties properties;
    private SsoSessionStore store;

    @BeforeEach
    void setUp() {
        // Back the mocked repository with a map so the store's DB round trip is exercised.
        when(repository.save(any(SsoHandshake.class))).thenAnswer(invocation -> {
            SsoHandshake row = invocation.getArgument(0);
            rows.put(row.getHandle(), row);
            return row;
        });
        when(repository.findByHandleAndKind(any(), any())).thenAnswer(invocation -> Optional
                .ofNullable(rows.get(invocation.<String>getArgument(0)))
                .filter(row -> row.getKind() == invocation.getArgument(1)));
        doAnswer(invocation -> rows.remove(invocation.<SsoHandshake>getArgument(0).getHandle()))
                .when(repository).delete(any(SsoHandshake.class));

        properties = new SsoProperties();
        store = new SsoSessionStore(properties, repository, new ObjectMapper());
    }

    @Test
    void loginStateRoundTripsOnce() {
        String state = store.createLoginState("http://localhost:3000/auth/callback");

        assertEquals("http://localhost:3000/auth/callback", store.consumeLoginState(state));
        assertThrows(BusinessException.class, () -> store.consumeLoginState(state));
    }

    @Test
    void ticketReturnsTokensOnce() {
        KeycloakTokenResponse tokens = new KeycloakTokenResponse("access", "refresh", "Bearer", 300L, 1800L, "openid");
        String ticket = store.storeTokens(tokens);

        assertEquals(tokens, store.consumeTokens(ticket));
        assertThrows(BusinessException.class, () -> store.consumeTokens(ticket));
    }

    @Test
    void stateCannotBeUsedAsTicket() {
        String state = store.createLoginState("http://localhost:3000/auth/callback");

        assertThrows(BusinessException.class, () -> store.consumeTokens(state));
    }

    @Test
    void expiredStateIsRejected() {
        String state = store.createLoginState("http://localhost:3000/auth/callback");
        rows.get(state).setExpiresAt(Instant.now().minusSeconds(1));

        assertThrows(BusinessException.class, () -> store.consumeLoginState(state));
    }

    @Test
    void unknownOrMissingHandleIsRejected() {
        assertThrows(BusinessException.class, () -> store.consumeTokens("bogus"));
        assertThrows(BusinessException.class, () -> store.consumeLoginState(null));
    }
}
