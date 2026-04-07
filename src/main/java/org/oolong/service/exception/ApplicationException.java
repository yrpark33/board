package org.oolong.service.exception;

import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {
	
	private int code;
	
	public ApplicationException(int code, String msg) {
		
		super(msg);
		this.code = code;
		
	}
	
}
