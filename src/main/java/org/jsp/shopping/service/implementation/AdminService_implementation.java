package org.jsp.shopping.service.implementation;

import org.jsp.shopping.Repository.Admin_Repository;
import org.jsp.shopping.dto.Admin;
import org.jsp.shopping.helper.ResponseStructure;
import org.jsp.shopping.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;

@Service
public class AdminService_implementation implements AdminService {

	@Autowired
	Admin_Repository admin_Repository;

	@Override
	public ResponseEntity<ResponseStructure<Admin>> createAdmin(Admin admin) {
		ResponseStructure<Admin> structure = new ResponseStructure<>();
		int existingEntries = admin_Repository.countByUsernameAndPassword(admin.getUsername(), admin.getPassword());
		if (existingEntries == 0) {
			admin_Repository.save(admin);
			structure.setData(admin);
			structure.setMessage("Account Create for Admin");
			structure.setStatus(HttpStatus.CREATED.value());
			return new ResponseEntity<>(structure, HttpStatus.CREATED);
		} else {
			structure.setData(null);
			structure.setMessage("Admin Cannot More than one");
			structure.setStatus(HttpStatus.ALREADY_REPORTED.value());
			return new ResponseEntity<>(structure, HttpStatus.ALREADY_REPORTED);
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<Admin>> login(String username, String password, HttpSession session) {
		ResponseStructure<Admin> structure = new ResponseStructure<>();
		Admin admin = admin_Repository.findByUsername(username);
		if (admin == null) {
			structure.setData(null);
			structure.setMessage("Incorrect username");
			structure.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new ResponseEntity<>(structure, HttpStatus.UNAUTHORIZED);
		} else {
			if (admin.getPassword().equals(password)) {
				session.setAttribute("admin", admin);

				structure.setData(admin);
				structure.setMessage("Login Success");
				structure.setStatus(HttpStatus.CREATED.value());
				return new ResponseEntity<>(structure, HttpStatus.CREATED);
			} else {
				structure.setData(null);
				structure.setMessage("Incorrect Password");
				structure.setStatus(HttpStatus.BAD_REQUEST.value());
				return new ResponseEntity<>(structure, HttpStatus.BAD_REQUEST);
			}
		}
	}
}
