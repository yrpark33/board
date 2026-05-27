package org.oolong.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.oolong.dto.BoardDTO;
import org.oolong.dto.BoardFileDTO;
import org.oolong.dto.BoardListDTO;
import org.oolong.dto.BoardPageRequestDTO;
import org.oolong.dto.BoardPageResponseDTO;
import org.oolong.dto.CommentDTO;
import org.oolong.mapper.CommentMapper;
import org.oolong.service.exception.ApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j2;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/root-context.xml")
@Log4j2
@Transactional
public class BoardServiceTests {
	
	@Autowired BoardService boardService;
	
	@Autowired CommentMapper commentMapper;
	
	@Autowired ObjectMapper objectMapper;
	
	
	@Autowired JdbcTemplate jdbcTemplate;
	
	@Value("${org.oolong.upload.path}")
	String uploadPath;
	
	
	@AfterEach
	void cleanUp() {
	    FileSystemUtils.deleteRecursively(new File(uploadPath));
	}
	
	
	private BoardDTO createBoardDTO() {
	    return BoardDTO.builder()
	            .title("테스트 제목")
	            .content("테스트 내용")
	            .writer("user01")
	            .build();
	}
	
	private MultipartFile[] createMockFiles(String type) throws IOException {
	    switch(type) {
	        case "NULL": return null;
	        case "EMPTY_ARRAY": return new MultipartFile[0];
	        case "EMPTY_FILE": 
	            return new MultipartFile[]{new MockMultipartFile("files", "", "", new byte[0])};
	        case "MIXED":
	            // 이미지 파일 (image/jpeg)
	    	    BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
	    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    		javax.imageio.ImageIO.write(bufferedImage, "jpg", baos);
	    		byte[] realImageBytes = baos.toByteArray();
	    	    
	    	    MockMultipartFile imageFile = new MockMultipartFile(
	    	            "files", "sample_image.jpg", "image/jpg", realImageBytes);
	    	    
	    	    // 일반 텍스트 파일 (text/plain)
	    	    MockMultipartFile textFile = new MockMultipartFile(
	    	            "files", "sample_note.txt", "text/plain", "this is plain text".getBytes());
	            return new MultipartFile[] {imageFile, textFile};
	        default: return null;
	    }
	}
	
	
//	@ParameterizedTest
//	@ValueSource(strings = {"NULL", "EMPTY_ARRAY", "EMPTY_FILE"})
	@DisplayName("파일이 비어있는 다양한 경우에도 게시물은 정상 등록되어야 한다")
	void writeBoard_EmptyCases(String type) throws IOException {
	    
	    //given
	    BoardDTO dto = createBoardDTO();
	    MultipartFile[] files = createMockFiles(type); 
	    
	    //when
	    Long boardId = boardService.writeBoard(dto, files);
	    
	    //then
	    assertNotNull(boardId);
	    BoardDTO savedBoard = boardService.getBoard(boardId);
	    
	    assertEquals(dto.getTitle(), savedBoard.getTitle());
	    assertEquals(dto.getWriter(), savedBoard.getWriter());
	    assertTrue(savedBoard.getFiles().isEmpty(), "파일 리스트는 비어있어야 함");
	}    
	   
	
	
//	@Test
	@DisplayName("이미지와 일반 파일이 섞여 있어도 각각의 특성에 맞게 정상 등록되어야 한다")
	void writeBoard_WithFiles() throws IOException {
	    
	    // given: 게시글 정보와 섞인 파일들 준비
	    BoardDTO board = createBoardDTO();
	    
	    // 이미지 파일 (image/jpeg)
	    MultipartFile[] files = createMockFiles("MIXED");
	    
	    
	    // when: 서비스 호출
	    Long boardId = boardService.writeBoard(board, files);
	    
	    // then: 검증
	    assertNotNull(boardId);
	    BoardDTO savedBoard = boardService.getBoard(boardId);
	    
	    assertEquals(board.getTitle(), savedBoard.getTitle());
	    assertEquals(board.getWriter(), savedBoard.getWriter());
	    
	    // 전체 파일 개수 확인
	    assertEquals(2, savedBoard.getFiles().size(), "총 2개의 파일 정보가 DB에 있어야 합니다.");
	    
	    
	    //물리적 파일
	    savedBoard.getFiles().forEach(dto -> {
			
			String originalPath = uploadPath + "/original/" + dto.getUuid() + "_" + dto.getFileName();
			
			File savedFile = new File(originalPath);
			assertTrue(savedFile.exists(), "물리적 파일이 생성되어야 합니다.");
			
			if(dto.isImage()) {
				
				String thumbnailPath = uploadPath + "/thumbnail/s_" + dto.getUuid() + "_" + dto.getFileName();
				File thumbFile = new File(thumbnailPath);
				assertTrue(thumbFile.exists(), "이미지라면 썸네일이 생성되어야 합니다.");
				
			
			}
	    });
	    
	    
	    // 이미지 파일 DB 검증 (isImage가 true여야 함)
	    BoardFileDTO savedImage = savedBoard.getFiles().stream()
	            .filter(f -> f.getFileName().equals("sample_image.jpg"))
	            .findFirst().orElseThrow();
	    assertTrue(savedImage.isImage(), "이미지 파일은 isImage가 true여야 합니다.");
	    
	    // 일반 파일 DB 검증 (isImage가 false여야 함)
	    BoardFileDTO savedText = savedBoard.getFiles().stream()
	            .filter(f -> f.getFileName().equals("sample_note.txt"))
	            .findFirst().orElseThrow();
	    assertFalse(savedText.isImage(), "일반 텍스트 파일은 isImage가 false여야 합니다.");
	
	    
	}
	    
	
//	@Test
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

	
	
//	@Test
	void 게시물_목록_조회_성공() throws IOException {
		
		
		//given
		String keyword = "getBoardList";
		
		for(int i = 1; i <= 25; i++) {
			
			BoardDTO board = createBoardDTO();
			
			
			if(i % 3 == 0) {
				
				board.setTitle(keyword + " [댓글] 제목 " + i);
				boardService.writeBoard(board, createMockFiles("EMPTY_FILE"));
				
				
				for(int j = 1; j <= 3; j++) {
					
					CommentDTO comment = CommentDTO.builder().boardId(board.getBoardId()).content("댓글" + j).writer("commenter" + j).build();
					commentMapper.insert(comment);
				}
				
				
				
			} else if(i % 2 == 0) {
				
				board.setTitle(keyword + " [첨부파일] 제목 " + i);
				boardService.writeBoard(board, createMockFiles("MIXED"));
				
			} else {
				
				board.setTitle(keyword + " [기본] 제목 " + i);
				boardService.writeBoard(board, createMockFiles("EMPTY_FILE"));
				
			}
			
		}


		//given
		BoardPageRequestDTO requestDTO = new BoardPageRequestDTO();
		
		requestDTO.setPage(3);
		requestDTO.setSize(10);
		requestDTO.setTypes("T");
		requestDTO.setKeyword(keyword);
		
		
		//when
		BoardPageResponseDTO responseDTO = boardService.getBoardList(requestDTO);
		
		//then
		
		List<BoardListDTO> list = responseDTO.getDtoList();
		
		assertNotNull(list);
		assertEquals(25, responseDTO.getTotalCount());
		assertEquals(5, list.size());
		
		list.forEach(boardListDTO -> {
			
			if(boardListDTO.getTitle().contains("[댓글]")) {
				
				assertEquals(3, boardListDTO.getCommentCount(), "이 게시물은 댓글이 3개여야 합니다.");
			}
			
			if(boardListDTO.getTitle().contains("[첨부파일]")) {
				
				assertEquals(2, boardService.getBoard(boardListDTO.getBoardId()).getFiles().size(), "이 게시물은 파일이 2개여야 합니다.");

			}
			
		});
		
				
	}
	
	
//	@Test
	void 게시물_수정_성공() throws IOException {
		
		//given
		BoardDTO board = createBoardDTO();
		Long boardId = boardService.writeBoard(board, createMockFiles("MIXED"));
		
		
		
		//when
		
		List<BoardFileDTO> files = boardService.getBoard(boardId).getFiles();
		
		List<BoardFileDTO> deletedFiles = new ArrayList<>();
		List<BoardFileDTO> oldFiles = new ArrayList<>();
		
		deletedFiles.add(files.get(0));
		oldFiles.add(files.get(1));
		
		

		String deletedFileInfosJson = objectMapper.writeValueAsString(deletedFiles);
		String oldFileInfosJson = objectMapper.writeValueAsString(oldFiles);
		
		BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		javax.imageio.ImageIO.write(bufferedImage, "jpg", baos);
		byte[] realImageBytes = baos.toByteArray();
		
		MockMultipartFile newImageFile = new MockMultipartFile("files", "new_image.jpg", "image/jpg", realImageBytes);
		MockMultipartFile newTextFile = new MockMultipartFile("files", "new_note.txt", "text/plain", "this is new text".getBytes());
		
		
		
		BoardDTO updateDTO = BoardDTO.builder().boardId(boardId).title("수정제목").writer(board.getWriter()).content("수정내용").build();
		
		boolean admin = false;
		
		boardService.modifyBoard(updateDTO, oldFileInfosJson, deletedFileInfosJson, new MultipartFile[] {newImageFile, newTextFile}, admin);
		
		
		
		
		//then
		BoardDTO result = boardService.getBoard(boardId);
		assertNotNull(result);
		assertEquals("수정제목", result.getTitle());
		assertEquals("수정내용", result.getContent());
		assertEquals("user01", result.getWriter());
		assertEquals(3, result.getFiles().size());
		
		
		List<BoardFileDTO> resultFiles = result.getFiles();
		
		
		//삭제되지 않고 남은 파일 DB에 존재하는지 확인
		BoardFileDTO oldFile = resultFiles.stream().filter(f -> f.getFileName().equals(oldFiles.get(0).getFileName())).findFirst().orElseThrow();
		
		//삭제되지 않고 남은 파일 물리저장소에 존재하는지 확인
		assertTrue(Paths.get(uploadPath, "original", oldFile.getUuid() + "_" + oldFile.getFileName()).toFile().exists());
		
		
		//추가된 파일(addedFile)이 DB에 존재하는지 확인
		BoardFileDTO addedText = resultFiles.stream().filter(f -> f.getFileName().equals("new_note.txt")).findFirst().orElseThrow();
		BoardFileDTO addedImage = resultFiles.stream().filter(f -> f.getFileName().equals("new_image.jpg")).findFirst().orElseThrow();
		
		//추가된 파일(addedFile)이 물리저장소에 존재하는지 확인
		assertTrue(Paths.get(uploadPath, "original", addedText.getUuid() + "_" + addedText.getFileName()).toFile().exists());
		assertFalse(Paths.get(uploadPath, "thumbnail",  "s_" + addedText.getUuid() + "_" + addedText.getFileName()).toFile().exists());
		assertTrue(Paths.get(uploadPath, "original", addedImage.getUuid() + "_" + addedImage.getFileName()).toFile().exists());
		assertTrue(Paths.get(uploadPath, "thumbnail",  "s_" + addedImage.getUuid() + "_" + addedImage.getFileName()).toFile().exists());
	
		//삭제하기로 한 파일(deletedFiles)이 DB에서 사라졌는지 확인
		assertFalse(resultFiles.stream().anyMatch(f -> f.getFileName().equals(deletedFiles.get(0).getFileName())), "삭제 요청한 파일은 DB에 없어야 합니다.");
		
		
		//삭제하기로 한 파일(deletedFiles)이 물리적으로 삭제되었는지 확인
		assertFalse(Paths.get(uploadPath, "original", deletedFiles.get(0).getUuid() + "_" + deletedFiles.get(0).getFileName()).toFile().exists(), "삭제 요청한 이미지의 원본은 물리적으로 삭제되어야 합니다.");
		assertFalse(Paths.get(uploadPath, "thumbnail", "s_" + deletedFiles.get(0).getUuid() + "_" + deletedFiles.get(0).getFileName()).toFile().exists(), "삭제 요청한 이미지의 썸네일은 물리적으로 삭제되어야 합니다.");

	}
	
	
//	@Test
	@DisplayName("잘못된 형식의 JSON이 전달되면 파싱 에러(RuntimeException)가 발생해야 한다")
	void testModifyBoard_ParsingError() throws IOException {
	    // given: 잘못된 JSON 데이터 준비
		BoardDTO board = createBoardDTO();
		boardService.writeBoard(board, createMockFiles("EMPTY_FILE"));
		BoardDTO updateDTO = BoardDTO.builder().boardId(board.getBoardId()).title("Json 테스트").content("Json 테스트").writer(board.getWriter()).build();
		String invalidJson = "!!!INVALID_JSON!!!";

	    // when & then: 예외가 발생하는지 검증
	    
		boolean admin = false;
		
		
		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
	        boardService.modifyBoard(updateDTO, invalidJson, "[]", createMockFiles("MIXED"), admin);
	    });

	    // 추가 검증: 에러 메시지가 내가 설정한 것과 일치하는지 확인
	    assertEquals("파일 정보 형식이 올바르지 않습니다.", exception.getMessage());
	    
	    File originalDir = new File(uploadPath, "original");
	    File thumbnailDir = new File(uploadPath, "thumbnail");

	    assertFalse(originalDir.exists(), "원본 폴더가 존재하지 않아야합니다.");
	    assertFalse(thumbnailDir.exists(), "썸네일 폴더가 존재하지 않아야합니다.");
	    
	}
	
	
//	@Test
	@DisplayName("DB 업데이트 중 에러 발생 시, 업로드되었던 파일이 수동으로 삭제되어야 한다")
	void testModifyBoard_FileRollback() throws IOException {
	    // given: 정상 게시글 등록
	    BoardDTO board = createBoardDTO();
	    boardService.writeBoard(board, null); // 일단 파일 없이 등록
	    
	    //에러 유도용 DTO: 제목을 길게 해서 DB에서 SQL 예외가 나게 만듦
	    BoardDTO updateDTO = BoardDTO.builder()
	            .boardId(board.getBoardId())
	            .title("A".repeat(5000)) // DB 컬럼 사이즈 초과 유도
	            .content("롤백 테스트")
	            .writer(board.getWriter())
	            .build();
	            
	    // 실제로 저장될 파일 준비
	    MultipartFile[] addedFiles = createMockFiles("MIXED");
	    
	    
	    boolean admin = false;
	    
	    // When & Then: 예외 발생 확인
	    assertThrows(Exception.class, () -> {
	        boardService.modifyBoard(updateDTO, "[]", "[]", addedFiles, admin);
	    });

	    // 파일 시스템에 파일이 남아있지 않아야 함
	    File originalDir = new File(uploadPath, "original");
	    File thumbnailDir = new File(uploadPath, "thumbnail");
	    
	    File[] originalFiles = originalDir.listFiles();
	    File[] thumbnailFiles = thumbnailDir.listFiles();

	    assertEquals(0, originalFiles.length, "원본 폴더가 비어있어야 합니다.");
	    assertEquals(0, thumbnailFiles.length, "썸네일 폴더가 비어있어야 합니다.");
	}
	
	
	
//	@Test
	void 게시물_삭제_성공() throws IOException {
		
		//given
		BoardDTO board = createBoardDTO();
		MultipartFile[] files = createMockFiles("MIXED");
		Long generatedId = boardService.writeBoard(board, files);
		int fileCount = files.length;
		
		//when
		
		boolean admin = false;
		boardService.removeBoard(board.getWriter(), generatedId, admin);
		
		
		//then
		ApplicationException ex = assertThrows(ApplicationException.class, () -> boardService.getBoard(generatedId));
		assertEquals(404, ex.getCode());
		assertEquals("BOARD_NOT_FOUND", ex.getMessage());
		
		
		Integer boardCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM tbl_board WHERE board_id = ? and deleted = true", Integer.class, generatedId);
		assertEquals(1, boardCount, "게시물은 삭제 상태(deleted=true)여야 합니다.");
		
		Integer deletedFileCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM tbl_board_file WHERE board_id = ? AND deleted = true", Integer.class, generatedId);
		assertEquals(fileCount, deletedFileCount, "모든 첨부파일이 삭제 상태(deleted=true)여야 합니다.");
		
		
		
		String sql = "SELECT uuid, file_name, image FROM tbl_board_file WHERE board_id = ? AND deleted = TRUE";

		List<BoardFileDTO> deletedFiles = jdbcTemplate.query(sql, (rs, rowNum) -> {
		    BoardFileDTO dto = new BoardFileDTO();
		    dto.setUuid(rs.getString("uuid"));
		    dto.setFileName(rs.getString("file_name"));
		    dto.setImage(rs.getBoolean("image"));
		    return dto;
		}, generatedId);
		
		for(BoardFileDTO file : deletedFiles) {
			
			assertTrue(Paths.get(uploadPath, "original", file.getUuid() + "_" + file.getFileName()).toFile().exists(), "물리저장소에는 원본파일이 존재해야한다.");
			
			if(file.isImage()) {
				
				assertTrue(Paths.get(uploadPath, "thumbnail", "s_" + file.getUuid() + "_" + file.getFileName()).toFile().exists(), "물리저장소에는 썸네일이 존재해야한다.");
				
				
			}
						
			
		}
		
	}
	
	
	
//	@Test
	void 존재하지_않는_게시물_수정시_예외_발생() {
	    
		// given
	    Long nonExistId = 999999L;
	    BoardDTO board = createBoardDTO();
	    board.setBoardId(nonExistId);
	    
	    // JSON 파라미터는 그냥 빈 배열 문자열로 퉁치기!
	    String emptyJson = "[]"; 
	    MultipartFile[] emptyFiles = new MultipartFile[0];

	    // when & then
	    boolean admin = false;
	    
	    ApplicationException ex = assertThrows(ApplicationException.class, () -> 
	        boardService.modifyBoard(board, emptyJson, emptyJson, emptyFiles, admin)
	    );
	    
	    assertEquals(404, ex.getCode());
	    assertEquals("BOARD_NOT_FOUND", ex.getMessage());
	}
	
	
	
	
	
	
//	@Test
	void 존재하지않는_게시물_삭제시_예외발생() {
		
		//given
		Long boardId = 9999999L;
		String username = "user01";
		
		
		//when & then
		boolean admin = false;
		ApplicationException ex = assertThrows(ApplicationException.class, () -> boardService.removeBoard(username, boardId, admin));
		
		assertEquals(404, ex.getCode());
		assertEquals("BOARD_NOT_FOUND", ex.getMessage());
		
	}
	
	
//	@Test
	void 이미_삭제된_게시물_삭제시_예외발생() throws IOException {
		
		
		//given
		BoardDTO board = createBoardDTO();
		MultipartFile[] files = createMockFiles("MIXED");
		boardService.writeBoard(board, files);
		Long generatedId = board.getBoardId();
		
		//삭제
		boolean admin = false;
		boardService.removeBoard(board.getWriter(), generatedId, admin);
		
		
		//when & then
		//삭제된 게시물 재삭제
		ApplicationException ex = assertThrows(ApplicationException.class, () -> boardService.removeBoard(board.getWriter(), generatedId, admin));
		
		assertEquals(404, ex.getCode());
		assertEquals("BOARD_NOT_FOUND", ex.getMessage());
		
	}
	
	
//	@Test
	void 이미_삭제된_게시물_수정시_예외발생() throws IOException {
		
		//given
		BoardDTO board = createBoardDTO();
		boardService.writeBoard(board, createMockFiles("MIXED"));
		Long generatedId = board.getBoardId();
		
		boolean admin = false;
		
		boardService.removeBoard(board.getWriter(), generatedId, false);
		
		//when
		BoardDTO updateDTO = BoardDTO.builder().boardId(generatedId).title("수정된 제목").content("수정된 내용").build();
		
		String emptyJson = "[]"; 
	    MultipartFile[] emptyFiles = new MultipartFile[0];
		
		ApplicationException ex = assertThrows(ApplicationException.class, () -> boardService.modifyBoard(updateDTO, emptyJson, emptyJson, emptyFiles, admin));
		
		assertEquals(404, ex.getCode());
		assertEquals("BOARD_NOT_FOUND", ex.getMessage());
		
	}
	
	
//	@Test
	void 작성자불일치_수정시_403예외() throws IOException {
	    // given
	    BoardDTO board = createBoardDTO();
	    boardService.writeBoard(board, createMockFiles("EMPTY_FILE"));
	    BoardDTO updateDTO = BoardDTO.builder()
	            .boardId(board.getBoardId())
	            .title("수정")
	            .content("수정")
	            .writer("다른사람")  // 작성자 불일치
	            .build();

	    // when & then
	    
	    boolean admin = false;
	    
	    ApplicationException ex = assertThrows(ApplicationException.class, () -> {
	        boardService.modifyBoard(updateDTO, "[]", "[]", createMockFiles("EMPTY_FILE"), admin);
	    });

	    assertEquals(403, ex.getCode());
	}
	
	
//	@Test
	void 작성자불일치_수정화면을_위한_조회시_403예외() throws IOException {
		
		//given
		BoardDTO board = createBoardDTO();
	    boardService.writeBoard(board, createMockFiles("EMPTY_FILE"));
	    
	    String wrongUsername = "다른사람"; //작성자 불일치
	    
	    //when&then
	    
	    boolean admin = false;
	    
	    ApplicationException ex = assertThrows(ApplicationException.class, () -> {
	    	boardService.getBoardForModify(wrongUsername, board.getBoardId(), admin);
	    });
	    
	    assertEquals(403, ex.getCode());
		
	}
	
//	@Test
	void 작성자불일치_삭제시_403예외() throws IOException {
		
		//given
		BoardDTO board = createBoardDTO();
	    boardService.writeBoard(board, createMockFiles("EMPTY_FILE"));
	    
	    String wrongUsername = "다른사람"; //작성자 불일치
	    
	    //when&then
	    
	    boolean admin = false;
	    
	    ApplicationException ex = assertThrows(ApplicationException.class, () -> {
	    	boardService.removeBoard(wrongUsername, board.getBoardId(), admin);
	    });
	    
	    assertEquals(403, ex.getCode());
		
	}
	
	
//	@Test
	@DisplayName("admin은 작성자가 아니어도 수정을 위한 조회가 가능해야 한다")
	void admin계정_작성자불일치_수정화면을_위한_조회_가능() throws IOException {
	    // given
	    BoardDTO board = createBoardDTO();
	    boardService.writeBoard(board, createMockFiles("EMPTY_FILE"));

	    String adminUsername = "admin"; // 작성자 아닌 admin 계정
	    boolean admin = true;

	    // when & then: 예외 없이 정상 삭제되어야 함
	    assertDoesNotThrow(() -> {
	        boardService.getBoardForModify(adminUsername, board.getBoardId(), admin);
	    });
	}
	
	
//	@Test
	@DisplayName("admin은 작성자가 아니어도 수정 가능해야 한다")
	void admin계정_작성자불일치_수정가능() throws IOException {
	    // given
	    BoardDTO board = createBoardDTO();
	    boardService.writeBoard(board, createMockFiles("EMPTY_FILE"));
	    
	    BoardDTO updateDTO = BoardDTO.builder()
	            .boardId(board.getBoardId())
	            .title("admin 수정")
	            .content("admin 수정")
	            .build();
	    
	    
	    
	    // when & then: 예외 없이 정상 수정되어야 함
	    
	    boolean admin = true;
	    assertDoesNotThrow(() -> {
	        boardService.modifyBoard(updateDTO, "[]", "[]", createMockFiles("EMPTY_FILE"), admin);
	    });
	}
	
//	@Test
	@DisplayName("admin은 작성자가 아니어도 삭제 가능해야 한다")
	void admin계정_작성자불일치_삭제가능() throws IOException {
	    // given
	    BoardDTO board = createBoardDTO();
	    boardService.writeBoard(board, createMockFiles("EMPTY_FILE"));

	    String adminUsername = "admin"; // 작성자 아닌 admin 계정
	    boolean admin = true;

	    // when & then: 예외 없이 정상 삭제되어야 함
	    assertDoesNotThrow(() -> {
	        boardService.removeBoard(adminUsername, board.getBoardId(), admin);
	    });
	}
	
	
	
	
}
