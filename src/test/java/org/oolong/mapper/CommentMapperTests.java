package org.oolong.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.oolong.dto.BoardDTO;
import org.oolong.dto.CommentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.log4j.Log4j2;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/root-context.xml")
@Log4j2
@Transactional
public class CommentMapperTests {
	
	@Autowired
	CommentMapper commentMapper;
	
	@Autowired
	BoardMapper boardMapper;
	
	private BoardDTO setUpBoard() {
		
		BoardDTO board = BoardDTO.builder().title("공통 게시물 제목").content("공통 게시물 내용").writer("user00").build();
		boardMapper.insert(board);
		
		return board;
	}
	
	private CommentDTO setUpComment(Long boardId, String content, String writer) {
		
		CommentDTO comment = CommentDTO.builder().boardId(boardId).content(content).writer(writer).build();
		commentMapper.insert(comment);
		
		return comment;
	}
	
	
	@Test
	void 댓글_등록_성공() {
		
		//given
		BoardDTO board = setUpBoard();
		CommentDTO comment = CommentDTO.builder().boardId(board.getBoardId()).content("댓글 등록").writer("commenter00").build();
		
		//when
		int count = commentMapper.insert(comment);
		
		//then
		
		assertNotNull(comment.getCommentId(), "DB에서 생성된 commentId가 DTO에 할당되어야 합니다.");
		
		//실제 DB에서 다시 꺼내와서 내용이 일치하는지 확인
		CommentDTO savedComment = commentMapper.selectById(comment.getCommentId());
		
		assertEquals(board.getBoardId(), savedComment.getBoardId());
		assertEquals(comment.getContent(), savedComment.getContent());
		assertEquals(comment.getWriter(), savedComment.getWriter());
	}
	
	
	@Test
	void 댓글_조회_성공() {
		
		//given
		BoardDTO board = setUpBoard();
		CommentDTO comment = setUpComment(board.getBoardId(), "댓글 조회 내용", "commenter00");
		
		//when
		CommentDTO foundComment = commentMapper.selectById(comment.getCommentId());
	
		//then
		assertNotNull(foundComment);
		assertEquals(comment.getCommentId(), foundComment.getCommentId());
		assertEquals(comment.getContent(), foundComment.getContent());
		assertEquals(comment.getWriter(), foundComment.getWriter());
		assertEquals(board.getBoardId(), foundComment.getBoardId());
		
	}
	
	
	
	@Test
	void 댓글_목록_조회_성공() {
		
		//given
		BoardDTO board = setUpBoard();
		Long boardId = board.getBoardId();
		 
		for(int i = 1; i <= 11; i++) {
			 
			setUpComment(boardId, "조회 댓글 내용" + i, "commenter" + i);
		 
		}
		 
		 int page = 1;
		 int limit = 10;
		 int offset = (page - 1) * limit;
		 
		 //when
		 List<CommentDTO> list = commentMapper.selectList(boardId, offset, limit);
		 
		 //then
		 assertNotNull(list);
		 assertEquals(10, list.size(), "1페이지에는 10개의 댓글이 조회되어야합니다.");
		 assertEquals("조회 댓글 내용1", list.get(0).getContent());
		 
		 for(CommentDTO dto : list) {
			assertEquals(boardId, dto.getBoardId());
		 }
		 
		 
	}
	
	@Test
	void 댓글_수정_성공() {
		
		//given
		BoardDTO board = setUpBoard();
		CommentDTO comment = setUpComment(board.getBoardId(), "기존 댓글 내용", "commenter00");
		
		//when
		comment.setContent("수정 댓글 내용");
		int count = commentMapper.update(comment);
		
		//then
		
		assertEquals(1, count, "수정된 행의 개수는 1이어야합니다.");
		CommentDTO updatedComment = commentMapper.selectById(comment.getCommentId());
		assertEquals("수정 댓글 내용", updatedComment.getContent());
		assertEquals("commenter00", updatedComment.getWriter());
		
		
	}
	

	
	@Test
	void 댓글_삭제_성공() {
	
		//given
		BoardDTO board = setUpBoard();
		CommentDTO comment = setUpComment(board.getBoardId(), "댓글 삭제 내용", "commenter00");
		
		//when
		int count = commentMapper.delete(comment.getCommentId());
		
	
		//then
		assertEquals(1, count, "삭제된 행의 개수는 1이어야합니다.");
		assertTrue(commentMapper.selectById(comment.getCommentId()).isDeleted());
	}
	
	
	@Test
	void 게시글별_댓글_개수_조회() {
	    //given: 게시글 A에 댓글 5개, 게시글 B에 댓글 2개 생성
	    BoardDTO boardA = setUpBoard();
	    BoardDTO boardB = setUpBoard();
	    
	    for(int i=0; i<5; i++) setUpComment(boardA.getBoardId(), "댓글", "commenter00");
	    for(int i=0; i<2; i++) setUpComment(boardB.getBoardId(), "댓글", "commenter00");

	    //when
	    int countA = commentMapper.selectTotalCount(boardA.getBoardId());
	    int countB = commentMapper.selectTotalCount(boardB.getBoardId());

	    //then
	    assertEquals(5, countA);
	    assertEquals(2, countB);
	} 
	
	
	
}
