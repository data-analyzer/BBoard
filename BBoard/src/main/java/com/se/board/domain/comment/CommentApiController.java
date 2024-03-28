package com.se.board.domain.comment;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.se.board.common.paging.PagingResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CommentApiController {

	private final CommentService commentService;

	// 신규 댓글 생성  postId가 @PathVariable 과 RequestBody에도 있어서 오류 발생 -> 명시해준다 @PathVariable("postId")
	@PostMapping("/posts/{postId}/comments")
	public CommentResponse saveComment(@PathVariable("postId") final Long postId, @RequestBody final CommentRequest params) {
		Long id = commentService.saveComment(params);
		return commentService.findCommentById(id);
	}

	// 댓글 리스트 조회
	@GetMapping("/posts/{postId}/comments")
//	public List<CommentResponse> findAllComment(@PathVariable("postId") final Long postId) {
//		return commentService.findAllComment(postId);
//	}
	public PagingResponse<CommentResponse> findAllComment(@PathVariable("postId") final Long postId, final CommentSearchDto params) {
		return commentService.findAllComment(params);
	}

	// 댓글 상세정보 조회
	@GetMapping("/posts/{postId}/comments/{id}")
	public CommentResponse findCommentById(@PathVariable("postId") final Long postId, @PathVariable("id") final Long id) {
		return commentService.findCommentById(id);
	}

	// 기존 댓글 수정
	@PatchMapping("/posts/{postId}/comments/{id}")
	public CommentResponse updateComment(@PathVariable("postId") final Long postId, @PathVariable("id") final Long id, @RequestBody final CommentRequest params) {
		commentService.updateComment(params);
		return commentService.findCommentById(id);
	}

	// 댓글 삭제
	@DeleteMapping("/posts/{postId}/comments/{id}")
	public Long deleteComment(@PathVariable("postId") final Long postId, @PathVariable("id") final Long id) {
		return commentService.deleteComment(id);
	}
}
