package com.supertiles.ecommerce.dto;

import org.apache.tomcat.util.codec.binary.Base64;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Data;

@Entity
@Data
public class Item {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int id;
	String name;
	String brand;
	String size;
	String description;
	int quantity;
	double price;

	@Lob
	@Column(columnDefinition = "MEDIUMBLOB")
	byte[] picture;

	public String base64Image() {
		return Base64.encodeBase64String(picture);
	}
}
