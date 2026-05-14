package org.oolong.service;

import java.util.List;

import org.oolong.dto.CommentDTO;
import org.oolong.dto.CommentPageResponseDTO;
import org.oolong.dto.PageRequestDTO;
import org.oolong.mapper.CommentMapper;
import org.oolong.service.exception.ApplicationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class CommentService {
	
	
	private final CommentMapper commentMapper;
	
	public void writeComment(CommentDTO commentDTO) {
		
		if(!StringUtils.hasText(commentDTO.getWriter())) {
			
			throw new ApplicationException(400, "작성자를 입력해주세요");
			
		}
		
		if(!StringUtils.hasText(commentDTO.getContent())) {
			
			throw new ApplicationException(400, "내용을 입력해주세요");
			
		}
		
		log.info("writeComment commentDTO: {}", commentDTO);
		
		int count = commentMapper.insert(commentDTO);
		
		if(count == 0) {
			throw new ApplicationException(500, "COMMENT_INSERT_ERROR");
		}
	}
	
	@Transactional(readOnly = true)
	public CommentDTO getComment(Long commentId) {

		
		log.debug("getComment commentId: {}", commentId);
		
		CommentDTO dto = commentMapper.selectById(commentId);
		
		if(dto == null) {
			throw new ApplicationException(404, "댓글이 존재하지 않습니다");
		}
		
		return dto;
		
	}
	
	public void modifyComment(CommentDTO commentDTO) {
		
		if(!StringUtils.hasText(commentDTO.getContent())) {
			throw new ApplicationException(400, "내용을 입력해주세요");
		}
		
		log.info("modifyComment commentDTO: {}", commentDTO);
		
		int count = commentMapper.update(commentDTO);
			
		if(count == 0) {
			throw new ApplicationException(404, "댓글이 존재하지 않습니다");
		}
		
	}
	
	public void removeComment(Long commentId) {
		
		log.info("removeComment commentId: {}", commentId);
		
		
		int count = commentMapper.delete(commentId);
			
		if(count == 0) {
			throw new ApplicationException(404, "댓글이 존재하지 않습니다");
		}
	}
	
	@Transactional(readOnly = true)
	public CommentPageResponseDTO getCommentList(Long boardId, PageRequestDTO pageRequestDTO) {
		
		int page = pageRequestDTO.getPage();
		
		int size = pageRequestDTO.getSize();
		
		page = page <= 0 ? 1 : page;
		size = size < 10 || size > 100 ? 10 : size;
		
		int offset = (page - 1) * size;
		
		
		log.debug("getCommentList boardId, pageRequestDTO : {}, {}", boardId, pageRequestDTO);
		
		
		List<CommentDTO> commentDTOList = commentMapper.selectList(boardId, offset, size);
		
		
		
		int totalCount = commentMapper.selectTotalCount(boardId);
		
		
		return new CommentPageResponseDTO(commentDTOList, page, size, totalCount);
		
	}
	
}
