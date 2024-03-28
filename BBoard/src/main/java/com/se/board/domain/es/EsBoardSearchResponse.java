package com.se.board.domain.es;

import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EsBoardSearchResponse {

	private String result;
	private long totalHits;
	private List<EsBoard> boards;
	private String searchHitStage;

	public static EsBoardSearchResponse emptyResponse() {
		EsBoardSearchResponse responseDto = new EsBoardSearchResponse();
		responseDto.setResult("OK");
		responseDto.setTotalHits(0);
		responseDto.setBoards(Collections.emptyList());
		responseDto.setSearchHitStage(EsSearchHitStage.NO_RESULT.toString());

		return responseDto;
	}
}
