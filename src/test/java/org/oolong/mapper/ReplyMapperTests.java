package org.oolong.mapper;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.oolong.dto.ReplyDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.extern.log4j.Log4j2;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/root-context.xml")
@Log4j2
public class ReplyMapperTests {
	
	@Autowired ReplyMapper replyMapper;
	
	
//	@Test
	public void testInsert() {
		Long bno = 6130L;
		
		ReplyDTO replyDTO = ReplyDTO.builder().bno(bno).replyText("Reply.....").replier("user1").build();
		replyMapper.insert(replyDTO);
	}
	
//	@Test
	public void testRead() {
		
		Long rno = 1L;
		log.info("-------------------------------");
		log.info(replyMapper.read(rno));
	}
	
//	@Test
	public void testDelete() {
		
		Long rno = 1L;
		log.info("-------------------------------");
		log.info(replyMapper.delete(rno));
			
		
	}
	
	
//	@Test
	public void testUpdate() {
		
		ReplyDTO replyDTO = ReplyDTO.builder().rno(1L).replyText("Update ReplyText").build();
		replyMapper.update(replyDTO);
		
	}
	
	
//	@Test
	public void testInserts() {
		
		Long[] bnos = {6130L, 6128L, 6125L};
		
		for(Long bno : bnos) {
			for(int i=0; i < 10; i++) {
				ReplyDTO replyDTO = ReplyDTO.builder().replyText("Sample Reply").bno(bno).replier("replier1").build();	
				replyMapper.insert(replyDTO);
			}
		}
		
	}
	
	@Test
	public void testListOfBoard() {
		
		Long bno = 6130L;
		
		List<ReplyDTO> replyList = replyMapper.listOfBoard(bno, 0, 10);
		
		replyList.stream().forEach(log::info);
	}
	
	
	
}
