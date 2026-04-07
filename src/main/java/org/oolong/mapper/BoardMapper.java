package org.oolong.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.oolong.dto.BoardDTO;

public interface BoardMapper {
	
	int insert(BoardDTO boardDTO);
	
	BoardDTO selectById(Long boardId);
	
	int delete(Long boardId);
	
	int update(BoardDTO boardDTO);
	
	int selectTotalCountSearch(@Param("types") String[] types, @Param("keyword") String keyword);
	
	List<BoardDTO> selectListSearch(@Param("offset") int offset, @Param("limit") int limit, @Param("types") String[] types, @Param("keyword") String keyword);
	
}
