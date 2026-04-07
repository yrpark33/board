package org.oolong.dto;

import java.util.List;
import java.util.stream.IntStream;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class PageResponseDTO {
	
	private int totalCount;
	private int page, size;
	
	private int start, end;
	
	private boolean prev, next;
	
	private List<Integer> pageNums;
	
	public PageResponseDTO(int page, int size, int totalCount) {
		

		this.totalCount = totalCount;
		this.page = page;
		this.size = size;
		
		int tempEnd = (int) Math.ceil(page / 10.0) * 10;
		
		this.start = tempEnd - 9;
		
		this.prev = start != 1;
		
		if((tempEnd * size) > totalCount) {
			
			this.end = (int) Math.ceil(totalCount / (double) size);
			
		} else {
			
			this.end = tempEnd;
		}
		
		this.next = totalCount > this.end * size;
		
		
		this.pageNums = IntStream.rangeClosed(start, end).boxed().toList();
		
	}
	
}
