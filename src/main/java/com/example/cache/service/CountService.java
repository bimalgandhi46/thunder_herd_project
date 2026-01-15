package com.example.cache.service;

import com.example.cache.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

@Service
public class CountService {

    private final TransactionRepository repository;
    private final CacheLockManager lockManager;

    public CountService(TransactionRepository repository, CacheLockManager lockManager) {
        this.repository = repository;
        this.lockManager = lockManager;
    }

    @Cacheable(value = "countByDomain", key = "#domain")
    public long countByDomainCached(String domain) {

        Object lock = lockManager.getLock(domain);

        synchronized (lock) {
            return repository.countByDomainIgnoreCase(domain);
        }
    }
}
