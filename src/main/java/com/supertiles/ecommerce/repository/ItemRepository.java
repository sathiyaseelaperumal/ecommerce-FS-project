package com.supertiles.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.supertiles.ecommerce.dto.Item;

public interface ItemRepository extends JpaRepository<Item, Integer> {

}
