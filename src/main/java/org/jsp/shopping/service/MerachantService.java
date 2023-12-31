package org.jsp.shopping.service;

import java.io.IOException;
import java.util.List;

import org.jsp.shopping.dto.Merchant;
import org.jsp.shopping.dto.Product;
import org.jsp.shopping.helper.ResponseStructure;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import freemarker.core.ParseException;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;
import io.jsonwebtoken.Jwt;
import jakarta.servlet.http.HttpSession;

public interface MerachantService<B> {
	public ResponseEntity<ResponseStructure<Merchant>> signup(Merchant merchant, String date, MultipartFile picture)
			throws IOException, TemplateException;

	public ResponseEntity<ResponseStructure<Merchant>> verifyOtp(String email, int otp);

	public ResponseEntity<ResponseStructure<Merchant>> resendOtp(String email) throws TemplateNotFoundException,
			MalformedTemplateNameException, ParseException, IOException, TemplateException;

	public ResponseEntity<ResponseStructure<Merchant>> login(String email, String password, HttpSession session);

	public ResponseEntity<ResponseStructure<Merchant>> addProduct(Product product, MultipartFile pic,
			String authToken, String email) throws IOException;

	public ResponseEntity<ResponseStructure<Merchant>> sendForgotOtp(String email) throws TemplateNotFoundException,
			MalformedTemplateNameException, ParseException, IOException, TemplateException;

	public ResponseEntity<ResponseStructure<Merchant>> submitForgotOtp(String email, int otp);

	public ResponseEntity<ResponseStructure<Merchant>> resendForgotOtp(@PathVariable String email)
			throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException,
			TemplateException;

	public ResponseEntity<ResponseStructure<List<Product>>> fetchAllProducts(String authToken,String email);

	public ResponseEntity<ResponseStructure<Product>> deleteProduct(int id, String authtoken,String email);

	public ResponseEntity<ResponseStructure<Product>> updateProduct(int id, String authtoken,String email);

	public ResponseEntity<ResponseStructure<Product>> updateProduct(Product product, String authtoken,String email,int id,MultipartFile pic) throws IOException;

	public ResponseEntity<ResponseStructure<Merchant>> setPassword(String email, String password);

	public ResponseEntity<ResponseStructure<Merchant>> logout(String token);
}
