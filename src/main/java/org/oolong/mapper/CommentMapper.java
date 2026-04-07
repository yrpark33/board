package org.oolong.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.oolong.dto.CommentDTO;

public interface CommentMapper {
	int insert(CommentDTO commentDTO);
	CommentDTO selectById(Long commentId);
	int delete(Long commentId);
	int update(CommentDTO commentDTO);
	List<CommentDTO> selectList(@Param("boardId") Long boardId, @Param("offset") int offset, @Param("limit") int limit);
	int selectTotalCount(Long boardId);
}
