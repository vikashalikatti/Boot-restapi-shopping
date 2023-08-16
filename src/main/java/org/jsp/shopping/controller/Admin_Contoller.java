package org.jsp.shopping.controller;

import java.util.List;

import org.jsp.shopping.dto.Admin;
import org.jsp.shopping.dto.Customer;
import org.jsp.shopping.dto.Merchant;
import org.jsp.shopping.dto.Payment;
import org.jsp.shopping.dto.Product;
import org.jsp.shopping.helper.ResponseStructure;
import org.jsp.shopping.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/admin")
public class Admin_Contoller {
	@Autowired
	AdminService adminService;

	@PostMapping("/create")
	public ResponseEntity<ResponseStructure<Admin>> createAdmin(@ModelAttribute Admin admin) {
		return adminService.createAdmin(admin);
	}

	@PostMapping("/login")
	public ResponseEntity<ResponseStructure<Admin>> login(String username, String password, HttpSession session) {
		return adminService.login(username, password, session);
	}

	@GetMapping("/view-all-products")
	public ResponseEntity<ResponseStructure<List<Product>>> viewAllProducts(HttpSession session) {
		return adminService.viewAllProducts(session);
	}

	@GetMapping("/product-changestatus/{id}")
	public ResponseEntity<ResponseStructure<List<Product>>> changeStatus(@PathVariable int id, HttpSession session) {
		return adminService.changeStatus(id, session);
	}

	@GetMapping("/view-all-merchants")
	public ResponseEntity<ResponseStructure<List<Merchant>>> viewallmerchant(HttpSession session) {
		return adminService.viewallmerchant(session);
	}

	@GetMapping("/view-all-customers")
	public ResponseEntity<ResponseStructure<List<Customer>>> viewallcustomer(HttpSession session) {
		return adminService.viewallcustomer(session);
	}

	@PostMapping("/payment-add")
	public ResponseEntity<ResponseStructure<Payment>> addpayment(HttpSession session, Payment payment) {
		return adminService.addpayment(session, payment);
	}
	
	@GetMapping("/view-all-payment")
	public ResponseEntity<ResponseStructure<List<Payment>>> viewallPayment(HttpSession session){
		return adminService.viewallPayment(session);
	}

}
