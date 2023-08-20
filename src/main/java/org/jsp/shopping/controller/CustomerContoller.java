package org.jsp.shopping.controller;

import java.util.List;

import org.jsp.shopping.dto.Customer;
import org.jsp.shopping.dto.Item;
import org.jsp.shopping.dto.Payment;
import org.jsp.shopping.dto.Product;
import org.jsp.shopping.dto.ShoppingOrder;
import org.jsp.shopping.dto.Wishlist;
import org.jsp.shopping.helper.ResponseStructure;
import org.jsp.shopping.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.razorpay.RazorpayException;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("customer")
@CrossOrigin
public class CustomerContoller {
	@Autowired
	CustomerService customerService;

	@PostMapping("/signup")
	public ResponseEntity<ResponseStructure<Customer>> signup(@ModelAttribute Customer customer,
			@RequestParam String date) throws Exception {
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

	@PostMapping("/wishlist-create/{id}")
	public ResponseEntity<ResponseStructure<List<Wishlist>>> create_wishlist(HttpSession session, @PathVariable int id,
			@RequestParam String name) {
		return customerService.create_wishlist(session, id, name);
	}

	@GetMapping("/wishlist-view")
	public ResponseEntity<ResponseStructure<List<Wishlist>>> view_wishlist(HttpSession session) {
		return customerService.view_wishlist(session);
	}

	@GetMapping("/wishlist/product-view/{id}")
	public ResponseEntity<ResponseStructure<Wishlist>> viewWishlistProducts(@PathVariable int id, HttpSession session) {
		return customerService.viewWishlistProducts(session, id);
	}

	@GetMapping("/wishlist-add/{wid}/{pid}")
	public ResponseEntity<ResponseStructure<Wishlist>> addToWishList(@PathVariable int wid, @PathVariable int pid,
			HttpSession session) {
		return customerService.addToWishList(wid, pid, session);
	}

	@GetMapping("/wishlist-remove/{wid}/{pid}")
	public ResponseEntity<ResponseStructure<Wishlist>> removeFromWishList(@PathVariable int wid, @PathVariable int pid,
			HttpSession session) {
		return customerService.removeFromWishList(wid, pid, session);
	}

	@GetMapping("/wishlist-delete/{wid}")
	public ResponseEntity<ResponseStructure<Wishlist>> deleteWishlist(@PathVariable int wid, HttpSession session) {
		return customerService.deleteWishlist(wid, session);
	}

	@GetMapping("/placeorder")
	public ResponseEntity<ResponseStructure<List<Payment>>> checkPayment(HttpSession session) {
		return customerService.checkPayment(session);
	}

	@PostMapping("/placeorder")
	public ResponseEntity<ResponseStructure<Customer>> checkAddress(HttpSession session, @RequestParam int pid) {
		return customerService.checkAddress(session, pid);
	}

	@PostMapping("/submitorder")
	public ResponseEntity<ResponseStructure<ShoppingOrder>> submitOrder(HttpSession session, @RequestParam int pid,
			@RequestParam String address) throws RazorpayException {
		return customerService.submitOrder(session, pid, address);
	}

	@GetMapping("/orders-view")
	public ResponseEntity<ResponseStructure<List<ShoppingOrder>>> viewOrder(HttpSession session) {
		return customerService.viewOrders(session);
	}

	@PostMapping("/forgotpassword")
	public ResponseEntity<ResponseStructure<Customer>> forgotLink(@RequestParam String email) throws Exception {
		return customerService.forgotLink(email);
	}

	@GetMapping("/reset-password/{email}/{token}")
	public ResponseEntity<ResponseStructure<Customer>> resetPassword(@PathVariable String email,
			@PathVariable String token) {
		return customerService.resetPassword(email, token);
	}

	@PostMapping("/reset-password")
	public ResponseEntity<ResponseStructure<Customer>> setpassword(@RequestParam String email,
			@RequestParam String password) {
		return customerService.setpassword(email, password);
	}
}
