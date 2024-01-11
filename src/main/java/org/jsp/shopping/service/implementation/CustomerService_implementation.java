package org.jsp.shopping.service.implementation;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;
import org.jsp.shopping.Repository.CustomerRepository;
import org.jsp.shopping.Repository.PaymentRepository;
import org.jsp.shopping.Repository.ProductRepository;
import org.jsp.shopping.Repository.ShoppingCartRepository;
import org.jsp.shopping.Repository.ShoppingOrderRepository;
import org.jsp.shopping.Repository.WishlistRepository;
import org.jsp.shopping.dto.Customer;
import org.jsp.shopping.dto.Item;
import org.jsp.shopping.dto.Merchant;
import org.jsp.shopping.dto.Payment;
import org.jsp.shopping.dto.Product;
import org.jsp.shopping.dto.ShoppingCart;
import org.jsp.shopping.dto.ShoppingOrder;
import org.jsp.shopping.dto.Wishlist;
import org.jsp.shopping.helper.JwtUtil;
import org.jsp.shopping.helper.ResponseStructure;
import org.jsp.shopping.helper.SendMail;
import org.jsp.shopping.service.CustomerService;
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
import org.springframework.web.bind.annotation.RequestParam;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import jakarta.servlet.http.HttpSession;

@Service
public class CustomerService_implementation implements CustomerService {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private SendMail mail;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private Item item;

	@Autowired
	private ShoppingCart shoppingCart;

	@Autowired
	private WishlistRepository wishlistRepository;

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private ShoppingCartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder encoder;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private ShoppingOrderRepository shoppingOrderRepository;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Override
	public ResponseEntity<ResponseStructure<Customer>> signup(Customer customer, String date) throws Exception {

		ResponseStructure<Customer> structure = new ResponseStructure<>();
		customer.setDob(LocalDate.parse(date));
		customer.setPassword(encoder.encode(customer.getPassword()));
		if (customerRepository.findByEmail(customer.getEmail()) != null
				|| customerRepository.findByMobile(customer.getMobile()) != null) {

			structure.setData(null);
			structure.setMessage("email and mobile shpuld be not to repeated");
			structure.setStatus(HttpStatus.ALREADY_REPORTED.value());
			return new ResponseEntity<>(structure, HttpStatus.ALREADY_REPORTED);
		}
//		String token = "EKART" + new Random().nextInt(10000, 999999);

		String token = jwtUtil.generateJwtTokenForCustomer(customer, Duration.ofMinutes(5));
//		System.out.println(token+"--------------------------------------------------->");

		customer.setToken(token);

		// logic for sending the mail
		if (mail.sendLink(customer)) {
			customer.setRole("customer");
			List<Customer> existingCustomers = customerRepository.findAll();
			Collections.sort(existingCustomers, Comparator.comparing(Customer::getName));
			existingCustomers.add(customer);

			customerRepository.saveAll(existingCustomers);

//			customerRepository.save(customer);

			structure.setData(customer);
			structure.setMessage("Verification Link send to Email Succesfull");
			structure.setStatus(HttpStatus.CREATED.value());
			return new ResponseEntity<>(structure, HttpStatus.CREATED);
		} else {
			structure.setData(null);
			structure.setMessage("Something Went Worng");
			structure.setStatus(HttpStatus.BAD_GATEWAY.value());
			return new ResponseEntity<>(structure, HttpStatus.BAD_GATEWAY);
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<Customer>> verify_link(String email, String token) {
		ResponseStructure<Customer> structure = new ResponseStructure<>();
		Customer customer = customerRepository.findByEmail(email);
		if (customer != null) {
			if (!jwtUtil.isValidToken(token)) {
				customer.setStatus(true);
				customer.setToken(null);
				customerRepository.save(customer);

				structure.setData(customer);
				structure.setMessage("Account Created Successfully");
				structure.setStatus(HttpStatus.CREATED.value());
				return new ResponseEntity<>(structure, HttpStatus.CREATED);
			} else {
				structure.setData(null);
				structure.setMessage("Incorrect or expired token");
				structure.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
				return new ResponseEntity<>(structure, HttpStatus.NOT_ACCEPTABLE);
			}
		} else {
			structure.setData(null);
			structure.setMessage("Incorrect email");
			structure.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
			return new ResponseEntity<>(structure, HttpStatus.NOT_ACCEPTABLE);
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<Customer>> resend_link(String email) throws Exception {
		Customer customer = customerRepository.findByEmail(email);
		ResponseStructure<Customer> structure = new ResponseStructure<>();
		if (customer == null) {
			structure.setData(null);
			structure.setMessage("Email dosen't exits");
			structure.setStatus(HttpStatus.NOT_FOUND.value());
			return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
		} else {
			String token = jwtUtil.generateJwtTokenForCustomer(customer, Duration.ofMinutes(5));
			customer.setToken(token);
			customerRepository.save(customer);
			if (mail.sendResetLink(customer)) {
				Customer customer2 = customerRepository.save(customer);
				structure.setData(customer2);
				structure.setMessage("Verification Link send to Email Succesfull");
				structure.setStatus(HttpStatus.OK.value());
				return new ResponseEntity<>(structure, HttpStatus.OK);
			} else {
				structure.setData(null);
				structure.setMessage("Something went wrong");
				structure.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
				return new ResponseEntity<>(structure, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<Customer>> forgotLink(String email) throws Exception {
		Customer customer = customerRepository.findByEmail(email);
		ResponseStructure<Customer> structure = new ResponseStructure<>();
		if (customer == null) {
			structure.setData(null);
			structure.setMessage("Email dosen't exits");
			structure.setStatus(HttpStatus.NOT_FOUND.value());
			return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
		} else {
			String token = jwtUtil.generateJwtTokenForCustomer(customer, Duration.ofMinutes(5));
			customer.setToken(token);
			customerRepository.save(customer);
			if (mail.sendResetLink(customer)) {
				Customer customer2 = customerRepository.save(customer);
				structure.setData(customer2);
				structure.setMessage("Verification Link send to Email Succesfull");
				structure.setStatus(HttpStatus.FOUND.value());
				return new ResponseEntity<>(structure, HttpStatus.FOUND);
			} else {
				structure.setData(null);
				structure.setMessage("Something went wrong");
				structure.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
				return new ResponseEntity<>(structure, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<Customer>> resetPassword(String email, String token) {
		Customer customer = customerRepository.findByEmail(email);
		ResponseStructure<Customer> structure = new ResponseStructure<>();

		if (jwtUtil.isTokenExpired(token)) {
			structure.setData(null);
			structure.setMessage("Token has expired");
			structure.setStatus(HttpStatus.BAD_REQUEST.value());
			return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);
		}

		if (customer.getToken().equals(token)) {
			customer.setStatus(true);
			Customer updatedCustomer = customerRepository.save(customer);

			structure.setData(updatedCustomer);
			structure.setMessage("Verified Successfully");
			structure.setStatus(HttpStatus.OK.value());
			return new ResponseEntity<>(structure, HttpStatus.OK);
		} else {
			structure.setData(null);
			structure.setMessage("Not Verified");
			structure.setStatus(HttpStatus.BAD_REQUEST.value());
			return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<Customer>> setpassword(String email, String password, String token) {
		Customer customer = customerRepository.findByEmail(email);
		ResponseStructure<Customer> structure = new ResponseStructure<>();
		if (jwtUtil.isTokenExpired(token)) {
			structure.setData(null);
			structure.setMessage("Token has expired");
			structure.setStatus(HttpStatus.BAD_REQUEST.value());
			return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);
		}
		if (customer.getToken().equals(token)) {
			customer.setToken(null);
			customer.setPassword(encoder.encode(password));
			customerRepository.save(customer);
			structure.setData(customer);
			structure.setMessage("Password Set Success");
			structure.setStatus(HttpStatus.OK.value());
			return new ResponseEntity<>(structure, HttpStatus.OK);
		} else {
			structure.setData(null);
			structure.setMessage("Invalid Token");
			structure.setStatus(HttpStatus.BAD_REQUEST.value());
			return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<Customer>> login(String email, String password) {
		ResponseStructure<Customer> structure = new ResponseStructure<>();
		Customer customer = customerRepository.findByEmail(email);
//		if (customer == null) {
//			structure.setData(null);
//			structure.setMessage("Incorrect Email");
//			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
//			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
//		}
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);

		Authentication authentication = authenticationManager.authenticate(authToken);

		SecurityContextHolder.getContext().setAuthentication(authentication);

		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//		System.out.println(userDetails + "---------------------------------
		if (userDetails != null) {
			if (customer.isStatus()) {
				long expirationMillis = System.currentTimeMillis() + 3600000; // 1 hour in milliseconds
				Date expirationDate = new Date(expirationMillis);

				String token = jwtUtil.generateToken(userDetails, customer.getRole(), expirationDate);
				structure.setData2(token);
				structure.setMessage("Login Success");
				structure.setStatus(HttpStatus.CREATED.value());
				return new ResponseEntity<>(structure, HttpStatus.CREATED);
			} else {
				structure.setData(null);
				structure.setMessage("Mail verification Pending, Click on Forgot password and verify otp");
				structure.setStatus(HttpStatus.BAD_REQUEST.value());
				return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);
			}
		}
		return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);
	}

//	@Override
//	public ResponseEntity<ResponseStructure<Customer>> login(String email, String password, HttpSession session) {
//		ResponseStructure<Customer> structure = new ResponseStructure<>();
//		Customer customer = customerRepository.findByEmail(email);
//		if (customer == null) {
//			structure.setData(null);
//			structure.setMessage("Incorrect Email");
//			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
//			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
//		} else {
//			if (encoder.matches(password, customer.getPassword())) {
//				if (customer.isStatus()) {
//					session.setAttribute("customer", customer);
//
//					structure.setData(customer);
//					structure.setMessage("Login Success");
//					structure.setStatus(HttpStatus.CREATED.value());
//					return new ResponseEntity<>(structure, HttpStatus.CREATED);
//				} else {
//					structure.setData(null);
//					structure.setMessage("Mail verification Pending, Click on Forgot password and verify otp");
//					structure.setStatus(HttpStatus.BAD_REQUEST.value());
//					return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);
//				}
//			} else {
//				structure.setData(null);
//				structure.setMessage("Incorrect Password");
//				structure.setStatus(HttpStatus.BAD_REQUEST.value());
//				return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);
//			}
//		}
//	}

	@Override
	public ResponseEntity<ResponseStructure<List<Product>>> view_products() {
		ResponseStructure<List<Product>> structure = new ResponseStructure<>();
		List<Product> products = productRepository.findByStatus(true);
		if (products.isEmpty()) {
			structure.setData(null);
			structure.setMessage("No Products Present");
			structure.setStatus(HttpStatus.NOT_FOUND.value());
			return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
		} else {
			structure.setData(products);
			structure.setMessage("Products");
			structure.setStatus(HttpStatus.OK.value());
			return new ResponseEntity<>(structure, HttpStatus.OK);
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<Product>> addCart(String email, String token, int id) {
		Customer customer = customerRepository.findByEmail(email);
		ResponseStructure<Product> structure = new ResponseStructure<>();
		if (jwtUtil.isValidToken(token)) {
			structure.setMessage("Unauthorized");
			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
		} else {
			Product product = productRepository.findById(id).orElse(null);
			if (product == null || product.getStock() < 1) {
				structure.setData(null);
				structure.setMessage("Out of Stock");
				structure.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
				return new ResponseEntity<>(structure, HttpStatus.NOT_ACCEPTABLE);
			}

			ShoppingCart cart = customer.getShoppingCart();
			if (cart == null) {
				cart = this.shoppingCart;
			}

			List<Item> items = cart.getItems();
			if (items == null) {
				items = new ArrayList<>();
			}

			Item newItem = new Item(); // Instantiate a new Item object
			newItem.setDescription(product.getDescription());
			newItem.setImage(product.getImage());
			newItem.setName(product.getName());
			newItem.setPrice(product.getPrice());
			newItem.setQuantity(1);

			boolean found = false;
			for (Item item : items) {
				if (item.getName().equals(newItem.getName())) {
					item.setQuantity(item.getQuantity() + 1);
					item.setPrice(item.getPrice() + product.getPrice());
					item.setDescription(product.getDescription());
					item.setImage(product.getImage());
					found = true;
					break;
				}
			}

			if (!found) {
				items.add(newItem);
			}

			cart.setItems(items);
			customer.setShoppingCart(cart);

			product.setStock(product.getStock() - 1);
			productRepository.save(product);
			customerRepository.save(customer);

			structure.setData(product);
			structure.setMessage("Product Added Successfully");
			structure.setStatus(HttpStatus.OK.value());
			return new ResponseEntity<>(structure, HttpStatus.OK);
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<List<Item>>> viewCart(String email, String token) {
		Customer customer = customerRepository.findByEmail(email);
		ResponseStructure<List<Item>> structure = new ResponseStructure<>();
		if (jwtUtil.isValidToken(token)) {
			structure.setMessage("Unauthorized");
			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
		} else {

			if (customer.getShoppingCart() == null || customer.getShoppingCart().getItems() == null
					|| customer.getShoppingCart().getItems().isEmpty()) {
				structure.setData(null);
				structure.setMessage("No Items in cart");
				structure.setStatus(HttpStatus.BAD_REQUEST.value());
				return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);
			} else {
				List<Item> items = customer.getShoppingCart().getItems();
				structure.setData(items);
				structure.setMessage("Items");
				structure.setStatus(HttpStatus.OK.value());
				return new ResponseEntity<>(structure, HttpStatus.OK);
			}
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<List<Item>>> removeFromCart(String email, String token, int id) {
		Customer customer = customerRepository.findByEmail(email);
		ResponseStructure<List<Item>> structure = new ResponseStructure<>();
		if (jwtUtil.isValidToken(token)) {
			structure.setMessage("Unauthorized");
			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
		} else {
			List<Item> items = customer.getShoppingCart().getItems();
			Item item = null;
			boolean flag = false;
			for (Item item1 : items) {
				if (item1.getId() == id) {
					item = item1;
					if (item1.getQuantity() > 1) {
						item1.setPrice(item1.getPrice() - (item1.getPrice() / item1.getQuantity()));
						item1.setQuantity(item1.getQuantity() - 1);
						break;
					} else {
						flag = true;
						break;
					}
				}

			}
			if (flag) {
				items.remove(item);
			}

			Product product = productRepository.findByName(item.getName());
			product.setStock(product.getStock() + 1);
			productRepository.save(product);
			customerRepository.save(customer);

			structure.setData(items);
			structure.setMessage("Product Removed from Cart Success");
			structure.setStatus(HttpStatus.OK.value());
			return new ResponseEntity<>(structure, HttpStatus.OK);
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<List<Wishlist>>> create_wishlist(String email, String token, int id,
			String name) {
		Customer customer = customerRepository.findByEmail(email);
		ResponseStructure<List<Wishlist>> structure = new ResponseStructure<>();
		if (jwtUtil.isValidToken(token)) {
			structure.setMessage("Unauthorized");
			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
		} else {
			if (wishlistRepository.findByName(name) == null) {
				Wishlist wishlist = new Wishlist();
				wishlist.setName(name);

				Product product = productRepository.findById(id).orElse(null);
				List<Wishlist> list = customer.getWishlists();
				if (list == null) {
					list = new ArrayList<>();
				}

				if (product != null) {
					List<Product> products = new ArrayList<>();
					products.add(product);
					wishlist.setProducts(products);

					list.add(wishlist);

					customer.setWishlists(list);
					customerRepository.save(customer);
					structure.setData(list);
					structure.setMessage("WishList Creation Success and Product added to Wishlist");
					structure.setStatus(HttpStatus.OK.value());
					return new ResponseEntity<>(structure, HttpStatus.OK);

				} else {

					list.add(wishlist);

					customer.setWishlists(list);
					customerRepository.save(customer);
					structure.setData(list);
					structure.setMessage("WishList Creation Success");
					structure.setStatus(HttpStatus.OK.value());
					return new ResponseEntity<>(structure, HttpStatus.OK);
				}
			} else {
				structure.setData(null);
				structure.setMessage("WishList Already Exists");
				structure.setStatus(HttpStatus.ALREADY_REPORTED.value());
				return new ResponseEntity<>(structure, HttpStatus.ALREADY_REPORTED);
			}
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<List<Wishlist>>> view_wishlist(String email, String token) {
		Customer customer = customerRepository.findByEmail(email);

		ResponseStructure<List<Wishlist>> structure = new ResponseStructure<>();
		if (jwtUtil.isValidToken(token)) {
			structure.setMessage("Unauthorized");
			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
		} else {
			List<Wishlist> list = customer.getWishlists();
			if (list == null || list.isEmpty()) {
				structure.setData(null);
				structure.setMessage("No Wishlist Found");
				structure.setStatus(HttpStatus.BAD_REQUEST.value());
				return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);
			} else {
				structure.setData(list);
				structure.setMessage("Wishlist");
				structure.setStatus(HttpStatus.OK.value());
				return new ResponseEntity<>(structure, HttpStatus.OK);
			}
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<List<Wishlist>>> viewWishlistProducts(String token, int id, String email) {
		Customer customer = customerRepository.findByEmail(email);
		ResponseStructure<List<Wishlist>> structure = new ResponseStructure<>();
		if (jwtUtil.isValidToken(token)) { // Corrected condition
			structure.setMessage("Unauthorized");
			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
		} else {
			List<Wishlist> wishlist = customer.getWishlists();
			if (wishlist == null || wishlist.isEmpty()) { // Simplified condition
				structure.setData(null);
				structure.setMessage("No items present");
				structure.setStatus(HttpStatus.BAD_REQUEST.value());
				return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);
			} else {
				structure.setData(wishlist);
				structure.setMessage("Products in Wishlist");
				structure.setStatus(HttpStatus.OK.value());
				return new ResponseEntity<>(structure, HttpStatus.OK);
			}
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<Wishlist>> addToWishList(int wid, int pid, String token, String email) {
		Customer customer = customerRepository.findByEmail(email);
		ResponseStructure<Wishlist> structure = new ResponseStructure<>();
		if (jwtUtil.isValidToken(token)) {
			structure.setMessage("Unauthorized");
			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
		} else {
			if (customer == null) {
				structure.setData(null);
				structure.setMessage("No customer found");
				structure.setStatus(HttpStatus.BAD_REQUEST.value());
				return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);
			} else {
				Wishlist wishlist = wishlistRepository.findById(wid).orElse(null);
				Product product = productRepository.findById(pid).orElse(null);

				List<Product> list = wishlist.getProducts();
				if (list == null) {
					list = new ArrayList<>();
				}
				boolean flag = true;
				for (Product product2 : list) {
					if (product2 == product) {
						flag = false;
						break;
					}
				}
				if (flag) {
					list.add(product);

					wishlist.setProducts(list);
					wishlistRepository.save(wishlist);
					structure.setData(wishlist);
					structure.setMessage("Item added to Wishlist");
					structure.setStatus(HttpStatus.OK.value());
					return new ResponseEntity<>(structure, HttpStatus.OK);
				} else {
					structure.setData(null);
					structure.setMessage("Item already exists in Wishlist");
					structure.setStatus(HttpStatus.ALREADY_REPORTED.value());
					return new ResponseEntity<>(structure, HttpStatus.ALREADY_REPORTED);
				}
			}
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<Wishlist>> removeFromWishList(int wid, int pid, String token,
			String email) {
		Customer customer = customerRepository.findByEmail(email);
		ResponseStructure<Wishlist> structure = new ResponseStructure<>();
		if (jwtUtil.isValidToken(token)) {
			structure.setMessage("Unauthorized");
			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
		} else {
			if (customer == null) {
				structure.setData(null);
				structure.setMessage("No customer found");
				structure.setStatus(HttpStatus.BAD_REQUEST.value());
				return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);
			} else {
				Wishlist wishlist = wishlistRepository.findById(wid).orElse(null);
				Product product = productRepository.findById(pid).orElse(null);
				wishlist.getProducts().remove(product);
				wishlistRepository.save(wishlist);
				structure.setData(wishlist);
				structure.setMessage("Item Removed from Wish list");
				structure.setStatus(HttpStatus.ACCEPTED.value());
				return new ResponseEntity<>(structure, HttpStatus.ACCEPTED);
			}
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<Wishlist>> deleteWishlist(int wid, String token, String email) {

		Customer customer = customerRepository.findByEmail(email);
		ResponseStructure<Wishlist> structure = new ResponseStructure<>();
		if (jwtUtil.isValidToken(token)) {
			structure.setMessage("Unauthorized");
			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
		} else {
			if (customer == null) {
				structure.setData(null);
				structure.setMessage("No customer found");
				structure.setStatus(HttpStatus.BAD_REQUEST.value());
				return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);
			} else {
				Wishlist wishlist = wishlistRepository.findById(wid).orElse(null);
				Wishlist wishlist2 = null;
				for (Wishlist wishlist3 : customer.getWishlists()) {
					if (wishlist3.getName().equals(wishlist.getName())) {
						wishlist2 = wishlist3;
					}
				}

				customer.getWishlists().remove(wishlist2);
				wishlistRepository.delete(wishlist);
				customerRepository.save(customer);
				structure.setData(wishlist);
				structure.setMessage("Wishlist deleted Success");
				structure.setStatus(HttpStatus.OK.value());
				return new ResponseEntity<>(structure, HttpStatus.OK);
			}
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<List<Payment>>> checkPayment(String token, String email) {

		ResponseStructure<List<Payment>> structure = new ResponseStructure<>();
		Customer customer = customerRepository.findByEmail(email);
		if (jwtUtil.isValidToken(token)) {
			structure.setMessage("Unauthorized");
			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
		} else {
			if (customer == null) {
				structure.setData(null);
				structure.setMessage("No customer found");
				structure.setStatus(HttpStatus.BAD_REQUEST.value());
				return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);
			} else {
				List<Payment> payments = paymentRepository.findAll();
				if (payments.isEmpty()) {
					structure.setData(null);
					structure.setMessage(
							"Sorry you can not place order, There is an internal error try after some time");
					structure.setStatus(HttpStatus.BAD_REQUEST.value());
					return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);
				} else {
					structure.setData(payments);
					structure.setMessage("List of Payments");
					structure.setStatus(HttpStatus.OK.value());
					return new ResponseEntity<>(structure, HttpStatus.OK);
				}
			}
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<Customer>> checkAddress(String token, String email, int pid) {

		ResponseStructure<Customer> structure = new ResponseStructure<>();
		Customer customer = customerRepository.findByEmail(email);
		if (jwtUtil.isValidToken(token)) {
			structure.setMessage("Unauthorized");
			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
		} else {
			if (customer == null) {
				structure.setData(null);
				structure.setMessage("No customer found");
				structure.setStatus(HttpStatus.BAD_REQUEST.value());
				return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);
			} else {
				structure.setData(customer);
				structure.setMessage("procced for payment");
				structure.setStatus(HttpStatus.OK.value());
				return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
			}
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<List<ShoppingOrder>>> viewOrders(String token, String email) {

		ResponseStructure<List<ShoppingOrder>> structure = new ResponseStructure<>();
		Customer customer = customerRepository.findByEmail(email);
		if (jwtUtil.isValidToken(token)) {
			structure.setMessage("Unauthorized");
			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
		} else {
			List<ShoppingOrder> list = customer.getOrders();
			if (list == null || list.isEmpty()) {
				structure.setData(null);
				structure.setMessage("No Orders Yet");
				structure.setStatus(HttpStatus.OK.value());
				return new ResponseEntity<>(structure, HttpStatus.OK);
			} else {
				structure.setData(list);
				structure.setMessage("Your Order");
				structure.setStatus(HttpStatus.CREATED.value());
				return new ResponseEntity<>(structure, HttpStatus.CREATED);
			}
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<ShoppingOrder>> submitOrder(String token, String email,
			@RequestParam int pid, @RequestParam String address) throws RazorpayException {
		ResponseStructure<ShoppingOrder> structure = new ResponseStructure<>();
		Customer customer = customerRepository.findByEmail(email);
		if (jwtUtil.isValidToken(token)) {
			structure.setMessage("Unauthorized");
			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
		} else {

			if (customer == null) {
				structure.setData(null);
				structure.setMessage("Logain Again");
				structure.setStatus(HttpStatus.UNAUTHORIZED.value());
				return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
			} else {
				Payment payment = paymentRepository.findById(pid).orElse(null);
				ShoppingOrder order = new ShoppingOrder();
				order.setAddress(address);
				order.setPaymentMode(payment.getName());
				order.setDeliveryDate(LocalDate.now().plusDays(3));
				ShoppingCart cart = customer.getShoppingCart();
				if (cart == null) {
					structure.setData(null);
					structure.setMessage("Please add items to your cart");
					structure.setStatus(HttpStatus.NOT_FOUND.value());
					return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
				}
				if (cart.getItems() == null || cart.getItems().isEmpty()) {
					structure.setData(null);
					structure.setMessage("Please add items to your cart");
					structure.setStatus(HttpStatus.BAD_REQUEST.value());
					return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);
				}
				double total = 0;
				for (Item item : cart.getItems()) {
					total = total + item.getPrice();
				}
				order.setTotalPrice(total);
				order.setItems(cart.getItems());

				if (payment.getName().equalsIgnoreCase("RazorPay")) {
					JSONObject object = new JSONObject();
					object.put("currency", "INR");
					object.put("amount", total * 100);

					RazorpayClient client = new RazorpayClient("rzp_test_a5jX27qK8Szlyb", "NmEibVAHSdK5qvYDrhLeUjZw");
					Order order1 = client.orders.create(object);
					order.setStatus(order1.get("status"));
					order.setCurrency("INR");
					order.setOrderId(order1.get("id"));
					order.setPayment_key("rzp_test_a5jX27qK8Szlyb");
					order.setCompany_name("E-Kart");
					structure.setMessage("Order created successfully");
					structure.setData(order);
					structure.setStatus(HttpStatus.CREATED.value());
					return new ResponseEntity<>(structure, HttpStatus.CREATED);
				} else {
					List<ShoppingOrder> list = customer.getOrders();
					if (list == null) {
						list = new ArrayList<>();
					}
					list.add(order);
					customer.setOrders(list);
					customer.setAddress(address);
					cart.setItems(null);
					customer.setShoppingCart(null);
//					Customer customer1 = customerRepository.save(customer);
					customerRepository.save(customer);
					cartRepository.delete(cart);
					structure.setMessage("Order created successfully");
					structure.setData(order);
					structure.setStatus(HttpStatus.CREATED.value());
					return new ResponseEntity<>(structure, HttpStatus.CREATED);
				}
			}
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<Customer>> logout(HttpSession httpSession) {
		httpSession.invalidate();
		ResponseStructure<Customer> structure = new ResponseStructure<>();
		structure.setStatus(HttpStatus.OK.value());
		structure.setMessage("Logged out successfully");
		return new ResponseEntity<>(structure, HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<ResponseStructure<List<Wishlist>>> addToWishlist(String email, String token, int id) {
		Customer customer = customerRepository.findByEmail(email);

		ResponseStructure<List<Wishlist>> structure = new ResponseStructure<>();
		if (jwtUtil.isValidToken(token)) {
			structure.setMessage("Unauthorized");
			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
		} else {
			List<Wishlist> list = customer.getWishlists();
			if (list.isEmpty()) {
				structure.setData(null);
				structure.setMessage("create wish list");
				structure.setStatus(HttpStatus.BAD_REQUEST.value());
				return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);
			} else {
				structure.setData(list);
				structure.setMessage("wishlist");
				structure.setStatus(HttpStatus.OK.value());
				return new ResponseEntity<>(structure, HttpStatus.OK);
			}

		}

	}

	@Override
	public ResponseEntity<ResponseStructure<List<Product>>> searchByBrandOrCategory(String brand, String category) {
		ResponseStructure<List<Product>> structure = new ResponseStructure<>();

		if (brand != null || category != null) {
			List<Product> products = productRepository.findByBrandOrCategory(brand, category);

			if (products.isEmpty()) {
				structure.setMessage("No customers found for the given brand or category.");
				structure.setData(null);
				structure.setStatus(HttpStatus.NOT_FOUND.value());
				return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
			}

			structure.setData(products);
			structure.setMessage("Customers found for the given brand or category.");
			structure.setStatus(HttpStatus.OK.value());
			return new ResponseEntity<>(structure, HttpStatus.OK);
		} else {
			structure.setMessage("Both brand and category are null. Provide at least one parameter.");
			structure.setStatus(HttpStatus.BAD_REQUEST.value());
			return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);
		}
	}
}
