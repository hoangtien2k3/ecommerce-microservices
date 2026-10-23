CREATE TABLE IF NOT EXISTS sso_handshake
(
    handle     VARCHAR(64) NOT NULL,
    kind       VARCHAR(10) NOT NULL,
    payload    TEXT NULL,
    expires_at DATETIME(6) NOT NULL,
    PRIMARY KEY (handle),
    INDEX idx_sso_handshake_expires_at (expires_at)
);
