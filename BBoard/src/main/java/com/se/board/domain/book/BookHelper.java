package com.se.board.domain.book;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
final class BookHelper {

	private static final String BOOK_INDEX = "book";
	private static final int COUNT_PER_PAGE = 10;
	private static final int AUTO_COMPLETE_LIMIT = 10;

	private final ObjectMapper objectMapper;

	public GetRequest createGetByIdRequest(String id) {
		return new GetRequest(BOOK_INDEX, id);
	}

	SearchRequest createTitleSearchRequest(String query, int page) {

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.should().add(QueryBuilders.termQuery("title", query).boost(100.0f));
		boolQueryBuilder.should()
				.add(QueryBuilders.matchQuery("title_text", query).operator(Operator.AND));

		SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
		searchSourceBuilder.query(boolQueryBuilder);
		searchSourceBuilder.from(COUNT_PER_PAGE * (page - 1));
		searchSourceBuilder.size(COUNT_PER_PAGE);
		String[] includes =
				{"isbn13", "title", "author", "publisher", "pubDate", "imageUrl", "description"};
		searchSourceBuilder.fetchSource(includes, null);

		SearchRequest searchRequest = new SearchRequest(BOOK_INDEX);
		searchRequest.source(searchSourceBuilder);

		return searchRequest;
	}

	SearchRequest createTitleAuthorSearchRequest(String query, int page) {

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.should().add(QueryBuilders.matchQuery("title_text", query));
		boolQueryBuilder.should().add(QueryBuilders.matchQuery("author_text", query).boost(10.0f));

		SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
		searchSourceBuilder.query(boolQueryBuilder);
		searchSourceBuilder.from(COUNT_PER_PAGE * (page - 1));
		searchSourceBuilder.size(COUNT_PER_PAGE);
		String[] includes =
				{"isbn13", "title", "author", "publisher", "pubDate", "imageUrl", "description"};
		searchSourceBuilder.fetchSource(includes, null);

		SearchRequest searchRequest = new SearchRequest(BOOK_INDEX);
		searchRequest.source(searchSourceBuilder);

		return searchRequest;
	}

	SearchRequest createAutoCompleteSearchRequest(String query) {

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.should()
				.add(QueryBuilders.matchQuery("title_ac", query.replaceAll("\\s+", "")).boost(10.0f));
		boolQueryBuilder.should().add(QueryBuilders.matchQuery("title_ac", query));
		SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
		searchSourceBuilder.query(boolQueryBuilder);
		searchSourceBuilder.from(0);
		searchSourceBuilder.size(AUTO_COMPLETE_LIMIT);
		String[] includes = {"title"};
		searchSourceBuilder.fetchSource(includes, null);

		SearchRequest searchRequest = new SearchRequest(BOOK_INDEX);
		searchRequest.source(searchSourceBuilder);

		return searchRequest;
	}

	SearchRequest createChosungSearchRequest(String query, int page, String[] includes) {
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.should()
				.add(QueryBuilders.matchQuery("title_chosung", query.replaceAll("\\s+", "")).boost(10.0f));
		boolQueryBuilder.should().add(QueryBuilders.matchQuery("title_chosung", query));
		SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
		searchSourceBuilder.query(boolQueryBuilder);
		searchSourceBuilder.from(COUNT_PER_PAGE * (page - 1));
		searchSourceBuilder.size(AUTO_COMPLETE_LIMIT);
		searchSourceBuilder.fetchSource(includes, null);

		SearchRequest searchRequest = new SearchRequest(BOOK_INDEX);
		searchRequest.source(searchSourceBuilder);

		return searchRequest;
	}

	SearchRequest createHanToEngSearchRequest(String query, int page, String[] includes) {

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.should()
				.add(QueryBuilders.matchQuery("title_hantoeng", query.replaceAll("\\s+", "")).boost(10.0f));
		boolQueryBuilder.should().add(QueryBuilders.matchQuery("title_hantoeng", query));
		SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
		searchSourceBuilder.query(boolQueryBuilder);
		searchSourceBuilder.from(COUNT_PER_PAGE * (page - 1));
		searchSourceBuilder.size(AUTO_COMPLETE_LIMIT);
		searchSourceBuilder.fetchSource(includes, null);

		SearchRequest searchRequest = new SearchRequest(BOOK_INDEX);
		searchRequest.source(searchSourceBuilder);

		return searchRequest;
	}

	SearchRequest createEngToHanSearchRequest(String query, int page, String[] includes) {
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.should()
				.add(QueryBuilders.matchQuery("title_engtohan", query.replaceAll("\\s+", "")).boost(10.0f));
		boolQueryBuilder.should().add(QueryBuilders.matchQuery("title_engtohan", query));
		SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
		searchSourceBuilder.query(boolQueryBuilder);
		searchSourceBuilder.from(COUNT_PER_PAGE * (page - 1));
		searchSourceBuilder.size(AUTO_COMPLETE_LIMIT);
		searchSourceBuilder.fetchSource(includes, null);

		SearchRequest searchRequest = new SearchRequest(BOOK_INDEX);
		searchRequest.source(searchSourceBuilder);

		return searchRequest;
	}

	BookSearchResponseDto createBookSearchResponseDto(SearchResponse response, String stage) {
		BookSearchResponseDto responseDto = new BookSearchResponseDto();
		responseDto.setResult("OK");
		responseDto.setSearchHitStage(stage);
		responseDto.setTotalHits(response.getHits().getTotalHits().value);

		SearchHit[] hits = response.getHits().getHits();
		List<Book> searchedBooks = new ArrayList<>();
		for (SearchHit hit : hits) {
			try {
				Book book = objectMapper.readValue(hit.getSourceAsString(), Book.class);
				searchedBooks.add(book);
			} catch (JsonProcessingException e) {
				log.error("Json Parse Exception in parsing searched book. book=" + hit.getSourceAsString(),
						e);
			}
		}

		responseDto.setBooks(searchedBooks);

		return responseDto;
	}

	SuggestResponseDto createSuggestResponseDto(SearchResponse response) {

		SuggestResponseDto responseDto = new SuggestResponseDto();
		responseDto.setResult("OK");
		responseDto.setTitles(Arrays.stream(response.getHits().getHits())
				.map(hit -> hit.getSourceAsMap().get((String) "title").toString())
				.collect(Collectors.toList()));

		return responseDto;
	}
}
