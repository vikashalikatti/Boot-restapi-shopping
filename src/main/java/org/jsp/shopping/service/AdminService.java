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

	ResponseEntity<ResponseStructure<Admin>> login(String username, String password, HttpSession session);

	ResponseEntity<ResponseStructure<Admin>> createAdmin(Admin admin);

	ResponseEntity<ResponseStructure<List<Product>>> viewAllProducts(HttpSession session);

	ResponseEntity<ResponseStructure<List<Product>>> changeStatus(int id, HttpSession session);

	ResponseEntity<ResponseStructure<List<Merchant>>> viewallmerchant(HttpSession session);

	ResponseEntity<ResponseStructure<List<Customer>>> viewallcustomer(HttpSession session);

	ResponseEntity<ResponseStructure<Payment>> addpayment(HttpSession session, Payment payment);

	ResponseEntity<ResponseStructure<List<Payment>>> viewallPayment(HttpSession session);

}
