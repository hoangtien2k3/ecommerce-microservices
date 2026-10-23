ALTER TABLE promotion_issue
    ADD COLUMN status VARCHAR(25) NOT NULL DEFAULT 'ISSUED',
    ADD COLUMN used_at DATETIME(6) NULL,
    ADD COLUMN order_id BIGINT NULL;

ALTER TABLE promotion_usage
    MODIFY product_id BIGINT NULL;
