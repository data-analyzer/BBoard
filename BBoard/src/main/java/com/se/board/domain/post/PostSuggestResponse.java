package com.se.board.domain.post;

import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PostSuggestResponse {
	private String result;
	private List<String> texts;

	public static PostSuggestResponse emptyResponse () {
		PostSuggestResponse response = new PostSuggestResponse();
		response.setResult("OK");
		response.setTexts(Collections.emptyList() );

		return response;
	}

}
