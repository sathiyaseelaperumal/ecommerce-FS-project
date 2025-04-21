package com.supertiles.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.supertiles.ecommerce.dto.Product;
import com.supertiles.ecommerce.dto.Seller;

public interface ProductRepository extends JpaRepository<Product, Integer> {

	List<Product> findBySeller(Seller seller);

	Product findByNameAndBrandAndPrice(String name, String brand, double price);

}
