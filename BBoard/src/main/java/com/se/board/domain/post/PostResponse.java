package com.se.board.domain.post;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostResponse {
	private Long id;						// PK
	private String title;					// 제목
	private String content;					// 내용
	private String writer;					// 작성자
	private int viewCnt;					// 조회수
	private Boolean noticeYn;				// 공지글 여부
	private Boolean deleteYn;				// 삭제 여부
	private LocalDateTime createdDate;		// 생성일시
	private LocalDateTime modifiedDate;		// 최종 수정일시


	private Long postId;					// es file_id
	private Long fileId;					// es file_id
	private String savedFilename;			// es file_id

	private float score;


	// highlight 추가

	private Map<String, List<String>> highlightsMap = new HashMap<>();

}
