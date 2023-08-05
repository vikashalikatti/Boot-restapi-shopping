package org.jsp.shopping.controller;

import java.io.IOException;

import org.jsp.shopping.dto.Merchant;
import org.jsp.shopping.dto.Product;
import org.jsp.shopping.exception.NoZeroException;
import org.jsp.shopping.helper.ResponseStructure;
import org.jsp.shopping.service.implementation.MerhantService_implementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
//@RequestMapping("/hello")

@RequestMapping("/merchant")
public class MerchantController {

	@Autowired
	MerhantService_implementation merhantService_implementation;

	@PostMapping("/hello")
	public ResponseEntity<ResponseStructure<Merchant>> saveStudent(@RequestBody Merchant merchant)
			throws NoZeroException {
		ResponseStructure<Merchant> structure = new ResponseStructure<>();
		if (merchant.getMobile() == 0000000000) {
			throw new NoZeroException("Mobile Number Not repeated");
		} else {
			structure.setData(merchant);
			structure.setStatus(HttpStatus.CREATED.value());
			structure.setMessage("Account created SUccess");
			return new ResponseEntity<ResponseStructure<Merchant>>(structure, HttpStatus.CREATED);
		}
	}

	@PostMapping("/signup")
	public ResponseEntity<ResponseStructure<Merchant>> signup(@ModelAttribute Merchant merchant,
			@RequestParam String date, @RequestPart MultipartFile pic) throws IOException {
		return merhantService_implementation.signup(merchant, date, pic);
	}

	@PostMapping("/verify-otp/{email}")
	public ResponseEntity<ResponseStructure<Merchant>> verifyOtp(@PathVariable String email, @RequestParam int otp) {
		return merhantService_implementation.verifyOtp(email, otp);
	}

	@GetMapping("/resend-otp/{email}")
	public ResponseEntity<ResponseStructure<Merchant>> resendOtp(@PathVariable String email) {
		return merhantService_implementation.resendOtp(email);
	}

	@PostMapping("/login")
	public ResponseEntity<ResponseStructure<Merchant>> login(@RequestParam String email, @RequestParam String password,
			HttpSession session, HttpServletResponse response) {
		return merhantService_implementation.login(email, password, session);
	}

	@PostMapping("/product-add")
	public ResponseEntity<ResponseStructure<Merchant>> addProduct(HttpSession session, @ModelAttribute Product product,
			@RequestParam MultipartFile pic) throws IOException {
		return merhantService_implementation.addProduct(session, product, pic);
	}

	@GetMapping("/product-view")
	public ResponseEntity<ResponseStructure<Merchant>> fetchallproduct(HttpSession session) {
		return merhantService_implementation.fetchallproduct(session);
	}

	@GetMapping("/product-delete/{id}")
	public ResponseEntity<ResponseStructure<Merchant>> deleteProduct(@PathVariable int id, HttpSession session) {
		return merhantService_implementation.deleteProduct(id, session);
	}

	@GetMapping("/product-update/{id}")
	public ResponseEntity<ResponseStructure<Merchant>> updateProduct(@PathVariable int id, HttpSession session) {
		return merhantService_implementation.updateProduct(id, session);
	}

	@PostMapping("/product-update/{id}")
	public ResponseEntity<ResponseStructure<Merchant>> updateProdut(Product product,HttpSession session){
		return merhantService_implementation.updateProduct(product,session);
	}
}
