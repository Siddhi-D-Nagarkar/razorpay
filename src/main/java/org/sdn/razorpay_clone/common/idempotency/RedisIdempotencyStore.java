package org.sdn.razorpay_clone.common.idempotency;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;


@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RedisIdempotencyStore implements IdempotencyStore {
    StringRedisTemplate stringRedisTemplate;
    static String PREFIX = "idempotency:";


    @Override
    public boolean setIfAbsent(String key, Duration ttl) {

        try {
            Boolean set = this.stringRedisTemplate.opsForValue().setIfAbsent(PREFIX + key, IN_PROCESS, ttl);
            return Boolean.TRUE.equals(set);
        } catch (Exception e) {
            log.error("Error while setting key in Redis: {}", e.getMessage(), e);
            return true;
        }

    }

    @Override
    public void store(String key, String value, Duration ttl) {
        try {
            this.stringRedisTemplate.opsForValue().set(PREFIX + key, value, ttl);
        } catch (Exception e) {
            log.error("Error while storing key in Redis: {}", e.getMessage(), e);
            log.info("Error while storing key in Redis: {}", key);
        }
    }

    @Override
    public Optional<String> get(String key) {
        try {
            return Optional.ofNullable(this.stringRedisTemplate.opsForValue().get(PREFIX + key));
        } catch (Exception e) {
            log.error("Error while getting key from Redis: {}", e.getMessage(), e);
            log.info("Error while getting key from Redis: {}", key);
            return Optional.empty();
        }
    }

    @Override
    public void delete(String key) {
        try {
            this.stringRedisTemplate.delete(PREFIX + key);
        } catch (DataAccessException e) {
            log.error("Error while deleting key from Redis: {}", e.getMessage(), e);
        }
    }
}
