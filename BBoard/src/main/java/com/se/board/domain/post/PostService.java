package com.se.board.domain.post;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.se.board.common.dto.SearchDto;
import com.se.board.common.paging.Pagination;
import com.se.board.common.paging.PagingResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
	private final PostMapper postMapper;

	private final PostEsService postEsService; // 추가

	/**
	 * 게시글 저장
	 * @param params - 게시글 정보
	 * @return Generated PK
	 */
	@Transactional
	public Long savePost(final PostRequest params) {
		postMapper.save(params);
		return params.getId();
	}

	/**
	 * 게시글 상세정보 조회
	 * @param id - PK
	 * @return 게시글 정보 PostResponse
	 */
	public PostResponse findPostById (final Long id) {
		return postMapper.findById(id);
	}

	/**
	 * 게시글 수정
	 * @param params - 게시글 정보
	 * @return PK
	 */
	@Transactional
	public Long updatPost(final PostRequest params) {
		postMapper.update(params);
		return params.getId();
	}

	/**
	 * 게시글 삭제
	 * @param id - PK
	 * @return PK
	 */
	@Transactional
	public Long deletePost(final Long id) {
		postMapper.deleteById(id);
		return id;
	}

	/**
	 * 게시글 리스트 조회
	 * @param params - search conditions
	 * @return 게시글 리스트
	 */
//	public List<PostResponse> findAllPost(final SearchDto params) {
//		return postMapper.findAll(params);
//	}
	// PagingResponse로 변경
//	public PagingResponse<PostResponse> findAllPost(final SearchDto params) {
//		// 조건에 해당하는 데이터가 없는 경우, 응답 데이터에 비어있는 리스트와 null을 담아 반환한다.
//		int count = postMapper.count(params);
//		if(count < 1) {
//			return new PagingResponse<PostResponse>(Collections.emptyList(), null);
//		}
//
//		// Pagination 객체를 생성해서 페이지 정보 계산 후 SearchDto 타입의 객체인 params에 계산된 페이지 정보 저장
//		Pagination pagination = new Pagination(count, params);
//		params.setPagination(pagination);
//
//		// 계산된 페이지 정보의 일부(limitStart, recordSize)를 기준으로 리스트 조회 후 응답 데이터 반환
//		List<PostResponse> list = postMapper.findAll(params);
//
//		return new PagingResponse<>(list, pagination);
//
//	}

	public PagingResponse<PostResponse> findAllPost(final SearchDto params) {
		String keyword = params.getKeyword();
		String searchType = params.getSearchType();

		int count = 0;

		if(keyword != null && !"".equals(keyword)) {
			PagingResponse<PostResponse> response = postEsService.search(params);
			count = Long.valueOf(response.getTotalHits()).intValue(); // (SearchResponse 의 )response.getHits().getTotalHits().value 에 함께 있다. => 추가한 PagingResponse totalHits에 헬퍼에서 담아주고 있음

			Pagination pagination = new Pagination(count, params);
			if(response != null) {
				List<PostResponse> rList = response.getList();
				for(PostResponse post : rList) {
					post.setId(post.getPostId());
					log.debug("list a post id:" + post.getId() + ", postId:" + post.getPostId() + ", fileId:" + post.getFileId() + ", savedFilename:" + post.getSavedFilename() + ", createdDate:" + post.getCreatedDate());
				}
			}
			params.setPagination(pagination);
			response.setPagination(pagination);
			return response;
		} else {
			// 조건에 해당하는 데이터가 없는 경우, 응답 데이터에 비어있는 리스트와 null을 담아 반환한다.
			count = postMapper.count(params);
			if(count < 1) {
				return new PagingResponse<PostResponse>(Collections.emptyList(), null);
			}

			// Pagination 객체를 생성해서 페이지 정보 계산 후 SearchDto 타입의 객체인 params에 계산된 페이지 정보 저장
			Pagination pagination = new Pagination(count, params);
			params.setPagination(pagination);

			// 계산된 페이지 정보의 일부(limitStart, recordSize)를 기준으로 리스트 조회 후 응답 데이터 반환
			List<PostResponse> list = postMapper.findAll(params);

			return new PagingResponse<>(list, pagination);
		}


		// 수정할 것
		// return new PagingResponse<PostResponse>(Collections.emptyList(), null);

	}

	public PagingResponse<PostResponse> findAllPostDB(final SearchDto params) {
		// 조건에 해당하는 데이터가 없는 경우, 응답 데이터에 비어있는 리스트와 null을 담아 반환한다.
		int count = postMapper.count(params);
		if(count < 1) {
			return new PagingResponse<PostResponse>(Collections.emptyList(), null);
		}

		// Pagination 객체를 생성해서 페이지 정보 계산 후 SearchDto 타입의 객체인 params에 계산된 페이지 정보 저장
		Pagination pagination = new Pagination(count, params);
		params.setPagination(pagination);

		// 계산된 페이지 정보의 일부(limitStart, recordSize)를 기준으로 리스트 조회 후 응답 데이터 반환
		List<PostResponse> list = postMapper.findAll(params);

		return new PagingResponse<>(list, pagination);

	}

}
