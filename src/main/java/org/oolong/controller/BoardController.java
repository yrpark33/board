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
@RequestMapping("board")
@Log4j2
@RequiredArgsConstructor
public class BoardController {
	
	
	private final BoardService boardService;
	
	@GetMapping("list")
	public void list(Model model) {
		log.info("--------------------------------------");
		log.info("board list");
		model.addAttribute("list", boardService.getList());
	}
	
	@GetMapping("register")
	public void register() {
		log.info("--------------------------------------");
		log.info("board register get");
	}
	
	@PostMapping("register")
	public String registerPost(BoardDTO dto, RedirectAttributes rttr) {
		log.info("--------------------------------------");
		log.info("board register post");
		
		Long bno = boardService.register(dto);
		rttr.addFlashAttribute("register", bno);
		
		return "redirect:/board/list";
	}
	
	@GetMapping("read/{bno}")
	public String read(@PathVariable("bno") Long bno, Model model) {
		
		log.info("--------------------------------------");
		log.info("board read");
		
		BoardDTO dto = boardService.read(bno);
		model.addAttribute("board", dto);
		
		
		return "board/read";	
	}
	
	@GetMapping("modify/{bno}")
	public String modify(@PathVariable("bno") Long bno, Model model) {
		
		log.info("--------------------------------------");
		log.info("board modify get");
		
		BoardDTO dto = boardService.read(bno);
		model.addAttribute("board", dto);
		
		return "board/modify";
	}
	
	@PostMapping("modify")
	public String modifyPost(BoardDTO boardDTO) {
		
		log.info("--------------------------------------");
		log.info("board modify post");
		
		boardService.modify(boardDTO);
		
		return "redirect:/board/read/" + boardDTO.getBno();
	}
	
	@PostMapping("remove")
	public String remove(@RequestParam("bno") Long bno, RedirectAttributes rttr) {
		
		log.info("--------------------------------------");
		log.info("board remove post");
		
		boardService.remove(bno);
		rttr.addFlashAttribute("remove", bno);
		
		return "redirect:/board/list";
	}
	
	
}
