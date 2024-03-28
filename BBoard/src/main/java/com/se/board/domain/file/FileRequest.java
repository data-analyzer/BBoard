package com.se.board.domain.file;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileRequest {

	private Long id;				// 파일 번호 (PK)
	private Long postId;			// 게시글 번호 (FK)
	private String originalName;	// 원본 파일명
	private String saveName;		// 저장 파일명
	private long size;				// 파일 크기

	private String addPath;		    // 읽어들인 파일 -> addPath (yyMMdd)
	private String b64Str;

	private String writer;		    // 게시글 작성자

	// add
	@Builder
	public FileRequest (String originalName, String saveName, long size, String addPath) {
		this.originalName = originalName;
		this.saveName = saveName;
		this.size = size;
		this.addPath = addPath;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}

	public void setB64Str (String b64Str) {
		this.b64Str = b64Str;
	}

	public void setWriter (String writer) {
		this.writer = writer;
	}

}
