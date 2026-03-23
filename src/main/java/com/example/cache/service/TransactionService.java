package com.example.cache.service;

import org.springframework.stereotype.Service;

import com.example.cache.dto.PageResponse;
import com.example.cache.dto.TransactionDto;
import com.example.cache.entity.Transactions;
import com.example.cache.repository.TransactionRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TransactionService {
	private static final Logger log = LoggerFactory.getLogger(TransactionService.class);
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

	public PageResponse<TransactionDto> getByDomain(String domain, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		// USE CACHED 
		List<Transactions> list = listCacheService.getByDomainCached(domain, pageable);
		
		// USE CACHED COUNT 
		long total = countService.countByDomainCached(domain);
		
		Page<Transactions> pageResult = new PageImpl<>(list, pageable, total);
		return toPageResponse(pageResult);
	}
	@CircuitBreaker(name = "locationQuery", fallbackMethod = "locationFallback")
    @RateLimiter(name = "locationRateLimiter", fallbackMethod = "locationRateLimited")
	public PageResponse<TransactionDto> getByLocation(String location, int page, int size) {
		Page<Transactions> result = repository.findByLocationIgnoreCase(location, PageRequest.of(page, size));
		return toPageResponse(result);
	}

	public TransactionDto saveTransaction(TransactionDto dto) {
		Transactions entity = toEntity(dto);
		Transactions saved = repository.save(entity);
		return toDto(saved);
	}
    // Circuit breaker fallback
    private PageResponse<TransactionDto> locationFallback(String location, int page, int size, Throwable ex) {
        log.warn("locationQuery circuit open or failure for location={}, reason={}", location, ex.toString());
        return new PageResponse<>(List.of(), page, size, 0, 0);
    }

    // Rate limiter fallback
    private PageResponse<TransactionDto> locationRateLimited(String location, int page, int size, Throwable ex) {
        log.warn("locationRateLimiter triggered for location={}, reason={}", location, ex.toString());
        return new PageResponse<>(List.of(), page, size, 0, 0);
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
