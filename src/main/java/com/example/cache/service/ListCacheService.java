package com.example.cache.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.example.cache.entity.Transactions;
import com.example.cache.repository.TransactionRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ListCacheService {

    private final StringRedisTemplate redis;
    private final TransactionRepository repository;
    private final ObjectMapper mapper;

    private static final long FRESH_TTL = 3600;     // 1 hour
    private static final long STALE_TTL = 7200;     // 2 hours
    private static final long LOCK_TTL_MS = 5000;   // 5 seconds

    public ListCacheService(StringRedisTemplate redis,
                            TransactionRepository repository,
                            ObjectMapper mapper) {
        this.redis = redis;
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<Transactions> getByDomainCached(String domain, Pageable pageable) {

        String key = "list:" + domain + ":" + pageable.getPageNumber() + ":" + pageable.getPageSize();
        String staleKey = key + ":stale";
        String lockKey = "lock:" + key;

        // 1. Fresh
        String freshJson = redis.opsForValue().get(key);
        if (freshJson != null) {
            return deserialize(freshJson);
        }

        // 2. Stale
        String staleJson = redis.opsForValue().get(staleKey);
        if (staleJson != null) {

            if (tryLock(lockKey, LOCK_TTL_MS)) {
                CompletableFuture.runAsync(() -> refresh(domain, pageable));
            }

            return deserialize(staleJson);
        }

        // 3. No cache → only one thread hits DB
        if (tryLock(lockKey, LOCK_TTL_MS)) {
            return refresh(domain, pageable);
        }

        // 4. Wait + retry
        try { Thread.sleep(50); } catch (Exception ignored) {}

        String retryJson = redis.opsForValue().get(key);
        if (retryJson != null) return deserialize(retryJson);

        return List.of();
    }

    private List<Transactions> refresh(String domain, Pageable pageable) {
        var page = repository.findByDomainIgnoreCase(domain, pageable);
        List<Transactions> list = page.getContent();

        try {
            String json = mapper.writeValueAsString(list);

            String key = "list:" + domain + ":" + pageable.getPageNumber() + ":" + pageable.getPageSize();
            String staleKey = key + ":stale";

            redis.opsForValue().set(key, json, FRESH_TTL, TimeUnit.SECONDS);
            redis.opsForValue().set(staleKey, json, STALE_TTL, TimeUnit.SECONDS);

            return list;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<Transactions> deserialize(String json) {
        try {
            return mapper.readValue(json, new TypeReference<List<Transactions>>() {});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean tryLock(String key, long ttlMs) {
        Boolean ok = redis.opsForValue().setIfAbsent(key, "1", ttlMs, TimeUnit.MILLISECONDS);
        return Boolean.TRUE.equals(ok);
    }
}
