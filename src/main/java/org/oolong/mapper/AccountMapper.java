package org.oolong.mapper;

import org.oolong.dto.AccountDTO;

public interface AccountMapper {
	
	int insert(AccountDTO accountDTO);
	
	int insertRoles(AccountDTO accountDTO);
	
	AccountDTO select(String username);
	
	int update(AccountDTO accountDTO);
	
	int delete(String username);
	
	int countByUsername(String username);
	
	int countByEmail(String email);
	
	int updatePassword(String username, String password);
	
	int deleteRememberMeToken(String username);
	
	int updateProfileImgNull(AccountDTO accountDTO);
}
