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
public class PostSuggest3EsService {

	private final RestHighLevelClient esClient;
	private final PostEsHelper3 postEsHelper3;

	public PostSuggestResponse suggest3(SearchDto params) {
		String query = params.getKeyword();
		log.debug(" suggest3 query : " + query);

		//if(HangulUtil.isChosungQuery(query)) {
		if(EsKoUtil.isFcQuery(query)) {
			PostSuggestResponse suggestResponse = fcSuggest3(query);
			if(hasSuggests(suggestResponse)) {
				log.debug("suggest3 fcSuggest : "  + suggestResponse.getTexts().size());
				return suggestResponse;
			}
		} else {
			PostSuggestResponse suggestResponse = autoComplete3(query);
			if(hasSuggests(suggestResponse)) {
				log.debug("suggest3 autoComplete : "  + suggestResponse.getTexts().size());
				return suggestResponse;
			}
		}

		PostSuggestResponse suggestResponse = koToEnSuggest3(query);
		if(hasSuggests(suggestResponse)) {
			log.debug("suggest3 koToEnSuggest : "  + suggestResponse.getTexts().size());
			return suggestResponse;
		}

		suggestResponse = enToKoSuggest3(query);
		if(hasSuggests(suggestResponse) ) {
			log.debug("suggest3 enToKoSuggest : "  + suggestResponse.getTexts().size());
			return suggestResponse;
		}

		return PostSuggestResponse.emptyResponse();
	}

	private PostSuggestResponse fcSuggest3(String query) throws ElasticsearchException {
		//query = HangulUtil.decomposeLayeredJaum(query);
		log.debug("  PostSuggest3EsService.fcSuggest( " + query + " )");
		query = EsKoUtil.decomposeDualConsonant(query);
		String[] includes = {"title"};
		SearchRequest searchRequest = postEsHelper3.createFcSearchRequest(query, 1, includes);
		try {
			SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
			return postEsHelper3.createTitleSuggestResponse(response);
		} catch (IOException e) {
			log.error("query=" + query, e);
			SlackLogBot.sendError(e);
			throw new ElasticsearchException("엘라스틱서치 Search API 호출 에러");
		}
	}

	private PostSuggestResponse autoComplete3(String query) throws ElasticsearchException {
		log.debug("  PostSuggestEsService.autoComplete( " + query + " )");
		SearchRequest searchRequest = postEsHelper3.createAutoCompleteSearchRequest(query);
		try {
			SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
			return postEsHelper3.createTitleSuggestResponse(response);
		} catch (IOException e) {
			log.error("query=" + query, e);
			SlackLogBot.sendError(e);
			throw new ElasticsearchException("엘라스틱서치 Search API 호출 에러");
		}
	}

	private PostSuggestResponse koToEnSuggest3(String query) throws ElasticsearchException {
		log.debug("  PostSuggestEsService.koToEnSuggest( " + query + " )");
		String[] includes = {"title"};

		SearchRequest searchRequest = postEsHelper3.createKoToEnSearchRequest(query, 1, includes);
		try {
			SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
			return postEsHelper3.createTitleSuggestResponse(response);
		} catch (IOException e) {
			log.error("query=" + query, e);
			SlackLogBot.sendError(e);
			throw new ElasticsearchException("엘라스틱서치 Search API 호출 에러");
		}
	}

	private PostSuggestResponse enToKoSuggest3(String query) throws ElasticsearchException {
		log.debug("  PostSuggestEsService.koToEnSuggest( " + query + " )");
		String[] includes = {"title"};

		SearchRequest searchRequest = postEsHelper3.createEnToKoSearchRequest(query, 1, includes);
		try {
			SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
			return postEsHelper3.createTitleSuggestResponse(response);
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
