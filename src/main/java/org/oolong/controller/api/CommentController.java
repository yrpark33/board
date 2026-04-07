 package org.oolong.controller.api;

import java.util.Map;

import org.oolong.dto.CommentDTO;
import org.oolong.dto.CommentPageResponseDTO;
import org.oolong.dto.PageRequestDTO;
import org.oolong.service.CommentService;
import org.springframework.http.ResponseEntity;
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
	
	
	@PostMapping("")
	public ResponseEntity<Map<String, Long>> write(CommentDTO commentDTO) {
		
		log.info("write commentDTO: {}", commentDTO);
		
		commentService.writeComment(commentDTO);
		
		return ResponseEntity.ok(Map.of("result", commentDTO.getCommentId()));
	}
	
	@GetMapping("{boardId}/list")
	public ResponseEntity<CommentPageResponseDTO> list(@PathVariable("boardId") Long boardId, PageRequestDTO pageRequestDTO) {
		
		log.info("list boardId, pageRequestDTO: {}, {}", boardId, pageRequestDTO);
		
		return ResponseEntity.ok(commentService.getCommentList(boardId, pageRequestDTO));
		
	}
	
	@GetMapping("{commentId}")
	public ResponseEntity<CommentDTO> read(@PathVariable("commentId") Long commentId) {
		 
		log.info("read commentId: {}", commentId);	
		return ResponseEntity.ok(commentService.getComment(commentId));
	
	}
	
	@DeleteMapping("{commentId}")
	public ResponseEntity<Map<String, String>> remove(@PathVariable("commentId") Long commentId) {
		
		log.info("removed commentId: {}", commentId);
		commentService.removeComment(commentId);
		return ResponseEntity.ok(Map.of("result", "removed"));
	}
	
	@PutMapping("{commentId}")
	public ResponseEntity<Map<String, String>> modify(@PathVariable("commentId") Long commentId, CommentDTO commentDTO) {
		
		log.info("modfiy commentId, commentDTO: ", commentId, commentDTO);
		commentDTO.setCommentId(commentId);
		commentService.modifyComment(commentDTO);
		
		return ResponseEntity.ok(Map.of("result", "modified"));
		
	}
}
