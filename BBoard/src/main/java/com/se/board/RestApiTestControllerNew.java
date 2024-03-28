package com.se.board;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.se.board.common.dto.SearchDto;
import com.se.board.common.paging.PagingResponse;
import com.se.board.domain.post.PostResponse;
import com.se.board.domain.post.PostService;

import lombok.RequiredArgsConstructor;

@RestController // 스프링 4 버전부터 제공, @RestController가 붙은 컨트롤러의 모든 메서드에는 자동으로 @ResponseBody가 적용됨
@RequiredArgsConstructor
public class RestApiTestControllerNew {
	private final PostService postService;

	@GetMapping("/posts")
	public PagingResponse<PostResponse> findAllPost() {
		return postService.findAllPost(new SearchDto());

	}

}
