package com.example.cache.service;

import org.springframework.stereotype.Service;

import com.example.cache.dto.TransactionDto;
import com.example.cache.entity.Transactions;
import com.example.cache.repository.TransactionRepository;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

	private TransactionDto toDto(Transactions t) {
		return new TransactionDto(
				t.getId(), 
				t.getDate(), 
				t.getDomain(), 
				t.getLocation(),
				t.getValue(),
				t.getTransactionCount());
	}

	private Transactions toEntity(TransactionDto dto) {
		Transactions t = new Transactions();
		t.setId(dto.id());
		t.setDate(dto.date());
		t.setDomain(dto.domain());
		t.setLocation(dto.location());
		t.setValue(dto.value());
		t.setTransactionCount(dto.transactionCount());
		return t;
	}

	public Page<TransactionDto> getAllTransactions(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Transactions> entityPage = repository.findAll(pageable);

		var dtoList = entityPage.getContent().stream().map(this::toDto).toList();

		return new PageImpl<>(dtoList, pageable, entityPage.getTotalElements());
	}

	public TransactionDto getTransactionById(Long id) {
		Transactions entity = repository.findById(id).orElseThrow(() -> new RuntimeException("Transaction not found"));
		return toDto(entity);
	}

	public Page<TransactionDto> getByDomain(String domain, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		var entityList = listCacheService.getByDomainCached(domain, pageable);
		var dtoList = entityList.stream().map(this::toDto).toList();
		long total = countService.countByDomainCached(domain);
		return new PageImpl<>(dtoList, pageable, total);
	}

	public Page<TransactionDto> getByLocation(String location, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);

		Page<Transactions> entityPage = repository.findByLocationIgnoreCase(location, pageable);

		var dtoList = entityPage.getContent().stream().map(this::toDto).toList();

		return new PageImpl<>(dtoList, pageable, entityPage.getTotalElements());
	}

	public TransactionDto saveTransaction(TransactionDto dto) {
		Transactions entity = toEntity(dto);
		Transactions saved = repository.save(entity);
		return toDto(saved);
	}

	public void deleteTransaction(Long id) {
		repository.deleteById(id);
	}
}
