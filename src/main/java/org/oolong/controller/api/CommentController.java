 package org.oolong.controller.api;

import java.util.Map;

import org.oolong.dto.AccountDTO;
import org.oolong.dto.AccountRole;
import org.oolong.dto.CommentDTO;
import org.oolong.dto.CommentPageResponseDTO;
import org.oolong.dto.PageRequestDTO;
import org.oolong.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
@Log4j2
public class CommentController {
	
	private final CommentService commentService;
	
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("")
	public ResponseEntity<Map<String, Long>> write(@AuthenticationPrincipal AccountDTO accountDTO, CommentDTO commentDTO) {
		
		log.info("write commentDTO: {}", commentDTO);
		
		commentDTO.setWriter(accountDTO.getUsername());
		
		commentService.writeComment(commentDTO);
		
		return ResponseEntity.ok(Map.of("result", commentDTO.getCommentId()));
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("{boardId}/list")
	public ResponseEntity<CommentPageResponseDTO> list(@AuthenticationPrincipal AccountDTO accountDTO, @PathVariable("boardId") Long boardId, PageRequestDTO pageRequestDTO) {
		
		log.info("list boardId, pageRequestDTO: {}, {}", boardId, pageRequestDTO);
		
		String username = accountDTO.getUsername();
		boolean admin = accountDTO.getRoleNames().contains(AccountRole.ADMIN);		
		
		return ResponseEntity.ok(commentService.getCommentList(boardId, pageRequestDTO, username, admin));
		
	}
	
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("{commentId}")
	public ResponseEntity<CommentDTO> getCommentForModify(@AuthenticationPrincipal AccountDTO accountDTO,  @PathVariable("commentId") Long commentId) {
		 
		String username = accountDTO.getUsername();
		
		boolean admin = accountDTO.getRoleNames().contains(AccountRole.ADMIN);
		
		
		log.info("read commentId: {}", commentId);
		return ResponseEntity.ok(commentService.getCommentForModify(commentId,username, admin));
	
	}
	
	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("{commentId}")
	public ResponseEntity<Map<String, String>> remove(@AuthenticationPrincipal AccountDTO accountDTO, @PathVariable("commentId") Long commentId) {
		
		String username = accountDTO.getUsername();
		boolean admin = accountDTO.getRoleNames().contains(AccountRole.ADMIN);
		
		log.info("removed commentId: {}", commentId);
		commentService.removeComment(commentId, username, admin);
		return ResponseEntity.ok(Map.of("result", "removed"));
	}
	
	@PreAuthorize("isAuthenticated()")
	@PutMapping("{commentId}")
	public ResponseEntity<Map<String, String>> modify(@AuthenticationPrincipal AccountDTO accountDTO, @PathVariable("commentId") Long commentId, CommentDTO commentDTO) {
		
		log.info("modfiy commentId, commentDTO: ", commentId, commentDTO);
		
		String username = accountDTO.getUsername();
		boolean admin = accountDTO.getRoleNames().contains(AccountRole.ADMIN);
		
		commentDTO.setCommentId(commentId);
		
		if(!admin) {
		
			commentDTO.setWriter(username);
		
		}
		
		commentService.modifyComment(commentDTO, admin);
		
		
		
		return ResponseEntity.ok(Map.of("result", "modified"));
		
	}
}
