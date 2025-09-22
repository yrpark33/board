package org.oolong.controller.advice;

import org.oolong.service.exception.ReplyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.log4j.Log4j2;

@RestControllerAdvice
@Log4j2
public class ReplyControllerAdvice {
	
	@ExceptionHandler(ReplyException.class)
	public ResponseEntity<String> handleReplyError(ReplyException ex) {
		
		log.error(ex.getMessage());
		
		return ResponseEntity.status(ex.getCode()).body(ex.getMsg());
		
	}
	
}
