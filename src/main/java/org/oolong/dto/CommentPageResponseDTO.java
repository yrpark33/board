package org.oolong.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CommentPageResponseDTO extends PageResponseDTO {
	
	private List<CommentDTO> commentDTOList;
	
	private String username;
	
	private boolean admin;
	
	public CommentPageResponseDTO(List<CommentDTO> commentDTOList, int page, int size, int totalCount, String username, boolean admin) {
		
		super(page, size, totalCount);
		this.commentDTOList = commentDTOList;
		this.username = username;
		this.admin = admin;
		
	}

}
