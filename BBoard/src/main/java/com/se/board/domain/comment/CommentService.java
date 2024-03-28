package com.se.board.domain.comment;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.se.board.common.paging.Pagination;
import com.se.board.common.paging.PagingResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentMapper commentMapper;

	/**
	 * 댓글 저장
	 * @param params - 댓글 정보
	 * @return Generated Key (PK)
	 */
	@Transactional
	public Long saveComment(final CommentRequest params) {
		commentMapper.save(params);
		return params.getId();
	}

	/**
	 * 댓글 상세정보 조회
	 * @param id - PK
	 * @return 댓글 상세정보
	 */

	public CommentResponse findCommentById(final Long id) {
		return commentMapper.findById(id);
	}

	/**
	 * 댓글 수정
	 * @param params - 댓글 정보
	 * @return PK
	 */
	@Transactional
	public Long updateComment(final CommentRequest params) {
		commentMapper.update(params);
		return params.getId();
	}

	/**
	 * 댓글 삭제
	 * @param id - PK
	 * @return PK
	 */
	@Transactional
	public Long deleteComment(final Long id) {
		commentMapper.deleteById(id);
		return id;
	}

	/**
	 * 댓글 리스트 조회
	 * @param postId - 게시글 번호 (FK)
	 * @return 댓글 리스트
	 */
//	List<CommentResponse> findAllComment(final Long postId) {
//		return commentMapper.findAll(postId);
//	}
	PagingResponse<CommentResponse> findAllComment(final CommentSearchDto params) {

		int count = commentMapper.count(params);
		if(count < 1) {
			return new PagingResponse<>(Collections.emptyList(), null);
		}

		Pagination pagination = new Pagination(count, params);
		List<CommentResponse> list = commentMapper.findAll(params);

		return new PagingResponse<>(list, pagination);
	}

}
