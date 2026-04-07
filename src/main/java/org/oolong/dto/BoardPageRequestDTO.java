package org.oolong.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class BoardPageRequestDTO extends PageRequestDTO {

	
	private String types;
	private String keyword;
	
	@Override
	public String toQueryString() {
		
		
		String queryString = super.toQueryString();
		if(this.types != null && this.keyword != null) {
		
			queryString += "&types=" + types + "&keyword=" + keyword;
		}
		
		
		return queryString;
	}
	
}
