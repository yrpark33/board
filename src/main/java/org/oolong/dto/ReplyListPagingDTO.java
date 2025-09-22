package org.oolong.dto;

import java.util.List;
import java.util.stream.IntStream;

import lombok.Data;

@Data
public class ReplyListPagingDTO {
	
	private List<ReplyDTO> replyDTOList;
	
	private int page, size;
	private int start, end;
	
	private int totalCount;
	
	private boolean prev, next;
	
	private List<Integer> pageNums;
	
	public ReplyListPagingDTO(List<ReplyDTO> replyDTOList, int totalCount, int page, int size) {
		
		this.replyDTOList = replyDTOList;
		this.totalCount = totalCount;
		this.page = page;
		this.size = size;
		
		
		
		int tempEnd = (int) (Math.ceil(page / 10.0)) * 10;
		
		this.start = tempEnd - 9;
		this.prev = (this.start != 1);
		
		if(tempEnd * size > totalCount) {
			
			this.end = (int) Math.ceil(totalCount / (double) size);
		
		} else {
			this.end = tempEnd;
		}
		
		
		
		
		this.next = totalCount > (this.end * size);
		
		
		this.pageNums = IntStream.rangeClosed(this.start, this.end).boxed().toList();
		
		
	}
}
