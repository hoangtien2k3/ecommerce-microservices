-- Gives back a coupon claimed by coupon-claim.lua whose MySQL row was not written.
-- KEYS[1] coupon:{id}:stock   KEYS[2] coupon:{id}:users   KEYS[3] coupon:{id}:pending
-- ARGV[1] user id   ARGV[2] '1' to also remove the user from the claimers
-- Returns 1 if the stock was given back, 0 if there was nothing to undo.
redis.call('HDEL', KEYS[3], ARGV[1])
-- SREM decides who undoes a claim, so two pods undoing the same one give back one coupon.
if ARGV[2] == '1' and redis.call('SREM', KEYS[2], ARGV[1]) == 0 then
  return 0
end
-- Stock that vanished meanwhile is rebuilt from MySQL, which already excludes this claim.
if redis.call('EXISTS', KEYS[1]) == 1 then
  redis.call('INCR', KEYS[1])
end
return 1
