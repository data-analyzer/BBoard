package com.se.board.domain.es;

import java.util.Map;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.get.GetResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.se.board.domain.book.SlackLogBot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EsApiController {
	private final EsService esService;
/////////////// 검색 ////////////////
	// 추가 테스트
	@GetMapping(value = "/api/esboard/usearch")
	public ResponseEntity<EsBoardSearchResponse> unitedSearch(EsBoardSearchRequest params) {
		log.info("/api/esboard/usearch. unitedSearch=" + params.toString());
		return ResponseEntity.ok(esService.searchTitleContentWriter(params)); // searchTitleContentWriter
	}


	// 기존
	@GetMapping(value = "/api/esboard/{id}")
	public ResponseEntity<EsBoard> getBook(@PathVariable("id") String id) {
		return ResponseEntity.ok(esService.getById(id));
	}

	@GetMapping(value = "/api/esboard/search")
	public ResponseEntity<EsBoardSearchResponse> search(EsBoardSearchRequest params) {
		log.info("/api/esboard/search. search=" + params.toString());
		return ResponseEntity.ok(esService.search(params));
	}

	@ExceptionHandler(ElasticsearchException.class)
	public ResponseEntity<String> handleSearchException(ElasticsearchException e) {
		log.error(e.getMessage(), e);
		SlackLogBot.sendError(e);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("FAIL");
	}

// 화면 부분








	// 아래는 기본

	@PostMapping("/{index}/_doc/{id}")
	public void createDoc(@PathVariable("index") final String index, @PathVariable("id") final String id, @RequestBody final Map<String, Object> params) {
		esService.createDocument(index, id, params, null);
	}

	@GetMapping("/{index}/_doc/{id}")
	public GetResponse getDocument(@PathVariable("index") final String index, @PathVariable("id") final String id) {
		return esService.getDocument(index, id);
	}

	@PostMapping("/{index}/_doc/{id}/_update")
	public void updateDocument(@PathVariable("index") final String index, @PathVariable("id") final String id, @RequestBody final Map<String, Object> params) {
		esService.updateDocument(index, id, params);
	}

	@DeleteMapping("/{index}/_doc/{id}")
	public void deleteDocument(@PathVariable("index") final String index, @PathVariable("id") final String id) {
		esService.deleteDocument(index, id);
	}

}
