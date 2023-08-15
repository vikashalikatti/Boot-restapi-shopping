package org.jsp.shopping.service.implementation;

import java.util.List;

import org.jsp.shopping.Repository.Admin_Repository;
import org.jsp.shopping.Repository.CustomerRepoditory;
import org.jsp.shopping.Repository.MerchantRepository;
import org.jsp.shopping.Repository.ProductRepository;
import org.jsp.shopping.dto.Admin;
import org.jsp.shopping.dto.Customer;
import org.jsp.shopping.dto.Merchant;
import org.jsp.shopping.dto.Product;
import org.jsp.shopping.helper.ResponseStructure;
import org.jsp.shopping.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;

@Service
public class AdminService_implementation implements AdminService {

	@Autowired
	Admin_Repository admin_Repository;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	MerchantRepository merchantRepository;
	
	@Autowired
	CustomerRepoditory customerRepoditory;

	@Override
	public ResponseEntity<ResponseStructure<Admin>> createAdmin(Admin admin) {
		ResponseStructure<Admin> structure = new ResponseStructure<>();
		int existingEntries = admin_Repository.countByUsernameAndPassword(admin.getUsername(), admin.getPassword());
		if (existingEntries == 0) {
			admin_Repository.save(admin);
			structure.setData(admin);
			structure.setMessage("Account Create for Admin");
			structure.setStatus(HttpStatus.CREATED.value());
			return new ResponseEntity<>(structure, HttpStatus.CREATED);
		} else {
			structure.setData(null);
			structure.setMessage("Admin Cannot More than one");
			structure.setStatus(HttpStatus.ALREADY_REPORTED.value());
			return new ResponseEntity<>(structure, HttpStatus.ALREADY_REPORTED);
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<Admin>> login(String username, String password, HttpSession session) {
		ResponseStructure<Admin> structure = new ResponseStructure<>();
		Admin admin = admin_Repository.findByUsername(username);
		if (admin == null) {
			structure.setData(null);
			structure.setMessage("Incorrect username");
			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
		} else {
			if (admin.getPassword().equals(password)) {
				session.setAttribute("admin", admin);

				structure.setData(admin);
				structure.setMessage("Login Success");
				structure.setStatus(HttpStatus.CREATED.value());
				return new ResponseEntity<>(structure, HttpStatus.CREATED);
			} else {
				structure.setData(null);
				structure.setMessage("Incorrect Password");
				structure.setStatus(HttpStatus.BAD_REQUEST.value());
				return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);
			}
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<List<Product>>> viewAllProducts(HttpSession session) {
		ResponseStructure<List<Product>> structure = new ResponseStructure<>();
		if (session.getAttribute("admin") == null) {
			structure.setData(null);
			structure.setMessage("Login Again");
			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
		} else {
			List<Product> products = productRepository.findAll();
			if (products.isEmpty()) {
				structure.setMessage("No Products Found");
				structure.setData(null);
				structure.setStatus(HttpStatus.NOT_FOUND.value());
				return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
			} else {
				structure.setMessage("Product Found");
				structure.setData(products);
				structure.setStatus(HttpStatus.FOUND.value());
				return new ResponseEntity<>(structure, HttpStatus.FOUND);
			}
		}

	}

	@Override
	public ResponseEntity<ResponseStructure<List<Product>>> changeStatus(int id, HttpSession session) {
		ResponseStructure<List<Product>> structure = new ResponseStructure<>();
		if (session.getAttribute("admin") == null) {
			structure.setData(null);
			structure.setMessage("Login Again");
			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
		} else {
			Product product = productRepository.findById(id).orElse(null);
			if (product.isStatus()) {
				product.setStatus(false);
			} else {
				product.setStatus(true);
			}
			productRepository.save(product);
			List<Product> products = productRepository.findAll();
			if (products.isEmpty()) {
				structure.setData(null);
				structure.setMessage("No Products Data");
				structure.setStatus(HttpStatus.NOT_FOUND.value());
				return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
			} else {
				structure.setData(products);
				structure.setMessage("Status Changed Success");
				structure.setStatus(HttpStatus.ACCEPTED.value());
				return new ResponseEntity<>(structure, HttpStatus.ACCEPTED);
			}
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<List<Merchant>>> viewallmerchant(HttpSession session) {
		ResponseStructure<List<Merchant>> structure = new ResponseStructure<>();
		if (session.getAttribute("admin") == null) {
			structure.setData(null);
			structure.setMessage("Login Again");
			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
		} else {
			List<Merchant> merchants = merchantRepository.findAll();
			if (merchants.isEmpty()) {
				structure.setData(null);
				structure.setMessage("No Merchants Data");
				structure.setStatus(HttpStatus.NOT_FOUND.value());
				return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
			} else {
				structure.setData(merchants);
				structure.setMessage("Merchants Data");
				structure.setStatus(HttpStatus.FOUND.value());
				return new ResponseEntity<>(structure, HttpStatus.FOUND);
			}
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<List<Customer>>> viewallcustomer(HttpSession session) {
		ResponseStructure<List<Customer>> structure = new ResponseStructure<>();
		if (session.getAttribute("admin") == null) {
			structure.setData(null);
			structure.setMessage("Login Again");
			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
		} else {
			List<Customer> customers  = customerRepoditory.findAll();
			if (customers.isEmpty()) {
				structure.setData(null);
				structure.setMessage("No Customers Data");
				structure.setStatus(HttpStatus.NOT_FOUND.value());
				return new ResponseEntity<>(structure, HttpStatus.NOT_FOUND);
			} else {
				structure.setData(customers);
				structure.setMessage("Customers Data");
				structure.setStatus(HttpStatus.FOUND.value());
				return new ResponseEntity<>(structure, HttpStatus.FOUND);
			}
		}
	}
}
