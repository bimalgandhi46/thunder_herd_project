package com.example.cache.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.example.cache.repository.TransactionRepository;

@Service
public class CountService {

    private final StringRedisTemplate redis;
    private final TransactionRepository repository;

    private static final long FRESH_TTL = 3600;
    private static final long STALE_TTL = 7200;
    private static final long LOCK_TTL_MS = 5000;

    public CountService(StringRedisTemplate redis,
                        TransactionRepository repository) {
        this.redis = redis;
        this.repository = repository;
    }

    public long countByDomainCached(String domain) {

        String key = "count:" + domain;
        String staleKey = key + ":stale";
        String lockKey = "lock:" + key;

        // 1. Fresh
        String fresh = redis.opsForValue().get(key);
        if (fresh != null) return Long.parseLong(fresh);

        // 2. Stale
        String stale = redis.opsForValue().get(staleKey);
        if (stale != null) {

            if (tryLock(lockKey, LOCK_TTL_MS)) {
                CompletableFuture.runAsync(() -> refresh(domain));
            }

            return Long.parseLong(stale);
        }

        // 3. No cache → only one thread hits DB
        if (tryLock(lockKey, LOCK_TTL_MS)) {
            return refresh(domain);
        }

        // 4. Wait + retry
        try { Thread.sleep(50); } catch (Exception ignored) {}

        String retry = redis.opsForValue().get(key);
        if (retry != null) return Long.parseLong(retry);

        return 0;
    }

    private long refresh(String domain) {
        long count = repository.countByDomainIgnoreCase(domain);

        String key = "count:" + domain;
        String staleKey = key + ":stale";

        redis.opsForValue().set(key, String.valueOf(count), FRESH_TTL, TimeUnit.SECONDS);
        redis.opsForValue().set(staleKey, String.valueOf(count), STALE_TTL, TimeUnit.SECONDS);

        return count;
    }

    private boolean tryLock(String key, long ttlMs) {
        Boolean ok = redis.opsForValue().setIfAbsent(key, "1", ttlMs, TimeUnit.MILLISECONDS);
        return Boolean.TRUE.equals(ok);
    }
}
