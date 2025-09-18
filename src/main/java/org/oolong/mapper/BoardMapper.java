package org.oolong.mapper;

import java.util.List;

import org.oolong.dto.BoardDTO;

public interface BoardMapper {
	
	int insert(BoardDTO vo);
	
	BoardDTO selectOne(Long bno);
	
	int remove(Long bno);
	
	int update(BoardDTO dto);
	
	List<BoardDTO> list();
}
