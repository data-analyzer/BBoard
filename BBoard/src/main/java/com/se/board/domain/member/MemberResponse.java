package com.se.board.domain.member;

import java.time.LocalDate;

import lombok.Getter;

@Getter
public class MemberResponse {
	private Long id;					// 회원 번호 (PK)
	private String loginId;				// 로그인 ID
	private String password;			// 비밀번호
	private String name;				// 이름
	private Gender gender;				// 성별
	private LocalDate birthday;			// 생일
	private Boolean deleteYn;			// 삭제 여부
	private LocalDate createdDate;		// 생성일시
	private LocalDate modifiedDate;		// 최종 수정일시

	public void clearPassword() {
		this.password = "";
	}
}
