package org.jsp.shopping.Repository;

import java.util.List;

import org.jsp.shopping.dto.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Integer> {

	Product findByName(String name);

	List<Product> findByStatus(boolean flag);

	@Query("SELECT p FROM Product p WHERE p.brand = :brand OR p.category = :category")
	List<Product> findByBrandOrCategory(@Param("brand") String brand, @Param("category") String category);

}
