package com.example.cache.dto;

import java.time.LocalDate;

public record TransactionDto(
		Long id,
        LocalDate date,
        String domain,
        String location,
        Long value,
        Integer transactionCount
		
		) {}
