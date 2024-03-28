package com.se.board.domain.post;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.se.board.common.MessageDto;
import com.se.board.common.dto.SearchDto;
import com.se.board.common.file.FileUtils;
import com.se.board.common.paging.Pagination;
import com.se.board.common.paging.PagingResponse;
import com.se.board.domain.es.EsBulkData;
import com.se.board.domain.file.FileRequest;
import com.se.board.domain.file.FileResponse;
import com.se.board.domain.file.FileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Controller
@RequiredArgsConstructor
public class PostController {
	private final PostService postService;

	private final FileService fileService;
	private final FileUtils fileUtils;
	private final PostEsService postEsService;
//	private final EsService esService;

//	private final String POST_INDEX = "ingest-test-v1";
	private final String POST_PIPELINE = "attachment";

	// ES 테스트
	@GetMapping("/search/search.do")
	public String openSearchList(@ModelAttribute("params") final SearchDto params, Model model) {
		//PagingResponse<PostResponse> response = postService.findAllPost(params);
		Pagination pagination = new Pagination(0, params);
		// params.setPagination(pagination);

		// 계산된 페이지 정보의 일부(limitStart, recordSize)를 기준으로 리스트 조회 후 응답 데이터 반환
		List<PostResponse> list = Collections.emptyList();
		PagingResponse<PostResponse> response = new PagingResponse<>(list, pagination);
		model.addAttribute("response", response);
		return "search/search";
	}

	// 게시글 작성 페이지
	@GetMapping("/post/write.do")
	public String openPostWrite(@RequestParam(value = "id", required = false) final Long id, Model model) {
		if(id != null) {
			PostResponse post = postService.findPostById(id);
			model.addAttribute("post", post);
		}

		return "post/write";
	}

	// redirect 하는 곳 : 신규 게시글 생성, 수정, 삭제 에서 showMessageAndRedirect() 호출하도록 변경
	@PostMapping("/post/save.do")
	public String savePost(final PostRequest params, Model model) {

		Long postId = postService.savePost(params);																	// 1. 게시글 INSERT
		List<FileRequest> files = fileUtils.uploadFiles(params.getFiles());											// 2. 디스크에 파일 업로드
		// fileService.saveFiles(postId, files);																		// 3. 업로드 된 파일 정보를 DB에 저장
		// 위 list 로 foreach 인서트 후에 첫번째 id 만 받아오고 나머지 id가 null 안 오류가 있오서 임시로 변경함
		fileService.saveFilesEach(postId, params.getWriter(), files);

		// ES bulk
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.KOREAN);
		String strDate = format.format(new Date());

		List<EsBulkData> bulkList = postEsService.makePostBulkData(EsBulkData.Type.CREATE, strDate, params);
		postEsService.bulkDocument(bulkList, null);

		// FileResponse 로 해야 , 또는 동일하게 날짜이용 (Paths)
		// files 에 File, b64Str 을 추가해 갖고 있게 하자
		bulkList.clear();
		if ( !files.isEmpty()) {
			fileUtils.encodeFiles(files);       			//postEsService.indexPost(files);
			bulkList = postEsService.makeFilesDocMap(EsBulkData.Type.CREATE, strDate, files, params);
		}

		if(bulkList != null &&  !CollectionUtils.isEmpty(bulkList)) {
			postEsService.bulkDocument(bulkList, POST_PIPELINE);
		}

		MessageDto message = new MessageDto("게시글 생성이 완료되었습니다.", "/post/list.do", RequestMethod.GET, null);
		return showMessageAndRedirect(message, model);
	}

	// 게시글 리스트 페이지
	// PagingResponse로 변경
	@GetMapping("/post/list.do")
	public String openPostList(@ModelAttribute("params") final SearchDto params, Model model) {
		PagingResponse<PostResponse> response = postService.findAllPost(params);
		model.addAttribute("response", response);
		return "post/list";
	}

	// 위 게시글 리스트 페이지는 하나로 사용하되 내용을 전체 List, 검색 List로 분리하자 (keyword 값에 따라 서비스에서 분리할까?)
	@GetMapping("/post/searchlist.do")
	public String openPostSearchList(@ModelAttribute("params") final SearchDto params, Model model) {
		PagingResponse<PostResponse> response = postService.findAllPost(params);
		model.addAttribute("response", response);
		return "post/list";
	}

	// 게시글 상세 페이지
	@GetMapping("/post/view.do")
	public String openPostView(@RequestParam(value = "id") final Long id, @RequestParam(value = "fileId") final Long fileId, Model model) {
		PostResponse post;

		log.debug("fileId:::"+fileId);
		log.debug("id:::"+id);


//		if(fileId != null) {
//			fId = Long.parseLong(fileId);
//		}

		if (fileId > 0 ) {
			post = postEsService.getById(id + "_" + fileId);
		} else {
			post = postService.findPostById(id);
		}

		model.addAttribute("post", post);
		return "post/view";
	}

	// 기존 게시글 수정
	@PostMapping("/post/update.do")
	public String updatePost(final PostRequest params, final SearchDto queryParams, Model model) {
		// 1. 게시글 정보 수정
		postService.updatPost(params);

		// 2. 파일 업로드 (to Disk)
		List<FileRequest> uploadFiles = fileUtils.uploadFiles(params.getFiles());

		// 3. 파일 정보 저장 (to DB)
		fileService.saveFiles(params.getId(), uploadFiles);

		// ES 저장

		// 4. 삭제할 파일 정보 조회 (from DB)
		List<FileResponse> deleteFiles = fileService.findAllFileByIds(params.getRemoveFileIds());

		// 5. 파일 삭제 (from Disk)
		fileUtils.deleteFiles(deleteFiles);

		// 6. 파일 삭제 (from DB)
		fileService.deleteAllFileByIds(params.getRemoveFileIds());

		// ES 삭제
		MessageDto message = new MessageDto("게시글 수정이 완료되었습니다.", "/post/list.do", RequestMethod.GET, queryParamsToMap(queryParams));
		return showMessageAndRedirect(message, model);
	}

	// 게시글 삭제
	@PostMapping("/post/delete.do")
	public String deletePost(@RequestParam(value = "id") final Long id, final SearchDto queryParams, Model model) {
		postService.deletePost(id);

// 댓글 정보 삭제 ?

// 파일 삭제 ?
		// 1. 삭제할 파일 정보 조회 (from DB)
		List<FileResponse> deleteFiles = fileService.findAllFileByPostId(id);
		// 2. 파일 삭제 (from Disk)
		//fileUtils.deleteFiles(deleteFiles);
		// 3. 파일 삭제 (from DB)
		//fileService.deleteAllFileByPostId(id);


// ES 삭제 ? 일단, 실제 삭제하자

		// ES bulk
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.KOREAN);
		String strDate = format.format(new Date());

		List<EsBulkData> bulkList = postEsService.makeDeleteBulkData(EsBulkData.Type.DELETE, strDate, id, deleteFiles);
		if(bulkList != null) {
			postEsService.bulkDocument(bulkList, null);
		}

		MessageDto message = new MessageDto("게시글 삭제가 완료되었습니다.", "/post/list.do", RequestMethod.GET, queryParamsToMap(queryParams));
		return showMessageAndRedirect(message, model);
	}

	// 사용자에게 메시지를 전달하고, 페이지를 리다이렉트 한다.
	private String showMessageAndRedirect(final MessageDto params, Model model) {
		model.addAttribute("params", params);
		return "common/messageRedirect";
	}

	// 퀴리 스트링 파라미터를 Map에 담아 반환
	private Map<String, Object> queryParamsToMap(SearchDto queryParams) {
		Map<String, Object> data = new HashMap<>();
		data.put("page", queryParams.getPage());
		data.put("recordSize", queryParams.getRecordSize());
		data.put("pageSize", queryParams.getPageSize());
		data.put("keyword", queryParams.getKeyword());
		data.put("searchType", queryParams.getSearchType());

		return data;
	}

}
