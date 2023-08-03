package org.jsp.shopping.service;

import java.io.IOException;

import org.jsp.shopping.dto.Merchant;
import org.jsp.shopping.helper.ResponseStructure;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface MerachantService {
	public ResponseEntity<ResponseStructure<Merchant>> signup(Merchant merchant, String date, MultipartFile pic)
			throws IOException;

	public ResponseEntity<ResponseStructure<Merchant>> verifyOtp(String email, int otp);
}
