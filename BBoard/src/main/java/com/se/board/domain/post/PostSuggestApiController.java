package com.se.board.domain.post;

import org.elasticsearch.ElasticsearchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.se.board.common.dto.SearchDto;
import com.se.board.domain.book.SlackLogBot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PostSuggestApiController {

	private final PostSuggestEsService postSuggestEsService;

	@GetMapping("/suggest2")
	public PostSuggestResponse suggest (final SearchDto params) {
		log.debug(" params.getKeyword : " + params.getKeyword());
		return postSuggestEsService.suggest(params);
	}

	@ExceptionHandler(ElasticsearchException.class)
	public ResponseEntity<String> handleSearchException(ElasticsearchException e) {
		log.error(e.getMessage(), e);
		SlackLogBot.sendError(e);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("FAIL");
	}
}
