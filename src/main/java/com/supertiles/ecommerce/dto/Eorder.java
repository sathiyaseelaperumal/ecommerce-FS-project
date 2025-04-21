package com.supertiles.ecommerce.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Data;

@Entity
@Data
public class Eorder {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	int id;
	String orderId;
	String paymentId;
	@ManyToMany
	List<Item> items;
	double price;
	@CreationTimestamp
	LocalDateTime timeStamp;
	boolean status;

}
