package org.jsp.shopping.helper;

import java.sql.Date;
import java.util.UUID;

import org.jsp.shopping.dto.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {
	@Autowired
	SecretKeyGenerator keyGenerator;

	private static final long EXPIRATION_TIME_MS = 3600000;

	private static final String SECRET_KEY = new SecretKeyGenerator().key();

	public String generateJwtTokenForCustomer(Customer customer) {
		Date now = new Date(EXPIRATION_TIME_MS);
		Date expiration = new Date(now.getTime() + EXPIRATION_TIME_MS);

		String token = Jwts.builder().setId(UUID.randomUUID().toString())
				// Include customer ID as a claim
				.setSubject(customer.getEmail()).setIssuedAt(now).setExpiration(expiration)
				.signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();

		return token;
	}
}
