package org.oolong.service;

import java.util.List;

import org.oolong.dto.BoardDTO;
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
	
	public List<BoardDTO> getBoardList() {
		log.info("getBoardList");
		return boardMapper.selectList();
		
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
