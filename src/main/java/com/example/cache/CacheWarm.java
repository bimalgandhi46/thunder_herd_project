package com.example.cache;

import java.util.List;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;

import com.example.cache.service.TransactionService;

public class CacheWarm {
	/*
	private final TransactionService transactionService;

	public CacheWarm(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void warmCache() {
		List<String> domains = List.of("INTERNATIONAL", "PUBLIC", "RESTRAUNT", "EDUCATION", "MEDICAL", "INVESTMENTS",
				"RETAIL");
		System.out.println("Cache Warmpup");
		var pageable = PageRequest.of(0, 10);
		domains.forEach(domain -> transactionService.getByDomain(domain, pageable));
	}*/
}
