-- First-come coupon demo data for promotion-service (database: promotionservice).
-- Not a Liquibase changeset on purpose: run it by hand only on demo environments.
--
--   docker compose exec -T mysql mysql -uroot -p"$MYSQL_ROOT_PASSWORD" promotionservice < deploy/seed/promotion-coupon-demo.sql
--   kubectl exec -i <mysql-pod> -- mysql -uroot -p"$MYSQL_ROOT_PASSWORD" promotionservice < deploy/seed/promotion-coupon-demo.sql
--
-- Safe to re-run: each coupon is inserted only if its slug does not exist yet.
-- Times are UTC (services store timestamps as UTC), relative to when the script runs.
-- chk_promotion_discount: FIXED coupons set discount_amount and leave discount_percentage NULL.
-- usage_limit maps to a primitive int, so it must not be NULL (0 is what the API stores for UNLIMITED).
-- apply_to is NOT NULL but coupon issuing/usage does not read promotion_apply, so no apply rows are added.

-- Open now, 100 coupons: claiming returns 201.
INSERT INTO promotion (name, slug, description, coupon_code, discount_type, discount_amount, discount_percentage,
                       is_active, start_date, end_date, usage_type, usage_limit, apply_to,
                       minimum_order_purchase_amount, issue_limit, issued_count, created_by, created_on)
SELECT '선착순 10,000원 할인', 'demo-first-come-open', 'Open now, 100 coupons', 'DEMOOPEN',
       'FIXED', 10000, NULL,
       TRUE, UTC_TIMESTAMP(6) - INTERVAL 1 HOUR, UTC_TIMESTAMP(6) + INTERVAL 7 DAY, 'UNLIMITED', 0, 'PRODUCT',
       NULL, 100, 0, 'seed', UTC_TIMESTAMP(6)
WHERE NOT EXISTS (SELECT 1 FROM promotion WHERE slug = 'demo-first-come-open');

-- Open now, 1,000 coupons: for the QA scale run (3,000+ users).
INSERT INTO promotion (name, slug, description, coupon_code, discount_type, discount_amount, discount_percentage,
                       is_active, start_date, end_date, usage_type, usage_limit, apply_to,
                       minimum_order_purchase_amount, issue_limit, issued_count, created_by, created_on)
SELECT '선착순 3,000원 할인 (1,000장)', 'demo-first-come-1000', 'Open now, 1,000 coupons', 'DEMO1000',
       'FIXED', 3000, NULL,
       TRUE, UTC_TIMESTAMP(6) - INTERVAL 1 HOUR, UTC_TIMESTAMP(6) + INTERVAL 7 DAY, 'UNLIMITED', 0, 'PRODUCT',
       NULL, 1000, 0, 'seed', UTC_TIMESTAMP(6)
WHERE NOT EXISTS (SELECT 1 FROM promotion WHERE slug = 'demo-first-come-1000');

-- Open now but no stock: claiming returns 409 promotion.coupon.sold.out.
INSERT INTO promotion (name, slug, description, coupon_code, discount_type, discount_amount, discount_percentage,
                       is_active, start_date, end_date, usage_type, usage_limit, apply_to,
                       minimum_order_purchase_amount, issue_limit, issued_count, created_by, created_on)
SELECT '선착순 5,000원 할인 (매진)', 'demo-first-come-sold-out', 'Open now, already sold out', 'DEMOSOLDOUT',
       'FIXED', 5000, NULL,
       TRUE, UTC_TIMESTAMP(6) - INTERVAL 1 HOUR, UTC_TIMESTAMP(6) + INTERVAL 7 DAY, 'UNLIMITED', 0, 'PRODUCT',
       NULL, 0, 0, 'seed', UTC_TIMESTAMP(6)
WHERE NOT EXISTS (SELECT 1 FROM promotion WHERE slug = 'demo-first-come-sold-out');

-- Opens in one day: claiming returns 400 promotion.coupon.not.open.
INSERT INTO promotion (name, slug, description, coupon_code, discount_type, discount_amount, discount_percentage,
                       is_active, start_date, end_date, usage_type, usage_limit, apply_to,
                       minimum_order_purchase_amount, issue_limit, issued_count, created_by, created_on)
SELECT '선착순 20,000원 할인 (오픈 예정)', 'demo-first-come-upcoming', 'Opens in one day', 'DEMOUPCOMING',
       'FIXED', 20000, NULL,
       TRUE, UTC_TIMESTAMP(6) + INTERVAL 1 DAY, UTC_TIMESTAMP(6) + INTERVAL 8 DAY, 'UNLIMITED', 0, 'PRODUCT',
       NULL, 50, 0, 'seed', UTC_TIMESTAMP(6)
WHERE NOT EXISTS (SELECT 1 FROM promotion WHERE slug = 'demo-first-come-upcoming');

SELECT id, slug, issue_limit, issued_count, start_date, end_date
FROM promotion
WHERE slug LIKE 'demo-first-come-%'
ORDER BY id;
