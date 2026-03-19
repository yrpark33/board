package org.oolong.dto;

import java.util.List;

import lombok.Data;

@Data
public class PageResponseDTO {
	
	private List<BoardDTO> boardDTOList;
	private int totalCount;
	private int page, size;
	
	
	
	
}
