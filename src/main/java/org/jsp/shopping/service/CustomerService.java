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

import com.razorpay.RazorpayException;

import jakarta.servlet.http.HttpSession;

public interface CustomerService {

	ResponseEntity<ResponseStructure<Customer>> signup(Customer customer, String date) throws Exception;

	ResponseEntity<ResponseStructure<Customer>> verify_link(String email, String token);

	ResponseEntity<ResponseStructure<Customer>> login(String email, String password);

	ResponseEntity<ResponseStructure<List<Product>>> view_products();

	ResponseEntity<ResponseStructure<Product>> addCart(String email, String token, int id);

	ResponseEntity<ResponseStructure<List<Item>>> viewCart(String email, String token);

	ResponseEntity<ResponseStructure<List<Item>>> removeFromCart(String email, String token, int id);

	ResponseEntity<ResponseStructure<List<Wishlist>>> create_wishlist(String email, String token, int id, String name);

	ResponseEntity<ResponseStructure<List<Wishlist>>> view_wishlist(HttpSession session);

	ResponseEntity<ResponseStructure<Wishlist>> viewWishlistProducts(HttpSession session, int id);

	ResponseEntity<ResponseStructure<Wishlist>> addToWishList(int wid, int pid, HttpSession session);

	ResponseEntity<ResponseStructure<Wishlist>> removeFromWishList(int wid, int pid, HttpSession session);

	ResponseEntity<ResponseStructure<Wishlist>> deleteWishlist(int wid, HttpSession session);

	ResponseEntity<ResponseStructure<List<Payment>>> checkPayment(HttpSession session);

	ResponseEntity<ResponseStructure<Customer>> checkAddress(HttpSession session, int pid);

	ResponseEntity<ResponseStructure<List<ShoppingOrder>>> viewOrders(HttpSession session);

	ResponseEntity<ResponseStructure<ShoppingOrder>> submitOrder(HttpSession session, int pid, String address)
			throws RazorpayException;

	ResponseEntity<ResponseStructure<Customer>> forgotLink(String email) throws Exception;

	ResponseEntity<ResponseStructure<Customer>> resetPassword(String email, String token);

	ResponseEntity<ResponseStructure<Customer>> setpassword(String email, String password);

	ResponseEntity<ResponseStructure<Customer>> logout(HttpSession httpSession);

	ResponseEntity<ResponseStructure<Customer>> resend_link(String email) throws Exception;

}
