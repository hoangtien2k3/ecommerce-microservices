CREATE TABLE IF NOT EXISTS tax_class
(
    id                 BIGINT NOT NULL AUTO_INCREMENT,
    `name`             VARCHAR(255) NOT NULL,
    created_by         VARCHAR(255) NULL,
    created_on         DATETIME(6) NULL,
    last_modified_by   VARCHAR(255) NULL,
    last_modified_on   DATETIME(6) NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS tax_rate
(
    id                    BIGINT NOT NULL AUTO_INCREMENT,
    rate                  DOUBLE NOT NULL,
    zip_code              VARCHAR(25) NULL,
    tax_class_id          BIGINT NOT NULL,
    state_or_province_id  BIGINT NULL,
    country_id            BIGINT NOT NULL,
    created_by            VARCHAR(255) NULL,
    created_on            DATETIME(6) NULL,
    last_modified_by      VARCHAR(255) NULL,
    last_modified_on      DATETIME(6) NULL,
    PRIMARY KEY (id),
    CONSTRAINT FKkud35ls1d40wpjb5htpp14yua FOREIGN KEY (tax_class_id) REFERENCES tax_class (id)
);
