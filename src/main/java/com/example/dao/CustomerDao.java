package com.example.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entity.Customer;

public interface CustomerDao extends JpaRepository<Customer, Long> {
	Customer findByApiKey(String apiKey);
}
