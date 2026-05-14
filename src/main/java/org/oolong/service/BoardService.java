package org.oolong.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.oolong.common.util.FileUploader;
import org.oolong.dto.BoardDTO;
import org.oolong.dto.BoardFileDTO;
import org.oolong.dto.BoardListDTO;
import org.oolong.dto.BoardPageRequestDTO;
import org.oolong.dto.BoardPageResponseDTO;
import org.oolong.mapper.BoardMapper;
import org.oolong.service.exception.ApplicationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class BoardService {
	
	private final BoardMapper boardMapper;
	
	private final FileUploader fileUploader;
	
	private final ObjectMapper objectMapper;
	
	
	@Transactional(readOnly = true)
	public BoardPageResponseDTO getBoardList(BoardPageRequestDTO dto) {
			
			int page = dto.getPage();
			int size = dto.getSize();
			String typeStr = dto.getTypes();
			String keyword = dto.getKeyword();
			
			page = page <= 0 ? 1 : page;
			size = size < 10 || size >= 100 ? 10 : size;
			 
			log.debug("getBoardList boardPageRequestDTO: {}", dto);
			
			int offset = (page - 1) * size;
			
			log.debug("BoardService offset: {}", offset);
			log.debug("BoardService limit: {}", size);
			
			String[] types = typeStr != null ? typeStr.split("") : null;
			
			List<BoardListDTO> dtoList = boardMapper.selectListSearch(offset, size, types, keyword);
			int totalCount = boardMapper.selectTotalCountSearch(types, keyword);
			
			
			
			return new BoardPageResponseDTO(dtoList, page, size, totalCount, typeStr, keyword);	
	}
	
	
	public Long writeBoard(BoardDTO boardDTO, MultipartFile[] files) throws IOException {
		log.info("writeBoard boardDTO: {}, {}", boardDTO, files);
		
		
		List<BoardFileDTO> uploadedFiles = null;
		
		try {
			uploadedFiles = fileUploader.uploadFiles(files);
		
			uploadedFiles.forEach(file -> {
		
				boardDTO.addFile(file);
		
			});
		
		
			int count = boardMapper.insert(boardDTO);
			
			if(count == 0) {
				throw new ApplicationException(500, "BOARD_INSERT_ERROR");
			}
			
			
			if(boardDTO.getFiles().isEmpty()) {
				return boardDTO.getBoardId();
			}
			
			boardMapper.insertFiles(boardDTO);
		
			return boardDTO.getBoardId();
		
		} catch (Exception e) {
			
			if(uploadedFiles != null && !uploadedFiles.isEmpty()) {
				fileUploader.deleteFiles(uploadedFiles);
			}
			
			throw e;
		}
	
	}
	
	
	@Transactional(readOnly = true)
	public BoardDTO getBoard(Long boardId) {
		log.debug("getBoard boardId: {}", boardId);
		BoardDTO dto = boardMapper.selectById(boardId);
		if(dto == null) {
			throw new ApplicationException(404, "BOARD_NOT_FOUND");
		}
		return dto;
	}
	
	
	public void removeBoard(Long boardId) {
		log.info("removeBoard boardId: {}", boardId);
		
		int count = boardMapper.delete(boardId);
		
		if(count == 0) {
			throw new ApplicationException(404, "BOARD_NOT_FOUND");
		}
		
		boardMapper.updateFilesAsDeleted(boardId);
		
	}
	
	
	
	private List<BoardFileDTO> parseJsonToList(String json) {
	    if (!StringUtils.hasText(json)) {
	        return Collections.emptyList(); // null 대신 빈 리스트 반환 (NPE 방지)
	    }
	    try {
	        return objectMapper.readValue(json, new TypeReference<List<BoardFileDTO>>() {});
	    } catch (JsonProcessingException e) {
	        log.error("JSON 파싱 실패: {}", json);
	        throw new RuntimeException("파일 정보 형식이 올바르지 않습니다.");
	    }
	}
	
	public void modifyBoard(BoardDTO boardDTO, String oldFileInfosJson, String deletedFileInfosJson, MultipartFile[] addedFiles) throws IOException {
		
		if(boardMapper.selectById(boardDTO.getBoardId()) == null) {
			throw new ApplicationException(404, "BOARD_NOT_FOUND");
		}
		
		
		log.info("modifyBoard boardDTO: {}", boardDTO);
		
		List<BoardFileDTO> addedFileList = new ArrayList<>();
		List<BoardFileDTO> oldFiles = parseJsonToList(oldFileInfosJson);
		List<BoardFileDTO> deletedFiles = parseJsonToList(deletedFileInfosJson);
		
		try {	
			//해당 게시물의 모든 파일 정보 DB에서 삭제
			boardMapper.deleteFiles(boardDTO.getBoardId());
			
			//삭제되지않고 남은 파일 정보 boardDTO에 담기
			oldFiles.forEach(boardDTO::addFile);
				
			
			//새로 추가된 파일 물리저장소에 저장
			addedFileList = fileUploader.uploadFiles(addedFiles);
			
				
			//새로 추가된 파일 정보 boardDTO에 담기
			addedFileList.forEach(boardDTO::addFile);
				
			
			//boardDTO를 통해 기존 파일 + 새로 추가된 파일 정보 DB에 등록
			if(boardDTO.getFiles() != null && boardDTO.getFiles().size() > 0) {
	        
				boardMapper.insertFiles(boardDTO);
			
			}
		
			//게시물 수정 DB에 반영
			boardMapper.update(boardDTO);
		
			
			
			try {
			
				fileUploader.deleteFiles(deletedFiles);
				
			} catch(Exception e) {
				
				log.error("물리 파일 삭제 중 에러 발생: " + e.getMessage());
			
			}
						
		
		} catch (Exception e1) {
			
			try {
				
				if(!addedFileList.isEmpty()) {
					fileUploader.deleteFiles(addedFileList);
					log.warn("DB 트랜잭션 실패로 인한 업로드 파일 수동 롤백 완료");
				}
				
			} catch(Exception e2) {
				
				log.error("add된 유령파일 삭제 실패 확인 필요: " + e2.getMessage());
				
			}
			
			throw e1; //DB롤백을 위해 다시 던짐
			
		}
	}
	
	
	
	
	
}
