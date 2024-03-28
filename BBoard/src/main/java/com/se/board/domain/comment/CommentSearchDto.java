package com.se.board.domain.comment;

import com.se.board.common.dto.SearchDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentSearchDto extends SearchDto {

	private Long postId;		// 게시글 번호 (FK)

}
