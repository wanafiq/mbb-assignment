package com.example.assignment.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Component
public class DistributedLock {
    private static final long LOCK_TIMEOUT = 20;
    private static final int MAX_RETRIES = 10;
    private static final long RETRY_DELAY_MS = 500;

    private final RedisTemplate<String, String> redisTemplate;

    public boolean waitForLock(String lockKey, String lockValue) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            log.debug("Attempting to acquire lock: {} (attempt {}/{})", lockKey, attempt, MAX_RETRIES);

            if (acquireLock(lockKey, lockValue)) {
                log.debug("Lock acquired: {} on attempt {}", lockKey, attempt);
                return true;
            }

            if (attempt < MAX_RETRIES) {
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }
        
        log.error("Failed to acquire lock: {} after {} attempts", lockKey, MAX_RETRIES);
        return false;
    }

    public void releaseLock(String lockKey, String lockValue) {
        String currentValue = redisTemplate.opsForValue().get(lockKey);
        if (currentValue != null && currentValue.equals(lockValue)) {
            redisTemplate.delete(lockKey);
            log.debug("Lock released: {}", lockKey);
        } else {
            log.warn("Failed to release lock: {} - value mismatch", lockKey);
        }
    }

    private boolean acquireLock(String lockKey, String lockValue) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue()
                .setIfAbsent(lockKey, lockValue, LOCK_TIMEOUT, TimeUnit.SECONDS));
    }
}
