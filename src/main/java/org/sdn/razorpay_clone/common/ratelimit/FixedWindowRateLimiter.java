package org.sdn.razorpay_clone.common.ratelimit;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@ConditionalOnProperty(name = "app.rate-limit.method", havingValue = "fixed")
public class FixedWindowRateLimiter implements RateLimiter {
    StringRedisTemplate redisTemplate;

    @Override
    public RateLimitResult check(String key, int maxRequestAllowed, long windowSeconds) {
        String redisKey = "ratelimit:fixed:" + key;
        Long count = this.redisTemplate.opsForValue().increment(redisKey);

        if (count == null) {
            return RateLimitResult.allowed(maxRequestAllowed);
        }

        if (count == 1) {
            this.redisTemplate.expire(redisKey, Duration.ofSeconds(windowSeconds));
        }

        if (count > maxRequestAllowed) {
            Long ttl = this.redisTemplate.getExpire(redisKey);

            int retryAfter = (ttl != null && ttl > 0) ? ttl.intValue() : (int) windowSeconds;
            return RateLimitResult.denied(retryAfter);
        }
        return RateLimitResult.allowed(maxRequestAllowed - count.intValue());
    }
}
