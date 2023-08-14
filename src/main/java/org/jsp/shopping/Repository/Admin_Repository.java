package org.jsp.shopping.Repository;

import org.jsp.shopping.dto.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface Admin_Repository extends JpaRepository<Admin, Integer> {

	@Query("SELECT COUNT(a) FROM Admin a")
	int countByUsernameAndPassword(String username, String password);

	Admin findByUsername(String username);
}
