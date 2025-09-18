package org.oolong.service;

import java.util.List;

import org.oolong.dto.BoardDTO;
import org.oolong.dto.BoardListPagingDTO;
import org.oolong.mapper.BoardMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class BoardService {
	
	private final BoardMapper boardMapper;
	
	
	public BoardListPagingDTO getList(int page, int size, String typeStr, String keyword) {
		
		//페이지 번호가 0보다 작으면 무조건 1페이지
		page = page <= 0 ? 1 : page;
		
		//사이즈가 10보다 작거나 100보다 크면 10
		size = (size < 10 || size > 100) ? 10 : size;
		
		
		int skip = (page - 1) * size;
		
		String[] types = typeStr != null ? typeStr.split("") : null;
		
		int total = boardMapper.listCountSearch(types, keyword);
		List<BoardDTO> list = boardMapper.listSearch(skip, size, types, keyword);
		
		
		return new BoardListPagingDTO(list, total, page, size, typeStr, keyword);
	}
	
	public Long register(BoardDTO dto) {
		
		int insertCount = boardMapper.insert(dto);
		
		log.info("insertCount: " + insertCount);
		
		return dto.getBno();
		
	}
	
	
	public BoardDTO read(Long bno) {
		
		BoardDTO boardDTO = boardMapper.selectOne(bno);
		
		return boardDTO;
	}
	
	
	public void remove(Long bno) {
		
		boardMapper.remove(bno);
		
	}
	
	public void modify(BoardDTO dto) {
		
		boardMapper.update(dto);
		
	}
	
	
}
