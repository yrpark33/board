package org.oolong.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.oolong.dto.BoardDTO;
import org.oolong.dto.CommentDTO;
import org.oolong.dto.CommentPageResponseDTO;
import org.oolong.dto.PageRequestDTO;
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
public class CommentServiceTests {
	
	@Autowired
	private CommentService commentService;
	
	@Autowired
	private BoardService boardService;
	
	
	private Long createBoard() {
		BoardDTO targetBoard = BoardDTO.builder().title("테스트용 게시글").content("내용").writer("writer1").build();
		return boardService.writeBoard(targetBoard);
	}
	
	
	@Test
	void 댓글_등록_성공() {
		
		//given
		Long targetBoardId = createBoard();
		CommentDTO newComment = CommentDTO.builder().boardId(targetBoardId).content("댓글 등록 성공").writer("commenter1").build();
		
		//when
		commentService.writeComment(newComment);
		Long generatedCommentId = newComment.getCommentId();
		
		
		//then
		CommentDTO foundComment = commentService.getComment(generatedCommentId);
		assertNotNull(foundComment);
		assertEquals(targetBoardId, foundComment.getBoardId());
		assertEquals("댓글 등록 성공", foundComment.getContent());
	}
	
	@Test
	void 댓글_조회_성공() {
		
		//given
		Long targetBoardId = createBoard();
		CommentDTO commentDTO = CommentDTO.builder().boardId(targetBoardId).content("댓글 조회 성공").writer("commenter1").build();
		commentService.writeComment(commentDTO);
		Long commentId = commentDTO.getCommentId();
		
		//when
		CommentDTO foundComment = commentService.getComment(commentId);
		
		//then
		assertNotNull(foundComment);
		assertEquals(targetBoardId, foundComment.getBoardId());
		assertEquals("댓글 조회 성공", foundComment.getContent());
		
		
	}
	
	@Test
	void 댓글_목록_조회_성공() {
		
		//given
		Long targetBoardId = createBoard();
		PageRequestDTO requestDTO = new PageRequestDTO();
		requestDTO.setPage(1);
		requestDTO.setSize(10);
		
		for(int i = 1; i <= 11; i++) {
			
			CommentDTO commentDTO = CommentDTO.builder().boardId(targetBoardId).content("목록조회용 댓글" + i).writer("commenter" + i).build();
			commentService.writeComment(commentDTO);
		
		}
		
		
		//when
		CommentPageResponseDTO responseDTO = commentService.getCommentList(targetBoardId, requestDTO);
		
		
		//then
		List<CommentDTO> dtoList = responseDTO.getCommentDTOList();
		assertNotNull(dtoList);
		assertEquals(10, dtoList.size());
		assertEquals(11, responseDTO.getTotalCount());
		
		assertEquals("목록조회용 댓글1", dtoList.get(0).getContent());
		dtoList.forEach((dto) -> log.info("조회 결과: {}", dto));
		
	}
	
	
	@Test
	void 댓글_수정_성공() {
		
		//given
		Long targetBoardId = createBoard();
		CommentDTO newComment = CommentDTO.builder().boardId(targetBoardId).content("원래 내용").writer("commenter1").build();
		commentService.writeComment(newComment);
		Long commentId = newComment.getCommentId();
		
		//when
		CommentDTO updateDTO = CommentDTO.builder().commentId(commentId).content("수정된 내용").build();
		commentService.modifyComment(updateDTO);
		
		//then
		CommentDTO result = commentService.getComment(commentId);
		assertEquals("수정된 내용", result.getContent());
		assertEquals("commenter1", result.getWriter());
	}
	
	@Test
	void 댓글_삭제_성공() {
		
		//given
		Long targetBoardId = createBoard();
		CommentDTO newComment = CommentDTO.builder().boardId(targetBoardId).content("삭제할 댓글").writer("commenter1").build();
		commentService.writeComment(newComment);
		Long commentId = newComment.getCommentId();
		
		//when
		commentService.removeComment(commentId);
		
		//then
		ApplicationException ex = assertThrows(ApplicationException.class, () -> commentService.getComment(commentId));
		assertEquals(404, ex.getCode());
		assertEquals("댓글이 존재하지 않습니다", ex.getMessage());
		
	}
	
	
	
	@Test
	void 댓글_등록시_내용_비어있으면_예외발생() {
		
		 CommentDTO commentDTO = CommentDTO.builder().writer("작성자").content("").build();		
		 
		 ApplicationException ex = assertThrows(ApplicationException.class, () -> commentService.writeComment(commentDTO));
		 
		 assertEquals(400, ex.getCode());
		 assertEquals("내용을 입력해주세요", ex.getMessage());
		 
		 
	}
	
	@Test
	void 댓글_등록시_작성자_비어있으면_예외_발생() {
		
		CommentDTO commentDTO = CommentDTO.builder().writer("").content("내용").build();
		 
		ApplicationException ex = assertThrows(ApplicationException.class, () -> commentService.writeComment(commentDTO));
		
		assertEquals(400, ex.getCode());
		assertEquals("작성자를 입력해주세요", ex.getMessage());
		
	}
	
	@Test
	void 댓글_수정시_내용_비어있으면_예외_발생() {
		CommentDTO commentDTO = CommentDTO.builder().content("").build();
		ApplicationException ex = assertThrows(ApplicationException.class, () -> commentService.modifyComment(commentDTO));
		
		assertEquals(400, ex.getCode());
		assertEquals("내용을 입력해주세요", ex.getMessage());
		
	}
	
	@Test
	void 존재하지않는_댓글_조회시_예외발생() {
		
		Long commentId = 1000L;
		
		ApplicationException ex = assertThrows(ApplicationException.class, () -> commentService.getComment(commentId));
		
		assertEquals(404, ex.getCode());
		assertEquals("댓글이 존재하지 않습니다", ex.getMessage());
		
	}
	
	@Test
	void 존재하지않는_댓글_수정시_예외발생() {
		
		Long commentId = 1000L;
		
		CommentDTO updateDTO = CommentDTO.builder().commentId(commentId).content("내용").writer("commenter1").build();
		
		ApplicationException ex = assertThrows(ApplicationException.class, () -> commentService.modifyComment(updateDTO));
		
		assertEquals(404, ex.getCode());
		assertEquals("댓글이 존재하지 않습니다", ex.getMessage());
		
	}
	
	@Test
	void 존재하지않는_댓글_삭제시_예외발생() {
		
		Long commentId = 1000L;
		
		ApplicationException ex = assertThrows(ApplicationException.class, () -> commentService.removeComment(commentId));
		
		assertEquals(404, ex.getCode());
		assertEquals("댓글이 존재하지 않습니다", ex.getMessage());
		
	}
	
	@Test
	void 이미_삭제된_댓글_삭제시_예외발생() {
		
		Long targetBoardId = createBoard();
		CommentDTO newComment = CommentDTO.builder().boardId(targetBoardId).content("내용").writer("commenter1").build();
		
		commentService.writeComment(newComment);
		Long generatedId = newComment.getCommentId();
		
		//삭제
		commentService.removeComment(generatedId);
		
		//이미 삭제된 댓글을 재삭제
		ApplicationException ex = assertThrows(ApplicationException.class, () -> commentService.removeComment(generatedId));
		
		assertEquals(404, ex.getCode());
		assertEquals("댓글이 존재하지 않습니다", ex.getMessage());
		
	}
	
	@Test
	void 이미_삭제된_댓글_수정시_예외발생() {
		
		Long targetBoardId = createBoard();
		
		CommentDTO newComment = CommentDTO.builder().boardId(targetBoardId).content("내용").writer("commenter1").build();
		commentService.writeComment(newComment);
		Long generatedId = newComment.getCommentId();
		
		//삭제
		commentService.removeComment(generatedId);
		
		CommentDTO updateDTO = CommentDTO.builder().commentId(generatedId).content("수정된 내용").build();
		
		//이미 삭제된 댓글 수정
		ApplicationException ex = assertThrows(ApplicationException.class, () -> commentService.modifyComment(updateDTO));
		assertEquals(404, ex.getCode());
		assertEquals("댓글이 존재하지 않습니다", ex.getMessage());
		
	}
	
	
}
