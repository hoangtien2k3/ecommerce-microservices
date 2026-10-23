ALTER TABLE promotion
    MODIFY discount_percentage BIGINT NULL,
    MODIFY discount_amount BIGINT NULL;

UPDATE promotion
SET discount_percentage = NULL
WHERE discount_type = 'FIXED';

UPDATE promotion
SET discount_amount = NULL
WHERE discount_type = 'PERCENTAGE';

ALTER TABLE promotion
    ADD CONSTRAINT chk_promotion_discount CHECK (
        (discount_type = 'FIXED' AND discount_amount IS NOT NULL AND discount_percentage IS NULL)
        OR (discount_type = 'PERCENTAGE' AND discount_percentage IS NOT NULL AND discount_amount IS NULL)
    );
