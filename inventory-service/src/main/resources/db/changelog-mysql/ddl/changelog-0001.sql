CREATE TABLE IF NOT EXISTS inventory
(
    id            BIGINT NOT NULL AUTO_INCREMENT,
    `product_name` VARCHAR(255) NULL,
    quantity      INT NULL,
    PRIMARY KEY (id)
);
