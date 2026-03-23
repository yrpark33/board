package org.oolong.mapper;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.oolong.dto.BoardDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.extern.log4j.Log4j2;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/root-context.xml")
@Log4j2
public class BoardMapperTests {
	
	@Autowired
	BoardMapper boardMapper;
	
	
//	@Test
	public void testInsert() {
		
		BoardDTO boardDTO = BoardDTO.builder().title("title").content("content").writer("user00").build();
		int insertCount = boardMapper.insert(boardDTO);
		log.info("------------------------");
		log.info("insertCount: " + insertCount);
		
	}
	
//	@Test
	public void testInsert2() {
		
		BoardDTO boardDTO = BoardDTO.builder().title("title").content("content").writer("user00").build();
		int insertCount = boardMapper.insert(boardDTO);
		log.info("------------------------");
		log.info("insertCount : " + insertCount);
		log.info("========================");
		log.info("boardId : " + boardDTO.getBoardId());
	}
	
//	@Test
	public void testSelectOne() {
		
		Long boardId = 2L;
		
		BoardDTO board = boardMapper.selectById(boardId);
		log.info("board: " + board);
	}
	
//	@Test
	public void testDelete() {
		
		Long boardId = 2L;
		
		int deleteCount = boardMapper.delete(boardId);
		log.info("--------------------");
		log.info("deleteCount: " + deleteCount);
	}
	
//	@Test
	public void testUpdate() {
		
		Long boardId = 2L;
		BoardDTO boardDTO = BoardDTO.builder().boardId(boardId).title("update title").content("update content").build();
		
		int updateCount = boardMapper.update(boardDTO);
		log.info("--------------------");
		log.info("updateCount: " + updateCount);
		
	}
	
	
	
//	@Test
	public void testSelectListSearch() {
		
		int page = 2;
		int count = 10;
		int skip = (page - 1) * 10;
		
		String[] types = null;
		String keyword = "Test";
		
		
		boardMapper.selectListSearch(skip, count, types, keyword);
		
		
	}
	
	
	
	
	
}
