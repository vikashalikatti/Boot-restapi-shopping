package org.jsp.shopping.dto;

import org.springframework.stereotype.Component;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Data;

@Data
@Entity
@Component
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String name;
	private double price;
	private String description;
	private int stock;
	@Lob
	@Column(columnDefinition = "MEDIUMBLOB")
	private byte[] image;
	private boolean status;
}
