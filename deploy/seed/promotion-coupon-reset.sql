-- Resets the first-come demo coupons between load test runs (database: promotionservice).
-- Removes every claim and sets issued_count back to 0. The coupons themselves stay,
-- so their ids (the promotionId in /promotion/storefront/promotions/{id}/issue) do not change.
--
--   kubectl exec -i <mysql-pod> -- mysql -uroot -p"$MYSQL_ROOT_PASSWORD" promotionservice < deploy/seed/promotion-coupon-reset.sql
--
-- With the Redis coupon store (promotion.issue.store=redis, the default) run
-- promotion-coupon-reset-redis.sh right after this, or the coupons stay sold out in Redis.
--
-- To reset a single coupon, replace the LIKE with: slug = 'demo-first-come-1000'

START TRANSACTION;

DELETE pi FROM promotion_issue pi
JOIN promotion p ON p.id = pi.promotion_id
WHERE p.slug LIKE 'demo-first-come-%';

UPDATE promotion SET issued_count = 0 WHERE slug LIKE 'demo-first-come-%';

COMMIT;

SELECT p.id, p.slug, p.issue_limit, p.issued_count,
       (SELECT COUNT(*) FROM promotion_issue pi WHERE pi.promotion_id = p.id) AS claims
FROM promotion p
WHERE p.slug LIKE 'demo-first-come-%'
ORDER BY p.id;
