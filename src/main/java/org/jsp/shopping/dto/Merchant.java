package org.jsp.shopping.dto;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
@Entity
@Validated
@Component
public class Merchant {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int id;
	@Email
	String email;
	@Digits(integer = 10, fraction = 0)
	long mobile;
}
