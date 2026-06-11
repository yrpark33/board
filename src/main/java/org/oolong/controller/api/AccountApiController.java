package org.oolong.controller.api;

import java.util.Map;

import org.oolong.dto.AccountDTO;
import org.oolong.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountApiController {
	
	private final AccountService accountService;
	
	private final PasswordEncoder encoder;
	
	
	@GetMapping("checkUsername")
	public Map<String, Boolean> checkUsername(@RequestParam String username) {
		
		return Map.of("duplicate", accountService.isUsernameDuplicate(username));
		
		
	}
	
	
	@GetMapping("checkEmail")
	public Map<String, Boolean> checkEmail(@RequestParam String email) {
		
		return Map.of("duplicate", accountService.isEmailDuplicate(email));
		
	}
	
	
	@PostMapping("checkPassword")
	public Map<String, Boolean> checkPassword(@RequestParam String password, @AuthenticationPrincipal AccountDTO accountDTO) {
	    boolean matches = encoder.matches(password, accountDTO.getPassword());
	    return Map.of("matches", matches);
	}
	
	
	
	@PostMapping("changePassword")
	public ResponseEntity<Void> changePassword(@RequestParam String newPassword, @AuthenticationPrincipal AccountDTO accountDTO, HttpSession httpSession, HttpServletRequest request, HttpServletResponse response) {
		
		accountService.changePassword(accountDTO.getUsername(), newPassword);
		
		httpSession.invalidate();
		
		Cookie cookie = new Cookie("remember-me", null);
	    cookie.setMaxAge(0);
	    cookie.setPath("/");
	    response.addCookie(cookie);
		
		return ResponseEntity.ok().build();
		
	}
	
	
}
