package org.oolong.controller.view;

import java.io.IOException;

import org.oolong.dto.AccountDTO;
import org.oolong.dto.AccountRole;
import org.oolong.dto.BoardDTO;
import org.oolong.dto.BoardPageRequestDTO;
import org.oolong.dto.BoardPageResponseDTO;
import org.oolong.service.BoardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/board")
@Log4j2
@RequiredArgsConstructor
public class BoardController {

	private final BoardService boardService;
	

	@GetMapping("/list")
	public String list(BoardPageRequestDTO dto, Model model) {

		log.info("list boardPageRequestDTO: {}", dto);

		BoardPageResponseDTO responseDTO = boardService.getBoardList(dto);
		log.info("BoardController: " + responseDTO);
		model.addAttribute("dto", responseDTO);

		return "/board/list";
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/write")
	public String writePage() {
		log.info("writePage()");
		return "/board/write";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/write")
	public String write(@AuthenticationPrincipal AccountDTO accountDTO, BoardDTO boardDTO, @RequestParam("attachedFiles") MultipartFile[] files, RedirectAttributes rttr) throws IOException {
		log.info("write boardDTO: {}", boardDTO);

		
		 if(!StringUtils.hasText(boardDTO.getTitle()) || !StringUtils.hasText(boardDTO.getContent())) { 
			 
			 rttr.addFlashAttribute("board", boardDTO);
			 rttr.addFlashAttribute("errorMsg", "모든 항목을 입력해주세요");
			 return"redirect:/board/write"; 
		}
		
		boardDTO.setWriter(accountDTO.getUsername());
		 
		Long boardId = boardService.writeBoard(boardDTO, files);
		
		return "redirect:/board/" + boardId;
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{boardId}")
	public String detail(@PathVariable("boardId") Long boardId, BoardPageRequestDTO dto, Model model) {
		log.info("detail boardId, boardPageRequestDTO : {}, {}", boardId, dto);
		
		model.addAttribute("board", boardService.getBoard(boardId));
		model.addAttribute("dto", dto);
		return "/board/detail";
	}

	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/modify/{boardId}")
	public String modify(@AuthenticationPrincipal AccountDTO accountDTO, @PathVariable("boardId") Long boardId, BoardPageRequestDTO dto, Model model) {
		log.info("modify boardId: {}, {}", boardId, dto);

		
		boolean admin = accountDTO.getRoleNames().contains(AccountRole.ADMIN);
		
		BoardDTO board = boardService.getBoardForModify(accountDTO.getUsername(), boardId, admin);
		
		if (model.containsAttribute("board")) {
			
			BoardDTO flashDTO = (BoardDTO) model.getAttribute("board");
			flashDTO.setCreatedAt(board.getCreatedAt());
			flashDTO.setWriter(board.getWriter());
		
		} else {
			
			model.addAttribute("board", board);
			model.addAttribute("dto", dto);
		}

		return "/board/modify";
	}


	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify")
	public String modifyPost(@AuthenticationPrincipal AccountDTO accountDTO, BoardDTO boardDTO, @RequestParam(name = "oldFileInfosJson", required = false) String oldFileInfosJson, @RequestParam("addedFiles") MultipartFile[] addedFiles, @RequestParam(name = "deletedFileInfosJson", required = false) String deletedFileInfosJson, RedirectAttributes rttr) throws IOException {
		log.info("modifyPost boardDTO: {}", boardDTO);

		if (boardDTO.getTitle().isBlank() || boardDTO.getContent().isBlank()) {
			rttr.addFlashAttribute("board", boardDTO);
			rttr.addFlashAttribute("errorMsg", "모든 항목을 입력해주세요");
			return "redirect:/board/modify/" + boardDTO.getBoardId();
		}
		
		
		boolean admin = accountDTO.getRoleNames().contains(AccountRole.ADMIN);
		
		if(!admin) {
			boardDTO.setWriter(accountDTO.getUsername());
		}
		
		boardService.modifyBoard(boardDTO, oldFileInfosJson, deletedFileInfosJson, addedFiles, admin);
		return "redirect:/board/" + boardDTO.getBoardId();
	}
	
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/remove")
	public String remove(@AuthenticationPrincipal AccountDTO accountDTO, @RequestParam("boardId") Long boardId, RedirectAttributes rttr) {
		log.info("remove boardId: {}, {}", accountDTO, boardId);
		
		boolean admin = accountDTO.getRoleNames().contains(AccountRole.ADMIN);
		
		boardService.removeBoard(accountDTO.getUsername(), boardId, admin);
		rttr.addFlashAttribute("removed", boardId);

		return "redirect:/board/list";

	}

}
