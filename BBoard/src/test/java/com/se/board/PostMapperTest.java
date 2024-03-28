package com.se.board;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.se.board.domain.post.PostMapper;
import com.se.board.domain.post.PostRequest;
import com.se.board.domain.post.PostService;

@SpringBootTest
public class PostMapperTest {
	@Autowired
	PostMapper postMapper;

//	@Test
//	void save() {
//		PostRequest params = new PostRequest();
//		params.setTitle("1번 게시글 제목");
//		params.setContent("1번 게시글 내용");
//		params.setWriter("테스터");
//		params.setNoticeYn(false);
//
//		postMapper.save(params);
//
//		List<PostResponse> posts = postMapper.findAll();
//		System.out.println("전제 게시글 개수는 : " + posts.size() + " 개 입니다.");
//	}
//
//	@Test
//	void findById() {
//		PostResponse post = postMapper.findById(1L);
//
//		try {
//			String postJson = new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(post);
//			System.out.println(postJson);
//		} catch (JsonProcessingException jpe) {
//			throw new RuntimeException(jpe);
//		}
//	}
//
//	@Test
//	void update() {
//		// 1. 게시글 수정
//		PostRequest params = new PostRequest();
//		params.setId(1L);
//		params.setTitle("1번 게시글 제목 수정합니다.");
//		params.setContent("1번 게시글 내용 수정합니다.");
//		params.setWriter("홀길동");
//		params.setNoticeYn(true);
//		postMapper.update(params);
//
//		// 2. 게시글 상세정보 조회
//		PostResponse post = postMapper.findById(1L);
//
//		try {
//			String postJson = new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(post);
//			System.out.println(postJson);
//		} catch (JsonProcessingException jpe) {
//			throw new RuntimeException(jpe);
//		}
//	}
//
//	@Test
//	void delete() {
//		System.out.println("삭제 이전의 전체 게시글 수는 : " + postMapper.findAll().size() + "개 입니다.");
//		postMapper.deleteById(1L);
//		System.out.println("삭제 이후의 전체 게시글 수는 : " + postMapper.findAll().size() + "개 입니다.");
//	}

}
