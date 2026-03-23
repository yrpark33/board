package org.oolong.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BoardPageRequestDTO {

	private int page = 1;
	private int size = 10;
	private String types;
	private String keyword;
	
	
	public String toQueryString() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("page=").append(this.page);
		sb.append("&size=").append(this.size);
		
		if(this.types != null && this.keyword != null) {
			sb.append("&types=").append(this.types);
			sb.append("&keyword=").append(this.keyword);
		}
		
		
		return sb.toString();
	}
	
}
