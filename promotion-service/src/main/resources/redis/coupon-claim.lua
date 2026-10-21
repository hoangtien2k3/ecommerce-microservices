-- Claims one first-come coupon for a user, atomically.
-- KEYS[1] coupon:{id}:stock   KEYS[2] coupon:{id}:users   KEYS[3] coupon:{id}:pending
-- ARGV[1] user id   ARGV[2] now (epoch millis)
-- Returns 0 claimed, 1 sold out, 2 already claimed, -1 not loaded (rebuild from MySQL first).
-- A claim stays in the pending hash until its MySQL row is written, so a claim whose caller
-- gave up (e.g. a timeout while this script still ran) can be found and given back later.
if redis.call('EXISTS', KEYS[1]) == 0 then
  return -1
end
if redis.call('SISMEMBER', KEYS[2], ARGV[1]) == 1 then
  return 2
end
if tonumber(redis.call('GET', KEYS[1])) <= 0 then
  return 1
end
redis.call('DECR', KEYS[1])
redis.call('SADD', KEYS[2], ARGV[1])
redis.call('HSET', KEYS[3], ARGV[1], ARGV[2])
return 0
