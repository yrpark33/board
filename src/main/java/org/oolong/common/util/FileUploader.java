package org.oolong.common.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.oolong.dto.FileDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnails;

@Component
@Log4j2
public class FileUploader {

	@Value("${org.oolong.upload.path}")
	private String uploadPath;
	
	private static final String ORIGIN_DIR = "original";
	private static final String THUMB_DIR = "thumbnail";
	
	public List<FileDTO> uploadFiles(MultipartFile[] files, String subDir) throws IOException {
		
		List<FileDTO> fileDTOList = new ArrayList<>();
		
		if(files == null || files.length == 0 || (files.length == 1 && files[0].isEmpty())) {
			return fileDTOList;
		}
		
		
		File originDir = new File(new File(uploadPath, subDir), ORIGIN_DIR);
        File thumbDir = new File(new File(uploadPath, subDir), THUMB_DIR);
        	
        
        if (!originDir.exists()) originDir.mkdirs();
        if (!thumbDir.exists()) thumbDir.mkdirs();
        
        
		for(MultipartFile file : files) {
			
			if(file.isEmpty()) continue;
			
			String fileName = file.getOriginalFilename();
			String uuid = UUID.randomUUID().toString();
			
			
			String uploadName = uuid + "_" + fileName;
			
			File targetFile = new File(originDir, uploadName);
			
			boolean isImage = file.getContentType().startsWith("image");
			
			try (
					InputStream fin = file.getInputStream();
					OutputStream fos = new FileOutputStream(targetFile);
			) {
				
				log.info(targetFile.getAbsolutePath());
				FileCopyUtils.copy(fin, fos);
				fileDTOList.add(FileDTO.builder().uuid(uuid).fileName(fileName).image(isImage).build());
				
			} catch(Exception e) {
				log.error(e.getMessage());
				throw new RuntimeException(e.getMessage());
			}
			
			
			if(isImage) {
				
				try {
					Thumbnails.of(targetFile).size(200, 200).toFile(new File(thumbDir, "s_" + uploadName));
				} catch (IOException e) {
					log.error("썸네일 생성 중 에러 발생: " + e.getMessage());
					e.printStackTrace();
				}
				
			}
			
			
		}
		
		return fileDTOList;
		
	}
	
	public void deleteFiles(List<FileDTO> uploadedFiles, String subDir) {
		try {
				for(FileDTO file : uploadedFiles) {
	
					String fullFileName = file.getUuid() + "_" + file.getFileName();
					
					File subDirFile = new File(uploadPath, subDir);
					
					File targetFile = new File(new File(subDirFile, ORIGIN_DIR), fullFileName);
					
					
					
					if(targetFile.exists()) {
						boolean deleted = targetFile.delete();
						log.info(fullFileName + " 원본 삭제 결과: " + deleted);
					}
					
					
					if(file.isImage()) {
						
						File targetThumb = new File(new File(subDirFile, THUMB_DIR), "s_" + fullFileName);
						
						if(targetThumb.exists()) {
							boolean deleted = targetThumb.delete();
							log.info("s_" + fullFileName + " 썸네일 삭제 결과: " + deleted);
						}
					}
				}
				
				
		} catch(Exception e) {
			log.error("파일 삭제 작업 중 오류 발생: " + e.getMessage());
			throw e;
		}
	}
	
	
	public FileDTO uploadFile(MultipartFile file, String subDir) throws IOException {
	    List<FileDTO> result = uploadFiles(new MultipartFile[]{file}, subDir);
	    return result.get(0);
	}
	
	
	
}
