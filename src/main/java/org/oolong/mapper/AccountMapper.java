package org.oolong.mapper;

import org.oolong.dto.AccountDTO;

public interface AccountMapper {
	
	int insert(AccountDTO accountDTO);
	
	int insertRoles(AccountDTO accountDTO);
	
	AccountDTO selectOne(String username);
	
}
