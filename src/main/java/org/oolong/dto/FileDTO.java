package org.oolong.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDTO {
	
	private Long fileId;
	private Long boardId;
	private String fileName;
	private String uuid;
	private int sortOrder;
	private boolean image;
}
