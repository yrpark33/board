package org.oolong.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.oolong.dto.BoardDTO;
import org.oolong.dto.BoardPageRequestDTO;
import org.oolong.dto.BoardPageResponseDTO;
import org.oolong.service.exception.ApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.log4j.Log4j2;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/root-context.xml")
@Log4j2
@Transactional
public class BoardServiceTests {
	
	@Autowired BoardService boardService;
	
	
	@Test
	void 게시물_등록_성공() {
		
		//given
		BoardDTO dto = BoardDTO.builder().title("테스트").content("내용").writer("writer1").build();
		
		//when
		Long boardId = boardService.writeBoard(dto);
		
		//then
		assertNotNull(boardId);
		
	}
	
	
	@Test
	void 게시물_조회_성공() {
		
		//given
		BoardDTO newBoard = BoardDTO.builder().title("제목").content("내용").writer("writer1").build();
		boardService.writeBoard(newBoard);
		Long generatedId = newBoard.getBoardId();
		
		//when
		BoardDTO foundBoard = boardService.getBoard(generatedId);
		
		//then
		assertNotNull(foundBoard);
		assertEquals(generatedId, foundBoard.getBoardId());
		assertEquals("제목", foundBoard.getTitle());
	}
	
	
	@Test
	void 게시물_목록_조회_성공() {
		
		//given
		BoardPageRequestDTO requestDTO = new BoardPageRequestDTO();
		
		requestDTO.setPage(1);
		requestDTO.setSize(10);
		requestDTO.setTypes("T");
		requestDTO.setKeyword("Modify");
		
		
		//when
		BoardPageResponseDTO responseDTO = boardService.getBoardList(requestDTO);
		
		//then
		
		assertNotNull(responseDTO.getBoardDTOList());
		assertTrue(responseDTO.getTotalCount() > 0);
		
		log.info("검색된 게시글 갯수: {}", responseDTO.getTotalCount());
		responseDTO.getBoardDTOList().forEach(dto -> log.info("검색 결과: {}", dto));
				
	}
	
	
	@Test
	void 게시물_수정_성공() {
		
		//given
		BoardDTO newBoard = BoardDTO.builder().title("원래제목").content("원래내용").writer("writer1").build();
		Long generatedId = boardService.writeBoard(newBoard);
		
		
		//when
		BoardDTO updateDTO = BoardDTO.builder().boardId(generatedId).title("수정제목").content("수정내용").build();
		boardService.modifyBoard(updateDTO);
		
		//then
		BoardDTO result = boardService.getBoard(generatedId);
		assertNotNull(result);
		assertEquals("수정제목", result.getTitle());
		assertEquals("수정내용", result.getContent());
		assertEquals("writer1", result.getWriter());
	}
	
	@Test
	void 게시물_삭제_성공() {
		
		//given
		BoardDTO newBoard = BoardDTO.builder().title("제목").content("내용").writer("writer1").build();
		Long generatedId = boardService.writeBoard(newBoard);
		
		//when
		boardService.removeBoard(generatedId);
		
		
		//then
		ApplicationException ex = assertThrows(ApplicationException.class, () -> boardService.getBoard(generatedId));
		assertEquals(404, ex.getCode());
		assertEquals("BOARD_NOT_FOUND", ex.getMessage());
	}
	
	
	@Test
	void 존재하지않는_게시물_조회시_예외발생() {
		
		// given
		Long boardId = 10000L;

	    // when & then
	    ApplicationException ex = assertThrows(
	        ApplicationException.class,
	        () -> boardService.getBoard(boardId)
	    );

	    assertEquals(404, ex.getCode());
	    assertEquals("BOARD_NOT_FOUND", ex.getMessage());
			
	}
	
	
	
	@Test
	void 존재하지않는_게시물_수정시_예외발생() {
		
		Long boardId = 10000L;
		BoardDTO dto = BoardDTO.builder().boardId(boardId).build();
		
		
		ApplicationException ex = assertThrows(ApplicationException.class, () -> boardService.modifyBoard(dto));
		
		assertEquals(404, ex.getCode());
		assertEquals("BOARD_NOT_FOUND", ex.getMessage());
		
		
	}
	
	
	@Test
	void 존재하지않는_게시물_삭제시_예외발생() {
		
		//given
		Long boardId = 10000L;
		
		ApplicationException ex = assertThrows(ApplicationException.class, () -> boardService.removeBoard(boardId));
		
		assertEquals(404, ex.getCode());
		assertEquals("BOARD_NOT_FOUND", ex.getMessage());
		
	}
	
	@Test
	void 이미_삭제된_게시물_삭제시_예외발생() {
		
		BoardDTO boardDTO = BoardDTO.builder().title("제목").content("내용").writer("writer1").build();
		
		boardService.writeBoard(boardDTO);
		Long generatedId = boardDTO.getBoardId();
		
		//삭제
		boardService.removeBoard(generatedId);
		
		//삭제된 게시물 재삭제
		ApplicationException ex =assertThrows(ApplicationException.class, () -> boardService.removeBoard(generatedId));
		
		assertEquals(404, ex.getCode());
		assertEquals("BOARD_NOT_FOUND", ex.getMessage());
		
	}
	
	
	@Test
	void 이미_삭제된_게시물_수정시_예외발생() {
		BoardDTO newBoard = BoardDTO.builder().title("제목").content("내용").writer("writer1").build();
		
		boardService.writeBoard(newBoard);
		Long generatedId = newBoard.getBoardId();
		
		//삭제
		boardService.removeBoard(generatedId);
		
		//삭제된 게시물 수정
		BoardDTO updateDTO = BoardDTO.builder().boardId(generatedId).title("수정된 제목").content("수정된 내용").build();
		
		
		ApplicationException ex = assertThrows(ApplicationException.class, () -> boardService.modifyBoard(updateDTO));
		
		assertEquals(404, ex.getCode());
		assertEquals("BOARD_NOT_FOUND", ex.getMessage());
		
	}
}
