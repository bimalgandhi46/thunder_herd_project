package com.example.cache.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.cache.entity.Transactions;
import com.example.cache.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
	
	private final TransactionService service;

	    public TransactionController(TransactionService service) {
	        this.service = service;
	    }

	    @GetMapping
	    public List<Transactions> getAll() {
	        return service.getAllTransactions();
	    }

	    @GetMapping("/{id}")
	    public Transactions getById(@PathVariable Long id) {
	        return service.getTransactionById(id);
	    }

	    @GetMapping("/domain/{domain}")
	    public Page<Transactions> getByDomain(@PathVariable String domain,Pageable pageable) {
	        return service.getByDomain(domain,pageable);
	    }

	    @GetMapping("/location/{location}")
	    public Page<Transactions> getByLocation(@PathVariable String location,Pageable pageable) {
	        return service.getByLocation(location,pageable);
	    }

	    @PostMapping
	    public Transactions create(@RequestBody Transactions transaction) {
	        return service.saveTransaction(transaction);
	    }

	    @DeleteMapping("/{id}")
	    public void delete(@PathVariable Long id) {
	        service.deleteTransaction(id);
	    }
	}


