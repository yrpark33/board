package org.oolong.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString(callSuper = true)
public class BoardPageResponseDTO extends PageResponseDTO {
	
	private List<BoardListDTO> dtoList;
	
	private String types;
	
	private String keyword;
	
	public BoardPageResponseDTO(List<BoardListDTO> dtoList, int page, int size, int totalCount, String types, String keyword) {
		
		super(page, size, totalCount);
		
		this.dtoList = dtoList;
		this.types = types;
		this.keyword = keyword;
		
	}
	
	
	
}
