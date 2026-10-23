CREATE TABLE IF NOT EXISTS order_items
(
    product_id       INT NOT NULL,
    order_id         INT NOT NULL,
    ordered_quantity INT NULL,
    created_at       DATETIME(6) NULL,
    updated_at       DATETIME(6) NULL,
    PRIMARY KEY (product_id, order_id)
);
