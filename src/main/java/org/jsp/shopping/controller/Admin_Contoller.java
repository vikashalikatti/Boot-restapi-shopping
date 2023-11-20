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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

@RestController
@CrossOrigin
@RequestMapping("/admin")
public class Admin_Contoller {
	@Autowired
	AdminService adminService;

	@PostMapping("/create")
	public ResponseEntity<ResponseStructure<Admin>> createAdmin(@ModelAttribute Admin admin) {
		return adminService.createAdmin(admin);
	}

	@PostMapping("/login")
	public ResponseEntity<ResponseStructure<Admin>> login(String username, String password) {
		return adminService.login(username, password);
	}

	@GetMapping("/view-all-products")
	public ResponseEntity<ResponseStructure<List<Product>>> viewAllProducts(@RequestParam("token") String authtoken) {
		return adminService.viewAllProducts(authtoken);
	}

	@GetMapping("/product-changestatus/{id}")
	public ResponseEntity<ResponseStructure<List<Product>>> changeStatus(@PathVariable int id, @RequestParam("token") String authtoken) {
		return adminService.changeStatus(id, authtoken);
	}

	@GetMapping("/view-all-merchants")
	public ResponseEntity<ResponseStructure<List<Merchant>>> viewallmerchant(@RequestParam("token") String authtoken) {
		return adminService.viewallmerchant(authtoken);
	}

	@GetMapping("/view-all-customers")
	public ResponseEntity<ResponseStructure<List<Customer>>> viewallcustomer(@RequestParam("token") String authtoken) {
		return adminService.viewallcustomer(authtoken);
	}

	@PostMapping("/payment-add")
	public ResponseEntity<ResponseStructure<Payment>> addpayment(@RequestParam("token") String authtoken, @ModelAttribute Payment payment) {
		return adminService.addpayment(authtoken, payment);
	}
	
	@GetMapping("/view-all-payment")
	public ResponseEntity<ResponseStructure<List<Payment>>> viewallPayment(@RequestParam("token") String authtoken){
		return adminService.viewallPayment(authtoken);
	}

	@GetMapping("/logout")
	public ResponseEntity<ResponseStructure<Admin>> logout(HttpSession httpSession) {
		return adminService.logout(httpSession);
	}

}
