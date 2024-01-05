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

	@GetMapping("/verify-link/{email}/{token}")
	public ResponseEntity<ResponseStructure<Customer>> verify_link(@PathVariable String email,
			@PathVariable String token) {
		return customerService.verify_link(email, token);
	}

	@GetMapping("/resend_link/{email}")
	public ResponseEntity<ResponseStructure<Customer>> resend_link(@PathVariable String email) throws Exception {
		return customerService.resend_link(email);
	}

	@PostMapping("/login")
	public ResponseEntity<ResponseStructure<Customer>> login(@RequestParam String email,
			@RequestParam String password) {
		return customerService.login(email, password);
	}

	@GetMapping("/products-view")
	public ResponseEntity<ResponseStructure<List<Product>>> view_products() {
		return customerService.view_products();
	}

	@GetMapping("/cart-add/{id}")
	public ResponseEntity<ResponseStructure<Product>> addCart(@RequestParam String email,@RequestParam String token, @PathVariable int id) {
		return customerService.addCart(email,token, id);
	}

	@GetMapping("/cart-view")
	public ResponseEntity<ResponseStructure<List<Item>>> viewCart(@RequestParam String email,@RequestParam String token) {
		return customerService.viewCart(email,token);
	}

	@GetMapping("/cart-remove/{id}")
	public ResponseEntity<ResponseStructure<List<Item>>> removeFromCart(@RequestParam String email,@RequestParam String token, @PathVariable int id) {
		return customerService.removeFromCart(email, token,id);
	}

	@GetMapping("/wishlist-add/{id}")
	public ResponseEntity<ResponseStructure<List<Wishlist>>> addToWishlist(@RequestParam String email,@RequestParam String token,@PathVariable int id){
		return customerService.addToWishlist(email,token,id);
	}
	@PostMapping("/wishlist-create/{id}")
	public ResponseEntity<ResponseStructure<List<Wishlist>>> create_wishlist(@RequestParam String email,@RequestParam String token, @PathVariable int id,
			@RequestParam String name) {
		return customerService.create_wishlist(email,token, id, name);
	}

	@GetMapping("/wishlist-view")
	public ResponseEntity<ResponseStructure<List<Wishlist>>> view_wishlist(@RequestParam String email,@RequestParam String token) {
		return customerService.view_wishlist(email,token);
	}

	@GetMapping("/wishlist/product-view/{id}")
	public ResponseEntity<ResponseStructure<List<Wishlist>>> viewWishlistProducts(@PathVariable int id, @RequestParam String token,@RequestParam String email) {
		return customerService.viewWishlistProducts(token, id,email);
	}

	@GetMapping("/wishlist-add/{wid}/{pid}")
	public ResponseEntity<ResponseStructure<Wishlist>> addToWishList(@PathVariable int wid, @PathVariable int pid,
			@RequestParam String token,@RequestParam String email) {
		return customerService.addToWishList(wid, pid, token,email);
	}

	@GetMapping("/wishlist-remove/{wid}/{pid}")
	public ResponseEntity<ResponseStructure<Wishlist>> removeFromWishList(@PathVariable int wid, @PathVariable int pid,
			@RequestParam String token,@RequestParam String email) {
		return customerService.removeFromWishList(wid, pid, token ,email);
	}

	@GetMapping("/wishlist-delete/{wid}")
	public ResponseEntity<ResponseStructure<Wishlist>> deleteWishlist(@PathVariable int wid, @RequestParam String token,@RequestParam String email) {
		return customerService.deleteWishlist(wid, token,email);
	}

	@GetMapping("/placeorder")
	public ResponseEntity<ResponseStructure<List<Payment>>> checkPayment(@RequestParam String token,@RequestParam String email) {
		return customerService.checkPayment(token,email);
	}

	@PostMapping("/placeorder")
	public ResponseEntity<ResponseStructure<Customer>> checkAddress(@RequestParam String token,@RequestParam String email, @RequestParam int payid) {
		return customerService.checkAddress(token,email, payid);
	}

	@PostMapping("/submitorder")
	public ResponseEntity<ResponseStructure<ShoppingOrder>> submitOrder(@RequestParam String token,@RequestParam String email, @RequestParam int payid,
			@RequestParam String address) throws RazorpayException {
		return customerService.submitOrder(token,email, payid, address);
	}

	@GetMapping("/orders-view")
	public ResponseEntity<ResponseStructure<List<ShoppingOrder>>> viewOrder(@RequestParam String token,@RequestParam String email) {
		return customerService.viewOrders(token,email);
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

	@GetMapping("/logout")
	public ResponseEntity<ResponseStructure<Customer>> logout(HttpSession httpSession) {
		return customerService.logout(httpSession);
	}
}
