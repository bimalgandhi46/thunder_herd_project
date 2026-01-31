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
            key = "'v2:' + #domain + ':' + #pageable.pageNumber + ':' + #pageable.pageSize"  )
    public List<Transactions> getByDomainCached(String domain, Pageable pageable) 
    { 
    	return repository .findByDomainIgnoreCase(domain, pageable) .getContent();
    }
    }

