package com.example.cache.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.cache.entity.Transactions;
import com.example.cache.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.cache.annotation.Cacheable;

;

@Service
public class TransactionService {



	private final TransactionRepository repository;
	private final CountService countService;

	public TransactionService(TransactionRepository repository, CountService countService) {
		this.repository = repository;
		this.countService = countService;
	}

	public List<Transactions> getAllTransactions() {
		return repository.findAll();
	}

	public Transactions getTransactionById(Long id) {
		return repository.findById(id).orElseThrow(() -> new RuntimeException("Transaction not found"));
	}

	@Cacheable(value = "transactionsByDomain", key = "#domain + ':' + #pageable.pageNumber + ':' + #pageable.pageSize")
	public List<Transactions> getByDomainCached(String domain, Pageable pageable) {
		return repository.findByDomainIgnoreCase(domain, pageable).getContent();
	}


	public Page<Transactions> getByDomain(String domain, Pageable pageable) {
		List<Transactions> cached = getByDomainCached(domain, pageable);
		long total = countService.countByDomainCached(domain);
		return new PageImpl<>(cached, pageable, total);
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
