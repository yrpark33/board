package org.oolong.dto;

import java.time.LocalDateTime;
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
	private LocalDateTime updatedAt;
	
	@Builder.Default
	private List<FileDTO> files = new ArrayList<>();
	
	
    public List<FileDTO> getFiles() {
        return this.files == null ? new ArrayList<>() : this.files;
    }
	
	public String getCreatedTime() {
		return getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}
	
	public String getUpdatedTime() {
		return getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}
	
	public void addFile(FileDTO boardFileDTO) {
		boardFileDTO.setSortOrder(this.files.size());
		files.add(boardFileDTO);
	}
	
	
	public void clearFiles() {
		files.clear();
	}
	
}
