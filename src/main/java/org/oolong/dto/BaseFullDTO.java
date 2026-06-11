package org.oolong.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BaseFullDTO extends BaseTimeDTO {
	
	private LocalDateTime deletedAt;
	private boolean deleted;
	
}
