package com.supertiles.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.supertiles.ecommerce.dto.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
	
	boolean existsByMobile(long mobile);
    
	boolean existsByEmail(String email);
    
	Customer findByEmail(String email);

}
