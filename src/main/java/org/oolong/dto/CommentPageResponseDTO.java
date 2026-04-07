package org.oolong.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CommentPageResponseDTO extends PageResponseDTO {
	
	private List<CommentDTO> commentDTOList;
	
	public CommentPageResponseDTO(List<CommentDTO> commentDTOList, int page, int size, int totalCount) {
		
		super(page, size, totalCount);
		this.commentDTOList = commentDTOList;
		
	}

}
