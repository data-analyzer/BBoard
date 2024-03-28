package com.se.board.domain.book;

import java.io.IOException;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.se.board.common.book.HangulUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookSearchService {
	private final RestHighLevelClient esClient;
	private final BookHelper bookHelper;

	public Book getById(String id) throws ElasticsearchException {

		GetResponse response;
		try {
			response = esClient.get(bookHelper.createGetByIdRequest(id), RequestOptions.DEFAULT);
		} catch (IOException e) {
			log.error("IOException occured.");
			SlackLogBot.sendError(e);
			throw new ElasticsearchException(e);
		}

		if (!response.isExists()) {
			return null;
		}

		Book book = new Gson().fromJson(response.getSourceAsString(), Book.class);

		return book;
	}

	public BookSearchResponseDto search(BookSearchRequestDto bookSearchRequestDto)
			throws ElasticsearchException {

		String query = bookSearchRequestDto.getQuery();
		int page = bookSearchRequestDto.getPage();

		BookSearchResponseDto bookSearchResponseDto;

		if (HangulUtil.isCompleteHangulQuery(query)) {
			bookSearchResponseDto = searchTitle(query, page);
			if (bookSearchResponseDto.getTotalHits() > 0) {
				return bookSearchResponseDto;
			}

			bookSearchResponseDto = searchTitleAuthor(query, page);
			if (bookSearchResponseDto.getTotalHits() > 0) {
				return bookSearchResponseDto;
			}
		}

		if (HangulUtil.isChosungQuery(bookSearchRequestDto.getQuery())) {
			bookSearchResponseDto = searchByChosung(query, page);
			if (bookSearchResponseDto.getTotalHits() > 0) {
				return bookSearchResponseDto;
			}
		}

		if (HangulUtil.isEnglishQuery(query)) {
			bookSearchResponseDto = searchHanToEng(query, page);
			if (bookSearchResponseDto.getTotalHits() > 0) {
				return bookSearchResponseDto;
			}
		}

		bookSearchResponseDto = searchEngToHan(query, page);
		if (bookSearchResponseDto.getTotalHits() > 0) {
			return bookSearchResponseDto;
		}

		return BookSearchResponseDto.emptyResponse();
	}

	private BookSearchResponseDto searchTitle(String query, int page) {
		try {
			SearchRequest searchRequest = bookHelper.createTitleSearchRequest(query, page);
			SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
			if (response != null && response.getHits().getTotalHits().value > 0) {
				BookSearchResponseDto responseDto =
						bookHelper.createBookSearchResponseDto(response, SearchHitStage.TITLE.toString());

				log.debug(responseDto.toString());
				log.info("==================== kibo searchTitle =========== SlackWebHookUrl :" + SlackLogBot.getSlackWebHookUrl());
				SlackLogBot.sendMessage(responseDto.toString());

				return responseDto;
			}
		} catch (IOException e) {
			log.error("query=" + query + ", page=" + page, e);
			SlackLogBot.sendError(e);
			throw new ElasticsearchException(e);
		}

		return BookSearchResponseDto.emptyResponse();
	}

	private BookSearchResponseDto searchTitleAuthor(String query, int page) {
		try {
			SearchRequest searchRequest = bookHelper.createTitleAuthorSearchRequest(query, page);
			SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
			if (response != null && response.getHits().getTotalHits().value > 0) {
				BookSearchResponseDto responseDto = bookHelper.createBookSearchResponseDto(response,
						SearchHitStage.TITLE_AUTHOR.toString());

				log.debug(responseDto.toString());
				log.info("==================== kibo searchTitleAuthor =========== SlackWebHookUrl :" + SlackLogBot.getSlackWebHookUrl());
				SlackLogBot.sendMessage(responseDto.toString());

				return responseDto;
			}
		} catch (IOException e) {
			log.error("query=" + query + ", page=" + page, e);
			SlackLogBot.sendError(e);
			throw new ElasticsearchException(e);
		}

		return BookSearchResponseDto.emptyResponse();
	}

	private BookSearchResponseDto searchByChosung(String query, int page) {
		query = HangulUtil.decomposeLayeredJaum(query);
		try {
			String[] includes =
					{"isbn13", "title", "author", "publisher", "pubDate", "imageUrl", "description"};
			SearchRequest searchRequest = bookHelper.createChosungSearchRequest(query, page, includes);
			SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
			if (response != null && response.getHits().getTotalHits().value > 0) {
				BookSearchResponseDto responseDto =
						bookHelper.createBookSearchResponseDto(response, SearchHitStage.CHOSUNG.toString());

				log.debug(responseDto.toString());
				log.info("==================== kibo searchByChosung =========== SlackWebHookUrl :" + SlackLogBot.getSlackWebHookUrl());
				SlackLogBot.sendMessage(responseDto.toString());

				return responseDto;
			}
		} catch (IOException e) {
			log.error("query=" + query + ", page=" + page, e);
			SlackLogBot.sendError(e);
			throw new ElasticsearchException(e);
		}

		return BookSearchResponseDto.emptyResponse();
	}

	private BookSearchResponseDto searchEngToHan(String query, int page) {
		try {
			String[] includes =
					{"isbn13", "title", "author", "publisher", "pubDate", "imageUrl", "description"};
			SearchRequest searchRequest = bookHelper.createEngToHanSearchRequest(query, page, includes);
			SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
			if (response != null && response.getHits().getTotalHits().value > 0) {
				BookSearchResponseDto responseDto =
						bookHelper.createBookSearchResponseDto(response, SearchHitStage.ENG_TO_HAN.toString());

				log.debug(responseDto.toString());
				log.info("==================== kibo searchEngToHan =========== SlackWebHookUrl :" + SlackLogBot.getSlackWebHookUrl());
				SlackLogBot.sendMessage(responseDto.toString());

				return responseDto;
			}
		} catch (IOException e) {
			log.error("query=" + query + ", page=" + page, e);
			SlackLogBot.sendError(e);
			throw new ElasticsearchException(e);
		}

		return BookSearchResponseDto.emptyResponse();
	}

	private BookSearchResponseDto searchHanToEng(String query, int page) {
		try {
			String[] includes =
					{"isbn13", "title", "author", "publisher", "pubDate", "imageUrl", "description"};
			SearchRequest searchRequest = bookHelper.createHanToEngSearchRequest(query, page, includes);
			SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
			if (response != null && response.getHits().getTotalHits().value > 0) {
				BookSearchResponseDto responseDto =
						bookHelper.createBookSearchResponseDto(response, SearchHitStage.HAN_TO_ENG.toString());

				log.debug(responseDto.toString());
				log.info("==================== kibo searchHanToEng =========== SlackWebHookUrl :" + SlackLogBot.getSlackWebHookUrl());
				SlackLogBot.sendMessage(responseDto.toString());

				return responseDto;
			}
		} catch (IOException e) {
			log.error("query=" + query + ", page=" + page, e);
			SlackLogBot.sendError(e);
			throw new ElasticsearchException(e);
		}

		return BookSearchResponseDto.emptyResponse();
	}
}
