package com.se.board.domain.es;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.google.gson.Gson;
import com.se.board.common.book.HangulUtil;

import com.se.board.domain.book.SlackLogBot;
import com.se.board.domain.file.FileRequest;
import com.se.board.domain.file.FileResponse;
import com.se.board.domain.post.PostRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EsService {
	private final RestHighLevelClient esClient;
	private final EsHelper esHelper;

	///////////////////////////////////////////////////////
	// 기본
	//////////////////////////////////////////////////////
	//create
	public void createDocument( final String index, final String id, final Map<String, Object> params, String _pipeline) throws ElasticsearchException {
		try {
			IndexRequest request = new IndexRequest(index)
					.id(id)
					.source(params); // .source(jsonBody, XContentType.JSON);
			request.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
			if(_pipeline != null && !"".equals(_pipeline)) {
				request.setPipeline(_pipeline);
			}
			esClient.index(request, RequestOptions.DEFAULT);
		} catch (IOException e) {
			log.error("IOException occured.");
			SlackLogBot.sendError(e);
			throw new ElasticsearchException(e);
		}
	}

	public BulkResponse bulkDocument (List<EsBulkData> bulkList, String pipeline) {
		BulkRequest request = new BulkRequest();
		for(EsBulkData data : bulkList) {
			switch (data.getActionType()) {
			case CREATE :
				request.add(
						new IndexRequest(data.getIndexName())
						.id(data.getId())
						.source(data.getMapDoc())
						);
				SlackLogBot.sendMessage("Bulk " + data.getActionType() + " added : " + data.getIndexName() + "/" + data.getId());
				break;
			case UPDATE :
				request.add(
						new UpdateRequest(data.getIndexName(), data.getId())
						.doc(data.getMapDoc())
						);
				SlackLogBot.sendMessage("Bulk " + data.getActionType() + " added : " + data.getIndexName() + "/" + data.getId());
				break;
			case DELETE :
				request.add(
						new DeleteRequest(data.getIndexName(), data.getId())
				);
				SlackLogBot.sendMessage("Bulk " + data.getActionType() + " added : " + data.getIndexName() + "/" + data.getId());
			}
		}

		BulkResponse bulkResponse = null;

		try {
			if(pipeline != null && !"".equals(pipeline)) {
				request.pipeline(pipeline);												// indexRequest.setPipeline("attachment");
			}
			request.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE); 			// WriteRequest.RefreshPolicy.WAIT_UNTIL
			bulkResponse = esClient.bulk(request, RequestOptions.DEFAULT);
		} catch (IOException e) {
			log.error("IOException occured.");
			SlackLogBot.sendError(e);
			throw new ElasticsearchException(e);
		}

		for (BulkItemResponse bulkItemResponse : bulkResponse) {
			if(bulkItemResponse.isFailed()) {
				BulkItemResponse.Failure failure = bulkItemResponse.getFailure();
				log.info(failure.getIndex() +" - " + failure.getType() + " - " + failure.getId() + " / " + failure.getMessage());
				SlackLogBot.sendMessage(failure.getIndex() +" - " + failure.getType() + " - " + failure.getId() + " / " + failure.getMessage());
			}
		}
		log.info("bulkResponse.getItems(): " + bulkResponse.getItems());
		log.info("bulkResponse.getTook()" + bulkResponse.getTook());
		SlackLogBot.sendMessage("bulkResponse.getItems()" + bulkResponse.getItems() + "\r\nbulkResponse.getTook()" + bulkResponse.getTook());

		return bulkResponse;
	}

	//get
	public GetResponse getDocument(final String index, final String id) throws ElasticsearchException {
		try {
			GetRequest request = new GetRequest(index, id);
			return esClient.get(request, RequestOptions.DEFAULT);
		} catch (IOException e) {
			log.error("IOException occured.");
			SlackLogBot.sendError(e);
			throw new ElasticsearchException(e);
		}
	}

	//update
	public void updateDocument(final String index, final String id, final Map<String, Object> params) throws ElasticsearchException {
		try {
			UpdateRequest request = new UpdateRequest(index, id)
					.doc(params);
			esClient.update(request, RequestOptions.DEFAULT);
		} catch (IOException e) {
			log.error("IOException occured.");
			SlackLogBot.sendError(e);
			throw new ElasticsearchException(e);
		}

	}

	//Delete
	public void deleteDocument(String index, String id) throws ElasticsearchException {
		try {
		  DeleteRequest request = new DeleteRequest(index, id);
		  esClient.delete(request, RequestOptions.DEFAULT);
		} catch (IOException e) {
			log.error("IOException occured.");
			SlackLogBot.sendError(e);
			throw new ElasticsearchException(e);
		}
	}

	// ES Post document Map 생성
	public List<EsBulkData> makePostBulkData (EsBulkData.Type actionType, String indexName, String strDate, PostRequest params) {
		List<EsBulkData> bulkList = new ArrayList<>();

		EsBulkData data = new EsBulkData();
		data.setActionType(actionType);
		data.setIndexName(indexName);
		data.setId(params.getId() + "_0");
		Map<String, Object> docMap = new HashMap<>();
		docMap.put("@timestamp", strDate);
		docMap.put("post_id", params.getId());
		docMap.put("file_id", "0");
		docMap.put("title", params.getTitle());
		docMap.put("writer", params.getWriter());
		docMap.put("notice_yn", (params.getNoticeYn() ? true : false) );
		docMap.put("content", params.getContent());
		docMap.put("writer", params.getWriter());
		data.setMapDoc(docMap);
		bulkList.add(data);

		return bulkList;
	}

	// ES File document Map 생성
	public List<EsBulkData> makeFilesDocMap (EsBulkData.Type actionType, String indexName, String strDate, List<FileRequest> files) {

		if(CollectionUtils.isEmpty(files)) {
			return null;
		}

		List<EsBulkData> bulkList = new ArrayList<>();
		for( FileRequest file : files) {
			EsBulkData data = new EsBulkData();
			data.setActionType(actionType);
			data.setIndexName(indexName);
			data.setId(file.getPostId() + "_" + file.getId());
			Map<String, Object> docMap = new HashMap<>();
			docMap.put("@timestamp", strDate);
			docMap.put("post_id", file.getPostId());
			docMap.put("file_id", file.getId());
			docMap.put("title", file.getOriginalName());
			docMap.put("savedfilename", file.getSaveName());
			docMap.put("writer", file.getWriter());           // 첨부파일의 wirter 를 넣어줄것인가? 주는게 맞을수도...
			docMap.put("data", file.getB64Str());
			data.setMapDoc(docMap);
			bulkList.add(data);
		}

		return bulkList;
	}

	// ES File 삭제 document Map 생성
	// 일단, 실제 삭제하자

	public List<EsBulkData> makeDeleteBulkData(EsBulkData.Type actionType, String indexName, String strDate, Long id, List<FileResponse> deleteFiles) {
		List<EsBulkData> bulkList = new ArrayList<>();
		EsBulkData data = new EsBulkData();
		data.setActionType(actionType);
		data.setIndexName(indexName);
		data.setId(id + "_" + "0");

		// 삭제아닌 update 할 때
//		Map<String, Object> docMap = new HashMap<>();
//		docMap.put("deleted_date", strDate);
//		docMap.put("delete_yn", true);
//		data.setMapDoc(docMap);

		bulkList.add(data);


		if ( !deleteFiles.isEmpty() ) {
			for (FileResponse deleteFile : deleteFiles) {

				EsBulkData dataFile = new EsBulkData();
				dataFile.setActionType(actionType);
				dataFile.setIndexName(indexName);
				dataFile.setId(id + "_" + deleteFile.getId());
				// 삭제아닌 update 할 때
//				Map<String, Object> docMapFile = new HashMap<>();
//				docMapFile.put("deleted_date", strDate);
//				docMapFile.put("delete_yn", true);
//				dataFile.setMapDoc(docMapFile);

				bulkList.add(dataFile);
			}
			// postEsService.deleteFiles(docIds);
		}

		return bulkList;
	}












	////////////////////////////////////////////
	//////////// 여기서 부터 검색  /////////////////

	public EsBoard getById(String id) throws ElasticsearchException {

		GetResponse response;
		try {
			response = esClient.get(esHelper.createGetByIdRequest(id), RequestOptions.DEFAULT);
		} catch (IOException e) {
			log.error("IOException occured.");
			SlackLogBot.sendError(e);
			throw new ElasticsearchException(e);
		}

		if (!response.isExists()) {
			return null;
		}

		EsBoard esBoard = new Gson().fromJson(response.getSourceAsString(), EsBoard.class);

		return esBoard;
	}


// 확장  content 위주 검색 (content, title, author)
		public EsBoardSearchResponse usearch(EsBoardSearchRequest params) throws ElasticsearchException {

			String query = params.getQuery();
			int page = params.getPage();

			EsBoardSearchResponse boardSearchResponse;

			if (HangulUtil.isCompleteHangulQuery(query)) {
				boardSearchResponse = searchTitle(query, page);
				if (boardSearchResponse.getTotalHits() > 0) {
					return boardSearchResponse;
				}

				boardSearchResponse = searchTitleWriter(query, page);
				if (boardSearchResponse.getTotalHits() > 0) {
					return boardSearchResponse;
				}
			}

			if (HangulUtil.isChosungQuery(params.getQuery())) {
				boardSearchResponse = searchByChosung(query, page);
				if (boardSearchResponse.getTotalHits() > 0) {
					return boardSearchResponse;
				}
			}

			if (HangulUtil.isEnglishQuery(query)) {
				boardSearchResponse = searchHanToEng(query, page);
				if (boardSearchResponse.getTotalHits() > 0) {
					return boardSearchResponse;
				}
			}

			boardSearchResponse = searchEngToHan(query, page);
			if (boardSearchResponse.getTotalHits() > 0) {
				return boardSearchResponse;
			}

			return EsBoardSearchResponse.emptyResponse();
		}

// 추가
		public EsBoardSearchResponse searchTitleContentWriter(EsBoardSearchRequest params) {
			String query = params.getQuery();
			int page = params.getPage();
			try {
				SearchRequest searchRequest = esHelper.createTitleContentAuthorSearchRequest(query, page);
				SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
				if (response != null && response.getHits().getTotalHits().value > 0) {
					EsBoardSearchResponse responseDto = esHelper.createEsBoardUSearchResponseDto(response,
							EsSearchHitStage.TITLE_CONTENT_WRITER.toString());

					log.debug(responseDto.toString());
					log.info("==================== kibo searchTitleContentWriter =========== SlackWebHookUrl :" + SlackLogBot.getSlackWebHookUrl());
					SlackLogBot.sendMessage(responseDto.toString());

					return responseDto;
				}
			} catch (IOException e) {
				log.error("query=" + query + ", page=" + page, e);
				SlackLogBot.sendError(e);
				throw new ElasticsearchException(e);
			}

			return EsBoardSearchResponse.emptyResponse();
		}


// 기존 title 위주 검색 (title, author)
	public EsBoardSearchResponse search(EsBoardSearchRequest params)
			throws ElasticsearchException {

		String query = params.getQuery();
		int page = params.getPage();

		EsBoardSearchResponse boardSearchResponse;

		if (HangulUtil.isCompleteHangulQuery(query)) {
			boardSearchResponse = searchTitle(query, page);
			if (boardSearchResponse.getTotalHits() > 0) {
				return boardSearchResponse;
			}

			boardSearchResponse = searchTitleWriter(query, page);
			if (boardSearchResponse.getTotalHits() > 0) {
				return boardSearchResponse;
			}

			// 추가
//			boardSearchResponse = searchTitleContentWriter(query, page);
//			if (boardSearchResponse.getTotalHits() > 0) {
//				return boardSearchResponse;
//			}
		}

		if (HangulUtil.isChosungQuery(params.getQuery())) {
			boardSearchResponse = searchByChosung(query, page);
			if (boardSearchResponse.getTotalHits() > 0) {
				return boardSearchResponse;
			}
		}

		if (HangulUtil.isEnglishQuery(query)) {
			boardSearchResponse = searchHanToEng(query, page);
			if (boardSearchResponse.getTotalHits() > 0) {
				return boardSearchResponse;
			}
		}

		boardSearchResponse = searchEngToHan(query, page);
		if (boardSearchResponse.getTotalHits() > 0) {
			return boardSearchResponse;
		}

		return EsBoardSearchResponse.emptyResponse();
	}

	private EsBoardSearchResponse searchTitle(String query, int page) {
		try {
			SearchRequest searchRequest = esHelper.createTitleSearchRequest(query, page);
			SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
			if (response != null && response.getHits().getTotalHits().value > 0) {
				EsBoardSearchResponse responseDto =
						esHelper.createBookSearchResponseDto(response, EsSearchHitStage.TITLE.toString());

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

		return EsBoardSearchResponse.emptyResponse();
	}

	private EsBoardSearchResponse searchTitleWriter(String query, int page) {
		try {
			SearchRequest searchRequest = esHelper.createTitleAuthorSearchRequest(query, page);
			SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
			if (response != null && response.getHits().getTotalHits().value > 0) {
				EsBoardSearchResponse responseDto = esHelper.createBookSearchResponseDto(response,
//						EsSearchHitStage.TITLE_AUTHOR.toString());
						EsSearchHitStage.TITLE_WRITER.toString());

				log.debug(responseDto.toString());
				log.info("==================== kibo searchTitleWriter =========== SlackWebHookUrl :" + SlackLogBot.getSlackWebHookUrl());
				SlackLogBot.sendMessage(responseDto.toString());

				return responseDto;
			}
		} catch (IOException e) {
			log.error("query=" + query + ", page=" + page, e);
			SlackLogBot.sendError(e);
			throw new ElasticsearchException(e);
		}

		return EsBoardSearchResponse.emptyResponse();
	}

	private EsBoardSearchResponse searchByChosung(String query, int page) {
		query = HangulUtil.decomposeLayeredJaum(query);
		try {
			String[] includes = {"@timestamp", "post_id", "file_id", "title", "content", "writer"};
			SearchRequest searchRequest = esHelper.createChosungSearchRequest(query, page, includes);
			SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
			if (response != null && response.getHits().getTotalHits().value > 0) {
				EsBoardSearchResponse responseDto =
						esHelper.createBookSearchResponseDto(response, EsSearchHitStage.CHOSUNG.toString());

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

		return EsBoardSearchResponse.emptyResponse();
	}

	private EsBoardSearchResponse searchEngToHan(String query, int page) {
		try {
			String[] includes = {"@timestamp", "post_id", "file_id", "title", "content", "writer"};
			SearchRequest searchRequest = esHelper.createEngToHanSearchRequest(query, page, includes);
			SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
			if (response != null && response.getHits().getTotalHits().value > 0) {
				EsBoardSearchResponse responseDto =
						esHelper.createBookSearchResponseDto(response, EsSearchHitStage.ENG_TO_HAN.toString());

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

		return EsBoardSearchResponse.emptyResponse();
	}

	private EsBoardSearchResponse searchHanToEng(String query, int page) {
		try {
			String[] includes = {"@timestamp", "post_id", "file_id", "title", "content", "writer"};
			SearchRequest searchRequest = esHelper.createHanToEngSearchRequest(query, page, includes);
			SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
			if (response != null && response.getHits().getTotalHits().value > 0) {
				EsBoardSearchResponse responseDto =
						esHelper.createBookSearchResponseDto(response, EsSearchHitStage.HAN_TO_ENG.toString());

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

		return EsBoardSearchResponse.emptyResponse();
	}

}