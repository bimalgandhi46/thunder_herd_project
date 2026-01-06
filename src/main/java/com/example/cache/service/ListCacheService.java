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

	public ListCacheService(TransactionRepository repository) { 
		this.repository = repository; 
		}
	
	@Cacheable( value = "transactionsByDomain", key = "#domain + ':' + #pageable.pageNumber + ':' + #pageable.pageSize"
			)
	public List<Transactions> getByDomainCached(String domain, Pageable pageable) { // THIS LINE IS CRITICAL 
		return repository.findByDomainIgnoreCase(domain, pageable).getContent(); }
	}
	
	