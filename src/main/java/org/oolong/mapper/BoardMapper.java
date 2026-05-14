package org.oolong.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.oolong.dto.BoardDTO;
import org.oolong.dto.BoardListDTO;

public interface BoardMapper {
	
	int insert(BoardDTO boardDTO);
	
	BoardDTO selectById(Long boardId);
	
	int delete(Long boardId);
	
	int update(BoardDTO boardDTO);
	
	int selectTotalCountSearch(@Param("types") String[] types, @Param("keyword") String keyword);
	
	List<BoardListDTO> selectListSearch(@Param("offset") int offset, @Param("limit") int limit, @Param("types") String[] types, @Param("keyword") String keyword);
	
	int updateFilesAsDeleted(Long boardId);
	
	int insertFiles(BoardDTO boardDTO);
	
	int deleteFiles(Long boardId);
	
}
