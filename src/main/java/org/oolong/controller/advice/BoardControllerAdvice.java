package org.oolong.controller.advice;



import org.oolong.service.exception.ApplicationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.extern.log4j.Log4j2;


@ControllerAdvice(basePackages = "org.oolong.controller.view")
@Log4j2
public class BoardControllerAdvice {


	@ExceptionHandler(ApplicationException.class)
	public String handle(ApplicationException ex, RedirectAttributes rttr) {
	
	    String msg;
	
	    switch (ex.getCode()) {
	        case 404:
	            msg = "게시글을 찾을 수 없습니다.";
	            break;
	        case 500:
	            msg = "서버 오류가 발생했습니다.";
	            break;
	        case 403:
	        	msg = "접근 권한이 없습니다.";
	        	rttr.addFlashAttribute("errorMsg", msg);
	        	return "redirect:/";
	        default:
	            msg = "알 수 없는 오류가 발생했습니다.";
	    }
	
	    rttr.addFlashAttribute("errorMsg", msg);
	
	    return "redirect:/board/list";
	}
	
	@ExceptionHandler(Exception.class)
	public String handleAll(Exception ex, RedirectAttributes rttr) {
		
		
		log.error("exception type: " + ex.getClass().getName());
		
		if (ex instanceof AuthenticationException || ex instanceof AccessDeniedException) {
	        throw new RuntimeException(ex); // 시큐리티 예외는 다시 던지기
	    }
		
		log.error("Internal Server Error: ", ex);
		rttr.addFlashAttribute("errorMsg", "서비스 이용에 불편을 드려 죄송합니다. 잠시 후 다시 시도해 주세요.");
		return "redirect:/";
		
		
	}

}