package com.se.board;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.se.board.domain.comment.CommentRequest;
import com.se.board.domain.comment.CommentService;

@SpringBootTest
public class CommentServiceTest {
	@Autowired
	private CommentService commentService;

	@Test
	void saveByForeach () {
		for (int i = 1; i <= 100; i++) {
			CommentRequest params = new CommentRequest();
			params.setPostId(2000L);
			params.setContent(i + "번째 댓글입니다.");
			params.setWriter("테스터" + i);

			commentService.saveComment(params);
		}
	}
}
