package org.oolong.controller.view;

import org.oolong.dto.AccountDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
@RequestMapping("/account")
public class AccountController {
	
	@GetMapping("login")
	public String loginGET(@AuthenticationPrincipal AccountDTO accountDTO) {
		
		if(accountDTO != null) {
			return "redirect:/board/list";
		}
		
		
		return "/account/login";
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("logout")
	public void logoutGET() {
		
		
	}
	
	
}
