package org.jsp.shopping.service;

import java.util.List;

import org.jsp.shopping.dto.Customer;
import org.jsp.shopping.dto.Item;
import org.jsp.shopping.dto.Payment;
import org.jsp.shopping.dto.Product;
import org.jsp.shopping.dto.ShoppingOrder;
import org.jsp.shopping.dto.Wishlist;
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

	ResponseEntity<ResponseStructure<List<Item>>> removeFromCart(HttpSession session, int id);

	ResponseEntity<ResponseStructure<List<Wishlist>>> create_wishlist(HttpSession session, int id, String name);

	ResponseEntity<ResponseStructure<List<Wishlist>>> view_wishlist(HttpSession session);

	ResponseEntity<ResponseStructure<Wishlist>> viewWishlistProducts(HttpSession session, int id);

	ResponseEntity<ResponseStructure<Wishlist>> addToWishList(int wid, int pid, HttpSession session);

	ResponseEntity<ResponseStructure<Wishlist>> removeFromWishList(int wid, int pid, HttpSession session);

	ResponseEntity<ResponseStructure<Wishlist>> deleteWishlist(int wid, HttpSession session);

	ResponseEntity<ResponseStructure<List<Payment>>> checkPayment(HttpSession session);

	ResponseEntity<ResponseStructure<Customer>> checkAddress(HttpSession session, int pid);

	ResponseEntity<ResponseStructure<List<ShoppingOrder>>> viewOrders(HttpSession session);

	ResponseEntity<ResponseStructure<ShoppingOrder>> submitOrder(HttpSession session, int pid, String address);

	ResponseEntity<ResponseStructure<Customer>> forgotLink(String email);

	ResponseEntity<ResponseStructure<Customer>> resetPassword(String email, String token);

	ResponseEntity<ResponseStructure<Customer>> setpassword(String email, String password);

}
