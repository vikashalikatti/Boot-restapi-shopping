package org.jsp.shopping.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
//
//	@Bean
//	public UserDetailsService service() {
//		UserDetails userDetail1 = User.withUsername("admin").password(encoder().encode("admin")).roles("ADMIN").build();
//		UserDetails userDetail2 = User.withUsername("user").password(encoder().encode("user")).roles("USER").build();
//
//		return new InMemoryUserDetailsManager(userDetail1, userDetail2);
//	}
//
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

//
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

//		http.authorizeHttpRequests(n -> n.anyRequest().permitAll());
//		return http.build();
//		http.cors()
//		.and()
//		.csrf().disable()
//		.authorizeRequests().requestMatchers("**")
////		.antMatchers("/error","/auth","/authbytoken")
//		.permitAll()
//		.anyRequest().authenticated()
//		.and()
////		.exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
////		.and()
//		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//
////		http.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class)
//		return http.build();
		http.cors().and().csrf().disable().authorizeRequests().requestMatchers("**").permitAll().anyRequest().authenticated().and().httpBasic(); 
		return http.build();
	}
//
}
