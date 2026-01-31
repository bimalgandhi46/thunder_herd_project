package com.example.cache.service;

import com.example.cache.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

@Service
public class CountService {
	private final TransactionRepository repository;

	public CountService(TransactionRepository repository) {
		this.repository = repository;
	}

	@Cacheable(value = "countByDomain", key = "#domain")
	public long countByDomainCached(String domain) {
		return repository.countByDomainIgnoreCase(domain);
	}
}
