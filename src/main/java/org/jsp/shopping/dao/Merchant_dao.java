package org.jsp.shopping.dao;

import org.jsp.shopping.Repository.MerchantRepository;
import org.jsp.shopping.dto.Merchant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class Merchant_dao {
	@Autowired
	MerchantRepository merchantRepository;

	public Merchant findByEmail(String email) {
		return merchantRepository.findByEmail(email);
	}

	public Merchant findByMobile(long mobile) {
		return merchantRepository.findByMobile(mobile);
	}

	public Merchant save(Merchant merchant) {
		return merchantRepository.save(merchant);
	}

}
