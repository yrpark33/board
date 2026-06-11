package org.oolong.service;

import java.io.IOException;
import java.util.List;

import org.oolong.common.util.FileUploader;
import org.oolong.dto.AccountDTO;
import org.oolong.dto.AccountRole;
import org.oolong.dto.FileDTO;
import org.oolong.mapper.AccountMapper;
import org.oolong.service.exception.ApplicationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class AccountService {
	
	private final AccountMapper accountMapper;
	
	private final PasswordEncoder encoder;
	
	private final FileUploader fileUploader;
	
	private String subDir = "profile";
	
	public void register(AccountDTO accountDTO) {
		
		accountDTO.setPassword(encoder.encode(accountDTO.getPassword()));
		
		int count = accountMapper.insert(accountDTO);
		
		if(count == 0) {
			throw new ApplicationException(500, "ACCOUNT_INSERT_ERROR");
		}
		
		accountDTO.addRole(AccountRole.USER);
		accountMapper.insertRoles(accountDTO);
		
	}
	
	public AccountDTO read(String username) {
		
		AccountDTO accountDTO = accountMapper.select(username);
		
		if(accountDTO == null || !accountDTO.isEnabled()) {
			throw new ApplicationException(404, "ACCOUNT_NOT_FOUND");
		}
		
		
		return accountDTO;
		
	}
	
	public void modify(AccountDTO accountDTO, MultipartFile file) throws IOException {
		
		
		AccountDTO dbDTO = accountMapper.select(accountDTO.getUsername());
		
		if(dbDTO == null || !dbDTO.isEnabled()) {
			
			throw new ApplicationException(404, "ACCOUNT_NOT_FOUND");
			
		}
		
		String oldProfileImg = dbDTO.getProfileImg();
		
	
		
		if(file != null && !file.isEmpty()) {
			
			FileDTO newProfileDTO = null;
			
			
			try {
				//새 프로필 사진 물리저장소에 업로드
				newProfileDTO = fileUploader.uploadFile(file, subDir);
				
				String newProfileImg = newProfileDTO.getUuid() + "_" + newProfileDTO.getFileName();
				accountDTO.setProfileImg(newProfileImg);
				
				
				
				accountMapper.update(accountDTO);
				
				
				if(oldProfileImg != null) {
					String[] parts = oldProfileImg.split("_", 2);
					String uuid = parts[0];
					String fileName = parts[1];
					
					FileDTO oldProfileDTO = FileDTO.builder().uuid(uuid).fileName(fileName).image(true).build();
					
					try {
						fileUploader.deleteFiles(List.of(oldProfileDTO), subDir);
					} catch (Exception e) {
						log.error("기존 프로필 이미지 물리저장소 삭제 실패: " + e.getMessage());
					}
				}	
				
			} catch(Exception e1) {
				
				try {
					if(newProfileDTO != null) {
						fileUploader.deleteFiles(List.of(newProfileDTO), subDir);
					}
				} catch(Exception e2) {
					log.error("새로 등록된 이미지 수동 롤백 실패: ", e2.getMessage());
				}
				
				throw e1;
				
			}
			
			
			
		} else {
			
			
			if(oldProfileImg != null) {
				accountDTO.setProfileImg(oldProfileImg);
			}
			
			
			accountMapper.update(accountDTO);
			
			
		}
		
		
	}
	
	public void remove(String username) {
		
		
		AccountDTO dto = accountMapper.select(username);
		
		if(dto == null || !dto.isEnabled()) {
			throw new ApplicationException(404, "ACCOUNT_NOT_FOUND");
		}
		
		String profileImg = dto.getProfileImg();		
		accountMapper.delete(username);
		accountMapper.deleteRememberMeToken(username);
		
		if(profileImg != null) {
			
			String[] split = profileImg.split("_", 2);
			
			FileDTO profileDTO = FileDTO.builder().uuid(split[0]).fileName(split[1]).image(true).build();
			
			
			try {
				
				fileUploader.deleteFiles(List.of(profileDTO), subDir);
				
			} catch(Exception e) {
				
				log.error("회원 탈퇴 중 프로필 이미지 물리저장소 삭제 실패: ", e.getMessage());
				
			}
		}
		
		
	}
	
	public boolean isUsernameDuplicate(String username) {
		
		return accountMapper.countByUsername(username) > 0;
		
	}
	
	public boolean isEmailDuplicate(String email) {
		
		return accountMapper.countByEmail(email) > 0;
		
	}
	
	public void changePassword(String username, String password) {
		
		accountMapper.updatePassword(username, encoder.encode(password));
		accountMapper.deleteRememberMeToken(username);
		
	}
	
	
	public void deleteProfileImg(AccountDTO accountDTO) throws IOException {
	    AccountDTO dbDTO = accountMapper.select(accountDTO.getUsername());
	    
	    if (dbDTO == null || !dbDTO.isEnabled()) {
	        throw new ApplicationException(404, "ACCOUNT_NOT_FOUND");
	    }
	    
	    String oldProfileImg = dbDTO.getProfileImg();
	    
	    accountMapper.updateProfileImgNull(accountDTO);
	    
	    
	    
	    if (oldProfileImg != null) {
	        // 물리 파일 삭제
	    	
	    	String[] split = oldProfileImg.split("_", 2);
	    	
	    	
	    	FileDTO oldFileDTO = FileDTO.builder().uuid(split[0]).fileName(split[1]).image(true).build();
	    	
	    	try {
	    	
	    		fileUploader.deleteFiles(List.of(oldFileDTO), subDir);
	    		
	    	} catch (Exception e) {
	    		
	    		log.error("프로필 이미지 삭제 중 물리저장소 삭제 실패: ", e.getMessage());
	    	
	    	}
	     
	    }
	}
	
	
	
	
}
