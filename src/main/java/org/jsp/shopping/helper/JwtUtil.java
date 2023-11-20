package org.jsp.shopping.helper;

import java.time.Duration;
import java.util.Date;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.jsp.shopping.Repository.MerchantRepository;
import org.jsp.shopping.dto.Admin;
import org.jsp.shopping.dto.Customer;
import org.jsp.shopping.dto.Merchant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {
	@Autowired
	SecretKeyGenerator keyGenerator;

	@Autowired
	MerchantRepository merchantRepository;

	private static final long EXPIRATION_TIME_MS = 3600000;

	private static final String SECRET_KEY = new SecretKeyGenerator().key();

	public String generateJwtTokenForCustomer(Customer customer, Duration duration) {
		Date now = new Date();
		Date expiration = new Date(now.getTime() + duration.toMillis());

		String alphanumericCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuilder randomString = new StringBuilder();
		Random random = new Random();
		int length = 10; // You can adjust the length of the random string

		for (int i = 0; i < length; i++) {
			int index = random.nextInt(alphanumericCharacters.length());
			char randomChar = alphanumericCharacters.charAt(index);
			randomString.append(randomChar);
		}

		String token = Jwts.builder().setId(UUID.randomUUID().toString()).setSubject(randomString.toString())
				.setIssuedAt(now).setExpiration(expiration).signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();

		return token;
	}

	public boolean isTokenExpired(String token) {
		Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
		Date expirationDate = claims.getExpiration();
		Date currentDate = new Date();
		return expirationDate.before(currentDate);
	}

	public String generateToken(UserDetails userDetails, String role, Date expirationDate) {
		// Create the JWT token
		String token = Jwts.builder().setSubject(userDetails.getUsername() + " | " + role) // Set the subject as the
																							// username
				.setExpiration(expirationDate) // Set the custom expiration date
				.signWith(SignatureAlgorithm.HS256, SECRET_KEY) // Sign the token with your secret key
				.compact();

		return token;
	}

	public boolean isValidToken(String authToken) {
		Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(authToken).getBody();
		Date expirationDate = claims.getExpiration();
		Date currentDate = new Date();
		return expirationDate.before(currentDate);

	}

	private Claims parseJwtClaims(String authToken) {
		if (authToken == null) {
			return null;
		}

		try {
			return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(authToken).getBody();
		} catch (JwtException | IllegalArgumentException e) {
			return null;
		}
	}

	public Merchant extractMerchantFromToken(String authToken) {
		try {
			Claims claims = parseJwtClaims(authToken);
			if (claims == null) {
				return null;
			}

			Integer merchantId = claims.get("merchantId", Integer.class);
			if (merchantId == null) {
				return null; // Handle missing or invalid merchantId gracefully
			}

			Optional<Merchant> merchantOptional = merchantRepository.findById(merchantId);
			return merchantOptional.orElse(null);
		} catch (JwtException | IllegalArgumentException e) {
			e.printStackTrace(); // Log the exception for debugging
			return null;
		}
	}

	public String generateToken_for_admin(UserDetails userDetails, String role, Date expirationDate) {
		String token = Jwts.builder().setSubject(userDetails.getUsername() + " | " + role) // Set the subject as the
																							// username
				.setExpiration(expirationDate) // Set the custom expiration date
				.signWith(SignatureAlgorithm.HS256, SECRET_KEY) // Sign the token with your secret key
				.compact();
		return token;
	}

	public boolean validateJwtToken(String token, Customer customer) {
		Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
		Date expirationDate = claims.getExpiration();
		Date currentDate = new Date();
		return expirationDate.before(currentDate);
	}
}
