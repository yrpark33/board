package org.oolong.controller.view;

import java.io.IOException;
import java.util.List;

import org.oolong.dto.BoardDTO;
import org.oolong.dto.BoardPageRequestDTO;
import org.oolong.dto.BoardPageResponseDTO;
import org.oolong.service.BoardService;
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

	@GetMapping("/write")
	public String writePage() {
		log.info("writePage()");
		return "/board/write";
	}

	@PostMapping("/write")
	public String write(BoardDTO boardDTO, @RequestParam("attachedFiles") MultipartFile[] files, RedirectAttributes rttr) throws IOException {
		log.info("write boardDTO: {}", boardDTO);

		
		 if(!StringUtils.hasText(boardDTO.getTitle()) || !StringUtils.hasText(boardDTO.getContent()) || !StringUtils.hasText(boardDTO.getWriter())) { 
			 
			 rttr.addFlashAttribute("board", boardDTO);
			 rttr.addFlashAttribute("errorMsg", "모든 항목을 입력해주세요");	 
			 return"redirect:/board/write"; 
		}
		
		Long boardId = boardService.writeBoard(boardDTO, files);
		
		return "redirect:/board/" + boardId;
	}

	@GetMapping("/{boardId}")
	public String detail(@PathVariable("boardId") Long boardId, BoardPageRequestDTO dto, Model model) {
		log.info("detail boardId, boardPageRequestDTO : {}, {}", boardId, dto);
		model.addAttribute("board", boardService.getBoard(boardId));
		model.addAttribute("dto", dto);
		return "/board/detail";
	}

	@GetMapping("/modify/{boardId}")
	public String modify(@PathVariable("boardId") Long boardId, BoardPageRequestDTO dto, Model model) {
		log.info("modify boardId: {}, {}", boardId, dto);

		if (model.containsAttribute("board")) {
			BoardDTO dbDTO = boardService.getBoard(boardId);
			BoardDTO flashDTO = (BoardDTO) model.getAttribute("board");
			flashDTO.setCreatedAt(dbDTO.getCreatedAt());
			flashDTO.setWriter(dbDTO.getWriter());
		} else {
			BoardDTO boardDTO = boardService.getBoard(boardId);
			model.addAttribute("board", boardDTO);
			model.addAttribute("dto", dto);
		}

		return "/board/modify";
	}

	@PostMapping("/modify")
	public String modifyPost(BoardDTO boardDTO, @RequestParam(name = "oldFileInfosJson", required = false) String oldFileInfosJson, @RequestParam("addedFiles") MultipartFile[] addedFiles, @RequestParam(name = "deletedFileInfosJson", required = false) String deletedFileInfosJson, RedirectAttributes rttr) throws IOException {
		log.info("modifyPost boardDTO: {}", boardDTO);

		if (boardDTO.getTitle().isBlank() || boardDTO.getContent().isBlank()) {
			rttr.addFlashAttribute("board", boardDTO);
			rttr.addFlashAttribute("errorMsg", "모든 항목을 입력해주세요");
			return "redirect:/board/modify/" + boardDTO.getBoardId();
		}

		boardService.modifyBoard(boardDTO, oldFileInfosJson, deletedFileInfosJson, addedFiles);
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
