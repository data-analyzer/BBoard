package com.se.board.domain.espost;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.se.board.common.dto.SearchDto;
import com.se.board.common.paging.Pagination;
import com.se.board.common.paging.PagingResponse;
import com.se.board.domain.post.PostResponse;
import com.se.board.domain.post.PostService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class EsPostController {
	EsPostService esPostService;
	private final PostService postService;

	// 게시글 리스트 페이지
//	@GetMapping("/espost/eslist.do")
//	public String openPostList(@ModelAttribute("params") final SearchDto params, Model model) {
//		PagingResponse<PostResponse> response = postService.findAllPost(params);
//		model.addAttribute("response", response);
//		return "espost/eslist";
//	}

	@GetMapping("/espost/eslist.do")
	public String openSearchList(@ModelAttribute("params") final SearchDto params, Model model) {
		//PagingResponse<PostResponse> response = postService.findAllPost(params);
		Pagination pagination = new Pagination(0, params);
		// params.setPagination(pagination);

		// 계산된 페이지 정보의 일부(limitStart, recordSize)를 기준으로 리스트 조회 후 응답 데이터 반환
		List<PostResponse> list = Collections.emptyList();
		PagingResponse<PostResponse> response = new PagingResponse<>(list, pagination);
		model.addAttribute("response", response);
		return "espost/eslist";
	}
}
