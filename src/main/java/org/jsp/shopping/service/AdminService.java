package org.jsp.shopping.service;

import org.jsp.shopping.dto.Admin;
import org.jsp.shopping.helper.ResponseStructure;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpSession;

public interface AdminService {

	ResponseEntity<ResponseStructure<Admin>> login(String username, String password, HttpSession session);

	ResponseEntity<ResponseStructure<Admin>> createAdmin(Admin admin);

}
