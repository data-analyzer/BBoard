package com.se.board.domain.post;

import java.io.IOException;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Service;

import com.se.board.common.book.EsKoUtil;
//import com.se.board.common.book.HangulUtil;
import com.se.board.common.dto.SearchDto;
import com.se.board.domain.book.SlackLogBot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostSuggestEsService {

	private final RestHighLevelClient esClient;
	private final PostEsHelper postEsHelper;

	public PostSuggestResponse suggest(SearchDto params) {
		String query = params.getKeyword();
		log.debug(" query : " + query);

		//if(HangulUtil.isChosungQuery(query)) {
		if(EsKoUtil.isFcQuery(query)) {
			PostSuggestResponse suggestResponse = fcSuggest(query);
			if(hasSuggests(suggestResponse)) {
				log.debug("fcSuggest : "  + suggestResponse.getTexts());
				return suggestResponse;
			}
		} else {
			PostSuggestResponse suggestResponse = autoComplete(query);
			if(hasSuggests(suggestResponse)) {
				log.debug("autoComplete : "  + suggestResponse.getTexts());
				return suggestResponse;
			}
		}

		PostSuggestResponse suggestResponse = koToEnSuggest(query);
		if(hasSuggests(suggestResponse)) {
			log.debug("koToEnSuggest : "  + suggestResponse.getTexts());
			return suggestResponse;
		}

		suggestResponse = enToKoSuggest(query);
		if(hasSuggests(suggestResponse) ) {
			log.debug("enToKoSuggest : "  + suggestResponse.getTexts());
			return suggestResponse;
		}

		return PostSuggestResponse.emptyResponse();
	}

	private PostSuggestResponse fcSuggest(String query) throws ElasticsearchException {
		//query = HangulUtil.decomposeLayeredJaum(query);
		query = EsKoUtil.decomposeDualConsonant(query);
		String[] includes = {"title"};
		SearchRequest searchRequest = postEsHelper.createFcSearchRequest(query, 1, includes);
		try {
			SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
			return postEsHelper.createTitleSuggestResponse(response);
		} catch (IOException e) {
			log.error("query=" + query, e);
			SlackLogBot.sendError(e);
			throw new ElasticsearchException("엘라스틱서치 Search API 호출 에러");
		}
	}

	private PostSuggestResponse autoComplete(String query) throws ElasticsearchException {
		SearchRequest searchRequest = postEsHelper.createAutoCompleteSearchRequest(query);
		try {
			SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
			return postEsHelper.createTitleSuggestResponse(response);
		} catch (IOException e) {
			log.error("query=" + query, e);
			SlackLogBot.sendError(e);
			throw new ElasticsearchException("엘라스틱서치 Search API 호출 에러");
		}
	}

	private PostSuggestResponse koToEnSuggest(String query) throws ElasticsearchException {
		String[] includes = {"title"};
		SearchRequest searchRequest = postEsHelper.createKoToEnSearchRequest(query, 1, includes);
		try {
			SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
			return postEsHelper.createTitleSuggestResponse(response);
		} catch (IOException e) {
			log.error("query=" + query, e);
			SlackLogBot.sendError(e);
			throw new ElasticsearchException("엘라스틱서치 Search API 호출 에러");
		}
	}

	private PostSuggestResponse enToKoSuggest(String query) throws ElasticsearchException {
		String[] includes = {"title"};
		SearchRequest searchRequest = postEsHelper.createEnToKoSearchRequest(query, 1, includes);
		try {
			SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
			return postEsHelper.createTitleSuggestResponse(response);
		} catch (IOException e) {
			log.error("query=" + query, e);
			SlackLogBot.sendError(e);
			throw new ElasticsearchException("엘라스틱서치 Search API 호출 에러");
		}
	}



	private boolean hasSuggests(PostSuggestResponse suggestResponse) {
		return suggestResponse.getTexts().size() > 0;
	}
}
