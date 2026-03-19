package org.oolong.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardDTO {
	
	private Long boardId;
	private String title;
	private String content;
	private String writer;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private boolean isDeleted;
	private LocalDateTime deletedAt;
	
	
	public String getCreatedDate() {
		return createdAt.format(DateTimeFormatter.ISO_DATE);
	}
	
}
