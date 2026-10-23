CREATE TABLE IF NOT EXISTS rating
(
    id                 BIGINT NOT NULL AUTO_INCREMENT,
    created_by         VARCHAR(255) NULL,
    created_on         DATETIME(6) NULL,
    last_modified_by   VARCHAR(255) NULL,
    last_modified_on   DATETIME(6) NULL,
    content            VARCHAR(255) NULL,
    first_name         VARCHAR(255) NULL,
    last_name          VARCHAR(255) NULL,
    product_id         BIGINT NULL,
    rating_star        INT NOT NULL,
    product_name       VARCHAR(255) NULL,
    PRIMARY KEY (id)
);
