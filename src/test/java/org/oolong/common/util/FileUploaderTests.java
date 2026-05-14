package org.oolong.common.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.oolong.dto.BoardFileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.log4j.Log4j2;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/root-context.xml")
@Log4j2
public class FileUploaderTests {
	
	@Autowired
	FileUploader fileUploader;
	
	@Value("${org.oolong.upload.path}")
	String uploadPath;
	
	
	@BeforeEach
	void setup() {
	    new File(uploadPath, "original").mkdirs();
	    new File(uploadPath, "thumbnail").mkdirs();
	}
	
	@AfterEach
	void cleanUp() {
	    FileSystemUtils.deleteRecursively(new File(uploadPath));
	}
	
	
	
	@Test
	@DisplayName("혼합 파일 업로드 성공 케이스")
	void testUploadFiles_withFiles() throws IOException {
		
		BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		javax.imageio.ImageIO.write(bufferedImage, "jpg", baos);
		byte[] realImageBytes = baos.toByteArray();
		
		//given		
		MockMultipartFile imageFile = new MockMultipartFile("files", "test_image.jpg", "image/jpeg", realImageBytes);
		MockMultipartFile textFile = new MockMultipartFile("files", "test_text.txt", "text/plain",  "test plain text".getBytes());
		MultipartFile[] files = {imageFile, textFile};
		
		//when
		List<BoardFileDTO> result = fileUploader.uploadFiles(files);
		
		
		//then
		result.forEach(dto -> {
			
			assertTrue(Paths.get(uploadPath, "original", dto.getUuid() + "_" + dto.getFileName()).toFile().exists()
						, "물리적 파일이 생성되어야 합니다.");
			
			
			if(dto.isImage()) {
				
				String thumbnailPath = uploadPath + "/thumbnail/s_" + dto.getUuid() + "_" + dto.getFileName();
				File thumbFile = new File(thumbnailPath);
				assertTrue(Paths.get(uploadPath, "thumbnail", "s_" + dto.getUuid() + "_" + dto.getFileName()).toFile().exists(), "이미지라면 썸네일이 생성되어야 합니다.");
				 
			
			}
			
		});
		
		
	}
	
	@Test
	@DisplayName("첨부파일이 비어있는(empty) 경우 업로드를 수행하지 않고 빈 리스트를 반환해야 한다")
	void testUploadFiles_EmptyFile() throws IOException {
	    
	    //given: 파일명은 있지만 데이터(byte)가 0인 MockMultipartFile 생성
	    // 브라우저가 빈 파일을 보낼 때의 상황을 그대로 묘사합니다.
		
		File uploadDir = new File(uploadPath, "original");
		String[] before = uploadDir.list();
		
		
	    MockMultipartFile emptyFile = new MockMultipartFile(
	        "files", 
	        "", 
	        "application/octet-stream", 
	        new byte[0]
	    );
	    
	    MultipartFile[] files = {emptyFile};
	    
	    //when: 업로드 메서드 호출
	    List<BoardFileDTO> result = fileUploader.uploadFiles(files);
	    
	    //then: 검증
	    assertNotNull(result, "결과 리스트는 null이 아니어야 합니다.");
	    assertTrue(result.isEmpty(), "파일이 비어있으므로 결과 리스트도 비어있어야 합니다.");
	    
	    // 추가 검증: 실제로 파일이 생성되지 않았는지 확인 (물리 저장소 체크)
	    // 이 시점에서는 uploadPath 내부에 어떤 파일도 생겨나선 안 됩니다.
	    String[] after = uploadDir.list();
	    
	    assertArrayEquals(before, after, "업로드 전후의 파일 목록이 일치해야 합니다.");
	}
	
	
	@Test
	@DisplayName("이미지 파일 삭제 시 원본과 썸네일이 모두 삭제되어야 한다")
	void testDeleteFiles_Image() throws IOException {
	    //given: 가짜 UUID와 파일명 설정
	
		String uuid = UUID.randomUUID().toString();
	    String fileName = "delete_test.jpg";
	    String fullFileName = uuid + "_" + fileName;
	    
	    // 물리적 파일 생성 (원본 & 썸네일)
	    File originFile = new File(new File(uploadPath, "original"), fullFileName);
	    File thumbFile = new File(new File(uploadPath, "thumbnail"), "s_" + fullFileName);
	    
	    
	    // 테스트용 빈 파일 실제로 만들기
	    originFile.createNewFile();
	    thumbFile.createNewFile();

	    // 삭제를 위해 DTO 조립
	    BoardFileDTO dto = BoardFileDTO.builder()
	            .uuid(uuid)
	            .fileName(fileName)
	            .image(true)
	            .build();
	    
	    // when: 삭제 수행
	    fileUploader.deleteFiles(List.of(dto));

	    // then: 둘 다 사라졌는지 검증
	    assertFalse(originFile.exists(), "원본 파일이 삭제되어야 합니다.");
	    assertFalse(thumbFile.exists(), "썸네일 파일이 삭제되어야 합니다.");
	}
	
	
	
	
	@Test
	@DisplayName("이미지가 아닌 일반 파일을 삭제할 경우, 원본만 삭제되어야 한다")
	void testDeleteFiles_TextFile() throws IOException {
	    //given: 텍스트 파일용 UUID와 파일명 설정
	    String uuid = UUID.randomUUID().toString();
	    String fileName = "sample_document.txt";
	    String fullFileName = uuid + "_" + fileName;

	    //물리 저장소의 'original' 폴더에만 파일 생성
	    File originalFile = new File(new File(uploadPath, "original"), fullFileName);
	    originalFile.createNewFile();

	    //썸네일 파일은 존재하지 않아야 함을 명시적으로 확인하기 위한 경로 설정
	    File thumbnailFile = new File(new File(uploadPath, "thumbnail"), "s_" + fullFileName);

	    //삭제를 위해 DTO 조립 (image = false 가 핵심!)
	    BoardFileDTO dto = BoardFileDTO.builder()
	            .uuid(uuid)
	            .fileName(fileName)
	            .image(false) // 텍스트 파일이므로 false
	            .build();
	    
	    List<BoardFileDTO> dtoList = List.of(dto);

	    //when: 삭제 수행
	    fileUploader.deleteFiles(dtoList);

	    //then: 검증
	    assertFalse(originalFile.exists(), "텍스트 원본 파일은 삭제되어야 합니다.");
	    assertFalse(thumbnailFile.exists(), "썸네일 파일은 애초에 생성되지 않았어야 합니다.");
	}
	
	
	@Test
	@DisplayName("삭제할 파일 리스트가 비어있는 경우, 에러 없이 메서드가 종료되어야 한다")
	void testDeleteFiles_EmptyList() {
	    //given: 비어있는 리스트 준비
	    List<BoardFileDTO> emptyList = Collections.emptyList();

	    //when & then: 실행 시 아무런 예외(Exception)가 발생하지 않아야 함
	    assertDoesNotThrow(() -> {
	        fileUploader.deleteFiles(emptyList);
	    }, "빈 리스트를 전달했을 때 어떤 예외도 발생해서는 안 됩니다.");
	}
	
	@Test
	@DisplayName("존재하지 않는 파일을 삭제 요청해도 에러 없이 로그만 남기고 정상 종료되어야 한다")
	void testDeleteFiles_NonExistentFile() {
	    // given: 물리적으로 존재하지 않는 파일 정보 생성
	    String uuid = UUID.randomUUID().toString();
	    String fileName = "never_exists.txt";
	    
	    BoardFileDTO dto = BoardFileDTO.builder()
	            .uuid(uuid)
	            .fileName(fileName)
	            .image(false)
	            .build();
	    
	    // when & then: 삭제 호출 시 어떤 Exception도 발생하지 않아야 함
	    // assertDoesNotThrow는 JUnit5에서 제공하는 "예외 발생 안 함" 검증 메서드입니다.
	    assertDoesNotThrow(() -> {
	        fileUploader.deleteFiles(List.of(dto));
	    }, "존재하지 않는 파일 삭제 시 예외가 발생하면 안 됩니다.");
	}
	
	
}
