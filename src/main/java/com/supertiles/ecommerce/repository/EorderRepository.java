package com.supertiles.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.supertiles.ecommerce.dto.Eorder;

public interface EorderRepository extends JpaRepository<Eorder, Integer> {

	Eorder findByOrderId(String orderId);

}
