package com.example.cache.service;

import com.example.cache.dto.TransactionDto;
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

	private TransactionDto toDto(Transactions t) {
		return new TransactionDto(t.getId(), 
				t.getDate(), t.getDomain(), 
				t.getLocation(), 
				t.getValue(),
				t.getTransactionCount());
	}

	@Cacheable(
	        value = "transactionsByDomain",
	        key = "'v3:' + #domain + ':' + #pageable.pageNumber + ':' + #pageable.pageSize"
	)
	public List<TransactionDto> getByDomainCached(String domain, Pageable pageable) {

	    Object lock = lockManager.getLock(domain);

	    synchronized (lock) {
	        System.out.println("Synchronized ListCacheService");

	        return repository
	                .findByDomainIgnoreCase(domain, pageable)
	                .getContent()
	                .stream()
	                .map(this::toDto)
	                .toList();
	    }
}
}