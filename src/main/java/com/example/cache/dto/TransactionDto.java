package com.example.cache.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
public record TransactionDto(
		Long id,
        @NotNull LocalDate date,
        @NotBlank String domain,
        @NotBlank String location,
        @NotNull @Min(0) Long value,
        @NotNull @Min(0) Integer transactionCount
		
		) {}
