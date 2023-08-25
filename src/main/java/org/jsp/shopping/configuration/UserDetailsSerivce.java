package org.jsp.shopping.configuration;

import java.util.Collections;

import org.jsp.shopping.Repository.Admin_Repository;
import org.jsp.shopping.Repository.CustomerRepository;
import org.jsp.shopping.Repository.MerchantRepository;
import org.jsp.shopping.dto.Admin;
import org.jsp.shopping.dto.Customer;
import org.jsp.shopping.dto.Merchant;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsSerivce implements UserDetailsService {

	private CustomerRepository customerRepository;

	private MerchantRepository merchantRepository;

	private Admin_Repository admin_Repository;

	public UserDetailsSerivce(CustomerRepository customerRepository, MerchantRepository merchantRepository,
			Admin_Repository admin_Repository) {
		super();
		this.customerRepository = customerRepository;
		this.merchantRepository = merchantRepository;
		this.admin_Repository = admin_Repository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Customer customer = customerRepository.findByEmail(email);
		if (customer != null) {
			return new User(customer.getEmail(), customer.getPassword(),
					Collections.singletonList(new SimpleGrantedAuthority("customer")));
		}

		
		Merchant merchant = merchantRepository.findByEmail(email);
		if (merchant != null) {
			return new User(merchant.getEmail(), merchant.getPassword(),
					Collections.singletonList(new SimpleGrantedAuthority("merchant")));
		}

		
		Admin admin = admin_Repository.findByUsername(email);
		if (admin != null) {
			return new User(admin.getUsername(), admin.getPassword(),
					Collections.singletonList(new SimpleGrantedAuthority("admin")));
		}

		throw new UsernameNotFoundException("User not found: " + email);
	}
}
