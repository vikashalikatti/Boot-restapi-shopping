package org.jsp.shopping.Repository;

import org.jsp.shopping.dto.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepoditory extends JpaRepository<Customer, String> {

}
