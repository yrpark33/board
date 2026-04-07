package org.oolong.controller.advice;

import org.oolong.service.exception.ApplicationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.log4j.Log4j2;


@RestControllerAdvice(basePackages = "org.oolong.controller.api")
@Log4j2
public class CommentControllerAdvice {
	
	@ExceptionHandler(ApplicationException.class)
	public ResponseEntity<String> handleCommentError(ApplicationException ex) {
		log.error("code: {}, msg: {}", ex.getCode(), ex.getMessage());
		return ResponseEntity.status(ex.getCode()).body(ex.getMessage());
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleAll(Exception ex) {
		log.error("Internal Server Error: ", ex);
		return ResponseEntity.status(500).body("서버 내부 오류가 발생했습니다. 관리자에게 문의하세요.");
	}
}
