package com.se.board.domain.file;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.se.board.common.file.FileUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FileApiController {

	private final FileService fileService;
	private final FileUtils fileUtils;

	// 파일 리스트 조회
	@GetMapping("/posts/{postId}/files")
	public List<FileResponse> findAllFileByPostId (@PathVariable("postId") final Long postId) {
		return fileService.findAllFileByPostId(postId);
	}

	// 첨부파일 다운로드
	@GetMapping("/posts/{postId}/files/{fileId}/download")
	public ResponseEntity<Resource> downloadFile(@PathVariable("postId") final Long postId, @PathVariable("fileId") final Long fileId) {
		FileResponse file = fileService.findFileById(fileId);
		Resource resource = fileUtils.readFileAsResource(file);

		try {
			String filename = URLEncoder.encode(file.getOriginalName(), "UTF-8");
			// space -> + 바뀌는 문제 (원래 + 가 있다면 %2B 로 변환되기때문에 괜찮음)
			if( filename != null) filename = filename.replaceAll("\\+", "%20");
			return ResponseEntity.ok()
					.contentType(MediaType.APPLICATION_OCTET_STREAM)
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\";")
					.header(HttpHeaders.CONTENT_LENGTH, file.getSize() + "")
					.body(resource);

		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("filename encoding failed : " + file.getOriginalName());
		}
	}


}
