package org.oolong.service;

import java.util.List;

import org.oolong.dto.BoardDTO;
import org.oolong.dto.BoardPageRequestDTO;
import org.oolong.dto.BoardPageResponseDTO;
import org.oolong.mapper.BoardMapper;
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
	
	public BoardPageResponseDTO getBoardList(BoardPageRequestDTO dto) {
		
		int page = dto.getPage();
		int size = dto.getSize();
		String typeStr = dto.getTypes();
		String keyword = dto.getKeyword();
		
		page = page <= 0 ? 1 : page;
		size = size < 10 || size >= 100 ? 10 : size;
		 
		log.info("getBoardList boardPageRequestDTO: {}", dto);
		
		int skip = (page - 1) * size;
		
		log.info("BoardService skip: {}", skip);
		log.info("BoardService count: {}", size);
		
		String[] types = typeStr != null ? typeStr.split("") : null;
		
		List<BoardDTO> boardDTOList = boardMapper.selectListSearch(skip, size, types, keyword);
		
		
		
		int totalCount = boardMapper.selectTotalCountSearch(types, keyword);
		return new BoardPageResponseDTO(boardDTOList, totalCount, page, size, typeStr, keyword);
		
	}
	
	public Long writeBoard(BoardDTO boardDTO) {
		log.info("writeBoard boardDTO: {}", boardDTO);
		boardMapper.insert(boardDTO);
		return boardDTO.getBoardId();
	}
	
	public BoardDTO getBoard(Long boardId) {
		log.info("getBoard boardId: {}", boardId);
		return boardMapper.selectById(boardId);
	}
	
	
	public void removeBoard(Long boardId) {
		log.info("removeBoard boardId: {}", boardId);
		boardMapper.delete(boardId);
	}
	
	public void modifyBoard(BoardDTO boardDTO) {
		log.info("modifyBoard boardDTO: {}", boardDTO);
		boardMapper.update(boardDTO);
	}
	
	
	
}
