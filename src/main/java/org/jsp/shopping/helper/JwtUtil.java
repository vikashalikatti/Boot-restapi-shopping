package org.jsp.shopping.helper;

import java.security.Key;
import java.util.Base64;

import org.jsp.shopping.dto.Merchant;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {
	@Autowired
	SecretKeyGenerator keyGenerator;

	private static final String SECRET_KEY = new SecretKeyGenerator().key();

	public static String generateJwtToken(Merchant merchant) {
		Key key = Keys.hmacShaKeyFor(Base64.getEncoder().encode(SECRET_KEY.getBytes()));
		Claims claims = Jwts.claims();

		ObjectMapper objectMapper = new ObjectMapper();
		String merchantJson;
		try {
			merchantJson = objectMapper.writeValueAsString(merchant);
		} catch (Exception e) {
			throw new RuntimeException("Error serializing merchant object to JSON.", e);
		}

		claims.put("merchantData", merchantJson);

		return Jwts.builder().setClaims(claims).signWith(key).compact();
	}

	public static Merchant extractMerchantFromToken(String token) {
		Key key = Keys.hmacShaKeyFor(Base64.getEncoder().encode(SECRET_KEY.getBytes()));
		Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();

		String merchantJson = (String) claims.get("merchantData");

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readValue(merchantJson, Merchant.class);
		} catch (Exception e) {
			throw new RuntimeException("Error deserializing JSON to merchant object.", e);
		}
	}
}
