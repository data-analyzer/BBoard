package com.se.board.domain.post;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.se.board.common.paging.PagingResponse;
import com.se.board.domain.book.SuggestResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
final class PostEsHelper {
	// 마지막 메소드 createSuggestResponseDto 차후 수정 필요

//	private static final String POST_INDEX = "ingest-test-v1";
//	protected static String POST_INDEX = "post-v1";
//	protected static String POST_INDEX = "post-v2";
	protected static String POST_INDEX = "post-v3";
	protected static int COUNT_PER_PAGE = 10;
	private static final int AUTO_COMPLETE_LIMIT = 10;

	private final ObjectMapper objectMapper;

//	public PostEsHelper() {
//		There was an unexpected error (type=Internal Server Error, status=500).
//		com.fasterxml.jackson.core.exc.StreamConstraintsException: String length (20054016) exceeds the maximum length (20000000)
//		ElasticsearchException[com.fasterxml.jackson.core.exc.StreamConstraintsException: String length (20054016) exceeds the maximum length (20000000)]; nested: StreamConstraintsException[String length (20054016) exceeds the maximum length (20000000)];
//			at com.se.board.domain.post.PostEsService.bulkDocument(PostEsService.java:107)
//		==> ?
//		https://github.com/FasterXML/jackson-core/issues/863
//		motlin commented on Apr 28, 2023
//		Thank you @pjfanning.
//
//		That comment shows that we're able to set the max length higher with code like:
//
//		objectMapper.getFactory()
//				.setStreamReadConstraints(StreamReadConstraints.builder().maxStringLength(10_000_000).build())
//		I'm able to effectively disable the feature by setting the max length to Integer.MAX_VALUE, but not 0.
//
//		ObjectMapper objectMapper = ...;
//		StreamReadConstraints streamReadConstraints = StreamReadConstraints
//		    .builder()
//		    .maxStringLength(Integer.MAX_VALUE)
//		    .build();
//		objectMapper.getFactory().setStreamReadConstraints(streamReadConstraints);
//		I'm going to set the max length high to unblock the upgrade. It's not clear to me if this is a good idea or if this indicates a real performance problem in my code. Are there other options I ought to consider?


//		objectMapper = new ObjectMapper();
//		StreamReadConstraints streamReadConstraints = StreamReadConstraints
//				.builder().maxStringLength(20000000)
//				.build();
//		objectMapper.getFactory().setStreamReadConstraints(StreamReadConstraints.builder().maxStringLength(50_000_000).build());
//	}

//	public PostEsHelper () {
//		objectMapper = new ObjectMapper();
//
//		StreamReadConstraints streamReadConstraints = StreamReadConstraints
//	    .builder()
//	    .maxStringLength(Integer.MAX_VALUE)
//	    .build();
//		objectMapper.getFactory().setStreamReadConstraints(streamReadConstraints);
//	}

	public GetRequest createGetByIdRequest(String id) {
		return new GetRequest(POST_INDEX, id);
	}

	SearchRequest createTitleSearchRequest(String query, int page) {

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.should().add(QueryBuilders.termQuery("title", query).boost(100.0f));
		boolQueryBuilder.should()
				.add(QueryBuilders.matchQuery("title_text", query).operator(Operator.AND));

		// creating HighlightBuilder  added
		HighlightBuilder highlightBuilder = new HighlightBuilder();
		// create a filed javaapi.client.highlighter for the title field
		HighlightBuilder.Field highlightTitle = new HighlightBuilder.Field("title_text");
		// set field javaapi.client.highlighter type
		highlightTitle.highlighterType("unified");
		highlightBuilder.field(highlightTitle);


		SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
		searchSourceBuilder.query(boolQueryBuilder);

		// sets
		searchSourceBuilder.highlighter(highlightBuilder);

		searchSourceBuilder.from(COUNT_PER_PAGE * (page - 1));
		searchSourceBuilder.size(COUNT_PER_PAGE);
		String[] includes = {"createdDate", "postId", "fileId", "title", "content", "writer", "noticeYn", "deleteYn", "savedFilename", "modifiedDate", "deletedDate"};

		searchSourceBuilder.fetchSource(includes, null);

		SearchRequest searchRequest = new SearchRequest(POST_INDEX);
		searchRequest.source(searchSourceBuilder);

		return searchRequest;
	}

//	SearchRequest createTitleAuthorSearchRequest(String query, int page) {
	SearchRequest createTitleWriterSearchRequest(String query, int page) {

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.should().add(QueryBuilders.matchQuery("title_text", query));
//		boolQueryBuilder.should().add(QueryBuilders.matchQuery("author_text", query).boost(10.0f));
		boolQueryBuilder.should().add(QueryBuilders.matchQuery("writer_text", query).boost(10.0f));


		// creating HighlightBuilder  added
		HighlightBuilder highlightBuilder = new HighlightBuilder();
		// create a filed javaapi.client.highlighter for the title field
		HighlightBuilder.Field highlightTitle = new HighlightBuilder.Field("title_text");
		// set field javaapi.client.highlighter type
		highlightTitle.highlighterType("unified");
		highlightBuilder.field(highlightTitle);

		// creating HighlightBuilder  added
		// create a filed javaapi.client.highlighter for the writer field
		HighlightBuilder.Field highlightWriter = new HighlightBuilder.Field("writer_text");
		// set field javaapi.client.highlighter type
		highlightWriter.highlighterType("unified");
		highlightBuilder.field(highlightWriter);



		SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
		searchSourceBuilder.query(boolQueryBuilder);

		// sets
		searchSourceBuilder.highlighter(highlightBuilder);

		searchSourceBuilder.from(COUNT_PER_PAGE * (page - 1));
		searchSourceBuilder.size(COUNT_PER_PAGE);
		String[] includes = {"createdDate", "postId", "fileId", "title", "content", "writer", "noticeYn", "deleteYn", "savedFilename", "modifiedDate", "deletedDate"};

		searchSourceBuilder.fetchSource(includes, null);

		SearchRequest searchRequest = new SearchRequest(POST_INDEX);
		searchRequest.source(searchSourceBuilder);

		return searchRequest;
	}

	// 추가 (title, content)
	SearchRequest createTitleContentSearchRequest(String query, int page) {

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//		boolQueryBuilder.should().add(QueryBuilders.matchQuery("title_text", query));
//		boolQueryBuilder.should().add(QueryBuilders.matchQuery("author_text", query).boost(10.0f));  // boost

		boolQueryBuilder.should().add(QueryBuilders.matchQuery("title_text", query).boost(10.0f));
		boolQueryBuilder.should().add(QueryBuilders.matchQuery("content_text", query));

		// creating HighlightBuilder  added
		HighlightBuilder highlightBuilder = new HighlightBuilder();
		// create a filed javaapi.client.highlighter for the title field
		HighlightBuilder.Field highlightTitle = new HighlightBuilder.Field("title_text");
		// set field javaapi.client.highlighter type
		highlightTitle.highlighterType("unified");
		highlightBuilder.field(highlightTitle);

		// creating HighlightBuilder  added
//		HighlightBuilder highlightBuilder = new HighlightBuilder();
		// create a filed javaapi.client.highlighter for the cotent field
		HighlightBuilder.Field highlightContent = new HighlightBuilder.Field("content_text");
		// set field javaapi.client.highlighter type
		highlightContent.highlighterType("unified");
		highlightBuilder.field(highlightContent);


		SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
		searchSourceBuilder.query(boolQueryBuilder);

		// sets
		searchSourceBuilder.highlighter(highlightBuilder);

		searchSourceBuilder.from(COUNT_PER_PAGE * (page - 1));
		searchSourceBuilder.size(COUNT_PER_PAGE);

		String[] includes = {"createdDate", "postId", "fileId", "title", "content", "writer", "noticeYn", "deleteYn", "savedFilename", "modifiedDate", "deletedDate"};

		searchSourceBuilder.fetchSource(includes, null);

		SearchRequest searchRequest = new SearchRequest(POST_INDEX);
		searchRequest.source(searchSourceBuilder);

		return searchRequest;
	}

	// 추가 (title, Writer, content)
	SearchRequest createTitleWriterContentSearchRequest(String query, int page) {

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//		boolQueryBuilder.should().add(QueryBuilders.matchQuery("title_text", query));
//		boolQueryBuilder.should().add(QueryBuilders.matchQuery("author_text", query).boost(10.0f));  // boost

		boolQueryBuilder.should().add(QueryBuilders.matchQuery("title_text", query).boost(5.0f));
		boolQueryBuilder.should().add(QueryBuilders.matchQuery("writer_text", query).boost(10.0f));
		boolQueryBuilder.should().add(QueryBuilders.matchQuery("content_text", query));


		// creating HighlightBuilder  added
		HighlightBuilder highlightBuilder = new HighlightBuilder();
		// create a filed javaapi.client.highlighter for the title field
		HighlightBuilder.Field highlightTitle = new HighlightBuilder.Field("title_text");
		// set field javaapi.client.highlighter type
		highlightTitle.highlighterType("unified");
		highlightBuilder.field(highlightTitle);

		// creating HighlightBuilder  added
		// create a filed javaapi.client.highlighter for the cotent field
		HighlightBuilder.Field highlightContent = new HighlightBuilder.Field("content_text");
		// set field javaapi.client.highlighter type
		highlightContent.highlighterType("unified");
		highlightBuilder.field(highlightContent);

		// creating HighlightBuilder  added
		// create a filed javaapi.client.highlighter for the writer field
		HighlightBuilder.Field highlightWriter = new HighlightBuilder.Field("writer_text");
		// set field javaapi.client.highlighter type
		highlightWriter.highlighterType("unified");
		highlightBuilder.field(highlightWriter);


		SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
		searchSourceBuilder.query(boolQueryBuilder);

		// sets
		searchSourceBuilder.highlighter(highlightBuilder);

		searchSourceBuilder.from(COUNT_PER_PAGE * (page - 1));
		searchSourceBuilder.size(COUNT_PER_PAGE);

		String[] includes = {"createdDate", "postId", "fileId", "title", "content", "writer", "noticeYn", "deleteYn", "savedFilename", "modifiedDate", "deletedDate"};

		searchSourceBuilder.fetchSource(includes, null);

		SearchRequest searchRequest = new SearchRequest(POST_INDEX);
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
//		String[] includes = {"createdDate", "postId", "fileId", "title", "content", "writer", "noticeYn", "deleteYn", "savedFilename", "modifiedDate", "deletedDate"};
		searchSourceBuilder.fetchSource(includes, null);

		SearchRequest searchRequest = new SearchRequest(POST_INDEX);
		searchRequest.source(searchSourceBuilder);

		return searchRequest;
	}

//	SearchRequest createChosungSearchRequest(String query, int page, String[] includes) {
	SearchRequest createFcSearchRequest(String query, int page, String[] includes) {
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.should()
//				.add(QueryBuilders.matchQuery("title_chosung", query.replaceAll("\\s+", "")).boost(10.0f));
//		boolQueryBuilder.should().add(QueryBuilders.matchQuery("title_chosung", query));
				.add(QueryBuilders.matchQuery("title_fc", query.replaceAll("\\s+", "")).boost(10.0f));
		boolQueryBuilder.should().add(QueryBuilders.matchQuery("title_fc", query));
		SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
		searchSourceBuilder.query(boolQueryBuilder);
		searchSourceBuilder.from(COUNT_PER_PAGE * (page - 1));
		searchSourceBuilder.size(AUTO_COMPLETE_LIMIT);
		searchSourceBuilder.fetchSource(includes, null);

		SearchRequest searchRequest = new SearchRequest(POST_INDEX);
		searchRequest.source(searchSourceBuilder);

		return searchRequest;
	}

	SearchRequest createKoToEnSearchRequest(String query, int page, String[] includes) {

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.should()
//				.add(QueryBuilders.matchQuery("title_hantoeng", query.replaceAll("\\s+", "")).boost(10.0f));
//		boolQueryBuilder.should().add(QueryBuilders.matchQuery("title_hantoeng", query));
				.add(QueryBuilders.matchQuery("title_kotoen", query.replaceAll("\\s+", "")).boost(10.0f));
		boolQueryBuilder.should().add(QueryBuilders.matchQuery("title_kotoen", query));
		SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
		searchSourceBuilder.query(boolQueryBuilder);
		searchSourceBuilder.from(COUNT_PER_PAGE * (page - 1));
		searchSourceBuilder.size(AUTO_COMPLETE_LIMIT);
		searchSourceBuilder.fetchSource(includes, null);

		SearchRequest searchRequest = new SearchRequest(POST_INDEX);
		searchRequest.source(searchSourceBuilder);

		return searchRequest;
	}

	SearchRequest createEnToKoSearchRequest(String query, int page, String[] includes) {
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.should()
//				.add(QueryBuilders.matchQuery("title_engtohan", query.replaceAll("\\s+", "")).boost(10.0f));
//		boolQueryBuilder.should().add(QueryBuilders.matchQuery("title_engtohan", query));
				.add(QueryBuilders.matchQuery("title_entoko", query.replaceAll("\\s+", "")).boost(10.0f));
		boolQueryBuilder.should().add(QueryBuilders.matchQuery("title_entoko", query));

		// creating HighlightBuilder  added
		HighlightBuilder highlightBuilder = new HighlightBuilder();
		// create a filed javaapi.client.highlighter for the title field
		HighlightBuilder.Field highlightTitle = new HighlightBuilder.Field("title_entoko");
		// set field javaapi.client.highlighter type
		highlightTitle.highlighterType("unified");
		highlightBuilder.field(highlightTitle);


		SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
		searchSourceBuilder.query(boolQueryBuilder);

		// sets
		searchSourceBuilder.highlighter(highlightBuilder);

		searchSourceBuilder.from(COUNT_PER_PAGE * (page - 1));
		searchSourceBuilder.size(AUTO_COMPLETE_LIMIT);
		searchSourceBuilder.fetchSource(includes, null);

		SearchRequest searchRequest = new SearchRequest(POST_INDEX);
		searchRequest.source(searchSourceBuilder);

		return searchRequest;
	}

	/*
	 * BookSearchResponseDto createBookSearchResponseDto(SearchResponse response,
	 * String stage) { BookSearchResponseDto responseDto = new
	 * BookSearchResponseDto(); responseDto.setResult("OK");
	 * responseDto.setSearchHitStage(stage);
	 * responseDto.setTotalHits(response.getHits().getTotalHits().value);
	 *
	 * SearchHit[] hits = response.getHits().getHits(); List<Book> searchedBooks =
	 * new ArrayList<>(); for (SearchHit hit : hits) { try { Book book =
	 * objectMapper.readValue(hit.getSourceAsString(), Book.class);
	 * searchedBooks.add(book); } catch (JsonProcessingException e) {
	 * log.error("Json Parse Exception in parsing searched book. book=" +
	 * hit.getSourceAsString(), e); } }
	 *
	 * responseDto.setBooks(searchedBooks);
	 *
	 * return responseDto; }
	 */
	PagingResponse<PostResponse> createPostSearchResponse(SearchResponse response, String stage) {
//		BookSearchResponseDto responseDto = new BookSearchResponseDto();
//		responseDto.setResult("OK");
//		responseDto.setSearchHitStage(stage);
//		responseDto.setTotalHits(response.getHits().getTotalHits().value);
		PagingResponse<PostResponse> postResponse = new PagingResponse<PostResponse>(Collections.emptyList(), null);
		postResponse.setResult("OK");
		postResponse.setSearchHitStage(stage);
		postResponse.setTotalHits(response.getHits().getTotalHits().value);

		SearchHit[] hits = response.getHits().getHits();

//		List<Book> searchedBooks = new ArrayList<>();
		List<PostResponse> list = new ArrayList<>();

		for (SearchHit hit : hits) {
			try {
//				Book book = objectMapper.readValue(hit.getSourceAsString(), Book.class);
//				searchedBooks.add(book);
				PostResponse post = objectMapper.readValue(hit.getSourceAsString(), PostResponse.class);

				// highlight add
				Map<String, HighlightField> highlightFieldMap = hit.getHighlightFields();
				if(!highlightFieldMap.isEmpty()) {
					for (Map.Entry<String, HighlightField> entry : highlightFieldMap.entrySet()) {
						post.getHighlightsMap()
						.put(entry.getKey(), Arrays.stream(entry.getValue().getFragments()).map(Text::toString).collect(Collectors.toList()));
					}
				}

				log.debug("stage ("  + stage + ") => post.title: " + post.getTitle());
				// log.debug("stage ("  + stage + ") => post.content: " + post.getContent());
				// title_text, content_text
				for (Map.Entry<String, List<String>> entrySet : post.getHighlightsMap().entrySet()) {
					//System.out.println(entrySet.getKey() + " : " + entrySet.getValue());
					log.debug("                       => "+ entrySet.getKey() +" : " + entrySet.getValue());
					List<String> newList = new ArrayList<>();
					for (String v : entrySet.getValue()) {

						newList.add(replace(v));
					}
					entrySet.setValue(newList);
					//String list,  for entrySet.getValue().get(0);
				}


				list.add(post);
			} catch (JsonProcessingException e) {
				log.error("Json Parse Exception in parsing searched Post(or File) info. Post(or File)=" + hit.getSourceAsString(), e);
			}
		}

//		responseDto.setBooks(searchedBooks);
		postResponse.setList(list);

//		return responseDto;
		return postResponse;
	}


	private String replace (String s) {
		String str = "";

		if(s == null || "".equals(s)) {
			return s;
		}

		str = s.replaceAll("<em>", "<font size='4' color='#24A6BD' style='italic' weight='600'>");
		str = str.replaceAll("</em>", "</font>");

		return str;
	}

	SuggestResponseDto createSuggestResponseDto(SearchResponse response) {

		SuggestResponseDto responseDto = new SuggestResponseDto();
		responseDto.setResult("OK");
		responseDto.setTitles(Arrays.stream(response.getHits().getHits())
				.map(hit -> hit.getSourceAsMap().get((String) "title").toString())
				.collect(Collectors.toList()));

		return responseDto;
	}


	// add
	PostSuggestResponse createTitleSuggestResponse(SearchResponse response) {

		PostSuggestResponse suggestResponse = new PostSuggestResponse();
		suggestResponse.setResult("OK");
		suggestResponse.setTexts(Arrays.stream(response.getHits().getHits())
				.map(hit -> hit.getSourceAsMap().get((String) "title").toString())
				.collect(Collectors.toList()));

		return suggestResponse;
	}




















/////////////////////////////// ElasticSearch API 종류 (2가지) /////////////////////////////////////////////////////////////////////////////////////
// 1. Java Low Level REST Client
//	ElasticSearch에서 제공하는 Low Level Java 통신 SDK 입니다. http 요청을 만들 수 있는 라이브러리입니다. ElasticSearch 버전에 종속되지 않고 좀 더 유연하게 사용할 수 있다는 장점이 있습니다.
//	Request request = new Request("GET", "/");
//	Response response = restClient.performRequest(request);

// 2. Java High Level REST Client
//	Java High Level REST Client는 Java Low Level REST Client를 한번 추상화 한 라이브러리입니다.
//	Rest API가 아닌 이미 정의되어있는 코드를 통해서 요청하는 것을 기본으로합니다. 아래 예제를 보겠습니다. 아래 예제는 index에 하나의 document를 생성하는 메서드 중 일부를 가져온 부분입니다.
//	IndexRequest request = new IndexRequest(index)
//	        .id(id)
//	        .source(jsonBody, XContentType.JSON);
//
//	client.index(request, RequestOptions.DEFAULT);

// 3. 환경 설정 (yml)
//	es:
//		  url: localhost
//		  port: 9200

//	RestHighLevelClient 객체를 만듭니다. 해당 객체가 실제 통신에 사용하는 객체입니다. 일반적인 통신에  사용하는 restTemplate을 생각해주시면 됩니다.
//	아래와 같이 선언합니다. ElasticSearch는 여러 노드를 등록할 수 있으며 HttpHost를 추가해주시면 됩니다. HttpHost의 파라미터로는 hostname, port 그리고 http 또는 https 사용의 스키마가 들어갑니다.

//	@Configuration
//	public class ElasticSearchConfig {
//
//	    @Value("${oci.es.url}")
//	    private String hostname; // localhost
//
//	    @Value("${oci.es.port}")
//	    private Integer port; // 9200
//
//	    @Bean
//	    public RestHighLevelClient restHighLevelClient() {
//	        return new RestHighLevelClient(RestClient.builder(new HttpHost(hostname, port, "http")));
//	    }
//	}

//	high level client 모듈은 내부에 low lovel client를 감싸고 있습니다. 사용이 끝난 클라이언트는 close 메서드를 통해 닫아주어야 정확한 리소스 반환이 이루어집니다. 아래와 같이 사용할 수 있습니다.
//	private final RestHighLevelClient client;

// 4. 인덱스 생성
//	ElasticSearch를 사용하기 위해서는 DataBase에 Table에 해당하는 인덱스를 먼저 만들어주어야 합니다. Index는 아래처럼 만들 수 있습니다.
//	CreateIndexRequest 를 이용하여 요청할 내용을 담을 수 있는 모델을 만듭니다. 옵션을 Settings를 통해서 넣을 수 있습니다.
//	아래 세팅은 인덱스의 샤드(데이터 노드 분할 수)의 숫자와 레플리카(복사본)의 숫자압니다. request용 모델을 만든 후 RestHighLevelClient 객체의 create 메서드를 호출하면 생성되게 됩니다.

//	String indexName = "game";
//	CreateIndexRequest request = new CreateIndexRequest(indexName);
//
//	request.settings(Settings.builder()
//	    .put("index.number_of_shards", 1)
//	    .put("index.number_of_replicas", 0)
//	);
//	client.indices().create(request, RequestOptions.DEFAULT);





/////////////////////////////// Document CRUD 만들기 /////////////////////////////////////////////////////////////////////////////////////
//	1. Document 생성
//	해당 프로토콜로 사용하면 랜덤 문자열 ID를 가지는 document가 하나 생성됩니다.
//
//	POST /{index}/_doc
//	{Json Body}

//	위 프로토콜을 직접 사용하면 아래와 같습니다. 이렇게 Rest API를 사용하면 HTTP status 코드 201(Created)과 함께 응답메시지를 리턴 받습니다. 응답내용의 _id값을 보시면 랜덤 문자열이 있는것을 알 수 있습니다.

//	만약 _id값이 겹친다면 기존 정보가 수정되는 것을 유의하시기 바랍니다. 이때는 아래처럼 rest API를 사용해주면 됩니다.
//	POST /{index}/_doc/{id}

//	public void createDocument( @NotNull String index, @NotNull String id, @NotNull String jsonBody) throws IOException {
//		IndexRequest request = new IndexRequest(index)
//		    .id(id)
//		    .source(jsonBody, XContentType.JSON);
//
//		client.index(request, RequestOptions.DEFAULT);
//		}

// 2. Document 읽기
//	그렇다면 이번에는 ElasticSearch의 document를 읽어보도록 하겠습니다. Rest API는 아래와 같습니다. JsonBody는 필요하지 않습니다.
//
//	GET /{index}/_doc/{document_id}
//	Rest API를 직접한번 사용해보겠습니다. 동작은 아래와 같습니다. 응답값의 _source를 보시면 Document가 정상적으로 생성되었다라는 사실을 알 수 있습니다.

//	public GetResponse getDocument(String index, String id) throws IOException {
//	    GetRequest request = new GetRequest(index, id);
//	    return client.get(request, RequestOptions.DEFAULT);
//	}


// 3. Document 부분 수정
//	document를 업데이트하기 위해서는 아래와 같은 Rest API를 사용하여야합니다.
//	Update를 하기 위해서는 해당 ID와 업데이트하고자 하는 필드가 json으로 주어져야합니다.	부분 업데이트를 진행하기 위한 Rest API는 아래와 같습니다.
//
//	POST /{index}/_doc/{document_id}/_update
//	{
//	    "doc" : {
//	        // 부분 업데이트 fields : values
//	    }
//	}
//
//	POST http://localhost:9200/game/_doc/test/_update
//	{
//	    "doc" : {
//	      "content" : "테스트당"
//	    }
//	}
//	아래는 다시 test ID를 GET을 했을 때 결과입니다. 업데이트한 content만 변경된것을 확인할 수 있습니다. 또한 _version 피드의 숫자값이 올라간 것도 알 수 있었습니다.

//	public void updateDocument( @NotNull String index, @NotNull String id, @NotNull Map<String, Object> bodyMap) throws IOException {
//		    UpdateRequest request = new UpdateRequest(index, id)
//		        .doc(bodyMap);
//
//		    client.update(request, RequestOptions.DEFAULT);
//		}
//	String, Object의 Map을 받아 doc으로 세팅해줍니다. bodyMap은 변경하고자 하는 key, value를 세팅해줍니다. 그리고 client의 update 메서드를 이용하면 업데이트를 할 수 있습니다.



//	4. delete Document
//	마지막으로 삭제에 대해서 알아보도록 하겠습니다. 삭제는 DELETE 메서드를 이용합니다. Body는 없이 사용합니다.
//
//	DELETE /{index}/_doc/{document_id}
//	실제로 사용하면 아래처럼 사용할 수 있으며 응답값을 보시면 _version이 올라갔으며, result로 deleted된 것을 알 수 있었습니다.

//	public void deleteDocument(String index, String id) throws IOException {
//		  DeleteRequest request = new DeleteRequest(index, id);
//		  client.delete(request, RequestOptions.DEFAULT);
//		}



/////////////////////////////// 기본적인 검색 만들기 /////////////////////////////////////////////////////////////////////////////////////
	// https://sabarada.tistory.com/158
	// 기본적인 검색에는 아래와 같은 명령어가 사용됩니다. {json body} 부분은 일반적으로 조건을 나타내며 이 부분은 옵셔널이며 생략이 가능합니다.
	// 생략했을 때는 해당 인덱스의 모든 document 중 먼저 입력되었던 값 10개를 가져오게 됩니다.

	// SDK 를 이용하여 search 요청 만들기
	// 1. SearchSourceBuilder 객체를 생성합니다.
	// 2. BoolQueryBuilder 객체를 생성합니다. ( 옵션, 조건을 추가할 때 사용 )
	// 3. 생성한 BoolQueryBuilder를 SearchSourceBuilder에 추가합니다.
	// 4. SearchRequest를 생성하며 검색을 진행할 index와 SearchSourceBuilder를 추가합니다.
	// 5. RestHighLevelClient로 searchRequest를 이용하여 검색을 진행합니다.

	// 검색 기본
	// POST /<index>/_serach

//	SearchRequest createSearchAll_ref_1(String query, int page, String[] includes) {
//
//		SearchRequest searchRequest =
//			    new SearchRequest(POST_INDEX)
//			        .source(new SearchSourceBuilder(   // 예제 그대로는 여기생성자가 없다네
//			            // QueryBuilders 추가 가능
//			            QueryBuilders.boolQuery()
//			              .should(QueryBuilders.wildcardQuery("title", "*" + query + "*"))
//			              .should(QueryBuilders.wildcardQuery("content", "*" + query + "*"))
//			        ));
//
//		// return esClient.search(searchRequest_ref, RequestOptions.DEFAULT);
//
//		return searchRequest;
//	}

	SearchRequest createSearchAll_ref_1(String query, int page, String[] includes) {

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.should().add(QueryBuilders.wildcardQuery("title", "*" + query.replaceAll("\\s+", "") + "*"));
		boolQueryBuilder.should().add(QueryBuilders.wildcardQuery("content", "*" + query.replaceAll("\\s+", "") + "*"));

		SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
		searchSourceBuilder.query(boolQueryBuilder);
		searchSourceBuilder.from(COUNT_PER_PAGE * (page - 1));
		searchSourceBuilder.size(AUTO_COMPLETE_LIMIT);
		searchSourceBuilder.fetchSource(includes, null);

		SearchRequest searchRequest = new SearchRequest(POST_INDEX);
		searchRequest.source(searchSourceBuilder);

		return searchRequest;
	}

	// 검색 조건
	// http 요청에 body를 추가하여 조건을 줘보도록 하겠습니다.
	// 다양한 조건이 있지만 검색에 대한 조건은 query 부분을 확인해주시면됩니다.
	// 아래 요청은 title 필드에 퀘스트라는 단어가 들어가 있는 조건을 만족하는 경우를 리턴하라는 요청입니다.
//	POST /<index>/_serach
//	{
//	  "from": 0,
//	  "size": 20,
//	  "sort": {
//	    "_score": "desc"
//	  },
//	  "query" : {
//	      "bool" : {
//	          "must" : [
//	              {
//	                  "term" : {"title" : "퀘스트"}
//	              }
//	          ]
//	      }
//	  }
//	}


	SearchRequest createSearchAll_ref_2(String query, int page, String[] includes) {

//		SearchRequest searchRequest =
//			    new SearchRequest(index)
//			        .source(new SearchSourceBuilder(
//			            QueryBuilders.boolQuery()
//			              .must(QueryBuilders.termQuery("title", "퀘스트"))
//			        ));

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
				.must(QueryBuilders.termQuery("title", "퀘스트"));

		SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
		searchSourceBuilder.query(boolQueryBuilder);
		searchSourceBuilder.from(COUNT_PER_PAGE * (page - 1));
		searchSourceBuilder.size(AUTO_COMPLETE_LIMIT);
		searchSourceBuilder.fetchSource(includes, null);

		SearchRequest searchRequest = new SearchRequest(POST_INDEX);
		searchRequest.source(searchSourceBuilder);

		return searchRequest;
	}


	// 또 하나의 예제를 보도록 하겠습니다.
	// 위 검색은 퀘스트라는 단어가 포함 되어있어야하는데 와일드카드 형식은 아닙니다.
	// 즉, 퀘스트하나 이런식으로 퀘스트라는 단어에 다른 단어가 이어져 있으면 토크나이즈되어있지 않아 가져오지 못합니다.
	// 따라서 DB의 like 검색 처럼 와일드카드를 사용할 수 있습니다. http 요청은 아래와 같습니다.
	// 퀘스트 뿐만 아니라 라스트의 결과값도 가져온다.

//	POST /<index>/_serach
//	{
//	  "from": 0,
//	  "size": 20,
//	  "sort": {
//	    "_score": "desc"
//	  },
//	  "query" : {
//	      "wildcard" : {
//	          "title" : {
//	              "value" : "*스트"
//	          }
//	      }
//	  }
//	}

	SearchRequest createSearchAll_ref_3(String query, int page, String[] includes) {

//		SearchRequest searchRequest =
//			    new SearchRequest(index)
//			        .source(new SearchSourceBuilder(
//			            QueryBuilders.boolQuery()
//			              .must(QueryBuilders.wildcardQuery("title", "*스트"))
//			        ));

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
				.must(QueryBuilders.wildcardQuery("title", "*스트"));

		SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource();
		searchSourceBuilder.query(boolQueryBuilder);
		searchSourceBuilder.from(COUNT_PER_PAGE * (page - 1));
		searchSourceBuilder.size(AUTO_COMPLETE_LIMIT);
		searchSourceBuilder.fetchSource(includes, null);

		SearchRequest searchRequest = new SearchRequest(POST_INDEX);
		searchRequest.source(searchSourceBuilder);

		return searchRequest;
	}




}
