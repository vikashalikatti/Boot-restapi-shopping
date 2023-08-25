package org.jsp.shopping.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Entity
@Component
@Valid
public class Merchant {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@NotBlank(message = "Name must not be blank.")
	private String name;

	@Email(message = "Please provide a valid email address.")
	@NotNull(message = "email is required")
	private String email;

	@Digits(integer = 10, fraction = 0, message = "Mobile number should have exactly 10 digits.")
	@NotNull(message = "Mobile Number is required")
	private long mobile;

	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", message = "Password must be at least 8 characters long and contain at least one lowercase letter, one uppercase letter, one digit, and one special character.")
	@NotNull(message = "Password is required")
	private String password;

	@NotNull(message = "Date of birth must not be null.")
	private LocalDate dob;

	@NotBlank(message = "Gender must not be blank.")
	private String gender;

	@NotBlank(message = "Address must not be blank.")
	private String address;

	private int otp;

	private boolean status;

	@NotBlank(message = "Role is Required")
	private String role;

	@Lob
	@Column(columnDefinition = "MEDIUMBLOB")
	private byte[] picture;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	List<Product> products;

	private LocalDateTime otpGeneratedTime;

	
}
