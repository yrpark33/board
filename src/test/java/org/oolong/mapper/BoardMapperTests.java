package org.oolong.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.oolong.dto.BoardDTO;
import org.oolong.dto.BoardFileDTO;
import org.oolong.dto.BoardListDTO;
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
public class BoardMapperTests {
	
	@Autowired
	BoardMapper boardMapper;
	
	@Autowired
	CommentMapper commentMapper;
	
	private BoardDTO setUpBoard() {
		
		BoardDTO board = BoardDTO.builder().title("공통 테스트 제목").content("공통 테스트 내용").writer("user00").build();
		boardMapper.insert(board);
		return board;
	}
	
//	@Test
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
	
//	@Test
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
	
//	@Test
	void 게시물_페이징_및_검색_기능_테스트() {
		
		//given
		int page = 3;
		int limit = 10;
		int offset = (page - 1) * limit;
		
		String[] types = {"T"};
		String keyword = "PagingSearchTest";
		
		for(int i = 1; i <= 25; i++) {
			
			BoardDTO board = BoardDTO.builder().title(keyword + " 제목 " + i).content("내용").writer("writer" + (i % 10)).build();
			
			
			if(i % 3 == 0) {
				
				board.setTitle(keyword + " [댓글] 제목 " + i);
				boardMapper.insert(board);
				
				for(int j = 1; j <= 3; j++) {
					
					CommentDTO comment = CommentDTO.builder().boardId(board.getBoardId()).content("댓글" + j).writer("commenter" + j).build();
					commentMapper.insert(comment);
				}
				
			} else if(i % 2 == 0) {
				
				board.setTitle(keyword + " [첨부파일] 제목 " + i);
				boardMapper.insert(board);
				
				board.addFile(BoardFileDTO.builder().boardId(board.getBoardId()).uuid(UUID.randomUUID().toString()).fileName("_test1_.jpg").sortOrder(0).image(true).build());
				board.addFile(BoardFileDTO.builder().boardId(board.getBoardId()).uuid(UUID.randomUUID().toString()).fileName("_test2_.jpg").sortOrder(1).image(true).build());
				boardMapper.insertFiles(board);
				
			} else {
				
				boardMapper.insert(board);
				
			}
			
		}
		
		//when
		List<BoardListDTO> list = boardMapper.selectListSearch(offset, limit, types, keyword);
		
		//then
		assertNotNull(list);
		assertEquals(5, list.size());
		
		list.forEach(boardListDTO -> log.info("조회된 목록: " + boardListDTO));
		
		
		list.forEach(boardListDTO -> {
			
			if(boardListDTO.getTitle().contains("[댓글]")) {
				
				assertEquals(3, boardListDTO.getCommentCount(), "이 게시물은 댓글이 3개여야 합니다.");
			}
			
		});
		
	}
	
//	@Test
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
	
	
//	@Test
	void 게시물_삭제_성공() {
		
		//given
		BoardDTO board = setUpBoard();
		
		
		//when
		boardMapper.delete(board.getBoardId());
		
		//then
		BoardDTO result = boardMapper.selectById(board.getBoardId());
		assertNull(result);
		
	}
	
	
//	@Test
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
	
//	@Test
	void 첨부파일을_포함한_게시물_등록_성공() {
		
		//given
		BoardDTO board = setUpBoard();
		board.addFile(BoardFileDTO.builder().boardId(board.getBoardId()).uuid(UUID.randomUUID().toString()).fileName("test1.jpg").sortOrder(0).image(true).build());
		board.addFile(BoardFileDTO.builder().boardId(board.getBoardId()).uuid(UUID.randomUUID().toString()).fileName("test2.jpg").sortOrder(1).image(true).build());
		
		//when
		boardMapper.insertFiles(board);
		
		//then
		BoardDTO boardWithFiles = boardMapper.selectById(board.getBoardId());
		assertEquals("test1.jpg", boardWithFiles.getFiles().get(0).getFileName(), "파일의 이름이 일치하지 않습니다.");
		assertEquals("test2.jpg", boardWithFiles.getFiles().get(1).getFileName(), "파일의 이름이 일치하지 않습니다.");
		
	}
	
//	@Test
	void 첨부파일을_포함한_게시물_조회_성공() {
		
		
		//given
		BoardDTO board = setUpBoard();
		
		String uuid1 = UUID.randomUUID().toString();
		String uuid2 = UUID.randomUUID().toString();
		
		board.addFile(BoardFileDTO.builder().boardId(board.getBoardId()).uuid(uuid1).fileName("select1.jpg").sortOrder(0).image(true).build());
		board.addFile(BoardFileDTO.builder().boardId(board.getBoardId()).uuid(uuid2).fileName("select2.jpg").sortOrder(1).image(true).build());
		
		
		boardMapper.insertFiles(board);
		
		
		//when
		BoardDTO selectedBoard = boardMapper.selectById(board.getBoardId());
		
		//then
		
		 assertNotNull(selectedBoard, "조회된 게시글이 있어야 합니다.");
		 assertEquals(2, selectedBoard.getFiles().size(), "첨부파일은 총 두 개여야 합니다.");
		 assertEquals(uuid1, selectedBoard.getFiles().get(0).getUuid()); log.info("조회된 파일 목록: " + selectedBoard.getFiles());
		
	}
	
//	@Test
	void 필드_초기값이_조회시에도_유지되는지_확인() {
	    // given: 파일 없는 게시글 저장
	    BoardDTO board = setUpBoard();

	    // when: 조회
	    BoardDTO savedBoard = boardMapper.selectById(board.getBoardId());

	    // then
	    // 1. MyBatis가 초기값을 유지해주면 결과는 Not Null (빈 리스트)
	    // 2. MyBatis가 덮어씌우면 결과는 Null
	    assertNotNull(savedBoard.getFiles(), "MyBatis가 초기값(ArrayList)을 유지해주는지 확인합니다.");
	    log.info("MyBatis 조회 후 files 필드 상태: " + savedBoard.getFiles());
	}
	
	
//	@Test
	@DisplayName("리플렉션 등으로 필드에 강제로 null이 할당되어도 Getter는 빈 리스트를 반환해야 한다")
	void getter_null_방어_테스트() throws Exception {
	    // 1. Given: 객체 생성
	    BoardDTO board = setUpBoard();

	    // 2. 리플렉션을 사용하여 강제로 필드에 null 주입 (MyBatis의 동작을 흉내냄)
	    
	    BoardDTO selectedBoard = boardMapper.selectById(board.getBoardId());
	    
	    java.lang.reflect.Field field = BoardDTO.class.getDeclaredField("files");
	    field.setAccessible(true);
	    field.set(selectedBoard, null);

	    // 3. When & Then: Getter 호출 시 null이 아닌 빈 리스트가 나오는지 확인
	    assertNotNull(selectedBoard.getFiles(), "필드가 null이어도 Getter는 빈 리스트를 반환해야 함");
	    assertEquals(0, selectedBoard.getFiles().size(), "반환된 리스트는 비어있어야 함");
	    log.info("MyBatis 조회 후 files 필드 상태: " + selectedBoard.getFiles());
	}
	
	
	
//	@Test
	void 첨부파일을_포함한_게시물_수정_성공() {
		
		//given
		BoardDTO board = setUpBoard();
		
		board.addFile(BoardFileDTO.builder().boardId(board.getBoardId()).uuid(UUID.randomUUID().toString()).fileName("update1.jpg").image(true).sortOrder(0).build());
		board.addFile(BoardFileDTO.builder().boardId(board.getBoardId()).uuid(UUID.randomUUID().toString()).fileName("update2.jpg").image(true).sortOrder(1).build());
		
		
		boardMapper.insertFiles(board);
		
		
		//when
		board.setTitle("수정된 제목");
		board.setContent("수정된 내용");
		
		board.clearFiles();
		board.addFile(BoardFileDTO.builder().boardId(board.getBoardId()).uuid(UUID.randomUUID().toString()).fileName("update3.jpg").image(true).sortOrder(0).build());
		board.addFile(BoardFileDTO.builder().boardId(board.getBoardId()).uuid(UUID.randomUUID().toString()).fileName("update4.jpg").image(true).sortOrder(1).build());
		board.addFile(BoardFileDTO.builder().boardId(board.getBoardId()).uuid(UUID.randomUUID().toString()).fileName("update5.jpg").image(true).sortOrder(2).build());
		
		
		boardMapper.deleteFiles(board.getBoardId());
		
		BoardDTO boardWithNoImages = boardMapper.selectById(board.getBoardId());
		
		assertEquals(0, boardWithNoImages.getFiles().size());
		
		boardMapper.update(board);
		boardMapper.insertFiles(board);
		
		
		//then
		BoardDTO result = boardMapper.selectById(board.getBoardId());
		assertEquals("수정된 제목", result.getTitle());
		assertEquals(3, result.getFiles().size());
		assertEquals("update3.jpg", result.getFiles().get(0).getFileName());
		
	}
		
}
