package com.se.board.domain.es;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EsBoardSearchRequest {

	private String query;
	private int page;
}
