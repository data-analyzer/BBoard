package com.se.board.domain.post;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
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
import com.google.gson.GsonBuilder;
import com.se.board.common.book.EsKoUtil;
import com.se.board.common.dto.SearchDto;
import com.se.board.common.gson.LocalDateTimeTypeAdapter;
import com.se.board.common.paging.PagingResponse;
import com.se.board.domain.book.SlackLogBot;
import com.se.board.domain.es.EsBulkData;
import com.se.board.domain.file.FileRequest;
import com.se.board.domain.file.FileResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostEsService {
	private final RestHighLevelClient esClient;
	private final PostEsHelper postEsHelper;

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

//			There was an unexpected error (type=Internal Server Error, status=500).
//			com.fasterxml.jackson.core.exc.StreamConstraintsException: String length (20054016) exceeds the maximum length (20000000)
//			ElasticsearchException[com.fasterxml.jackson.core.exc.StreamConstraintsException: String length (20054016) exceeds the maximum length (20000000)]; nested: StreamConstraintsException[String length (20054016) exceeds the maximum length (20000000)];
//				at com.se.board.domain.post.PostEsService.bulkDocument(PostEsService.java:107)
//			==> ?
//			https://github.com/FasterXML/jackson-core/issues/863
//			motlin commented on Apr 28, 2023
//			Thank you @pjfanning.
//
//			That comment shows that we're able to set the max length higher with code like:
//
//			objectMapper.getFactory()
//					.setStreamReadConstraints(StreamReadConstraints.builder().maxStringLength(10_000_000).build())
//			I'm able to effectively disable the feature by setting the max length to Integer.MAX_VALUE, but not 0.
//
//			ObjectMapper objectMapper = ...;
//			StreamReadConstraints streamReadConstraints = StreamReadConstraints
//			    .builder()
//			    .maxStringLength(Integer.MAX_VALUE)
//			    .build();
//			objectMapper.getFactory().setStreamReadConstraints(streamReadConstraints);
//			I'm going to set the max length high to unblock the upgrade. It's not clear to me if this is a good idea or if this indicates a real performance problem in my code. Are there other options I ought to consider?

//			이렇게 해봐도 안되네... String length (20054016) exceeds the maximum length (20000000)


//			There was an unexpected error (type=Internal Server Error, status=500).
//			java.net.SocketTimeoutException: 30,000 milliseconds timeout on connection http-outgoing-1 [ACTIVE]
//			ElasticsearchException[java.net.SocketTimeoutException: 30,000 milliseconds timeout on connection http-outgoing-1 [ACTIVE]]; nested: SocketTimeoutException[30,000 milliseconds timeout on connection http-outgoing-1 [ACTIVE]]; nested: SocketTimeoutException[30,000 milliseconds timeout on connection http-outgoing-1 [ACTIVE]];
//				at com.se.board.domain.post.PostEsService.bulkDocument(PostEsService.java:114)
//			Caused by: java.net.SocketTimeoutException: 30,000 milliseconds timeout on connection http-outgoing-1 [ACTIVE]
//			==> keepAlive use true, keepAlive timeout sec
//			==> 적용 후 괜찮았으나 며칠 뒤 다시 업로드해보니 동일 오류 발생함
//			==>
//			https://discuss.elastic.co/t/how-to-avoid-30-000ms-timeout-during-reindexing/231370
//			After debugging enough, Came to know that socketTimeout should be set during client creation time and request timeout is something completely different from socketTimeout.
//
//			I started setting socketTimeout as below and worked fine.
//
//			return new RestHighLevelClient( RestClient.builder( HttpHost
//			                                                            .create( elasticSearchConfig().getEndPoint() ) )
//			                                        .setHttpClientConfigCallback( hacb -> hacb.addInterceptorLast( interceptor ) )
//			                                        .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder.setConnectTimeout(elasticSearchConfig().getClientConnectionTimeout())
//			                                                .setSocketTimeout(elasticSearchConfig().getClientSocketTimeout())));


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
	public List<EsBulkData> makePostBulkData (EsBulkData.Type actionType, String strDate, PostRequest params) {
		List<EsBulkData> bulkList = new ArrayList<>();

		EsBulkData data = new EsBulkData();
		data.setActionType(actionType);
		data.setIndexName(PostEsHelper.POST_INDEX);
		data.setId(params.getId() + "_0");
		Map<String, Object> docMap = new HashMap<>();
		docMap.put("createdDate", strDate);
		docMap.put("postId", params.getId());
		docMap.put("fileId", "0");
		docMap.put("title", params.getTitle());
		docMap.put("writer", params.getWriter());
		docMap.put("noticeYn", (params.getNoticeYn() ? true : false) );
		docMap.put("deleteYn", false);
		docMap.put("content", params.getContent());
		docMap.put("writer", params.getWriter());
		data.setMapDoc(docMap);
		bulkList.add(data);

		return bulkList;
	}

	// ES File document Map 생성
	public List<EsBulkData> makeFilesDocMap (EsBulkData.Type actionType, String strDate, List<FileRequest> files, PostRequest params) {

		if(CollectionUtils.isEmpty(files)) {
			return null;
		}

		List<EsBulkData> bulkList = new ArrayList<>();
		for( FileRequest file : files) {
			EsBulkData data = new EsBulkData();
			data.setActionType(actionType);
			data.setIndexName(PostEsHelper.POST_INDEX);
			data.setId(file.getPostId() + "_" + file.getId());
			Map<String, Object> docMap = new HashMap<>();
			docMap.put("createdDate", strDate);
			docMap.put("postId", file.getPostId());
			docMap.put("fileId", file.getId());
			docMap.put("title", file.getOriginalName());
			docMap.put("savedFilename", file.getSaveName());
			docMap.put("writer", params.getWriter());          // 첨부파일의 wirter 값을 넣어줄것인가? 주는게 맞을수도...
			docMap.put("noticeYn", (params.getNoticeYn() ? true : false) );
			docMap.put("deleteYn", false);
			docMap.put("data", file.getB64Str());
			data.setMapDoc(docMap);
			bulkList.add(data);
		}

		return bulkList;
	}

	// ES File 삭제 document Map 생성
	// 일단, 실제 삭제하자

	public List<EsBulkData> makeDeleteBulkData(EsBulkData.Type actionType, String strDate, Long id, List<FileResponse> deleteFiles) {
		List<EsBulkData> bulkList = new ArrayList<>();
		EsBulkData data = new EsBulkData();
		data.setActionType(actionType);
		data.setIndexName(PostEsHelper.POST_INDEX);
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
				dataFile.setIndexName(PostEsHelper.POST_INDEX);
				dataFile.setId(id + "_" + deleteFile.getId());
				// 삭제아닌 update 할 때
//				Map<String, Object> docMapFile = new HashMap<>();
//				docMapFile.put("deletedDate", strDate);
//				docMapFile.put("deleteYn", true);
//				dataFile.setMapDoc(docMapFile);

				bulkList.add(dataFile);
			}
			// postEsService.deleteFiles(docIds);
		}

		return bulkList;
	}














	////////////////////////////////////////////
	//////////// 여기서 부터 검색  /////////////////

	/*
	 * public Book getById(String id) throws ElasticsearchException {
	 *
	 * GetResponse response; try { response =
	 * esClient.get(postEsHelper.createGetByIdRequest(id), RequestOptions.DEFAULT);
	 * } catch (IOException e) { log.error("IOException occured.");
	 * SlackLogBot.sendError(e); throw new ElasticsearchException(e); }
	 *
	 * if (!response.isExists()) { return null; }
	 *
	 * Book book = new Gson().fromJson(response.getSourceAsString(), Book.class);
	 *
	 * return book; }
	 */

	public PostResponse getById(String id) throws ElasticsearchException {

		GetResponse response;
		try {
			response = esClient.get(postEsHelper.createGetByIdRequest(id), RequestOptions.DEFAULT);

		} catch (IOException e) {
			log.error("IOException occured.");
			SlackLogBot.sendError(e);
			throw new ElasticsearchException(e);
		}

		if (!response.isExists()) {
			return null;
		}

		Gson gson = new GsonBuilder()
				.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
				.create();

		PostResponse post = gson.fromJson(response.getSourceAsString(), PostResponse.class);
		if (post != null) {
			post.setId(post.getPostId());
		}

//		There was an unexpected error (type=Internal Server Error, status=500).
//		Failed making field 'java.time.LocalDateTime#date' accessible; either change its visibility or write a custom TypeAdapter for its declaring type
//		com.google.gson.JsonIOException: Failed making field 'java.time.LocalDateTime#date' accessible; either change its visibility or write a custom TypeAdapter for its declaring type
//		Caused by: java.lang.reflect.InaccessibleObjectException: Unable to make field private final java.time.LocalDate java.time.LocalDateTime.date accessible: module java.base does not "opens java.time" to unnamed module @6e1ac305
//		at java.base/java.lang.reflect.AccessibleObject.checkCanSetAccessible(AccessibleObject.java:354)
//		at java.base/java.lang.reflect.AccessibleObject.checkCanSetAccessible(AccessibleObject.java:297)
//		at java.base/java.lang.reflect.Field.checkCanSetAccessible(Field.java:178)
//		at java.base/java.lang.reflect.Field.setAccessible(Field.java:172)
//		at com.google.gson.internal.reflect.ReflectionHelper.makeAccessible(ReflectionHelper.java:19)

		return post;
	}


	// BookSearchResponseDto 대신 SearchDto 사용
	// 리턴을 BookSearchResponseDto 대신 PagingResponse<PostResponse> 로 변경해야함
//	public BookSearchResponseDto search(SearchDto params) throws ElasticsearchException {
	public PagingResponse<PostResponse> search(SearchDto params) throws ElasticsearchException {

		String query = params.getKeyword();
		int page = params.getPage();
		PostEsHelper.COUNT_PER_PAGE = params.getRecordSize();

//		BookSearchResponseDto bookSearchResponseDto;

		PagingResponse<PostResponse> postResponse;

		if (EsKoUtil.isCompleteKoQuery(query)) {
			// 추가 (title, content)
			postResponse = searchTitleContent(query, page);
			if (postResponse.getTotalHits() > 0) {
				return postResponse;
			}

			// 추가 (title, writer, content)
			postResponse = searchTitleWriterContent(query, page);
			if (postResponse.getTotalHits() > 0) {
				return postResponse;
			}

			// 순서를 위 title + content 먼저
			postResponse = searchTitle(query, page);
			if (postResponse.getTotalHits() > 0) {
				return postResponse;
			}

			// 단순 명칭 변경
			postResponse = searchTitleWriter(query, page);
			if (postResponse.getTotalHits() > 0) {
				return postResponse;
			}

		}

		// 현재 title 에 대해서만 하고 있으므로 content 에 대해서도 추가 검토
		// ["title_text", "title_ac", "title_chosung", "title_engtohan", "title_hantoeng"]
		// ["content_text", "content_ac", "content_chosung", "content_engtohan", "content_hantoeng"]
		// 인덱스 수정 필요, attachment.content 에서도 중복 copy하고 있는데, 빼고 content 에서 copy되고 있는지 확인 필요함

		if (EsKoUtil.isFcQuery(query)) {
			postResponse = searchByFc(query, page); // searchByChosung(query, page);
			if (postResponse.getTotalHits() > 0) {
				return postResponse;
			}
		}

		if (EsKoUtil.isEnQuery(query)) {
			postResponse = searchKoToEn(query, page);
			if (postResponse.getTotalHits() > 0) {
				return postResponse;
			}
		}

		postResponse = searchEnToKo(query, page);
		if (postResponse.getTotalHits() > 0) {
			return postResponse;
		}

//		return BookSearchResponseDto.emptyResponse();
		return new PagingResponse<PostResponse>(Collections.emptyList(), null, Boolean.TRUE);
	}

//	private BookSearchResponseDto searchTitle(String query, int page) {
	private PagingResponse<PostResponse> searchTitle(String query, int page) {
		log.debug("  PostEsService.searchTitle(" + query + ", " + page + ")");
		try {
			SearchRequest searchRequest = postEsHelper.createTitleSearchRequest(query, page);
			SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
			if (response != null && response.getHits().getTotalHits().value > 0) {

//				BookSearchResponseDto responseDto =
//						postEsHelper.createBookSearchResponseDto(response, SearchHitStage.TITLE.toString());
				PagingResponse<PostResponse> postResponse =
						postEsHelper.createPostSearchResponse(response, EsSearchHitStage.TITLE.toString());

				log.debug("response.getHits().getTotalHits(): " + response.getHits().getTotalHits());
				log.debug(postResponse.toString());
				log.info("==================== kibo searchTitle =========== SlackWebHookUrl :" + SlackLogBot.getSlackWebHookUrl());
				SlackLogBot.sendMessage(postResponse.toString());

				return postResponse;
			}
		} catch (IOException e) {
			log.error("query=" + query + ", page=" + page, e);
			SlackLogBot.sendError(e);
			throw new ElasticsearchException(e);
		}

//		return BookSearchResponseDto.emptyResponse();
		//return PagingResponse<PostResponse>.emptyResponse();
		return new PagingResponse<PostResponse>(Collections.emptyList(), null, Boolean.TRUE);
	}

//	private BookSearchResponseDto searchTitleAuthor(String query, int page) {
//	private PagingResponse<PostResponse> searchTitleAuthor(String query, int page) {
	private PagingResponse<PostResponse> searchTitleWriter(String query, int page) {
		log.debug("  PostEsService.searchTitleWriter(" + query + ", " + page + ")");
		try {
//			SearchRequest searchRequest = postEsHelper.createTitleAuthorSearchRequest(query, page);
			SearchRequest searchRequest = postEsHelper.createTitleWriterSearchRequest(query, page);
			SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
			if (response != null && response.getHits().getTotalHits().value > 0) {
//				BookSearchResponseDto responseDto = postEsHelper.createBookSearchResponseDto(response,
//						SearchHitStage.TITLE_AUTHOR.toString());
				PagingResponse<PostResponse> postResponse = postEsHelper.createPostSearchResponse(response,
						EsSearchHitStage.TITLE_WRITER.toString());

				log.debug(postResponse.toString());
				log.info("==================== kibo searchTitleAuthor =========== SlackWebHookUrl :" + SlackLogBot.getSlackWebHookUrl());
				SlackLogBot.sendMessage(postResponse.toString());

				return postResponse;
			}
		} catch (IOException e) {
			log.error("query=" + query + ", page=" + page, e);
			SlackLogBot.sendError(e);
			throw new ElasticsearchException(e);
		}

//		return BookSearchResponseDto.emptyResponse();
		return new PagingResponse<PostResponse>(Collections.emptyList(), null, Boolean.TRUE);
	}

	private PagingResponse<PostResponse> searchTitleWriterContent(String query, int page) {
		log.debug("  PostEsService.searchTitleWriterContent(" + query + ", " + page + ")");
		try {
//			SearchRequest searchRequest = postEsHelper.createTitleAuthorSearchRequest(query, page);
			SearchRequest searchRequest = postEsHelper.createTitleWriterContentSearchRequest(query, page);
			SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
			if (response != null && response.getHits().getTotalHits().value > 0) {
//				BookSearchResponseDto responseDto = postEsHelper.createBookSearchResponseDto(response,
//						SearchHitStage.TITLE_AUTHOR.toString());
				PagingResponse<PostResponse> postResponse = postEsHelper.createPostSearchResponse(response,
						EsSearchHitStage.TITLE_WRITER_CONTENT.toString());

				log.debug(postResponse.toString());
				log.info("==================== kibo searchTitleAuthor =========== SlackWebHookUrl :" + SlackLogBot.getSlackWebHookUrl());
				SlackLogBot.sendMessage(postResponse.toString());

				return postResponse;
			}
		} catch (IOException e) {
			log.error("query=" + query + ", page=" + page, e);
			SlackLogBot.sendError(e);
			throw new ElasticsearchException(e);
		}

//		return BookSearchResponseDto.emptyResponse();
		return new PagingResponse<PostResponse>(Collections.emptyList(), null, Boolean.TRUE);
	}

	private PagingResponse<PostResponse> searchTitleContent(String query, int page) {
		log.debug("  PostEsService.searchTitleContent(" + query + ", " + page + ")");
		try {
//			SearchRequest searchRequest = postEsHelper.createTitleAuthorSearchRequest(query, page);
			SearchRequest searchRequest = postEsHelper.createTitleContentSearchRequest(query, page);
			SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
			if (response != null && response.getHits().getTotalHits().value > 0) {
//				BookSearchResponseDto responseDto = postEsHelper.createBookSearchResponseDto(response,
//						SearchHitStage.TITLE_AUTHOR.toString());
				PagingResponse<PostResponse> postResponse = postEsHelper.createPostSearchResponse(response,
						EsSearchHitStage.TITLE_CONTENT.toString());

				log.debug(postResponse.toString());
				log.info("==================== kibo searchTitleAuthor =========== SlackWebHookUrl :" + SlackLogBot.getSlackWebHookUrl());
				SlackLogBot.sendMessage(postResponse.toString());

				return postResponse;
			}
		} catch (IOException e) {
			log.error("query=" + query + ", page=" + page, e);
			SlackLogBot.sendError(e);
			throw new ElasticsearchException(e);
		}

//		return BookSearchResponseDto.emptyResponse();
		return new PagingResponse<PostResponse>(Collections.emptyList(), null, Boolean.TRUE);
	}

//	private BookSearchResponseDto searchByChosung(String query, int page) {
	private PagingResponse<PostResponse> searchByFc(String query, int page) {
		log.debug("  PostEsService.searchByFc(" + query + ", " + page + ")");
		query = EsKoUtil.decomposeDualConsonant(query);
		try {
//			String[] includes = {"isbn13", "title", "author", "publisher", "pubDate", "imageUrl", "description"};
			String[] includes = {"createdDate", "postId", "fileId", "title", "content", "writer", "noticeYn", "deleteYn", "savedFilename", "modifiedDate", "deletedDate"};

			SearchRequest searchRequest = postEsHelper.createFcSearchRequest(query, page, includes);
			SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
			if (response != null && response.getHits().getTotalHits().value > 0) {
//				BookSearchResponseDto responseDto =
//						postEsHelper.createBookSearchResponseDto(response, SearchHitStage.CHOSUNG.toString());
				PagingResponse<PostResponse> postResponse =
						postEsHelper.createPostSearchResponse(response,	EsSearchHitStage.FC.toString());

				log.debug(postResponse.toString());
				log.info("==================== kibo searchByFC =========== SlackWebHookUrl :" + SlackLogBot.getSlackWebHookUrl());
				SlackLogBot.sendMessage(postResponse.toString());

				return postResponse;
			}
		} catch (IOException e) {
			log.error("query=" + query + ", page=" + page, e);
			SlackLogBot.sendError(e);
			throw new ElasticsearchException(e);
		}

//		return BookSearchResponseDto.emptyResponse();
		return new PagingResponse<PostResponse>(Collections.emptyList(), null, Boolean.TRUE);
	}

//	private BookSearchResponseDto searchEngToHan(String query, int page) {
	private PagingResponse<PostResponse> searchEnToKo(String query, int page) {
		log.debug("  PostEsService.searchEnToKo(" + query + ", " + page + ")");
		try {
//			String[] includes = {"isbn13", "title", "author", "publisher", "pubDate", "imageUrl", "description"};
			String[] includes = {"createdDate", "postId", "fileId", "title", "content", "writer", "noticeYn", "deleteYn", "savedFilename", "modifiedDate", "deletedDate"};

			SearchRequest searchRequest = postEsHelper.createEnToKoSearchRequest(query, page, includes);
			SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
			if (response != null && response.getHits().getTotalHits().value > 0) {
//				BookSearchResponseDto responseDto =
//						postEsHelper.createBookSearchResponseDto(response, SearchHitStage.ENG_TO_HAN.toString());
				PagingResponse<PostResponse> postResponse =
						postEsHelper.createPostSearchResponse(response,	EsSearchHitStage.EN_TO_KO.toString());

				log.debug(postResponse.toString());
				log.info("==================== kibo searchEnToKo =========== SlackWebHookUrl :" + SlackLogBot.getSlackWebHookUrl());
				SlackLogBot.sendMessage(postResponse.toString());

				return postResponse;
			}
		} catch (IOException e) {
			log.error("query=" + query + ", page=" + page, e);
			SlackLogBot.sendError(e);
			throw new ElasticsearchException(e);
		}

//		return BookSearchResponseDto.emptyResponse();
		return new PagingResponse<PostResponse>(Collections.emptyList(), null, Boolean.TRUE);
	}

//	private BookSearchResponseDto searchHanToEng(String query, int page) {
	private PagingResponse<PostResponse> searchKoToEn(String query, int page) {
		log.debug("  PostEsService.searchKoToEn(" + query + ", " + page + ")");
		try {
//			String[] includes = {"isbn13", "title", "author", "publisher", "pubDate", "imageUrl", "description"};
			String[] includes = {"createdDate", "postId", "fileId", "title", "content", "writer", "noticeYn", "deleteYn", "savedFilename", "modifiedDate", "deletedDate"};

			SearchRequest searchRequest = postEsHelper.createKoToEnSearchRequest(query, page, includes);
			SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
			if (response != null && response.getHits().getTotalHits().value > 0) {
//				BookSearchResponseDto responseDto =
//						postEsHelper.createBookSearchResponseDto(response, SearchHitStage.HAN_TO_ENG.toString());
				PagingResponse<PostResponse> postResponse =
						postEsHelper.createPostSearchResponse(response,	EsSearchHitStage.KO_TO_EN.toString());

				log.debug(postResponse.toString());
				log.info("==================== kibo searchKoToEn =========== SlackWebHookUrl :" + SlackLogBot.getSlackWebHookUrl());
				SlackLogBot.sendMessage(postResponse.toString());

				return postResponse;
			}
		} catch (IOException e) {
			log.error("query=" + query + ", page=" + page, e);
			SlackLogBot.sendError(e);
			throw new ElasticsearchException(e);
		}

//		return BookSearchResponseDto.emptyResponse();
		return new PagingResponse<PostResponse>(Collections.emptyList(), null, Boolean.TRUE);
	}
}
