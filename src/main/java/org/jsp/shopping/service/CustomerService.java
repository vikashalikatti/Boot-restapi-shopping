package org.jsp.shopping.service;

import java.util.List;

import org.jsp.shopping.dto.Customer;
import org.jsp.shopping.dto.Item;
import org.jsp.shopping.dto.Product;
import org.jsp.shopping.helper.ResponseStructure;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpSession;

public interface CustomerService {

	ResponseEntity<ResponseStructure<Customer>> signup(Customer customer, String date);

	ResponseEntity<ResponseStructure<Customer>> verify_link(String email, String token);

	ResponseEntity<ResponseStructure<Customer>> login(String email, String password, HttpSession session);

	ResponseEntity<ResponseStructure<List<Product>>> view_products(HttpSession session);

	ResponseEntity<ResponseStructure<Product>> addCart(HttpSession session, int id);

	ResponseEntity<ResponseStructure<List<Item>>> viewCart(HttpSession session);

}
