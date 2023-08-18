package org.jsp.shopping.Repository;

import org.jsp.shopping.dto.ShoppingOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingOrderRepository extends JpaRepository<ShoppingOrder, Integer>{

}
