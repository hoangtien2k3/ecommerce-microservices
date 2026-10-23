ALTER TABLE `orders`
    ADD COLUMN promotion_id BIGINT NULL,
    ADD COLUMN discount_amount DECIMAL(19, 2) NULL;
