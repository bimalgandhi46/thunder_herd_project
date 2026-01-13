package com.example.cache.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.cache.entity.Transactions;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
@Repository
public interface TransactionRepository extends JpaRepository<Transactions, Long>{
	Page<Transactions> findByDomainIgnoreCase(String domain, Pageable pageable);
    Page<Transactions> findByLocationIgnoreCase(String location, Pageable pageable);
	long countByDomainIgnoreCase(String domain);
	
}
