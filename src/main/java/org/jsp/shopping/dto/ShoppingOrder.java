package org.jsp.shopping.dto;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
@Component
public class ShoppingOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int id;
	private String paymentMode;
	private String address;
	private double totalPrice;
	private LocalDate deliveryDate;
	private String orderId;
	private String transactionId;
	private String currency;
	private String payment_key;
	private String company_name;
	private String status;

	@OneToMany
	private List<Item> items;

}
