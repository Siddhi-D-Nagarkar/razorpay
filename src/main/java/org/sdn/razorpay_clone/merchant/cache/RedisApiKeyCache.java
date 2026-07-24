package org.sdn.razorpay_clone.merchant.cache;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Optional;


@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RedisApiKeyCache implements ApiKeyCache {
    StringRedisTemplate redisTemplate;

    static Duration TTL = Duration.ofMinutes(5);
    static String PREFIX = "apikey:";
    ObjectMapper objectMapper;

    @Override
    public Optional<ApiKeyCacheEntry> get(String keyId) {
        try {
            String json = redisTemplate.opsForValue().get(PREFIX + keyId);
            if (json == null) {
                return Optional.empty();
            }

            return Optional.of(this.objectMapper.readValue(json, ApiKeyCacheEntry.class));
        } catch (Exception e) {
            log.warn("Failed to get API key cache entry for keyId: {}", keyId, e);
            return Optional.empty();
        }
    }

    @Override
    public void put(String keyId, ApiKeyCacheEntry entry) {
        try {
            String json = this.objectMapper.writeValueAsString(entry);
            redisTemplate.opsForValue().set(PREFIX + keyId, json, TTL);
        } catch (Exception e) {
            log.warn("Failed to put API key cache entry for keyId: {}", keyId, e);
        }
    }

    @Override
    public void evict(String keyId) {
        try {
            redisTemplate.delete(PREFIX + keyId);
        } catch (Exception e) {
            log.warn("Failed to evict API key cache entry for keyId: {}", keyId, e);
        }
    }
}
