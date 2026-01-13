package com.example.cache.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.cache.entity.Transactions;
import com.example.cache.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.cache.annotation.Cacheable;

@Service
public class TransactionService {

	
	private final ListCacheService listCacheService;
	private final CountService countService;
	private final TransactionRepository repository;

	public TransactionService(ListCacheService listCacheService, CountService countService,
			TransactionRepository repository) {
		this.listCacheService = listCacheService;
		this.countService = countService;
		this.repository = repository;
		
	}

	public List<Transactions> getAllTransactions() {
		return repository.findAll();
	}

	public Transactions getTransactionById(Long id) {
		return repository.findById(id).orElseThrow(() -> new RuntimeException("Transaction not found"));
	}

	public Page<Transactions> getByDomain(String domain, Pageable pageable) {
		List<Transactions> cachedList = listCacheService.getByDomainCached(domain, pageable);
		long total = countService.countByDomainCached(domain);
		return new PageImpl<>(cachedList, pageable, total);
	}

	public Page<Transactions> getByLocation(String location, Pageable pageable) {
		return repository.findByLocationIgnoreCase(location, pageable);
	}

	public Transactions saveTransaction(Transactions transaction) {
		return repository.save(transaction);
	}

	public void deleteTransaction(Long id) {
		repository.deleteById(id);
	}


}
