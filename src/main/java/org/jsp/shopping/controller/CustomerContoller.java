package org.jsp.shopping.controller;

import java.util.List;

import org.jsp.shopping.dto.Customer;
import org.jsp.shopping.dto.Item;
import org.jsp.shopping.dto.Product;
import org.jsp.shopping.helper.ResponseStructure;
import org.jsp.shopping.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("customer")
public class CustomerContoller {
	@Autowired
	CustomerService customerService;

	@PostMapping("/signup")
	public ResponseEntity<ResponseStructure<Customer>> signup(@ModelAttribute Customer customer,
			@RequestParam String date) {
		return customerService.signup(customer, date);
	}

	@GetMapping("/verify-otp/{email}/{token}")
	public ResponseEntity<ResponseStructure<Customer>> verify_link(@PathVariable String email,
			@PathVariable String token) {
		return customerService.verify_link(email, token);
	}

	@PostMapping("/login")
	public ResponseEntity<ResponseStructure<Customer>> login(@RequestParam String email, @RequestParam String password,
			HttpSession session) {
		return customerService.login(email, password, session);
	}

	@GetMapping("/products-view")
	public ResponseEntity<ResponseStructure<List<Product>>> view_products(HttpSession session) {
		return customerService.view_products(session);
	}

	@GetMapping("/cart-add/{id}")
	public ResponseEntity<ResponseStructure<Product>> addCart(HttpSession session, @PathVariable int id) {
		return customerService.addCart(session, id);
	}

	@GetMapping("/cart-view")
	public ResponseEntity<ResponseStructure<List<Item>>> viewCart(HttpSession session) {
		return customerService.viewCart(session);
	}

	@GetMapping("/cart-remove/{id}")
	public ResponseEntity<ResponseStructure<List<Item>>> removeFromCart(HttpSession session, @PathVariable int id) {
		return customerService.removeFromCart(session, id);
	}
	
	

}