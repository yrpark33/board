package org.oolong.security;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Custom403Handler implements AccessDeniedHandler {
	
	
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
	
		log.info("------AccessDeniedHandler--------");
		
		String requestedWith = request.getHeader("X-Requested-With");
	    
	    if("XMLHttpRequest".equals(requestedWith)) {
	        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
	        response.getWriter().write("제한된 접근입니다.");
	    } else {
	        response.sendRedirect(request.getContextPath() + "/error/403");
	    }
	}

	
	
}
