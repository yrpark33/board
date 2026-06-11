package org.oolong.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import lombok.extern.log4j.Log4j2;

@Configuration
@Log4j2
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
	DataSource dataSource;
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		log.info("-------------security config--------------------");
		
		http.formLogin(config -> {
			
			config.loginPage("/account/login");
			config.successHandler(new CustomLoginSuccessHandler());
			
		});
		
		http.rememberMe(config -> {
			config.tokenRepository(persistentTokenRepository());
			config.tokenValiditySeconds(60*60*24*30);
		});
		
		
		http.exceptionHandling(handler -> {
			
			handler.accessDeniedHandler(new Custom403Handler());
			
		});
		
		http.logout(config -> {
			config.deleteCookies("JSESSIONID", "remember-me");
		});
		
		
		return http.build();
		
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		
		return new BCryptPasswordEncoder();
		
	}
	
	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
		
		JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
		
		tokenRepository.setDataSource(dataSource);
		
		return tokenRepository;
		
	}
	
}
