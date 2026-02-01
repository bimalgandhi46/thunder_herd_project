package com.example.cache.service;

import org.springframework.stereotype.Service;

import com.example.cache.dto.PageResponse;
import com.example.cache.dto.TransactionDto;
import com.example.cache.entity.Transactions;
import com.example.cache.repository.TransactionRepository;

import java.time.LocalDate;
import java.util.List;

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
		return new TransactionDto(t.getId(), t.getDate(), t.getDomain(), t.getLocation(), t.getValue(),
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

	public PageResponse<TransactionDto> getAllTransactions(int page, int size) {
		Page<Transactions> result = repository.findAll(PageRequest.of(page, size));
		return toPageResponse(result);
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

	public PageResponse<TransactionDto> getByLocation(String location, int page, int size) {
		Page<Transactions> result = repository.findByLocationIgnoreCase(location, PageRequest.of(page, size));
		return toPageResponse(result);
	}

	public TransactionDto saveTransaction(TransactionDto dto) {
		Transactions entity = toEntity(dto);
		Transactions saved = repository.save(entity);
		return toDto(saved);
	}

	private PageResponse<TransactionDto> toPageResponse(Page<Transactions> page) {
		List<TransactionDto> dtoList = page.getContent().stream().map(this::toDto).toList();
		return new PageResponse<>(dtoList, page.getNumber(), page.getSize(), page.getTotalElements(),
				page.getTotalPages());
	}

	public void deleteTransaction(Long id) {
		repository.deleteById(id);
	}
}
