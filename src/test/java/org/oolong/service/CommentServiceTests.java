package org.oolong.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.oolong.dto.BoardDTO;
import org.oolong.dto.CommentDTO;
import org.oolong.dto.CommentPageResponseDTO;
import org.oolong.dto.PageRequestDTO;
import org.oolong.mapper.CommentMapper;
import org.oolong.service.exception.ApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
	
	
	@Autowired
	private CommentMapper commentMapper;
	
	
	private Long createBoard() throws IOException {
		BoardDTO targetBoard = BoardDTO.builder().title("테스트용 게시글").content("내용").writer("writer1").build();
		MultipartFile[] files = new MultipartFile[0];
		return boardService.writeBoard(targetBoard, files);
	}
	
	
	
	
	
	@Test
	void 댓글_등록_성공() throws IOException {
		
		//given
		Long targetBoardId = createBoard();
		CommentDTO newComment = CommentDTO.builder().boardId(targetBoardId).content("댓글 등록 성공").writer("commenter1").build();
		
		//when
		commentService.writeComment(newComment);
		Long generatedCommentId = newComment.getCommentId();
		
		
		//then
		CommentDTO foundComment = commentMapper.selectById(generatedCommentId);
		assertNotNull(foundComment);
		assertEquals(targetBoardId, foundComment.getBoardId());
		assertEquals("댓글 등록 성공", foundComment.getContent());
	}
	
	
	@Test
	void 댓글_수정을_위한_조회_성공() throws IOException {
		
		//given
		Long targetBoardId = createBoard();
		CommentDTO commentDTO = CommentDTO.builder().boardId(targetBoardId).content("댓글 조회 성공").writer("commenter1").build();
		commentService.writeComment(commentDTO);
		Long commentId = commentDTO.getCommentId();
		
		//when
		boolean admin = false;
		CommentDTO foundComment = commentService.getCommentForModify(commentId, commentDTO.getWriter(), admin);
		
		//then
		assertNotNull(foundComment);
		assertEquals(targetBoardId, foundComment.getBoardId());
		assertEquals("댓글 조회 성공", foundComment.getContent());
		
		
	}
	
	@Test
	void 댓글_목록_조회_성공() throws IOException {
		
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
		String username = "user00";
		boolean admin = false;
		CommentPageResponseDTO responseDTO = commentService.getCommentList(targetBoardId, requestDTO, username, admin);
		
		
		//then
		List<CommentDTO> dtoList = responseDTO.getCommentDTOList();
		assertNotNull(dtoList);
		assertEquals(10, dtoList.size());
		assertEquals(11, responseDTO.getTotalCount());
		
		assertEquals("목록조회용 댓글1", dtoList.get(0).getContent());
		dtoList.forEach((dto) -> log.info("조회 결과: {}", dto));
		
	}
	
	
	@Test
	void 댓글_수정_성공() throws IOException {
		
		//given
		Long targetBoardId = createBoard();
		CommentDTO newComment = CommentDTO.builder().boardId(targetBoardId).content("원래 내용").writer("commenter1").build();
		commentService.writeComment(newComment);
		Long commentId = newComment.getCommentId();
		
		//when
		
		CommentDTO updateDTO = CommentDTO.builder().commentId(commentId).content("수정된 내용").build();
		updateDTO.setWriter(newComment.getWriter());
		boolean admin = false;
		commentService.modifyComment(updateDTO, admin);
		
		//then
		CommentDTO result = commentMapper.selectById(commentId);
		assertEquals("수정된 내용", result.getContent());
		assertEquals("commenter1", result.getWriter());
	}
	
	
	
	@Test
	void 댓글_삭제_성공() throws IOException {
		
		//given
		Long targetBoardId = createBoard();
		CommentDTO newComment = CommentDTO.builder().boardId(targetBoardId).content("삭제할 댓글").writer("commenter1").build();
		commentService.writeComment(newComment);
		Long commentId = newComment.getCommentId();
		
		//when
		String username = newComment.getWriter();
		boolean admin = false;
		commentService.removeComment(commentId, username, admin);
		
		//then
		CommentDTO commentDTO = commentMapper.selectById(commentId);
		assertTrue(commentDTO.isDeleted());
		
	}
	
	
	
	@Test
	void 댓글_등록시_내용_비어있으면_예외발생() {
		
		 CommentDTO commentDTO = CommentDTO.builder().writer("작성자").content("").build();		
		 
		 ApplicationException ex = assertThrows(ApplicationException.class, () -> commentService.writeComment(commentDTO));
		 
		 assertEquals(400, ex.getCode());
		 assertEquals("내용을 입력해주세요", ex.getMessage());
		 
		 
	}
	
	
	
	@Test
	void 댓글_수정시_내용_비어있으면_예외_발생() {
		CommentDTO commentDTO = CommentDTO.builder().content("").build();
		boolean admin = false;
		
		ApplicationException ex = assertThrows(ApplicationException.class, () -> commentService.modifyComment(commentDTO, admin));
		
		assertEquals(400, ex.getCode());
		assertEquals("내용을 입력해주세요", ex.getMessage());
		
	}
	
	@Test
	void 존재하지않는_댓글_조회시_예외발생() {
		
		Long commentId = 9999999L;
		
		String username = "user00";
		boolean admin = false;
		
		ApplicationException ex = assertThrows(ApplicationException.class, () -> commentService.getCommentForModify(commentId, username, admin));
		
		assertEquals(404, ex.getCode());
		assertEquals("댓글이 존재하지 않습니다", ex.getMessage());
		
	}
	
	@Test
	void 존재하지않는_댓글_수정시_예외발생() {
		
		Long commentId = 9999999L;
		
		CommentDTO updateDTO = CommentDTO.builder().commentId(commentId).content("내용").writer("commenter1").build();
		
		boolean admin = false;
		
		ApplicationException ex = assertThrows(ApplicationException.class, () -> commentService.modifyComment(updateDTO, admin));
		
		assertEquals(404, ex.getCode());
		assertEquals("댓글이 존재하지 않습니다", ex.getMessage());
		
	}
	
	@Test
	void 존재하지않는_댓글_삭제시_예외발생() {
		
		Long commentId = 9999999L;
		
		String username = "unknown";
		boolean admin = false;
		
		ApplicationException ex = assertThrows(ApplicationException.class, () -> commentService.removeComment(commentId, username, admin));
		
		assertEquals(404, ex.getCode());
		assertEquals("댓글이 존재하지 않습니다", ex.getMessage());
		
	}
	
	@Test
	void 이미_삭제된_댓글_조회시_예외발생() throws IOException {
		
		Long targetBoardId = createBoard();
		CommentDTO newComment = CommentDTO.builder().boardId(targetBoardId).content("내용").writer("commenter1").build();
		
		commentService.writeComment(newComment);
		Long generatedId = newComment.getCommentId();
		
		//삭제
		boolean admin = false;
		commentService.removeComment(generatedId, newComment.getWriter(), admin);
		
		//이미 삭제된 댓글을 조회
		ApplicationException ex = assertThrows(ApplicationException.class, () -> commentService.getCommentForModify(generatedId, newComment.getWriter(), admin));
		
		assertEquals(404, ex.getCode());
		assertEquals("댓글이 존재하지 않습니다", ex.getMessage());
		
	}
	
	
	@Test
	void 이미_삭제된_댓글_삭제시_예외발생() throws IOException {
		
		Long targetBoardId = createBoard();
		CommentDTO newComment = CommentDTO.builder().boardId(targetBoardId).content("내용").writer("commenter1").build();
		
		commentService.writeComment(newComment);
		Long generatedId = newComment.getCommentId();
		
		//삭제
		boolean admin = false;
		commentService.removeComment(generatedId, newComment.getWriter(), admin);
		
		//이미 삭제된 댓글을 재삭제
		ApplicationException ex = assertThrows(ApplicationException.class, () -> commentService.removeComment(generatedId, newComment.getWriter(), admin));
		
		assertEquals(404, ex.getCode());
		assertEquals("댓글이 존재하지 않습니다", ex.getMessage());
		
	}
	
	@Test
	void 이미_삭제된_댓글_수정시_예외발생() throws IOException {
		
		Long targetBoardId = createBoard();
		
		CommentDTO newComment = CommentDTO.builder().boardId(targetBoardId).content("내용").writer("commenter1").build();
		commentService.writeComment(newComment);
		Long generatedId = newComment.getCommentId();
		
		//삭제
		boolean admin = false;
		commentService.removeComment(generatedId, newComment.getWriter(), admin);
		
		CommentDTO updateDTO = CommentDTO.builder().commentId(generatedId).content("수정된 내용").build();
		
		//이미 삭제된 댓글 수정
		ApplicationException ex = assertThrows(ApplicationException.class, () -> commentService.modifyComment(updateDTO, admin));
		assertEquals(404, ex.getCode());
		assertEquals("댓글이 존재하지 않습니다", ex.getMessage());
		
	}
	
	@Test
	void admin계정_조회_성공() throws IOException {
		
		//given
		Long targetBoardId = createBoard();
		CommentDTO comment = CommentDTO.builder().boardId(targetBoardId).content("내용").writer("writer00").build();
		commentService.writeComment(comment);
		Long commentId = comment.getCommentId();
		
		//when
		String username = "다른사람";
		boolean admin = true;
		CommentDTO result = commentService.getCommentForModify(commentId, username, admin);
		
		//then
		assertNotNull(result);
		assertEquals(comment.getContent(), result.getContent());
		assertEquals(comment.getWriter(), result.getWriter());
		
	}
	
	@Test
	void admin계정_수정_성공() throws IOException {
		
		//given
		Long targetBoardId = createBoard();
		CommentDTO comment = CommentDTO.builder().boardId(targetBoardId).content("내용").writer("writer00").build();
		commentService.writeComment(comment);
		Long commentId = comment.getCommentId();
		
		//when	
		CommentDTO updateDTO = CommentDTO.builder().commentId(commentId).content("수정된 내용").build();
		boolean admin = true;
		commentService.modifyComment(updateDTO, admin);
		
		
		//then
		CommentDTO result = commentMapper.selectById(commentId);
		assertEquals("수정된 내용", result.getContent());
		assertEquals(comment.getWriter(), result.getWriter());
	}
	
	
	@Test
	void admin계정_삭제_성공() throws IOException {
		
		//given
		Long targetBoardId = createBoard();
		CommentDTO comment = CommentDTO.builder().boardId(targetBoardId).content("내용").writer("writer00").build();
		commentService.writeComment(comment);
		Long commentId = comment.getCommentId();
		
		//when & then
		boolean admin = true;
		String username = "다른사람";
		assertDoesNotThrow(() -> commentService.removeComment(commentId, username, admin));
		CommentDTO result = commentMapper.selectById(commentId);
		assertTrue(result.isDeleted());
		
	}
	
	@Test
	void 작성자불일치_조회시_403예외() throws IOException {
			
		//given
		Long targetBoardId = createBoard();
		CommentDTO comment = CommentDTO.builder().boardId(targetBoardId).content("내용").writer("writer00").build();
		commentService.writeComment(comment);
		Long commentId = comment.getCommentId();
		
		//when
		String wrongUsername = "다른사람";
		boolean admin = false;
		ApplicationException ex = assertThrows(ApplicationException.class, () -> commentService.getCommentForModify(commentId, wrongUsername, admin));
		
		//then
		assertEquals(403, ex.getCode());
		assertEquals("ACCESS_DENIED", ex.getMessage());
		
	}

	@Test
	void 작성자불일치_수정시_403예외() throws IOException {
			
		//given
		Long targetBoardId = createBoard();
		CommentDTO comment = CommentDTO.builder().boardId(targetBoardId).content("내용").writer("writer00").build();
		commentService.writeComment(comment);
		Long commentId = comment.getCommentId();
		
		//when
		String wrongUsername = "다른사람";
		CommentDTO updateComment = CommentDTO.builder().commentId(commentId).content("수정된 내용").writer(wrongUsername).build();
		
		boolean admin = false;
		ApplicationException ex = assertThrows(ApplicationException.class, () -> commentService.modifyComment(updateComment, admin));
		
		//then
		assertEquals(403, ex.getCode());
		assertEquals("ACCESS_DENIED", ex.getMessage());
		
	}
	
	@Test
	void 작성자불일치_삭제시_403예외() throws IOException {
			
		//given
		Long targetBoardId = createBoard();
		CommentDTO comment = CommentDTO.builder().boardId(targetBoardId).content("내용").writer("writer00").build();
		commentService.writeComment(comment);
		Long commentId = comment.getCommentId();
		
		//when
		String wrongUsername = "다른사람";
		boolean admin = false;
		ApplicationException ex = assertThrows(ApplicationException.class, () -> commentService.removeComment(commentId, wrongUsername, admin));
		
		//then
		assertEquals(403, ex.getCode());
		assertEquals("ACCESS_DENIED", ex.getMessage());
		
	}
	
	
	
}
