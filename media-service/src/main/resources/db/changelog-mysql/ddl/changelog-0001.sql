CREATE TABLE IF NOT EXISTS media
(
    id         BIGINT NOT NULL AUTO_INCREMENT,
    caption    VARCHAR(255) NULL,
    file_name  VARCHAR(255) NULL,
    file_path  TEXT NULL,
    media_type VARCHAR(128) NULL,
    PRIMARY KEY (id)
);
