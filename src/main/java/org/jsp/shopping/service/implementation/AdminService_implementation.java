package org.jsp.shopping.service.implementation;

import java.util.Date;
import java.util.List;

import org.jsp.shopping.Repository.Admin_Repository;
import org.jsp.shopping.Repository.CustomerRepository;
import org.jsp.shopping.Repository.MerchantRepository;
import org.jsp.shopping.Repository.PaymentRepository;
import org.jsp.shopping.Repository.ProductRepository;
import org.jsp.shopping.dto.Admin;
import org.jsp.shopping.dto.Customer;
import org.jsp.shopping.dto.Merchant;
import org.jsp.shopping.dto.Payment;
import org.jsp.shopping.dto.Product;
import org.jsp.shopping.helper.JwtUtil;
import org.jsp.shopping.helper.ResponseStructure;
import org.jsp.shopping.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;

@Service
public class AdminService_implementation implements AdminService {

	@Autowired
	private Admin_Repository admin_Repository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private MerchantRepository merchantRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private BCryptPasswordEncoder encoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	JwtUtil jwtUtil;

	@Override
	public ResponseEntity<ResponseStructure<Admin>> createAdmin(Admin admin) {
		ResponseStructure<Admin> structure = new ResponseStructure<>();
		int existingEntries = admin_Repository.countByUsernameAndPassword(admin.getUsername(), admin.getPassword());
		if (existingEntries == 0) {
			admin.setRole("admin");
			admin.setPassword(encoder.encode(admin.getPassword()));
			admin_Repository.save(admin);
			structure.setData(admin);
			structure.setMessage("Account Create for Admin");
			structure.setStatus(HttpStatus.CREATED.value());
			return new ResponseEntity<>(structure, HttpStatus.CREATED);
		} else {
			structure.setData(null);
			structure.setMessage("Admin Cannot More than one");
			structure.setStatus(HttpStatus.ALREADY_REPORTED.value());
			return new ResponseEntity<>(structure, HttpStatus.ALREADY_REPORTED);
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<Admin>> login(String username, String password) {
		ResponseStructure<Admin> structure = new ResponseStructure<>();
		Admin admin = admin_Repository.findByUsername(username);

		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

		Authentication authentication = authenticationManager.authenticate(authToken);

		SecurityContextHolder.getContext().setAuthentication(authentication);

		UserDetails userDetails = (UserDetails) authentication.getPrincipal();

		if (userDetails != null) {
			long expirationMillis = System.currentTimeMillis() + 3600000; // 1 hour in milliseconds
			Date expirationDate = new Date(expirationMillis);

			String token = jwtUtil.generateToken_for_admin(userDetails, admin.getRole(), expirationDate);
			structure.setData2(token);
			structure.setMessage("Login Success");
			structure.setStatus(HttpStatus.CREATED.value());
		}
		return new ResponseEntity<>(structure, HttpStatus.CREATED);
	}

//	@Override
//	public ResponseEntity<ResponseStructure<Admin>> login(String username, String password, HttpSession session) {
//		ResponseStructure<Admin> structure = new ResponseStructure<>();
//		Admin admin = admin_Repository.findByUsername(username);
//		if (admin == null) {
//			structure.setData(null);
//			structure.setMessage("Incorrect username");
//			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
//			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
//		} else {
////			if (admin.getPassword().equals(password)) {
//			if(encoder.matches(password, admin.getPassword())) {
//				session.setAttribute("admin", admin);
//
//				structure.setData(admin);
//				structure.setMessage("Login Success");
//				structure.setStatus(HttpStatus.CREATED.value());
//				return new ResponseEntity<>(structure, HttpStatus.CREATED);
//			} else {
//				structure.setData(null);
//				structure.setMessage("Incorrect Password");
//				structure.setStatus(HttpStatus.BAD_REQUEST.value());
//				return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);
//			}
//		}
//	}

	@Override
	public ResponseEntity<ResponseStructure<List<Product>>> viewAllProducts(String token) {
		ResponseStructure<List<Product>> structure = new ResponseStructure<>();

		if (jwtUtil.isValidToken(token)) {
			structure.setData(null);
			structure.setMessage("UNAUTHORIZED");
			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
		} else {
			List<Product> products = productRepository.findAll();
			if (products.isEmpty()) {
				structure.setMessage("No Products Found");
				structure.setData(null);
				structure.setStatus(HttpStatus.NOT_FOUND.value());
				return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
			} else {
				structure.setMessage("Products Found");
				structure.setData(products);
				structure.setStatus(HttpStatus.OK.value());
				return new ResponseEntity<>(structure, HttpStatus.OK);
			}
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<List<Product>>> changeStatus(int id, String token) {
		ResponseStructure<List<Product>> structure = new ResponseStructure<>();

		if (jwtUtil.isValidToken(token)) {
			structure.setData(null);
			structure.setMessage("UNAUTHORIZED");
			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
		} else {
			Product product = productRepository.findById(id).orElse(null);
			if (product.isStatus()) {
				product.setStatus(false);
			} else {
				product.setStatus(true);
			}
			productRepository.save(product);
			List<Product> products = productRepository.findAll();
			if (products.isEmpty()) {
				structure.setData(null);
				structure.setMessage("No Products Data");
				structure.setStatus(HttpStatus.NOT_FOUND.value());
				return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
			} else {
				structure.setData(products);
				structure.setMessage("Status Changed Success");
				structure.setStatus(HttpStatus.ACCEPTED.value());
				return new ResponseEntity<>(structure, HttpStatus.ACCEPTED);
			}
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<List<Merchant>>> viewallmerchant(String token) {
		ResponseStructure<List<Merchant>> structure = new ResponseStructure<>();
		if (jwtUtil.isValidToken(token)) {
			structure.setData(null);
			structure.setMessage("UNAUTHORIZED");
			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
		} else {
			List<Merchant> merchants = merchantRepository.findAll();
			if (merchants.isEmpty()) {
				structure.setData(null);
				structure.setMessage("No Merchants Data");
				structure.setStatus(HttpStatus.NOT_FOUND.value());
				return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
			} else {
				structure.setData(merchants);
				structure.setMessage("Merchants Data");
				structure.setStatus(HttpStatus.OK.value());
				return new ResponseEntity<>(structure, HttpStatus.OK);
			}
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<List<Customer>>> viewallcustomer(String token) {
		ResponseStructure<List<Customer>> structure = new ResponseStructure<>();
		if (jwtUtil.isValidToken(token)) {
			structure.setData(null);
			structure.setMessage("UNAUTHORIZED");
			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
		} else {
			List<Customer> customers = customerRepository.findAll();
			System.out.println(customers);
			if (customers.isEmpty()) {
				structure.setData(null);
				structure.setMessage("No Customers Data");
				structure.setStatus(HttpStatus.NOT_FOUND.value());
				return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
			} else {
				structure.setData(customers);
				structure.setMessage("Customers Data");
				structure.setStatus(HttpStatus.OK.value());
				return new ResponseEntity<>(structure, HttpStatus.OK);
			}
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<Payment>> addpayment(String token, Payment payment) {
		Payment payment2 = paymentRepository.findByName(payment.getName());
		ResponseStructure<Payment> structure = new ResponseStructure<>();
		if (jwtUtil.isValidToken(token)) {
			structure.setData(null);
			structure.setMessage("UNAUTHORIZED");
			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
		} else {
			int paymentCount = paymentRepository.countByName(payment.getName());
			if (paymentCount >= 2) {
				structure.setData(null);
				structure.setMessage("Cannot add more than 2 payment methods with the same name");
				structure.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
				return new ResponseEntity<>(structure, HttpStatus.NOT_ACCEPTABLE);
			} else if (payment2 == null) {
				paymentRepository.save(payment);
				structure.setData(payment);
				structure.setMessage("Payment method added successfully");
				structure.setStatus(HttpStatus.CREATED.value());
				return new ResponseEntity<>(structure, HttpStatus.CREATED);
			} else {
				structure.setData(null);
				structure.setMessage("Payment Method Already Exists");
				structure.setStatus(HttpStatus.CONFLICT.value());
				return new ResponseEntity<>(structure, HttpStatus.CONFLICT);
			}
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<List<Payment>>> viewallPayment(String token) {
		ResponseStructure<List<Payment>> structure = new ResponseStructure<>();
		List<Payment> payment = paymentRepository.findAll();
		if (jwtUtil.isValidToken(token)) {
			structure.setData(null);
			structure.setMessage("UNAUTHORIZED");
			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
		} else {
			structure.setData(payment);
			structure.setMessage("All Payment Method");
			structure.setStatus(HttpStatus.OK.value());
			return new ResponseEntity<>(structure, HttpStatus.OK);
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<Admin>> logout(HttpSession httpSession) {
		httpSession.invalidate();
		ResponseStructure<Admin> structure = new ResponseStructure<>();
		structure.setStatus(HttpStatus.OK.value());
		structure.setMessage("Logged out successfully");
		return new ResponseEntity<>(structure, HttpStatus.CREATED);
	}
}
