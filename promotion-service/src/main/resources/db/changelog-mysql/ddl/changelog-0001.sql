CREATE TABLE IF NOT EXISTS promotion
(
    id                             BIGINT NOT NULL AUTO_INCREMENT,
    `name`                         VARCHAR(255) NOT NULL,
    slug                           VARCHAR(255) NOT NULL,
    description                    VARCHAR(255) NULL,
    coupon_code                    VARCHAR(255) NULL,
    discount_percentage            BIGINT NOT NULL,
    discount_amount                BIGINT NOT NULL,
    is_active                      BOOLEAN NOT NULL,
    start_date                     DATETIME(6) NULL,
    end_date                       DATETIME(6) NULL,
    created_by                     VARCHAR(255) NULL,
    created_on                     DATETIME(6) NULL,
    last_modified_by               VARCHAR(255) NULL,
    last_modified_on               DATETIME(6) NULL,
    discount_type                  VARCHAR(25) NOT NULL,
    usage_limit                    INT NULL,
    usage_count                    INT NOT NULL DEFAULT 0,
    usage_type                     VARCHAR(25) NOT NULL DEFAULT 'UNLIMITED',
    apply_to                       VARCHAR(25) NOT NULL,
    minimum_order_purchase_amount  BIGINT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS promotion_usage
(
    id                 INT NOT NULL AUTO_INCREMENT,
    promotion_id       BIGINT NOT NULL,
    user_id            VARCHAR(255) NOT NULL,
    order_id           BIGINT NOT NULL,
    product_id         BIGINT NOT NULL,
    created_by         VARCHAR(255) NULL,
    created_on         DATETIME(6) NULL,
    last_modified_by   VARCHAR(255) NULL,
    last_modified_on   DATETIME(6) NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_promotion_usage_promotion FOREIGN KEY (promotion_id) REFERENCES promotion (id)
);

CREATE TABLE IF NOT EXISTS promotion_apply
(
    id                 INT NOT NULL AUTO_INCREMENT,
    promotion_id       BIGINT NOT NULL,
    brand_id           BIGINT NULL,
    category_id        BIGINT NULL,
    product_id         BIGINT NULL,
    created_by         VARCHAR(255) NULL,
    created_on         DATETIME(6) NULL,
    last_modified_by   VARCHAR(255) NULL,
    last_modified_on   DATETIME(6) NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_promotion_apply_promotion FOREIGN KEY (promotion_id) REFERENCES promotion (id)
);
