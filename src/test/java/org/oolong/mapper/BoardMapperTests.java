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
@Log4j2
@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/root-context.xml")
public class BoardMapperTests {
	
	@Autowired
	BoardMapper boardMapper;
	
	
//	@Test
	public void testInsert() {
		
		BoardDTO boardDTO = BoardDTO.builder().title("title").content("content").writer("user00").build();
		
		int insertCount = boardMapper.insert(boardDTO);
		
		log.info("--------------------");
		log.info("insertCount: " + insertCount);
		
		
	}
	
//	@Test
	public void testInsert2() {
		BoardDTO boardDTO = BoardDTO.builder().title("title").content("content").writer("user00").build();
		
		int insertCount = boardMapper.insert(boardDTO);
		
		log.info("-----------------");
		log.info("insertCount: " + insertCount);
		log.info("=================");
		log.info("BNO: " + boardDTO.getBno());
		
	}
	
	
//	@Test
	public void testSelectOne() {
		
		Long bno = 2L;
		
		BoardDTO board = boardMapper.selectOne(bno);
		
		log.info("------------------------");
		log.info("board: " + board);
		
	}
	
	
//	@Test
	public void testRemove() {
		
		Long bno = 2L;
		int removeCount = boardMapper.remove(bno);
		
		
		log.info("---------------");
		log.info("removeCount: " + removeCount);
		
	}
	
//	@Test
	public void testUpdate() {
		
		BoardDTO board = BoardDTO.builder().bno(2L).title("Update Title").content("Update Content").delFlag(false).build();
		
		int updateCount = boardMapper.update(board);
		log.info("---------------------------");
		log.info("updateCount: " + updateCount);
	}
	
//	@Test
	public void testList() {
		
		List<BoardDTO> dtoList = boardMapper.list();
		log.info("dtoList");
		log.info(dtoList);
		dtoList.stream().forEach(log::info);
		
	}
	
//	@Test
	public void testList2() {
		
		int page = 2;
		
		int count = 10;
		int skip = (page - 1) * count;
		
		
		List<BoardDTO> dtoList = boardMapper.list2(skip, count);
		
		dtoList.stream().forEach(log::info);
		
	}
	
	@Test
	public void testListSearch() {
		
		int page = 2;
		
		int count = 10;
		
		int skip = (page - 1) * count;
		
		String[] types = new String[] {"C"};
		
		String keyword = "test";
		
		boardMapper.listSearch(skip, count, types, keyword);
		
	}
	
	
}
