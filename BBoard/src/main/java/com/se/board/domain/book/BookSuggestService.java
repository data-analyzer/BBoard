package com.se.board.domain.book;

import java.io.IOException;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Service;

import com.se.board.common.book.HangulUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookSuggestService {
	private final RestHighLevelClient esClient;
	private final BookHelper bookHelper;

	public SuggestResponseDto suggest(String query) throws ElasticsearchException {

		if (HangulUtil.isChosungQuery(query)) {
			SuggestResponseDto responseDto = chosungSuggest(query);
			if (hasSuggests(responseDto)) {
				return responseDto;
			}
		} else {
			SuggestResponseDto responseDto = autoComplete(query);
			if (hasSuggests(responseDto)) {
				return responseDto;
			}
		}

		SuggestResponseDto responseDto = hanToEngSuggest(query);
		if (hasSuggests(responseDto)) {
			return responseDto;
		}

		responseDto = engToHanSuggest(query);
		if (hasSuggests(responseDto)) {
			return responseDto;
		}

		return SuggestResponseDto.emptyResponse();
	}

	private SuggestResponseDto chosungSuggest(String query) throws ElasticsearchException {
		query = HangulUtil.decomposeLayeredJaum(query);
		String[] includes = {"title"};
		SearchRequest searchRequest = bookHelper.createChosungSearchRequest(query, 1, includes);
		try {
			SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
			return bookHelper.createSuggestResponseDto(response);
		} catch (IOException e) {
			log.error("query=" + query, e);
			SlackLogBot.sendError(e);
			throw new ElasticsearchException("엘라스틱서치 Search API 호출 에러");
		}
	}

	private SuggestResponseDto autoComplete(String query) throws ElasticsearchException {
		SearchRequest searchRequest = bookHelper.createAutoCompleteSearchRequest(query);
		try {
			SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
			return bookHelper.createSuggestResponseDto(response);
		} catch (IOException e) {
			log.error("query=" + query, e);
			SlackLogBot.sendError(e);
			throw new ElasticsearchException("엘라스틱서치 Search API 호출 에러");
		}
	}

	private SuggestResponseDto hanToEngSuggest(String query) throws ElasticsearchException {
		String[] includes = {"title"};
		SearchRequest searchRequest = bookHelper.createHanToEngSearchRequest(query, 1, includes);
		try {
			SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
			return bookHelper.createSuggestResponseDto(response);
		} catch (IOException e) {
			log.error("query=" + query, e);
			SlackLogBot.sendError(e);
			throw new ElasticsearchException("엘라스틱서치 Search API 호출 에러");
		}
	}

	private SuggestResponseDto engToHanSuggest(String query) throws ElasticsearchException {
		String[] includes = {"title"};
		SearchRequest searchRequest = bookHelper.createEngToHanSearchRequest(query, 1, includes);
		try {
			SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
			return bookHelper.createSuggestResponseDto(response);
		} catch (IOException e) {
			log.error("query=" + query, e);
			SlackLogBot.sendError(e);
			throw new ElasticsearchException("엘라스틱서치 Search API 호출 에러");
		}
	}



	private boolean hasSuggests(SuggestResponseDto suggestResponseDto) {
		return suggestResponseDto.getTitles().size() > 0;
	}
}
