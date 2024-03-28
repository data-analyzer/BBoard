package com.se.board.domain.book;

import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SuggestResponseDto {

	private String result;
	private List<String> titles;

	public static SuggestResponseDto emptyResponse() {
		SuggestResponseDto responseDto = new SuggestResponseDto();
		responseDto.setResult("OK");
		responseDto.setTitles(Collections.emptyList());

		return responseDto;
	}
}
