package org.oolong.dto;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardDTO extends BaseFullDTO {
	
	private Long boardId;
	private String title;
	private String content;
	private String writer;
	
	@Builder.Default
	private List<BoardFileDTO> files = new ArrayList<>();
	
	
    public List<BoardFileDTO> getFiles() {
        return this.files == null ? new ArrayList<>() : this.files;
    }
	
	public String getCreatedDate() {
		return getCreatedAt().format(DateTimeFormatter.ISO_DATE);
	}
	
	public void addFile(BoardFileDTO boardFileDTO) {
		boardFileDTO.setSortOrder(this.files.size());
		files.add(boardFileDTO);
	}
	
	
	public void clearFiles() {
		files.clear();
	}
	
}
