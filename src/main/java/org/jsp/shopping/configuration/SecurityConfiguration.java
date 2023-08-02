//package org.jsp.shopping.configuration;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.provisioning.InMemoryUserDetailsManager;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfiguration {
//
//	@Bean
//	public UserDetailsService service() {
//		UserDetails userDetail1 = User.withUsername("admin").password(encoder().encode("admin")).roles("ADMIN").build();
//		UserDetails userDetail2 = User.withUsername("user").password(encoder().encode("user")).roles("USER").build();
//
//		return new InMemoryUserDetailsManager(userDetail1, userDetail2);
//	}
//
//	@Bean
//	public PasswordEncoder encoder() {
//		return new BCryptPasswordEncoder();
//	}
//
//	@Bean
//	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//
//		http.authorizeHttpRequests(n -> n.anyRequest().permitAll());
//		return http.build();
//	}
//
//}
