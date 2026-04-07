package org.oolong.service;

import java.util.List;

import org.oolong.dto.BoardDTO;
import org.oolong.dto.BoardPageRequestDTO;
import org.oolong.dto.BoardPageResponseDTO;
import org.oolong.mapper.BoardMapper;
import org.oolong.service.exception.ApplicationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class BoardService {
	
	private final BoardMapper boardMapper;
	
	@Transactional(readOnly = true)
	public BoardPageResponseDTO getBoardList(BoardPageRequestDTO dto) {
			
			int page = dto.getPage();
			int size = dto.getSize();
			String typeStr = dto.getTypes();
			String keyword = dto.getKeyword();
			
			page = page <= 0 ? 1 : page;
			size = size < 10 || size >= 100 ? 10 : size;
			 
			log.debug("getBoardList boardPageRequestDTO: {}", dto);
			
			int offset = (page - 1) * size;
			
			log.debug("BoardService offset: {}", offset);
			log.debug("BoardService limit: {}", size);
			
			String[] types = typeStr != null ? typeStr.split("") : null;
			
			List<BoardDTO> boardDTOList = boardMapper.selectListSearch(offset, size, types, keyword);
			int totalCount = boardMapper.selectTotalCountSearch(types, keyword);
			
			
			
			return new BoardPageResponseDTO(boardDTOList, page, size, totalCount, typeStr, keyword);
		
	}
	
	public Long writeBoard(BoardDTO boardDTO) {
		log.info("writeBoard boardDTO: {}", boardDTO);
		
		int count = boardMapper.insert(boardDTO);
		
		if(count == 0) {
			throw new ApplicationException(500, "BOARD_INSERT_ERROR");
		}
		
		return boardDTO.getBoardId();
	}
	
	@Transactional(readOnly = true)
	public BoardDTO getBoard(Long boardId) {
		log.debug("getBoard boardId: {}", boardId);
		BoardDTO dto = boardMapper.selectById(boardId);
		if(dto == null) {
			throw new ApplicationException(404, "BOARD_NOT_FOUND");
		}
		return dto;
	}
	
	
	public void removeBoard(Long boardId) {
		log.info("removeBoard boardId: {}", boardId);
		
		int count = boardMapper.delete(boardId);
		
		if(count == 0) {
			throw new ApplicationException(404, "BOARD_NOT_FOUND");
		}
		
	}
	
	public void modifyBoard(BoardDTO boardDTO) {
		log.info("modifyBoard boardDTO: {}", boardDTO);
		
		int count = boardMapper.update(boardDTO);
		
		if(count == 0) {
			throw new ApplicationException(404, "BOARD_NOT_FOUND");
		}
	}
	
	
	
}
