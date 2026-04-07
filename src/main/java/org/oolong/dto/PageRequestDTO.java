package org.oolong.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class PageRequestDTO {
	
	private int page = 1;
	private int size = 10;
	
	public String toQueryString() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("page=").append(this.page);
		sb.append("&size=").append(this.size);
		
		return sb.toString();
		
	}
	
}
