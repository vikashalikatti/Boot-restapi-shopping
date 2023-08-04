package org.jsp.shopping.service;

import java.io.IOException;

import org.jsp.shopping.dto.Merchant;
import org.jsp.shopping.dto.Product;
import org.jsp.shopping.helper.ResponseStructure;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;

public interface MerachantService {
	public ResponseEntity<ResponseStructure<Merchant>> signup(Merchant merchant, String date, MultipartFile picture)
			throws IOException;

	public ResponseEntity<ResponseStructure<Merchant>> verifyOtp(String email, int otp);

	public ResponseEntity<ResponseStructure<Merchant>> resendOtp(String email);

	public ResponseEntity<ResponseStructure<Merchant>> login(String email, String password, HttpSession session);

	public ResponseEntity<ResponseStructure<Merchant>> addProduct(HttpSession session, Product product,
			MultipartFile pic) throws IOException;

	public ResponseEntity<ResponseStructure<Merchant>> fetchallproduct(HttpSession session);

	public ResponseEntity<ResponseStructure<Merchant>> deleteProduct(int id, HttpSession session);
}
