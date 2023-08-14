package org.jsp.shopping.service.implementation;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jsp.shopping.Repository.ProductRepository;
import org.jsp.shopping.dao.Merchant_dao;
import org.jsp.shopping.dto.Merchant;
import org.jsp.shopping.dto.Product;
import org.jsp.shopping.helper.ResponseStructure;
import org.jsp.shopping.helper.SendMail;
import org.jsp.shopping.service.MerachantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;

@Service
public class MerhantService_implementation implements MerachantService {
	@Autowired
	Merchant_dao merchantDao;

	@Autowired
	SendMail mail;

	@Autowired
	ProductRepository productRepository;

	@Override
	public ResponseEntity<ResponseStructure<Merchant>> signup(Merchant merchant, String date, MultipartFile pic)
			throws IOException {
		ResponseStructure<Merchant> structure = new ResponseStructure<>();

		merchant.setDob(LocalDate.parse(date));

		byte[] picture = new byte[pic.getInputStream().available()];
		pic.getInputStream().read(picture);
		merchant.setPicture(picture);

		if (merchantDao.findByEmail(merchant.getEmail()) != null
				|| merchantDao.findByMobile(merchant.getMobile()) != null) {
			structure.setStatus(HttpStatus.BAD_REQUEST.value());
			structure.setMessage("Email or Mobile Should not be repeated");
			return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);
		}

		int otp = new Random().nextInt(100000, 999999);
		merchant.setOtp(otp);

		if (mail.sendOtp(merchant)) {
			Merchant merchant2 = merchantDao.save(merchant);
			structure.setData(merchant2);
			structure.setStatus(HttpStatus.CREATED.value());
			structure.setMessage("OTP Send Successfully");
			return new ResponseEntity<>(structure, HttpStatus.CREATED);
		} else {
			structure.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			structure.setMessage("Something went wrong, Check email and try again");
			return new ResponseEntity<>(structure, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<Merchant>> verifyOtp(String email, int otp) {
		ResponseStructure<Merchant> structure = new ResponseStructure<>();
		Merchant merchant = merchantDao.findByEmail(email);
//		System.out.println(merchant+"------------------------------------------------");
		if (merchant.getOtp() == otp) {
			merchant.setStatus(true);
			merchant.setOtp(0);
			merchantDao.save(merchant);
			structure.setData(merchant);
			structure.setStatus(HttpStatus.CREATED.value());
			structure.setMessage("Otp Verified Successfully");
			return new ResponseEntity<>(structure, HttpStatus.CREATED);
		} else {
			structure.setData(null);
			structure.setMessage("Otp Not Verified Sucessfully");
			structure.setStatus(HttpStatus.BAD_REQUEST.value());
			return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);
		}

	}
	
	@Override
	public ResponseEntity<ResponseStructure<Merchant>> resendOtp(String email) {
		ResponseStructure<Merchant> structure = new ResponseStructure<>();
		Merchant merchant = merchantDao.findByEmail(email);
		int otp = new Random().nextInt(100000, 999999);
		merchant.setOtp(otp);

		if (mail.sendOtp(merchant)) {
			Merchant merchant2 = merchantDao.save(merchant);
			structure.setMessage("Successfully resend OTP");
			structure.setData(merchant2);
			structure.setStatus(HttpStatus.CREATED.value());
			return new ResponseEntity<>(structure, HttpStatus.CREATED);
		} else {
			structure.setData(null);
			structure.setMessage("Something went Wrong, Check email and try again");
			structure.setStatus(HttpStatus.BAD_REQUEST.value());
			return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<Merchant>> login(String email, String password, HttpSession session) {
		ResponseStructure<Merchant> structure = new ResponseStructure<>();
		Merchant merchant = merchantDao.findByEmail(email);
		if (merchant == null) {
			structure.setData(null);
			structure.setMessage("Incorrect Email");
			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
		} else {
			if (merchant.getPassword().equals(password)) {
				if (merchant.isStatus()) {
					session.setAttribute("merchant", merchant);

					structure.setData(merchant);
					structure.setMessage("Login Success");
					structure.setStatus(HttpStatus.CREATED.value());
					return new ResponseEntity<>(structure, HttpStatus.CREATED);
				} else {
					structure.setData(null);
					structure.setMessage("Mail verification Pending, Click on Forgot password and verify otp");
					structure.setStatus(HttpStatus.BAD_REQUEST.value());
					return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);
				}
			} else {
				structure.setData(null);
				structure.setMessage("Incorrect Password");
				structure.setStatus(HttpStatus.BAD_REQUEST.value());
				return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);
			}
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<Merchant>> addProduct(HttpSession session, Product product,
			MultipartFile pic) throws IOException {
		ResponseStructure<Merchant> structure = new ResponseStructure<>();
		if (session.getAttribute("merchant") == null) {
			structure.setData(null);
			structure.setMessage("Login Again");
			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
		} else {
			Merchant merchant = (Merchant) session.getAttribute("merchant");

			byte[] image = new byte[pic.getInputStream().available()];
			pic.getInputStream().read(image);

			product.setImage(image);
			product.setName(merchant.getName() + "-" + product.getName());

			Product product2 = merchantDao.findProductByName(product.getName());
			if (product2 != null) {
				product.setId(product2.getId());
				product.setStock(product.getStock() + product2.getStock());
			}

			List<Product> products = merchant.getProducts();
			if (products == null) {
				products = new ArrayList<>();
			}
			products.add(product);
			merchant.setProducts(products);

			session.setAttribute("merchant", merchantDao.save(merchant));
			structure.setData(merchant);
			structure.setMessage("Product Added Successfully");
			structure.setStatus(HttpStatus.CREATED.value());
			return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<List<Product>>> fetchAllProducts(HttpSession session) {
		ResponseStructure<List<Product>> responseStructure = new ResponseStructure<>();

		Merchant merchant = (Merchant) session.getAttribute("merchant");

		if (merchant == null) {
			responseStructure.setData(null);
			responseStructure.setMessage("Login Again");
			responseStructure.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new ResponseEntity<>(responseStructure, HttpStatus.UNAUTHORIZED);
		} else {

			List<Product> products = merchant.getProducts();

			if (products == null || products.isEmpty()) {
				responseStructure.setData(null);
				responseStructure.setMessage("No Products Found");
				responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
				return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
			}

			responseStructure.setData(products);
			responseStructure.setMessage("Products Found");
			responseStructure.setStatus(HttpStatus.OK.value());
			return new ResponseEntity<>(responseStructure, HttpStatus.OK);
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<Product>> deleteProduct(int id, HttpSession session) {
		ResponseStructure<Product> structure = new ResponseStructure<>();
		if (session.getAttribute("merchant") == null) {
			structure.setData(null);
			structure.setMessage("Login Again");
			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
		} else {
			Product product = merchantDao.findProductById(id);
			Merchant merchant = (Merchant) session.getAttribute("merchant");
			structure.setStatus(HttpStatus.ACCEPTED.value());
			if (merchant.getProducts() == null || merchant.getProducts().isEmpty()) {
				structure.setStatus(HttpStatus.NOT_FOUND.value());
				structure.setMessage("Products Not Found");
				return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
			} else {

				merchant.getProducts().remove(product);
				session.removeAttribute("merchant");
				session.setAttribute("merchant", merchantDao.save(merchant));
				merchantDao.removeProduct(product);
				structure.setStatus(HttpStatus.ACCEPTED.value());
				structure.setMessage("Deleted Successfully");
				return new ResponseEntity<>(structure, HttpStatus.ACCEPTED);
			}

		}
	}

	@Override
	public ResponseEntity<ResponseStructure<Product>> updateProduct(int id, HttpSession session) {
		Product product = merchantDao.findProductById(id);
		if (session.getAttribute("merchant") == null) {
			ResponseStructure<Product> structure = new ResponseStructure<>();
			structure.setData(null);
			structure.setMessage("Login Again");
			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
		} else {
			ResponseStructure<Product> structure = new ResponseStructure<>();
			if (product == null) {
				structure.setData(null);
				structure.setStatus(HttpStatus.NOT_FOUND.value());
				structure.setMessage("Product Not Found");
				return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
			} else {
				structure.setData(product);
				structure.setStatus(HttpStatus.FOUND.value());
				structure.setMessage("Product Found");
				return new ResponseEntity<>(structure, HttpStatus.FOUND);
			}
		}
	}
	
	@Override
	public ResponseEntity<ResponseStructure<Product>> updateProduct(Product product, HttpSession session) {

		if (session.getAttribute("merchant") == null) {
			ResponseStructure<Product> structure = new ResponseStructure<>();
			structure.setData(null);
			structure.setMessage("Login Again");
			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
		} else {
			ResponseStructure<Product> structure = new ResponseStructure<>();
			Merchant merchant1 = (Merchant) session.getAttribute("merchant");
			Merchant merchant = merchantDao.findByEmail(merchant1.getEmail());
			session.setAttribute("merchant", merchant);
			if (merchant.getProducts() == null || merchant.getProducts().isEmpty()) {
				structure.setData(product);
				structure.setMessage("Product Not Found");
				structure.setStatus(HttpStatus.CREATED.value());
				return new ResponseEntity<>(structure, HttpStatus.CREATED);
			} else {
				product.setImage(merchantDao.findProductById(product.getId()).getImage());
				product.setStatus(merchantDao.findProductById(product.getId()).isStatus());

				structure.setData(product);
				structure.setMessage("Product Updated Successfully");
				structure.setStatus(HttpStatus.CREATED.value());
//				session.setAttribute("merchant", );
				productRepository.save(product);
				return new ResponseEntity<>(structure, HttpStatus.CREATED);
			}
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<Merchant>> sendForgotOtp(String email) {
		ResponseStructure<Merchant> structure = new ResponseStructure<>();
		Merchant merchant = merchantDao.findByEmail(email);
		if (merchant == null) {
			structure.setData(merchant);
			structure.setMessage(merchant.getEmail() + "Email Doesn't Exist Signup First");
			structure.setStatus(HttpStatus.NO_CONTENT.value());
			return new ResponseEntity<>(structure, HttpStatus.NOT_ACCEPTABLE);
		} else {
			int otp = new Random().nextInt(100000, 999999);
			merchant.setOtp(otp);

			if (mail.sendOtp(merchant)) {
				Merchant merchant2 = merchantDao.save(merchant);
				structure.setData(merchant2);
				structure.setStatus(HttpStatus.OK.value());
				structure.setMessage(merchant2.getEmail() + "Otp Sent Success");
				return new ResponseEntity<>(structure, HttpStatus.OK);
			} else {
				structure.setData(null);
				structure.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
				structure.setMessage("Something went Wrong, Check email and try again");
				return new ResponseEntity<>(structure, HttpStatus.NOT_ACCEPTABLE);
			}
		}

	}

	@Override
	public ResponseEntity<ResponseStructure<Merchant>> submitForgotOtp(String email, int otp) {
		ResponseStructure<Merchant> structure = new ResponseStructure<>();
		Merchant merchant = merchantDao.findByEmail(email);
		if (merchant.getOtp() == otp) {
			merchant.setStatus(true);
			merchant.setOtp(0);
			merchantDao.save(merchant);
			structure.setData(merchant);
			structure.setMessage("Account Verified Successfully");
			structure.setStatus(HttpStatus.ACCEPTED.value());
			return new ResponseEntity<>(structure, HttpStatus.ACCEPTED);
		} else {
			structure.setData(null);
			structure.setMessage("Incorrect OTP");
			structure.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
			return new ResponseEntity<>(structure, HttpStatus.NOT_ACCEPTABLE);
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<Merchant>> resendForgotOtp(String email) {
		ResponseStructure<Merchant> structure = new ResponseStructure<>();
		Merchant merchant = merchantDao.findByEmail(email);
		int otp = new Random().nextInt(100000, 999999);
		merchant.setOtp(otp);

		if (mail.sendOtp(merchant)) {
			Merchant merchant2 = merchantDao.save(merchant);
			structure.setData(merchant2);
			structure.setMessage("Otp resend Success");
			structure.setStatus(HttpStatus.ACCEPTED.value());
			return new ResponseEntity<>(structure, HttpStatus.ACCEPTED);
		} else {
			structure.setData(null);
			structure.setMessage("Something went Wrong, Check email and try again");
			structure.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
			return new ResponseEntity<>(structure, HttpStatus.NOT_ACCEPTABLE);
		}
	}
}
