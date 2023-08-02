package org.jsp.shopping.controller;

import org.jsp.shopping.dto.Merchant;
import org.jsp.shopping.exception.NoZeroException;
import org.jsp.shopping.helper.ResponseStructure;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/hello")
public class MerchantController {

	@PostMapping
	public ResponseEntity<ResponseStructure<Merchant>> saveStudent(@RequestBody Merchant merchant) throws NoZeroException {
		ResponseStructure<Merchant> structure = new ResponseStructure<>();
		if (merchant.getMobile() == 0000000000) {
			throw new NoZeroException("Mobile Number Not repeated")	;
			} else {
			structure.setData(merchant);
			structure.setStatus(HttpStatus.CREATED.value());
			structure.setMessage("Account created SUccess");
			return new ResponseEntity<ResponseStructure<Merchant>>(structure, HttpStatus.CREATED);
		}
	}

}
