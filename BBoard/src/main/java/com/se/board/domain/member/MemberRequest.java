package com.se.board.domain.member;

import java.time.LocalDate;

import org.apache.groovy.parser.antlr4.util.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberRequest {

	private Long id;					// 회원 번호 (PK)
	private String loginId;				// 로그인 ID
	private String password;			// 비밀번호
	private String name;				// 이름
	private Gender gender;				// 성별
	private LocalDate birthday;			// 생일

	public void encodingPassword (PasswordEncoder passwordEncoder) {
		if(StringUtils.isEmpty(this.password)) {
			return;
		}
		this.password = passwordEncoder.encode(this.password);
	}
}
