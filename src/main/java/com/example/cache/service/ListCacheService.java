package com.example.cache.service;

import com.example.cache.entity.Transactions;
import com.example.cache.repository.TransactionRepository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListCacheService {

    private final TransactionRepository repository;
    private final CacheLockManager lockManager;

    public ListCacheService(TransactionRepository repository, CacheLockManager lockManager) {
        this.repository = repository;
        this.lockManager = lockManager;
    }

    @Cacheable(
        value = "transactionsByDomain",
        key = "#domain + ':' + #pageable.pageNumber + ':' + #pageable.pageSize"
    )
    public List<Transactions> getByDomainCached(String domain, Pageable pageable) {

        String key = domain + ":" + pageable.getPageNumber() + ":" + pageable.getPageSize();
        Object lock = lockManager.getLock(key);

        synchronized (lock) {
        	System.out.println("Synchronized ListCacheService");
            return repository
                    .findByDomainIgnoreCase(domain, pageable)
                    .getContent();
        }
    }
}

	