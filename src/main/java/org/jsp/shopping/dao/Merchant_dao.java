package org.jsp.shopping.dao;

import org.jsp.shopping.Repository.MerchantRepository;
import org.jsp.shopping.Repository.ProductRepository;
import org.jsp.shopping.dto.Merchant;
import org.jsp.shopping.dto.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class Merchant_dao {
	@Autowired
	MerchantRepository merchantRepository;

	@Autowired
	ProductRepository productRepository;

	public Merchant findByEmail(String email) {
		return merchantRepository.findByEmail(email);
	}

	public Merchant findByMobile(long mobile) {
		return merchantRepository.findByMobile(mobile);
	}

	public Merchant save(Merchant merchant) {
		return merchantRepository.save(merchant);
	}

	public Product findProductByName(String name) {
		return productRepository.findByName(name);
	}

	public Product findProductById(int id) {
		return productRepository.findById(id).orElse(null);
	}

	public void removeProduct(Product product) {
		productRepository.delete(product);
	}

}
