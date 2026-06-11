package org.oolong.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.oolong.dto.AccountDTO;
import org.oolong.dto.AccountRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.log4j.Log4j2;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/root-context.xml")
@Log4j2
@Transactional
public class AccountMapperTests {
	
	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	AccountMapper accountMapper;
	
	@Test
	void testEncoding() {
		
		String pw = "1111";
		
		String enPw = encoder.encode(pw);
		
		log.info(enPw);
		
		log.info("------");
		
		assertTrue(encoder.matches(pw, enPw));
		
	}
	
	@Test
	void testInsert() {
		
	 	
		//given
		AccountDTO accountDTO = AccountDTO.builder().username("testUser").password(encoder.encode("1111")).name("홍길동").email("test@naver.com").build();
		accountDTO.addRole(AccountRole.USER);
		
		
		//when
		accountMapper.insert(accountDTO);
		
		
		//then
		AccountDTO foundAccount = accountMapper.select(accountDTO.getUsername());
		
		assertNotNull(foundAccount);
		assertEquals(foundAccount.getEmail(), accountDTO.getEmail());
		assertEquals(foundAccount.getPassword(), accountDTO.getPassword());
		assertEquals(foundAccount.getName(), accountDTO.getName());
		
		
	}
	
	
	@Test
	void testInsertRoles() {
		
		//given
		AccountDTO accountDTO = AccountDTO.builder().username("testUser").password(encoder.encode("1111")).name("홍길동").email("test@naver.com").build();
		accountDTO.addRole(AccountRole.USER);
		accountMapper.insert(accountDTO);
		
		
		//when
		accountMapper.insertRoles(accountDTO);
		
		//then
		AccountDTO result = accountMapper.select(accountDTO.getUsername());
		assertEquals(1, result.getRoleNames().size());
		assertEquals(AccountRole.USER, result.getRoleNames().get(0));
		
	}
	
	
	@Test
	void testUpdate() {
		
		//given
		AccountDTO accountDTO = AccountDTO.builder().username("testUser").password(encoder.encode("1111")).name("홍길동").email("test@naver.com").build();
		accountMapper.insert(accountDTO);
		
		//when
		accountDTO.setEmail("update@naver.com");
		accountDTO.setProfileImg("test/profile.jpg");
		accountMapper.update(accountDTO);
		
		
		//then
		AccountDTO result = accountMapper.select(accountDTO.getUsername());
		assertEquals("update@naver.com", result.getEmail());
		assertEquals("test/profile.jpg", result.getProfileImg());
		
	}
	
	
	@Test
	void testDelete() {
		
		//given
		AccountDTO accountDTO = AccountDTO.builder().username("testUser").password(encoder.encode("1111")).name("홍길동").email("test@naver.com").build();
		accountMapper.insert(accountDTO);
				
		//when
		accountMapper.delete(accountDTO.getUsername());
		
		//then
		AccountDTO result = accountMapper.select(accountDTO.getUsername());
		assertFalse(result.isEnabled());
	}
	
	@Test
	void testUpdateProfileImgNull() {
		
		//given
		AccountDTO accountDTO = AccountDTO.builder().username("testUser").password(encoder.encode("1111")).name("홍길동").email("test@naver.com").build();
		accountMapper.insert(accountDTO);
		accountDTO.setProfileImg("test/profile.jpg");
		accountMapper.update(accountDTO);
		
		AccountDTO before = accountMapper.select(accountDTO.getUsername());
		assertNotNull(before.getProfileImg());
		
		
		//when
		accountDTO.setEmail("null@naver.com");
		accountMapper.updateProfileImgNull(accountDTO);
		
		//then
		AccountDTO result = accountMapper.select(accountDTO.getUsername());
		assertEquals("null@naver.com", result.getEmail());
		assertNull(result.getProfileImg());
		
	}
	
	@Test
	void testUpdatePassword() {
		
		//given
		AccountDTO accountDTO = AccountDTO.builder().username("testUser").password(encoder.encode("1111")).name("홍길동").email("test@naver.com").build();
		accountMapper.insert(accountDTO);
		
		//when
		accountMapper.updatePassword(accountDTO.getUsername(), encoder.encode("2222"));
		
		//then
		AccountDTO result = accountMapper.select(accountDTO.getUsername());
		assertTrue(encoder.matches("2222", result.getPassword()));
		
	}
	
	
	
}
