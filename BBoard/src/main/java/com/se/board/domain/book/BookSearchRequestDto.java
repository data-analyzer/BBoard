package com.se.board.domain.book;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BookSearchRequestDto {

	private String query;
	private int page;
}
