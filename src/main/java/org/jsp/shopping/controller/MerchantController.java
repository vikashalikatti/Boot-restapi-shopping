package org.jsp.shopping.controller;

import java.io.IOException;
import java.util.List;

import org.jsp.shopping.dto.Merchant;
import org.jsp.shopping.dto.Product;
import org.jsp.shopping.helper.ResponseStructure;
import org.jsp.shopping.service.implementation.MerhantService_implementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import freemarker.core.ParseException;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;
import io.jsonwebtoken.Jwt;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
//@RequestMapping("/hello")

@RequestMapping("/merchant")
@CrossOrigin
public class MerchantController {

	@Autowired
	MerhantService_implementation merhantService_implementation;

//	@PostMapping("/hello")
//	public ResponseEntity<ResponseStructure<Merchant>> saveStudent(@RequestBody Merchant merchant)
//			throws NoZeroException {
//		ResponseStructure<Merchant> structure = new ResponseStructure<>();
//		if (merchant.getMobile() == 0000000000) {
//			throw new NoZeroException("Mobile Number Not repeated");
//		} else {
//			structure.setData(merchant);
//			structure.setStatus(HttpStatus.CREATED.value());
//			structure.setMessage("Account created SUccess");
//			return new ResponseEntity<ResponseStructure<Merchant>>(structure, HttpStatus.CREATED);
//		}
//	}

	@PostMapping("/signup")
	public ResponseEntity<ResponseStructure<Merchant>> signup(@ModelAttribute Merchant merchant,
			@RequestParam String date, @RequestPart MultipartFile pic) throws IOException, TemplateException {
		return merhantService_implementation.signup(merchant, date, pic);
	}

	@PostMapping("/verify-otp/{email}")
	public ResponseEntity<ResponseStructure<Merchant>> verifyOtp(@PathVariable String email, @RequestParam int otp) {
		return merhantService_implementation.verifyOtp(email, otp);
	}

	@GetMapping("/resend-otp/{email}")
	public ResponseEntity<ResponseStructure<Merchant>> resendOtp(@PathVariable String email)
			throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException,
			TemplateException {
		return merhantService_implementation.resendOtp(email);
	}

	@PostMapping("/forgotpassword")
	public ResponseEntity<ResponseStructure<Merchant>> sendForgotOtp(@RequestParam String email)
			throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException,
			TemplateException {
		return merhantService_implementation.sendForgotOtp(email);
	}

	@PostMapping("/forgot-otp/{email}")
	public ResponseEntity<ResponseStructure<Merchant>> submitForgotOtp(@PathVariable String email,
			@RequestParam int otp) {
		return merhantService_implementation.submitForgotOtp(email, otp);
	}

	@PostMapping("/login")
	public ResponseEntity<ResponseStructure<Merchant>> login(@RequestParam String email, @RequestParam String password,
			HttpSession session) {
//		System.out.println(session.getId());
		return merhantService_implementation.login(email, password, session);
	}

	@PostMapping("/product-add")
	public ResponseEntity<ResponseStructure<Merchant>> addProduct(@ModelAttribute Product product,
			@RequestParam MultipartFile pic,@RequestParam("token") String authtoken,@RequestParam String email) throws IOException {
		return merhantService_implementation.addProduct(product, pic,authtoken,email);
	}

	@GetMapping("/product-view")
	public ResponseEntity<ResponseStructure<List<Product>>> fetchallproduct(@RequestParam("token") String authtoken,@RequestParam String email) {
		return merhantService_implementation.fetchAllProducts(authtoken,email);
	}

	@GetMapping("/product-delete/{id}")
	public ResponseEntity<ResponseStructure<Product>> deleteProduct(@PathVariable int id,@RequestParam("token") String authtoken,@RequestParam String email) {
		return merhantService_implementation.deleteProduct(id, authtoken,email);
	}

	@GetMapping("/product-update/{id}")
	public ResponseEntity<ResponseStructure<Product>> updateProduct(@PathVariable int id,@RequestParam("token") String authtoken,@RequestParam String email) {
		return merhantService_implementation.updateProduct(id, authtoken,email);
	}

	@PostMapping("/product-update/{id}")
	public ResponseEntity<ResponseStructure<Product>> updateProdut(@ModelAttribute Product product,@RequestParam("token") String authtoken,@RequestParam String email,@PathVariable int id,	@RequestParam MultipartFile pic) throws IOException {
		return merhantService_implementation.updateProduct(product, authtoken,email,id,pic);
	}

	@GetMapping("/resend-forgot-otp/{email}")
	public ResponseEntity<ResponseStructure<Merchant>> resendForgotOtp(@PathVariable String email)
			throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException,
			TemplateException {
		return merhantService_implementation.resendForgotOtp(email);
	}

	@PostMapping("/reset-password")
	public ResponseEntity<ResponseStructure<Merchant>> setPassword(@RequestParam String email,
			@RequestParam String password) {
		return merhantService_implementation.setPassword(email, password);
	}

	@GetMapping("/logout")
	public ResponseEntity<ResponseStructure<Merchant>> logout(@ModelAttribute String token) {
		return merhantService_implementation.logout(token);
	}
}
