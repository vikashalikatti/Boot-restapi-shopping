package org.jsp.shopping.dto;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Entity
@Component
@Data
public class Customer {

	@Id
	@Email(message = "Invalid email format")
	@NotBlank(message = "Email is required")
	private String email;

	@NotBlank(message = "Name is required")
	private String name;

	@NotNull(message = "Mobile number is required")
	@Digits(integer = 10, fraction = 0, message = "Mobile number should have exactly 10 digits.")
	private long mobile;

	@NotBlank(message = "Password is required")
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", message = "Password must be at least 8 characters long and contain at least one lowercase letter, one uppercase letter, one digit, and one special character.")
	private String password;

	@NotNull(message = "Date of birth is required")
	private LocalDate dob;

	@NotBlank(message = "Gender is required")
	private String gender;

	@NotBlank(message = "Address is required")
	private String address;

	private String token;

	private boolean status;
//	@OneToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
//	ShoppingCart shoppingCart;
//	
//	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//	List<Wishlist> wishlists;
//
//	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//	List<ShoppingOrder> orders;
}