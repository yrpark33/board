package org.oolong.controller;

import org.oolong.dto.BoardDTO;
import org.oolong.service.BoardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	public String list(Model model) {
		log.info("list");
		model.addAttribute("list", boardService.getBoardList());
		
		return "/board/list";
	}
	
	
	@GetMapping("/write")
	public String writePage() {
		log.info("writePage()");
		return "/board/write";
	}
	
	@PostMapping("/write")
	public String write(BoardDTO boardDTO) {
		log.info("write boardDTO: {}", boardDTO);
		Long boardId = boardService.writeBoard(boardDTO);
		return "redirect:/board/" + boardId;
	}
	
	@GetMapping("/{boardId}")
	public String detail(@PathVariable("boardId") Long boardId, Model model) {
		log.info("detail boardId : {}", boardId);
		model.addAttribute("board", boardService.getBoard(boardId));
		return "/board/detail";
	}
	
	@GetMapping("/modify/{boardId}")
	public String modify(@PathVariable("boardId") Long boardId, Model model) {
		log.info("modify boardId: {}", boardId);
		BoardDTO boardDTO = boardService.getBoard(boardId);
		model.addAttribute("board", boardDTO);
		return "/board/modify";
	}
	
	@PostMapping("/modify")
	public String modifyPost(BoardDTO boardDTO) {
		log.info("modifyPost boardDTO: {}", boardDTO);
		boardService.modifyBoard(boardDTO);
		return "redirect:/board/" + boardDTO.getBoardId();
	}
	
	@PostMapping("/remove")
	public String remove(@RequestParam("boardId") Long boardId, RedirectAttributes rttr) {
		log.info("remove boardId: {}", boardId);
		boardService.removeBoard(boardId);
		rttr.addFlashAttribute("removed", boardId);
		return "redirect:/board/list";
		
	}
	
}
