package com.ecommerce.authservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * One short-lived SSO handshake secret (a login {@code state} or a token {@code ticket}),
 * kept in the database so any auth-service replica can finish a login another one started.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sso_handshake")
public class SsoHandshake {

    public enum Kind { STATE, TICKET }

    @Id
    @Column(name = "handle", length = 64, nullable = false, updatable = false)
    private String handle;

    @Enumerated(EnumType.STRING)
    @Column(name = "kind", length = 10, nullable = false)
    private Kind kind;

    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
}
