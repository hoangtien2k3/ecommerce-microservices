CREATE TABLE IF NOT EXISTS categories
(
    category_id        INT NOT NULL AUTO_INCREMENT,
    category_title     VARCHAR(255) NULL,
    image_url          VARCHAR(255) NULL,
    parent_category_id INT NULL,
    created_at         DATETIME(6) NULL,
    updated_at         DATETIME(6) NULL,
    PRIMARY KEY (category_id),
    CONSTRAINT fk_categories_parent FOREIGN KEY (parent_category_id) REFERENCES categories (category_id)
);

CREATE TABLE IF NOT EXISTS products
(
    product_id    INT NOT NULL AUTO_INCREMENT,
    product_title VARCHAR(255) NULL,
    image_url     VARCHAR(255) NULL,
    sku           VARCHAR(255) NULL,
    price_unit    DECIMAL(19, 2) NULL,
    quantity      INT NULL,
    category_id   INT NULL,
    created_at    DATETIME(6) NULL,
    updated_at    DATETIME(6) NULL,
    PRIMARY KEY (product_id),
    UNIQUE KEY uk_products_sku (sku),
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories (category_id)
);
