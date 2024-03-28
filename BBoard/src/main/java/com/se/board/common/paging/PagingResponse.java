package com.se.board.common.paging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.se.board.domain.es.EsSearchHitStage;
import com.se.board.domain.post.PostResponse;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter		// 추가
@ToString	// 추가
public class PagingResponse<T> {

	private List<T> list = new ArrayList<>();
	private Pagination pagination;

	public PagingResponse(List<T> list, Pagination pagination) {
		this.list.addAll(list);
		this.pagination = pagination;

	}

	// 추가
	private String result;
	private long totalHits;
	private String searchHitStage;
	// private List<EsBoard> boards;  // 삭제

//	public static PagingResponse<PostResponse> emptyResponse() {
//		PagingResponse<PostResponse> response = new PagingResponse<PostResponse>(Collections.emptyList(), null);
//
//		response.setResult("OK");
//		response.setTotalHits(0);
//		response.setSearchHitStage(EsSearchHitStage.NO_RESULT.toString());
//
//		return response;
//	}

//	public void setEmptyValues() {
//		this.result = "OK";
//		this.totalHits = 0;
//		this.searchHitStage = EsSearchHitStage.NO_RESULT.toString();
//	}

	public PagingResponse(List<T> list, Pagination pagination, Boolean initializeOtherValue) {
		this.list.addAll(list);
		this.pagination = pagination;

		if(initializeOtherValue) {
			this.result = "OK";
			this.totalHits = 0;
			this.searchHitStage = EsSearchHitStage.NO_RESULT.toString();
		}
	}

}
