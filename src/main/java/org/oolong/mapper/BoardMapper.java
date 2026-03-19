package org.oolong.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.oolong.dto.BoardDTO;

public interface BoardMapper {
	
	int insert(BoardDTO boardDTO);
	
	BoardDTO selectById(Long boardId);
	
	int delete(Long boardId);
	
	int update(BoardDTO boardDTO);
	
	List<BoardDTO> selectList();
	
}
