package org.oolong.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.extension.ExtendWith;
import org.oolong.dto.AccountDTO;
import org.oolong.dto.AccountRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.log4j.Log4j2;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/root-context.xml")
@Log4j2
public class AccountMapperTests {
	
	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	AccountMapper accountMapper;
	
//	@Test
	void testEncoding() {
		
		String pw = "1111";
		
		String enPw = encoder.encode(pw);
		
		log.info(enPw);
		
		log.info("------");
		
		assertTrue(encoder.matches(pw, enPw));
		
	}
	
	
//	@Test
	@Transactional
	@Commit
	void testInsert() {
		
		for(int i = 1; i <= 100; i++) {
			
			AccountDTO accountDTO = new AccountDTO();
			
			accountDTO.setUsername("user" + i);
			accountDTO.setPassword(encoder.encode("1111"));
			accountDTO.setName("User" + i);
			accountDTO.setEmail("user" + i + "@aaa.com");
			accountDTO.addRole(AccountRole.USER);
			
			
			if(i >= 80) {
				
				accountDTO.addRole(AccountRole.MANAGER);
				
			}
			
			if (i >= 90) {
				
				accountDTO.addRole(AccountRole.ADMIN);
				
			}
			
			
			accountMapper.insert(accountDTO);
			
			accountMapper.insertRoles(accountDTO);
			
			
		}
		
	} 
	
//	@Test
	void testSelectOne() {
		
		
		String username = "user100";
		
		AccountDTO account = accountMapper.selectOne(username);
		
		assertNotNull(account);
		assertEquals("User100", account.getName());
		assertEquals(3, account.getRoleNames().size());
		
		
	}
	
	
	
	
	
	
	
	
	
}
