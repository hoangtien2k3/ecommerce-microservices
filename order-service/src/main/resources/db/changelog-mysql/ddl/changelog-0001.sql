CREATE TABLE IF NOT EXISTS carts
(
    cart_id    INT NOT NULL AUTO_INCREMENT,
    user_id    BIGINT NULL,
    created_at DATETIME(6) NULL,
    updated_at DATETIME(6) NULL,
    PRIMARY KEY (cart_id)
);

CREATE TABLE IF NOT EXISTS `orders`
(
    order_id   INT NOT NULL AUTO_INCREMENT,
    order_date DATETIME(6) NULL,
    order_desc VARCHAR(255) NULL,
    order_fee  DECIMAL(19, 2) NULL,
    product_id INT NULL,
    cart_id    INT NULL,
    created_at DATETIME(6) NULL,
    updated_at DATETIME(6) NULL,
    PRIMARY KEY (order_id),
    CONSTRAINT fk_orders_cart FOREIGN KEY (cart_id) REFERENCES carts (cart_id)
);

CREATE TABLE IF NOT EXISTS payments
(
    payment_id     INT NOT NULL AUTO_INCREMENT,
    order_id       INT NULL,
    user_id        BIGINT NULL,
    is_payed       BOOLEAN NULL,
    payment_status VARCHAR(255) NULL,
    created_at     DATETIME(6) NULL,
    updated_at     DATETIME(6) NULL,
    PRIMARY KEY (payment_id)
);
