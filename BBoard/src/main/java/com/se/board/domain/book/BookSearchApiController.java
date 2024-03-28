package com.se.board.domain.book;

import org.elasticsearch.ElasticsearchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
@Slf4j
public class BookSearchApiController {
	private final BookSearchService bookSearchService;

	@GetMapping(value = "/{id}")
	public ResponseEntity<Book> getBook(@PathVariable("id") String id) {
		log.info("/api/book" + id);
		return ResponseEntity.ok(bookSearchService.getById(id));
	}

	@GetMapping(value = "/search")
	public ResponseEntity<BookSearchResponseDto> search(BookSearchRequestDto bookSearchRequestDto) {
		log.info("/api/book/search. bookSearchRequestDto=" + bookSearchRequestDto.toString());
		return ResponseEntity.ok(bookSearchService.search(bookSearchRequestDto));
	}

	@ExceptionHandler(ElasticsearchException.class)
	public ResponseEntity<String> handleSearchException(ElasticsearchException e) {
		log.error(e.getMessage(), e);
		SlackLogBot.sendError(e);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("FAIL");
	}


}
