package org.oolong.dto;

import java.util.List;
import java.util.stream.IntStream;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BoardPageResponseDTO {
	
	private List<BoardDTO> boardDTOList;
	private int totalCount;
	private int page, size;
	
	private int start, end;
	
	private boolean prev, next;
	
	private List<Integer> pageNums;
	
	private String types;
	
	private String keyword;
	
	public BoardPageResponseDTO(List<BoardDTO> boardDTOList, int totalCount, int page, int size, String types, String keyword) {
		
		this.boardDTOList = boardDTOList;
		this.totalCount = totalCount;
		this.page = page;
		this.size = size;
		this.types = types;
		this.keyword = keyword;
		
		int tempEnd = (int) (Math.ceil(page / 10.0)) * 10;
		
		this.start = tempEnd - 9;
		
		this.prev = start != 1;
		
		if((tempEnd * size) > totalCount) {
			this.end = (int) Math.ceil(totalCount / (double) size);
		} else {
			this.end = tempEnd;
		}
		
		this.next = totalCount > (this.end * size);
		
		this.pageNums = IntStream.rangeClosed(start, end).boxed().toList();
		
	}
	
	
}
