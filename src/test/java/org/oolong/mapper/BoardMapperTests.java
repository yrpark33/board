package org.oolong.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.oolong.dto.BoardDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.log4j.Log4j2;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/root-context.xml")
@Log4j2
@Transactional
public class BoardMapperTests {
	
	@Autowired
	BoardMapper boardMapper;
	
	private BoardDTO setUpBoard() {
		
		BoardDTO board = BoardDTO.builder().title("공통 테스트 제목").content("공통 테스트 내용").writer("user00").build();
		boardMapper.insert(board);
		return board;
	}
	
	@Test
	void 게시물_등록_성공() {
		
		//given
		BoardDTO board = BoardDTO.builder().title("등록 제목").content("등록 내용").writer("user00").build();
		
		
		//when
		boardMapper.insert(board);
		
		//then
		assertNotNull(board.getBoardId(), "DB에서 생성된 boardId가 DTO에 할당되어야 합니다.");
		
		//실제 DB에서 다시 꺼내와서 내용이 일치하는지 확인
		BoardDTO savedBoard = boardMapper.selectById(board.getBoardId());
		assertEquals("등록 제목", savedBoard.getTitle());
		assertEquals("user00", savedBoard.getWriter());
	}
	
	@Test
	void 게시물_조회_성공() {
		
		
		//given
		BoardDTO board = setUpBoard();
		
		
		//when
		BoardDTO foundBoard = boardMapper.selectById(board.getBoardId());
		
		//then
		assertNotNull(foundBoard);
		assertEquals(board.getTitle(), foundBoard.getTitle());
		assertEquals(board.getContent(), foundBoard.getContent());
		
	}
	
	@Test
	void 게시물_목록_조회_성공() {
		
		//given
		int page = 1;
		int limit = 10;
		int offset = (page - 1) * limit;
		
		String[] types = {"T", "W"};
		String keyword = "Modify";
		
		//when
		List<BoardDTO> list = boardMapper.selectListSearch(offset, limit, types, keyword);
		
		//then
		assertNotNull(list);
		assertTrue(list.size() > 0, "검색 결과가 최소 1개 이상 존재해야 합니다.");
		assertTrue(list.size() <= limit, "결과 개수는 설정한 limit보다 작거나 같아야 합니다.");
		
		for(BoardDTO dto : list) {
			
			boolean matches = (dto.getTitle().contains(keyword) || dto.getWriter().contains(keyword));
			
			assertTrue(matches, "검색 결과는 제목이나 작성자에 키워드를 포함해야 합니다. 글번호: " + dto.getBoardId());
			assertFalse(dto.isDeleted(), "삭제된 게시물은 목록에 노출되지 않아야 합니다.");
			
		}
		
		log.info("조회된 목록 개수: " + list.size());
		
	}
	
	@Test
	void 게시물_수정_성공() {
		
		//given
		BoardDTO board = setUpBoard();
		board.setTitle("수정 제목");
		board.setContent("수정 내용");
		
		//when
		int count = boardMapper.update(board);
		
		//then
		assertEquals(1, count);
		
		BoardDTO updatedDTO = boardMapper.selectById(board.getBoardId());
		assertEquals("수정 제목", updatedDTO.getTitle());
		assertEquals("user00", updatedDTO.getWriter());
		
		
	}
	
	
	@Test
	void 게시물_삭제_성공() {
		
		//given
		BoardDTO board = setUpBoard();
		
		
		//when
		boardMapper.delete(board.getBoardId());
		
		//then
		BoardDTO result = boardMapper.selectById(board.getBoardId());
		assertNull(result);
		
	}
	
	
	@Test
	void 검색_조건에_따른_전체_개수_조회() {
	    //given: 특정 키워드가 포함된 글 3개, 포함 안 된 글 2개 생성
	    for(int i=1; i<=3; i++) {
	        boardMapper.insert(BoardDTO.builder().title("사과 " + i).content("내용").writer("u1").build());
	    }
	    for(int i=1; i<=2; i++) {
	        boardMapper.insert(BoardDTO.builder().title("포도 " + i).content("내용").writer("u1").build());
	    }

	    String[] types = {"T"};
	    String keyword = "사과";
	    
	    
	    //when: '사과'로 검색했을 때의 개수 확인
	    
	    int count = boardMapper.selectTotalCountSearch(types, keyword);

	    // 3. then
	    assertEquals(3, count, "제목에 '사과'가 포함된 게시물은 3개여야 합니다.");
	}
		
}
