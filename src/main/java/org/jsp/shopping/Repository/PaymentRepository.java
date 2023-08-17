package org.jsp.shopping.Repository;

import org.jsp.shopping.dto.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

	@Query("SELECT COUNT(a) FROM Payment a")
	int countByName(String name);

	Payment findByName(String name);

}
