package org.jsp.shopping.Repository;

import org.jsp.shopping.dto.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {

	Wishlist findByName(String name);

}
