package com.example.cache.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.cache.dto.PageResponse;
import com.example.cache.dto.TransactionDto;

import com.example.cache.service.TransactionService;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

	private final TransactionService service;

	public TransactionController(TransactionService service) {
		this.service = service;
	}

	@GetMapping
	public ResponseEntity<PageResponse<TransactionDto>> getAll(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		return ResponseEntity.ok(service.getAllTransactions(page, size));
	}

	@GetMapping("/{id}")
	public ResponseEntity<TransactionDto> getById(@PathVariable Long id) {
		return ResponseEntity.ok(service.getTransactionById(id));
	}

	@GetMapping(value = "/domain/{domain}/cache", produces = "application/json")
	public ResponseEntity<PageResponse<TransactionDto>> getByDomain(@PathVariable String domain, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		
		return ResponseEntity.ok(service.getByDomain(domain,page, size));
	}

	@GetMapping(value="/location/{location}/nocache",produces = "application/json")
	public ResponseEntity<PageResponse<TransactionDto>> getByLocation( @PathVariable String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

		return ResponseEntity.ok(service.getByLocation(location, page,size));
	}

	@PostMapping
	public ResponseEntity<TransactionDto> create(@RequestBody TransactionDto dto) {
		return ResponseEntity.ok(service.saveTransaction(dto));
	}
	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		service.deleteTransaction(id);
	}
}
