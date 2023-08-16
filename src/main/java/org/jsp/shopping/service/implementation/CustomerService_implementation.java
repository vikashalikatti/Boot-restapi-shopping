package org.jsp.shopping.service.implementation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jsp.shopping.Repository.CustomerRepository;
import org.jsp.shopping.Repository.PaymentRepository;
import org.jsp.shopping.Repository.ProductRepository;
import org.jsp.shopping.Repository.ShoppingCartRepository;
import org.jsp.shopping.Repository.WishlistRepository;
import org.jsp.shopping.dto.Customer;
import org.jsp.shopping.dto.Item;
import org.jsp.shopping.dto.Product;
import org.jsp.shopping.dto.ShoppingCart;
import org.jsp.shopping.helper.ResponseStructure;
import org.jsp.shopping.helper.SendMail;
import org.jsp.shopping.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;

@Service
public class CustomerService_implementation implements CustomerService {

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	SendMail mail;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	Item item;

	@Autowired
	ShoppingCart shoppingCart;

	@Autowired
	WishlistRepository wishlistRepository;

	@Autowired
	PaymentRepository paymentRepository;

	@Autowired
	ShoppingCartRepository cartRepository;

	@Override
	public ResponseEntity<ResponseStructure<Customer>> signup(Customer customer, String date) {

		ResponseStructure<Customer> structure = new ResponseStructure<>();
		customer.setDob(LocalDate.parse(date));
		if (customerRepository.findByEmail(customer.getEmail()) != null
				|| customerRepository.findByMobile(customer.getMobile()) != null) {

			structure.setData(null);
			structure.setMessage("email and mobile shpuld be not to repeated");
			structure.setStatus(HttpStatus.ALREADY_REPORTED.value());
			return new ResponseEntity<>(structure, HttpStatus.ALREADY_REPORTED);
		}
		String token = "EKART" + new Random().nextInt(10000, 999999);
		customer.setToken(token);

		// logic for sending the mail
		if (mail.sendLink(customer)) {
			customerRepository.save(customer);

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
		if (customer.getToken().equals(token)) {
			customer.setStatus(true);
			customer.setToken(null);
			customerRepository.save(customer);
			structure.setData(customer);
			structure.setMessage("Account Created Succesfuully");
			structure.setStatus(HttpStatus.CREATED.value());
			return new ResponseEntity<>(structure, HttpStatus.CREATED);
		} else {
			structure.setData(null);
			structure.setMessage("Incorrect link");
			structure.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
			return new ResponseEntity<>(structure, HttpStatus.NOT_ACCEPTABLE);
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<Customer>> login(String email, String password, HttpSession session) {
		ResponseStructure<Customer> structure = new ResponseStructure<>();
		Customer customer = customerRepository.findByEmail(email);
		if (customer == null) {
			structure.setData(null);
			structure.setMessage("Incorrect Email");
			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
		} else {
			if (customer.getPassword().equals(password)) {
				if (customer.isStatus()) {
					session.setAttribute("customer", customer);

					structure.setData(customer);
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
	public ResponseEntity<ResponseStructure<List<Product>>> view_products(HttpSession session) {
		ResponseStructure<List<Product>> structure = new ResponseStructure<>();
		List<Product> products = productRepository.findByStatus(true);
		if (session.getAttribute("customer") == null) {
			structure.setData(null);
			structure.setMessage("Logain Again");
			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
		} else {
			if (products.isEmpty()) {
				structure.setData(null);
				structure.setMessage("No Products Present");
				structure.setStatus(HttpStatus.NOT_FOUND.value());
				return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
			} else {
				structure.setData(products);
				structure.setMessage("Products");
				structure.setStatus(HttpStatus.FOUND.value());
				return new ResponseEntity<>(structure, HttpStatus.FOUND);
			}
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<Product>> addCart(HttpSession session, int id) {
		Customer customer = (Customer) session.getAttribute("customer");
		ResponseStructure<Product> structure = new ResponseStructure<>();
		if (session.getAttribute("customer") == null) {
			structure.setData(null);
			structure.setMessage("Logain Again");
			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
		} else {
			Product product = productRepository.findById(id).orElse(null);
			if (product.getStock() >= 1) {

				ShoppingCart cart = customer.getShoppingCart();
				if (cart == null) {
					cart = this.shoppingCart;
				}
				List<Item> items = cart.getItems();
				if (items == null) {
					items = new ArrayList<>();
				}

				if (items.isEmpty()) {
					item.setDescription(product.getDescription());
					item.setImage(product.getImage());
					item.setName(product.getName());
					item.setPrice(product.getPrice());
					item.setQuantity(1);
					items.add(item);
				} else {
					boolean flag = false;
					for (Item item : items) {
						if (item.getName().equals(product.getName())) {
							item.setQuantity(item.getQuantity() + 1);
							item.setPrice(item.getPrice() + product.getPrice());
							item.setDescription(product.getDescription());
							item.setImage(product.getImage());
							flag = false;
							break;
						} else {
							flag = true;
						}
					}
					if (flag) {
						item.setDescription(product.getDescription());
						item.setImage(product.getImage());
						item.setName(product.getName());
						item.setPrice(product.getPrice());
						item.setQuantity(1);
						items.add(item);
					}
				}
				cart.setItems(items);
				customer.setShoppingCart(cart);

				product.setStock(product.getStock() - 1);
				productRepository.save(product);

				session.removeAttribute("customer");
				session.setAttribute("customer", customerRepository.save(customer));
				structure.setData(product);
				structure.setMessage("Product Added Successful");
				structure.setStatus(HttpStatus.ACCEPTED.value());
				return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
			} else {
				structure.setData(null);
				structure.setMessage("Out of Stock");
				structure.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
				return new ResponseEntity<>(structure, HttpStatus.NOT_ACCEPTABLE);
			}
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<List<Item>>> viewCart(HttpSession session) {
		Customer customer = (Customer) session.getAttribute("customer");
		ResponseStructure<List<Item>> structure = new ResponseStructure<>();
		if (customer == null) {
			structure.setData(null);
			structure.setMessage("Logain Again");
			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
		} else {

			if (customer.getShoppingCart() == null || customer.getShoppingCart().getItems() == null
					|| customer.getShoppingCart().getItems().isEmpty()) {
				structure.setData(null);
				structure.setMessage("No Items in cart");
				structure.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
				return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
			} else {
				List<Item> items = customer.getShoppingCart().getItems();
				structure.setData(items);
				structure.setMessage("Items");
				structure.setStatus(HttpStatus.FOUND.value());
				return new ResponseEntity<>(structure, HttpStatus.FOUND);
			}
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<List<Item>>> removeFromCart(HttpSession session, int id) {
		Customer customer = (Customer) session.getAttribute("customer");
		ResponseStructure<List<Item>> structure = new ResponseStructure<>();
		if (customer == null) {
			structure.setData(null);
			structure.setMessage("Logain Again");
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

			session.removeAttribute("customer");
			session.setAttribute("customer", customerRepository.save(customer));

			structure.setData(items);
			structure.setMessage("Product Removed from Cart Success");
			structure.setStatus(HttpStatus.OK.value());
			return new ResponseEntity<>(structure, HttpStatus.FOUND);
		}
	}
}
