package org.oolong.controller.view;

import java.io.IOException;

import org.oolong.dto.AccountDTO;
import org.oolong.service.AccountService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {
	
	
	private final AccountService accountService;
	
	
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
	
	@GetMapping("register")
	public String register(@AuthenticationPrincipal AccountDTO accountDTO) {
		
		if(accountDTO != null) {
			return "redirect:/board/list";
		}
		
		return "/account/register";
		
	}
	
	
	@PostMapping("register")
	public String registerPOST(AccountDTO accountDTO, RedirectAttributes rttr) {
		
		
		if(!StringUtils.hasText(accountDTO.getUsername()) ||
			!StringUtils.hasText(accountDTO.getPassword()) ||
			!StringUtils.hasText(accountDTO.getName()) ||
			!StringUtils.hasText(accountDTO.getEmail())) {
			rttr.addFlashAttribute("errorMsg", "모든 항목을 입력해주세요.");
			
			return "redirect:/account/register";
		}
		
		accountService.register(accountDTO);
		
		return "redirect:/account/login";
	}
	
	
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("mypage")
	public void mypage(@AuthenticationPrincipal AccountDTO accountDTO, Model model) {
		
		
		model.addAttribute("account", accountService.read(accountDTO.getUsername()));
		
	}
	
	
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("modify")
	public void modify(@AuthenticationPrincipal AccountDTO accountDTO, Model model) {
		
		model.addAttribute("account", accountService.read(accountDTO.getUsername()));
		
	}
	
	
	
	@PostMapping("modify")
	public String modify(AccountDTO accountDTO, @RequestParam("file") MultipartFile file, @RequestParam(value = "deleteProfileImg", defaultValue = "false") String deleteProfileImg, @AuthenticationPrincipal AccountDTO loginUser, RedirectAttributes rttr) throws IOException {
		
		if(!StringUtils.hasText(accountDTO.getEmail())) {
			rttr.addFlashAttribute("errorMsg", "이메일을 입력해주세요.");
			return "redirect:/account/modify";
		}
		
		accountDTO.setUsername(loginUser.getUsername());
		
		if(deleteProfileImg.equals("true") && (file == null || file.isEmpty())) {
			
			accountService.deleteProfileImg(accountDTO);
			
		} else {
			
			accountService.modify(accountDTO, file);
		}
		
		AccountDTO updatedAccount = accountService.read(accountDTO.getUsername());
		UsernamePasswordAuthenticationToken newAuth = 
		    new UsernamePasswordAuthenticationToken(
		        updatedAccount, 
		        null, 
		        updatedAccount.getAuthorities()
		    );
		SecurityContextHolder.getContext().setAuthentication(newAuth);
		
		
		return "redirect:/account/mypage";
		
	}
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("remove")
	public String remove(@AuthenticationPrincipal AccountDTO accountDTO, HttpSession session, RedirectAttributes rttr) {
		
		accountService.remove(accountDTO.getUsername());
		session.invalidate();
		rttr.addFlashAttribute("msg", "탈퇴가 완료되었습니다.");
		return "redirect:/account/login";
	
	}
	
	
}
