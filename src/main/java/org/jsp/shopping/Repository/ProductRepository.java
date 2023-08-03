package org.jsp.shopping.Repository;

import java.util.List;

import org.jsp.shopping.dto.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {

	Product findByName(String name);

	List<Product> findByStatus(boolean flag);

}
