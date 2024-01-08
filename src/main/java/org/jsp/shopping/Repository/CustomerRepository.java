package org.jsp.shopping.Repository;

import java.util.List;

import org.jsp.shopping.dto.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, String> {

	Customer findByEmail(String email);

	Customer findByMobile(long mobile);

	
}
