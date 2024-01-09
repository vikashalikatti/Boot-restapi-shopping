package org.jsp.shopping.Repository;

import java.util.Optional;

import org.jsp.shopping.dto.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantRepository extends JpaRepository<Merchant, String> {

	Merchant findByEmail(String email);

	Merchant findByMobile(long mobile);

//	Optional<Merchant> findById(int merchantId);
}
