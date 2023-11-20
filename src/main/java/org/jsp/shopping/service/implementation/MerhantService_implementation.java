package org.jsp.shopping.service.implementation;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.jsp.shopping.Repository.ProductRepository;
import org.jsp.shopping.dao.Merchant_dao;
import org.jsp.shopping.dto.Merchant;
import org.jsp.shopping.dto.Product;
import org.jsp.shopping.helper.JwtUtil;
import org.jsp.shopping.helper.ResponseStructure;
import org.jsp.shopping.helper.SendMail;
import org.jsp.shopping.service.MerachantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import freemarker.core.ParseException;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpSession;

@Service
public class MerhantService_implementation implements MerachantService {
	@Autowired
	private Merchant_dao merchantDao;

	@Autowired
	private SendMail mail;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private BCryptPasswordEncoder encoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	JwtUtil jwtUtil;

	private Set<String> invalidatedTokens = new HashSet<>();

	@Override
	public ResponseEntity<ResponseStructure<Merchant>> signup(Merchant merchant, String date, MultipartFile pic)
			throws IOException, TemplateException {
		ResponseStructure<Merchant> structure = new ResponseStructure<>();

		merchant.setDob(LocalDate.parse(date));
		merchant.setPassword(encoder.encode(merchant.getPassword()));
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
		merchant.setOtpGeneratedTime(LocalDateTime.now());

		if (mail.sendOtp(merchant)) {
			merchant.setRole("merchant");
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
		if (merchant != null && merchant.getOtp() == otp) {
			LocalDateTime otpGeneratedTime = merchant.getOtpGeneratedTime();
			LocalDateTime currentTime = LocalDateTime.now();
			Duration duration = Duration.between(otpGeneratedTime, currentTime);

			if (duration.toMinutes() <= 5) {
				merchant.setStatus(true);
				merchant.setOtp(0);
				merchantDao.save(merchant);
				structure.setData(merchant);
				structure.setStatus(HttpStatus.OK.value());
				structure.setMessage("OTP Verified Successfully");
				return new ResponseEntity<>(structure, HttpStatus.OK);
			} else {
				structure.setData(null);
				structure.setStatus(HttpStatus.BAD_REQUEST.value());
				structure.setMessage("OTP has expired.");
				return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);
			}
		} else {
			structure.setData(null);
			structure.setMessage("Otp Not Verified Sucessfully");
			structure.setStatus(HttpStatus.BAD_REQUEST.value());
			return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);
		}

	}

	@Override
	public ResponseEntity<ResponseStructure<Merchant>> resendOtp(String email) throws TemplateNotFoundException,
			MalformedTemplateNameException, ParseException, IOException, TemplateException {
		ResponseStructure<Merchant> structure = new ResponseStructure<>();
		Merchant merchant = merchantDao.findByEmail(email);

		if (merchant != null) {
			int otp = new Random().nextInt(100000, 999999);
			merchant.setOtp(otp);
			merchant.setOtpGeneratedTime(LocalDateTime.now()); // Store OTP generation time

			if (mail.sendOtp(merchant)) {
				Merchant merchant2 = merchantDao.save(merchant);
				structure.setMessage("Successfully resend OTP");
				structure.setData(merchant2);
				structure.setStatus(HttpStatus.OK.value());
			} else {
				structure.setData(null);
				structure.setMessage("Something went Wrong, Check email and try again");
				structure.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
			}
		} else {
			structure.setData(null);
			structure.setMessage("Merchant not found");
			structure.setStatus(HttpStatus.NOT_FOUND.value());
		}

		return new ResponseEntity<>(structure, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<ResponseStructure<Merchant>> login(String email, String password, HttpSession session) {
		ResponseStructure<Merchant> structure = new ResponseStructure<>();
		Merchant merchant = merchantDao.findByEmail(email);
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);

		Authentication authentication = authenticationManager.authenticate(authToken);

		SecurityContextHolder.getContext().setAuthentication(authentication);

		UserDetails userDetails = (UserDetails) authentication.getPrincipal();

		if (userDetails != null) {
			if (merchant.isStatus()) {
				long expirationMillis = System.currentTimeMillis() + 3600000; // 1 hour in milliseconds
				Date expirationDate = new Date(expirationMillis);

				String token = jwtUtil.generateToken(userDetails, merchant.getRole(), expirationDate);
				session.setAttribute("token", token);
				structure.setData2(token);
				structure.setMessage("Login Success");
				structure.setStatus(HttpStatus.CREATED.value());
				return new ResponseEntity<>(structure, HttpStatus.CREATED);
			} else {
				structure.setData(null);
				structure.setMessage("Mail verification Pending, Click on Forgot password and verify OTP");
				structure.setStatus(HttpStatus.BAD_REQUEST.value());
				return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);
			}
		}
		return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);
	}

	@Override
	public ResponseEntity<ResponseStructure<Merchant>> addProduct(Product product, MultipartFile pic, String authToken,
			String email) throws IOException {
		ResponseStructure<Merchant> structure = new ResponseStructure<>();

		try {
			// Check if the JWT token is valid
			if (jwtUtil.isValidToken(authToken)) {
				structure.setMessage("Unauthorized");
				structure.setStatus(HttpStatus.UNAUTHORIZED.value());
				return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
			}

//			Merchant merchant = jwtUtil.extractMerchantFromToken(authToken);
			Merchant merchant = merchantDao.findByEmail(email);

			// Read and set the product image
			byte[] image = pic.getBytes();
			product.setImage(image);

			// Generate a unique product name
			String uniqueProductName = merchant.getName() + "-" + product.getName();
			product.setName(uniqueProductName);

			// Check if a product with the same name already exists
			Product existingProduct = merchantDao.findProductByName(uniqueProductName);
			if (existingProduct != null) {
				product.setId(existingProduct.getId());
				product.setStock(product.getStock() + existingProduct.getStock());
			}

			List<Product> products = merchant.getProducts();
			if (products == null) {
				products = new ArrayList<>();
			}
			products.add(product);
			merchant.setProducts(products);
			merchant = merchantDao.save(merchant);

			structure.setData(merchant);
			structure.setMessage("Product Added Successfully");
			structure.setStatus(HttpStatus.CREATED.value());
			return new ResponseEntity<>(structure, HttpStatus.CREATED);
		} catch (IOException e) {
			structure.setMessage("Failed to read the product image");
			structure.setStatus(HttpStatus.BAD_REQUEST.value());
			return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);
		} catch (JwtException e) {
			structure.setMessage("Invalid JWT token");
			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
		}
//	    } catch (Exception e) {
//	        structure.setMessage("Internal Server Error");
//	        structure.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
//	        return new ResponseEntity<>(structure, HttpStatus.INTERNAL_SERVER_ERROR);
//	    }
	}

	@Override
	public ResponseEntity<ResponseStructure<List<Product>>> fetchAllProducts(String authToken, String email) {
		ResponseStructure<List<Product>> responseStructure = new ResponseStructure<>();

//		Merchant merchant = (Merchant) session.getAttribute("merchant");
		Merchant merchant = merchantDao.findByEmail(email);
		if (jwtUtil.isValidToken(authToken)) {
			responseStructure.setData(null);
			responseStructure.setMessage("UNAUTHORIZED");
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
	public ResponseEntity<ResponseStructure<Product>> deleteProduct(int id, String authToken, String email) {
		ResponseStructure<Product> structure = new ResponseStructure<>();

		// Check if the authentication token is valid
		if (jwtUtil.isValidToken(authToken)) {
			structure.setData(null);
			structure.setMessage("UNAUTHORIZED");
			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
		} else {
			Product product = merchantDao.findProductById(id);
			Merchant merchant = merchantDao.findByEmail(email);

			if (product == null) {
				structure.setStatus(HttpStatus.NOT_FOUND.value());
				structure.setMessage("Product Not Found");
				return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
			}

			if (merchant == null) {
				structure.setStatus(HttpStatus.NOT_FOUND.value());
				structure.setMessage("Merchant Not Found");
				return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
			}

			// Check if the merchant owns the product
			if (!merchant.getProducts().contains(product)) {
				structure.setStatus(HttpStatus.FORBIDDEN.value());
				structure.setMessage("Forbidden: Merchant does not own this product");
				return new ResponseEntity<>(structure, HttpStatus.FORBIDDEN);
			}

			// Remove the product from the merchant's product list
			merchant.getProducts().remove(product);
			merchantDao.save(merchant);

			// Remove the product from the database
			merchantDao.removeProduct(product);

			structure.setStatus(HttpStatus.ACCEPTED.value());
			structure.setMessage("Deleted Successfully");
			return new ResponseEntity<>(structure, HttpStatus.ACCEPTED);
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<Product>> updateProduct(int id, String authtoken, String email) {
		ResponseStructure<Product> structure = new ResponseStructure<>();
		Product product = merchantDao.findProductById(id);
		if (jwtUtil.isValidToken(authtoken)) {
			structure.setData(null);
			structure.setMessage("UNAUTHORIZED");
			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
		} else {
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
	public ResponseEntity<ResponseStructure<Product>> updateProduct(Product product, String authtoken, String email,
	        int id, MultipartFile pic) throws IOException {
	    ResponseStructure<Product> structure = new ResponseStructure<>();

	    // Check if the authentication token is valid
	    if (jwtUtil.isValidToken(authtoken)) {
	        structure.setData(null);
	        structure.setMessage("UNAUTHORIZED");
	        structure.setStatus(HttpStatus.UNAUTHORIZED.value());
	        return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
	    }

	    // Find the merchant by email
	    Merchant merchant = merchantDao.findByEmail(email);

	    if (merchant == null) {
	        structure.setData(null);
	        structure.setMessage("Merchant Not Found");
	        structure.setStatus(HttpStatus.NOT_FOUND.value());
	        return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
	    }

	    // Try to find an existing product with the specified id
	    Product existingProduct = null;
	    for (Product p : merchant.getProducts()) {
	        if (p.getId() == id) {
	            existingProduct = p;
	            break;
	        }
	    }

	    if (existingProduct == null) {
	        // If the product doesn't exist, create a new one
	        existingProduct = new Product();
	        existingProduct.setId(id); // Set the ID if needed
	        merchant.getProducts().add(existingProduct); // Add it to the merchant's products
	    }

	    // Update the product's fields with the new values
	    existingProduct.setName(product.getName());
	    existingProduct.setDescription(product.getDescription());
	    existingProduct.setPrice(product.getPrice());
	    existingProduct.setStock(product.getStock());

	    // Check if a new image is provided in the 'pic' parameter
	    if (pic != null && !pic.isEmpty()) {
	        // Update the product's image with the new image
	        existingProduct.setImage(pic.getBytes());
	    }

	    existingProduct.setStatus(product.isStatus());

	    // Save the updated (or newly created) product to the database
	    try {
	        productRepository.save(existingProduct);
	        System.out.println(existingProduct+"-------------->");
	    } catch (Exception e) {
	        e.printStackTrace(); // Log the exception for debugging
	        structure.setData(null);
	        structure.setMessage("Error occurred while saving the product.");
	        structure.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
	        return new ResponseEntity<>(structure, HttpStatus.INTERNAL_SERVER_ERROR);
	    }

	    structure.setData(existingProduct);
	    structure.setMessage("Product Updated Successfully");
	    structure.setStatus(HttpStatus.OK.value());

	    return new ResponseEntity<>(structure, HttpStatus.OK);
	}




	@Override
	public ResponseEntity<ResponseStructure<Merchant>> sendForgotOtp(String email) throws TemplateNotFoundException,
			MalformedTemplateNameException, ParseException, IOException, TemplateException {
		ResponseStructure<Merchant> structure = new ResponseStructure<>();
		Merchant merchant = merchantDao.findByEmail(email);

		if (merchant == null) {
			structure.setData(merchant);
			structure.setMessage(email + " Email Doesn't Exist. Signup First");
			structure.setStatus(HttpStatus.NO_CONTENT.value());
			return new ResponseEntity<>(structure, HttpStatus.NO_CONTENT);
		} else {
			int otp = new Random().nextInt(100000, 999999);
			merchant.setOtp(otp);
			merchant.setOtpGeneratedTime(LocalDateTime.now());

			if (mail.sendOtp(merchant)) {
				Merchant merchant2 = merchantDao.save(merchant);
				structure.setData(merchant2);
				structure.setStatus(HttpStatus.OK.value());
				structure.setMessage(merchant2.getEmail() + " Otp Sent Successfully");
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

		if (merchant != null && merchant.getOtp() == otp) {
			LocalDateTime otpGeneratedTime = merchant.getOtpGeneratedTime();
			LocalDateTime currentTime = LocalDateTime.now();
			Duration duration = Duration.between(otpGeneratedTime, currentTime);

			if (duration.toMinutes() <= 5) {
				merchant.setStatus(true);
				merchant.setOtp(0);
				merchantDao.save(merchant);
				structure.setData(merchant);
				structure.setMessage("Account Verified Successfully");
				structure.setStatus(HttpStatus.ACCEPTED.value());
			} else {
				structure.setData(null);
				structure.setMessage("OTP has expired.");
				structure.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
			}
		} else {
			structure.setData(null);
			structure.setMessage("Incorrect OTP");
			structure.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
		}

		return new ResponseEntity<>(structure, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<ResponseStructure<Merchant>> resendForgotOtp(String email) throws TemplateNotFoundException,
			MalformedTemplateNameException, ParseException, IOException, TemplateException {
		ResponseStructure<Merchant> structure = new ResponseStructure<>();
		Merchant merchant = merchantDao.findByEmail(email);

		if (merchant != null) {
			int otp = new Random().nextInt(100000, 999999);
			merchant.setOtp(otp);
			merchant.setOtpGeneratedTime(LocalDateTime.now());

			if (mail.sendOtp(merchant)) {
				Merchant merchant2 = merchantDao.save(merchant);
				structure.setData(merchant2);
				structure.setMessage("Otp Resend Success");
				structure.setStatus(HttpStatus.ACCEPTED.value());
			} else {
				structure.setData(null);
				structure.setMessage("Something went Wrong, Check email and try again");
				structure.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
			}
		} else {
			structure.setData(null);
			structure.setMessage(email + " Email Doesn't Exist");
			structure.setStatus(HttpStatus.NO_CONTENT.value());
		}

		return new ResponseEntity<>(structure, HttpStatus.OK);
	}

	public ResponseEntity<ResponseStructure<Merchant>> setPassword(String email, String password) {
		ResponseStructure<Merchant> structure = new ResponseStructure<>();
		Merchant merchant = merchantDao.findByEmail(email);
		merchant.setPassword(encoder.encode(password));
		merchantDao.save(merchant);
		structure.setData(merchant);
		structure.setMessage("Password Reset Success");
		structure.setStatus(HttpStatus.CREATED.value());
		return new ResponseEntity<>(structure, HttpStatus.CREATED);
	}

	public ResponseEntity<ResponseStructure<Merchant>> logout(String token) {
		ResponseStructure<Merchant> structure = new ResponseStructure<>();

		if (isTokenInvalid(token)) {
			structure.setStatus(HttpStatus.BAD_REQUEST.value());
			structure.setMessage("Token is already invalidated");
			return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);
		}

		// Mark the token as invalidated (add it to the list)
		invalidatedTokens.add(token);

		structure.setStatus(HttpStatus.OK.value());
		structure.setMessage("Logged out successfully");
		return new ResponseEntity<>(structure, HttpStatus.CREATED);
	}

	private boolean isTokenInvalid(String token) {
		return invalidatedTokens.contains(token);
	}
}
