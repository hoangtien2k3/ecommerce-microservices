CREATE TABLE IF NOT EXISTS notifications
(
    id                BIGINT NOT NULL AUTO_INCREMENT,
    content           VARCHAR(255) NULL,
    recipient_id      VARCHAR(255) NULL,
    `read`            BOOLEAN NOT NULL,
    `timestamp`       DATETIME(6) NULL,
    notification_type VARCHAR(255) NULL,
    link              VARCHAR(255) NULL,
    PRIMARY KEY (id)
);
