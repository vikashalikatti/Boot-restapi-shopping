package org.jsp.shopping.service;

import java.util.List;

import org.jsp.shopping.dto.Admin;
import org.jsp.shopping.dto.Customer;
import org.jsp.shopping.dto.Merchant;
import org.jsp.shopping.dto.Payment;
import org.jsp.shopping.dto.Product;
import org.jsp.shopping.helper.ResponseStructure;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpSession;

public interface AdminService {

	ResponseEntity<ResponseStructure<Admin>> login(String username, String password);

	ResponseEntity<ResponseStructure<Admin>> createAdmin(Admin admin);

	ResponseEntity<ResponseStructure<List<Product>>> viewAllProducts(String token);

	ResponseEntity<ResponseStructure<List<Product>>> changeStatus(int id, String token);

	ResponseEntity<ResponseStructure<List<Merchant>>> viewallmerchant(String token);

	ResponseEntity<ResponseStructure<List<Customer>>> viewallcustomer(String token);

	ResponseEntity<ResponseStructure<Payment>> addpayment(String token, Payment payment);

	ResponseEntity<ResponseStructure<List<Payment>>> viewallPayment(String token);

	ResponseEntity<ResponseStructure<Admin>> logout(HttpSession httpSession);

}
