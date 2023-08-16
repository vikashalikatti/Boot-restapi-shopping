package org.jsp.shopping.Repository;

import org.jsp.shopping.dto.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

	Payment findByName(String name);

}
