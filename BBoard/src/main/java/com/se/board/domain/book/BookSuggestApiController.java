package com.se.board.domain.book;

import org.elasticsearch.ElasticsearchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
@Slf4j
public class BookSuggestApiController {

	private final BookSuggestService bookSuggestService;

	@GetMapping(value = "/suggest")
	public ResponseEntity<SuggestResponseDto> suggest(String query) {
		log.info("/api/book/suggest. query=" + query);
		return ResponseEntity.ok(bookSuggestService.suggest(query));
	}

	@ExceptionHandler(ElasticsearchException.class)
	public ResponseEntity<String> handleSearchException(ElasticsearchException e) {
		log.error(e.getMessage(), e);
		SlackLogBot.sendError(e);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("FAIL");
	}
}
