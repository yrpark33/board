package org.oolong.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.Data;

@Data
public class BoardListDTO {
	
	private Long boardId;
	
	private String title;
	
	private String writer;
	
	private LocalDateTime createdAt;
	
	private int commentCount;
	
	private String uuid;
	
	private String fileName;
	
	
	public String getCreatedDate() {
		
		return createdAt.format(DateTimeFormatter.ISO_DATE);
	
	}
	
	
	
}
