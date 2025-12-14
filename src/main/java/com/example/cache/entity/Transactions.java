package com.example.cache.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDate;

@Entity
public class Transactions {
	  @Id
	    private Long id;
	    private LocalDate date;
	    private String domain;
	    private String location;
	    private Long value;
	    private Integer transactionCount;

	    // Getters and setters
	    public Long getId() { return id; }
	    public void setId(Long id) { this.id = id; }

	    public LocalDate getDate() { return date; }
	    public void setDate(LocalDate date) { this.date = date; }

	    public String getDomain() { return domain; }
	    public void setDomain(String domain) { this.domain = domain; }

	    public String getLocation() { return location; }
	    public void setLocation(String location) { this.location = location; }

	    public Long getValue() { return value; }
	    public void setValue(Long value) { this.value = value; }

	    public Integer getTransactionCount() { return transactionCount; }
	    public void setTransactionCount(Integer transactionCount) { this.transactionCount = transactionCount; }
	
}
