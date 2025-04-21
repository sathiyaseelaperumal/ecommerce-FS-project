package com.supertiles.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.supertiles.ecommerce.dto.Seller;

public interface SellerRepository extends JpaRepository<Seller, Integer> {
	boolean existsByMobile(long mobile);

	boolean existsByEmail(String email);

	Seller findByEmail(String email);

}
