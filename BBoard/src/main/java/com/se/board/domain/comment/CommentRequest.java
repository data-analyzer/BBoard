package com.se.board.domain.comment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
// 게시글 요청 클래스에서는 @Setter도 선언해서 사용함. 요청클래스의 각 멤버변수 = HTML 폼 태그에 선언된 필드(input, textarea 등)의 name 값 기준으로 파라미터를 전송, 요청 클래스의 set() 메소드에 의해 값이 매핑됨.
// 하지만 일반적인 REST API 방식에서는 데이터를 등록/수정할 때 폼 자체를 전송하지 않고, key-value 구조인 JSON 문자열 포맷으로 전송하기때문에 set() 메소드가 불필요함.(파일을 전송하는 경우는 제외)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC) // 기본생성자를 만들어 준다. access속성을 이용해 객체 생성을 protected로 제한 AccessLevel.PROTECTED (Setter 안쓰고..)
public class CommentRequest {

	private Long id;			// 댓글 번호 (PK)
	private Long postId;		// 게시글 번호 (FK)
	private String content;		// 내용
	private String writer;		// 작성자

}
