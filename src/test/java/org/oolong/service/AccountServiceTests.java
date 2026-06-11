package org.oolong.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.oolong.dto.AccountDTO;
import org.oolong.dto.AccountRole;
import org.oolong.mapper.AccountMapper;
import org.oolong.service.exception.ApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;

import lombok.extern.log4j.Log4j2;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/root-context.xml")
@Log4j2
@Transactional
public class AccountServiceTests {
	
	@Autowired
	AccountService accountService;
	
	@Autowired
	AccountMapper accountMapper;
	
	@Autowired
	PasswordEncoder encoder;
	
	
	@Value("${org.oolong.upload.path}")
	String uploadPath;
	
	File originalDir;
	File thumbnailDir;
	
	String subDir = "profile";
	
	@BeforeEach
	void init() {
	    originalDir = new File(new File(uploadPath, subDir), "original");
	    thumbnailDir = new File(new File(uploadPath, subDir), "thumbnail");
	}
	
	@AfterEach
	void cleanUp() {
	    FileSystemUtils.deleteRecursively(new File(uploadPath));
	}
	
	
	
	
	
	
	@Test
	void account_register_성공() {
		
		
		//given
		AccountDTO accountDTO = AccountDTO.builder().username("testUser").password("1111").name("홍길동").email("test@naver.com").build();
		
		//when
		accountService.register(accountDTO);
		
		
		//then
		AccountDTO result = accountMapper.select(accountDTO.getUsername());
		assertNotNull(result);
		assertTrue(encoder.matches("1111", result.getPassword()));
		assertEquals(accountDTO.getEmail(), result.getEmail());
		assertEquals(1, result.getRoleNames().size());
		assertEquals(AccountRole.USER, result.getRoleNames().get(0));
	}
	
	@Test
	void account_read_성공() {
		
		//given
		AccountDTO accountDTO = AccountDTO.builder().username("testUser").password("1111").name("홍길동").email("test@naver.com").build();
		accountService.register(accountDTO);
		
		//when
		AccountDTO result = accountService.read(accountDTO.getUsername());
		
		
		//then
		assertNotNull(result);
		assertEquals(accountDTO.getPassword(), result.getPassword());
		assertEquals(accountDTO.getName(), result.getName());
		assertTrue(encoder.matches("1111", result.getPassword()));
		assertEquals(accountDTO.getRoleNames().get(0), result.getRoleNames().get(0));
		
	}
	
	@Test
	void profileImg_null일_때_새_프로필_이미지_modify_성공() throws IOException {
		
		
		//given
		AccountDTO accountDTO = AccountDTO.builder().username("testUser").password("1111").name("홍길동").email("test@naver.com").build();
		accountService.register(accountDTO);
		
		BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, "jpg", baos);
		byte[] realImageBytes = baos.toByteArray();
		
		MockMultipartFile file = new MockMultipartFile("file", "test_image.jpg", "image/jpeg", realImageBytes);
		
		
		
		//when
		accountDTO.setEmail("newEmail@naver.com");
		accountService.modify(accountDTO, file);
		
		
		//then
		AccountDTO result = accountMapper.select(accountDTO.getUsername());
		assertEquals(accountDTO.getName(), result.getName());
		assertEquals(accountDTO.getEmail(), result.getEmail());
		assertEquals("test_image.jpg", result.getProfileImg().split("_", 2)[1]);
		
		assertTrue(new File(originalDir, result.getProfileImg()).exists(), "등록된 프로필 이미지가 물리저장소에 존재해야한다.");
		assertTrue(new File(thumbnailDir, "s_" + result.getProfileImg()).exists(), "등록된 프로필 이미지의 썸네일이 물리저장소에 존재해야한다.");
		
	}
	
	
	@Test
	void profileImg_존재하는_상태일_때_새_프로필_이미지_modify_성공() throws IOException {
		
		//given
		String oldUuid = UUID.randomUUID().toString();
		String oldFileName = "old_profile.jpg";
		String oldProfileImg = oldUuid + "_" + oldFileName;

		AccountDTO accountDTO = AccountDTO.builder()
		    .username("testUser")
		    .password("1111")
		    .name("홍길동")
		    .email("test@naver.com")
		    .build();
		//회원 등록
		accountService.register(accountDTO);
		
		
		//프로필 이미지 등록
		accountDTO.setProfileImg(oldProfileImg);
		accountMapper.update(accountDTO);

		// 물리 파일 직접 생성
		originalDir.mkdirs();
		thumbnailDir.mkdirs();
		new File(originalDir, oldUuid + "_" + oldFileName).createNewFile();
		new File(thumbnailDir, "s_" + oldUuid + "_" + oldFileName).createNewFile();
		
		
		
		//새 profileImg 등록
		BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, "jpg", baos);
		byte[] realImageBytes = baos.toByteArray();
		
		MockMultipartFile newFile = new MockMultipartFile("file", "new_profile.jpg", "image/jpeg", realImageBytes);
		
		
		//when
		accountService.modify(accountDTO, newFile);
		
		//then
		AccountDTO result = accountMapper.select(accountDTO.getUsername());
		assertFalse(new File(originalDir, oldProfileImg).exists(), "이전 프로필 이미지가 물리저장소에서 삭제되어야한다.");
		assertFalse(new File(thumbnailDir, "s_" + oldProfileImg).exists(), "이전 프로필 이미지의 썸네일이 물리저장소에서 삭제되어야한다.");
		assertTrue(new File(originalDir, result.getProfileImg()).exists(), "등록된 프로필 이미지가 물리저장소에 존재해야한다.");
		assertTrue(new File(thumbnailDir, "s_" + result.getProfileImg()).exists(), "등록된 프로필 이미지의 썸네일이 물리저장소에 존재해야한다.");
		
	}
	
	@Test
	void 이미지_없이_이메일만_modify_성공() throws IOException {
		
		
		//given
		String uuid = UUID.randomUUID().toString();
		String fileName = "profile.jpg";
		String profileImg = uuid + "_" + fileName;

		AccountDTO accountDTO = AccountDTO.builder()
		    .username("testUser")
		    .password("1111")
		    .name("홍길동")
		    .email("test@naver.com")
		    .build();
		//회원 등록
		accountService.register(accountDTO);
		
		
		//프로필 이미지 등록
		accountDTO.setProfileImg(profileImg);
		accountMapper.update(accountDTO);

		// 물리 파일 직접 생성
		originalDir.mkdirs();
		thumbnailDir.mkdirs();
		new File(originalDir, profileImg).createNewFile();
		new File(thumbnailDir, "s_" + profileImg).createNewFile();
		
		
		//when
		accountDTO.setEmail("newEmail@naver.com");
		MockMultipartFile emptyFile = new MockMultipartFile("file", "", "application/octet-stream", new byte[0]);
		accountService.modify(accountDTO, emptyFile);
		
		//then
		AccountDTO result = accountMapper.select(accountDTO.getUsername());
		assertTrue(new File(originalDir, profileImg).exists(), "기존 프로필 이미지가 물리저장소에 존재해야합니다.");
		assertTrue(new File(thumbnailDir, "s_" + profileImg).exists(), "기존 프로필 이미지의 썸네일이 물리저장소에 존재해야합니다.");		
		assertEquals(accountDTO.getEmail(), result.getEmail(), "이메일은 변경되어야합니다.");
		
	}
	
	
	@Test
	void account_remove_성공() throws IOException {
		
		//given
		String uuid = UUID.randomUUID().toString();
		String fileName = "profile.jpg";
		String profileImg = uuid + "_" + fileName;
		
		AccountDTO accountDTO = AccountDTO.builder()
			    .username("testUser")
			    .password("1111")
			    .name("홍길동")
			    .email("test@naver.com")
			    .build();
			//회원 등록
		accountService.register(accountDTO);
		
		//프로필 이미지 등록
		accountDTO.setProfileImg(profileImg);
		accountMapper.update(accountDTO);

		// 물리 파일 직접 생성
		originalDir.mkdirs();
		thumbnailDir.mkdirs();
		new File(originalDir, profileImg).createNewFile();
		new File(thumbnailDir, "s_" + profileImg).createNewFile();
		
			
		//when
		accountService.remove(accountDTO.getUsername());
		
		//then
		AccountDTO result = accountMapper.select(accountDTO.getUsername());
		assertFalse(result.isEnabled(), "삭제된 계정은 비활성화되어야합니다.");
		assertFalse(new File(originalDir, profileImg).exists(), "기존 프로필 이미지가 물리저장소에서 삭제되어야 합니다.");
		assertFalse(new File(thumbnailDir, "s_" + profileImg).exists(), "기존 프로필 이미지의 썸네일이 물리저장소에서 삭제되어야 합니다.");	
	}
	
	
	
	@Test
	void account_deleteProfileImg_성공() throws IOException {
		
		//given
		String uuid = UUID.randomUUID().toString();
		String fileName = "profile.jpg";
		String profileImg = uuid + "_" + fileName;

		AccountDTO accountDTO = AccountDTO.builder()
		    .username("testUser")
		    .password("1111")
		    .name("홍길동")
		    .email("test@naver.com")
		    .build();
		//회원 등록
		accountService.register(accountDTO);
		
		
		//프로필 이미지 등록
		accountDTO.setProfileImg(profileImg);
		accountMapper.update(accountDTO);

		// 물리 파일 직접 생성
		originalDir.mkdirs();
		thumbnailDir.mkdirs();
		new File(originalDir, profileImg).createNewFile();
		new File(thumbnailDir, "s_" + profileImg).createNewFile();
			
		
		//when
		accountService.deleteProfileImg(accountDTO);
		
		//then
		AccountDTO result = accountService.read(accountDTO.getUsername());
		assertNull(result.getProfileImg());
		assertFalse(new File(originalDir, profileImg).exists(), "기존 프로필 이미지가 물리저장소에서 삭제되어야 합니다.");
		assertFalse(new File(thumbnailDir, "s_" + profileImg).exists(), "기존 프로필 이미지의 썸네일이 물리저장소에서 삭제되어야 합니다.");		
		
	}
	
	
	
	
	@Test
	void 존재하지_않는_계정_read_404예외() {
		
		//given
		//존재하지 않는 username
		String wrongUsername = "wrongUser";
		
		//when & then
		ApplicationException ex = assertThrows(ApplicationException.class, () -> accountService.read(wrongUsername));
		assertEquals(404, ex.getCode());
		assertEquals("ACCOUNT_NOT_FOUND", ex.getMessage());
	}
	
	@Test
	void 존재하지_않는_계정_remove_404예외() {
		
		//given
		//존재하지 않는 username
		String wrongUsername = "wrongUser";
		
		//when & then
		ApplicationException ex = assertThrows(ApplicationException.class, () -> accountService.remove(wrongUsername));
		assertEquals(404, ex.getCode());
		assertEquals("ACCOUNT_NOT_FOUND", ex.getMessage());
		
	}
	
	
	@Test
	void 존재하지_않는_계정_modify_404예외() throws IOException {
		
		//given
		
		//존재하지않는 username
		String wrongUsername = "wrongUser";
		
		AccountDTO accountDTO = AccountDTO.builder()
			    .username(wrongUsername)
			    .password("1111")
			    .name("홍길동")
			    .email("test@naver.com")
			    .build();
		
		
		MockMultipartFile emptyFile = new MockMultipartFile("file", "", "application/octet-stream", new byte[0]);
		
		//when
		ApplicationException ex = assertThrows(ApplicationException.class, () -> accountService.modify(accountDTO, emptyFile));
		assertEquals(404, ex.getCode());
		assertEquals("ACCOUNT_NOT_FOUND", ex.getMessage());
		
	}
	
	
	@Test
	void 존재하지_않는_계정_deleteProfileImg_404예외() {
		
		//given
		
		//존재하지않는 username
		String wrongUsername = "wrongUser";
		
		AccountDTO accountDTO = AccountDTO.builder()
			    .username(wrongUsername)
			    .password("1111")
			    .name("홍길동")
			    .email("test@naver.com")
			    .build();
		
		//when & then
		ApplicationException ex = assertThrows(ApplicationException.class, () -> accountService.deleteProfileImg(accountDTO));
		assertEquals(404, ex.getCode());
		assertEquals("ACCOUNT_NOT_FOUND", ex.getMessage());
		
	}
	
	
	@Test
	void 삭제된_계정_read_404예외() {
		
		//given
		AccountDTO accountDTO = AccountDTO.builder().username("testUser").password("1111").name("홍길동").email("test@naver.com").build();
		accountService.register(accountDTO);
		accountService.remove(accountDTO.getUsername());
		
		
		//when & then
		ApplicationException ex = assertThrows(ApplicationException.class, () -> accountService.read(accountDTO.getUsername()));
		assertEquals(404, ex.getCode());
		assertEquals("ACCOUNT_NOT_FOUND", ex.getMessage());
		
	}
	
	
	@Test
	void 삭제된_계정_remove_404예외() {
		
		//given
		AccountDTO accountDTO = AccountDTO.builder().username("testUser").password("1111").name("홍길동").email("test@naver.com").build();
		accountService.register(accountDTO);
		accountService.remove(accountDTO.getUsername());
		
		//when & then
		ApplicationException ex = assertThrows(ApplicationException.class, () -> accountService.remove(accountDTO.getUsername()));
		assertEquals(404, ex.getCode());
		assertEquals("ACCOUNT_NOT_FOUND", ex.getMessage());
		
	}
	
	@Test
	void 삭제된_계정_modify_404예외() {
		
		//given
		AccountDTO accountDTO = AccountDTO.builder().username("testUser").password("1111").name("홍길동").email("test@naver.com").build();
		accountService.register(accountDTO);
		accountService.remove(accountDTO.getUsername());
		
		//when & then
		accountDTO.setPassword("1234");
		accountDTO.setEmail("fail@naver.com");
		MockMultipartFile emptyFile = new MockMultipartFile("file", "", "application/octet-stream", new byte[0]);
		
		
		ApplicationException ex = assertThrows(ApplicationException.class, () -> accountService.modify(accountDTO, emptyFile));
		assertEquals(404, ex.getCode());
		assertEquals("ACCOUNT_NOT_FOUND", ex.getMessage());
		
	}
	
	@Test
	void 삭제된_계정_deleteProfileImg_404예외() {
		
		//given
		AccountDTO accountDTO = AccountDTO.builder().username("testUser").password("1111").name("홍길동").email("test@naver.com").build();
		accountService.register(accountDTO);
		accountService.remove(accountDTO.getUsername());
		
		//when&then
		ApplicationException ex = assertThrows(ApplicationException.class, () -> accountService.remove(accountDTO.getUsername()));
		assertEquals(404, ex.getCode());
		assertEquals("ACCOUNT_NOT_FOUND", ex.getMessage());
		
		
	}
	
	
	
	
	
}
