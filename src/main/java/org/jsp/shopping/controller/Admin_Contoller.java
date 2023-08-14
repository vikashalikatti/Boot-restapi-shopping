package org.jsp.shopping.controller;

import org.jsp.shopping.dto.Admin;
import org.jsp.shopping.helper.ResponseStructure;
import org.jsp.shopping.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/admin")
public class Admin_Contoller {
	@Autowired
	AdminService adminService;

	@PostMapping("/create")
	public ResponseEntity<ResponseStructure<Admin>> createAdmin(@ModelAttribute Admin admin) {
		return adminService.createAdmin(admin);
	}

	@PostMapping("/login")
	public ResponseEntity<ResponseStructure<Admin>> login(String username, String password, HttpSession session) {
		return adminService.login(username, password, session);
	}
}
