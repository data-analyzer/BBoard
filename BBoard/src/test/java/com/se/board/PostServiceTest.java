package com.se.board;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.se.board.domain.post.PostRequest;
import com.se.board.domain.post.PostService;

@SpringBootTest
public class PostServiceTest {
	@Autowired
	PostService postService;

	@Test
	void save() {
		PostRequest params = new PostRequest();
		params.setTitle("1번 게시글 제목");
		params.setContent("1번 게시글 내용");
		params.setWriter("테스터");
		params.setNoticeYn(false);

		Long id = postService.savePost(params);
		System.out.println("생성된 게시글 ID : " + id);
	}

	@Test
	void saveByForeach() {
		for (int i = 1001; i <= 2000; i++) {
			PostRequest params = new PostRequest();
			params.setTitle(i + "번 게시글 제목");
			params.setContent(i + "번 게시글 내용");
			params.setWriter("테스트" + i);
			params.setNoticeYn(false);
			postService.savePost(params);
		}
	}

}
