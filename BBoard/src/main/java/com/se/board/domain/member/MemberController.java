package com.se.board.domain.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {

	@Autowired
	private MemberService memberService;

	// 로그인 페이지
	@GetMapping("/login.do")
	public String openLogin() {
		return "member/login";
	}

	// 회원 정보 저장 (회원 가입)
	@PostMapping("/members")
	@ResponseBody
	public Long saveMember(@RequestBody final MemberRequest params) {
		return memberService.saveMember(params);
	}

	// 회원 상세정보 조회
	@GetMapping("/members/{loginId}")
	@ResponseBody
	public MemberResponse findMemberByLoginId (@PathVariable("loginId") final String loginId) {
		return memberService.findMemberByLoginId(loginId);
	}

	// 회원 정보 수정
	@PatchMapping("/members/{id}")
	@ResponseBody
	public Long updateMember(@PathVariable("id") final Long id, @RequestBody final MemberRequest params) {
		return memberService.updateMember(params);
	}

	// 회원 정보 삭제 (회원 탈퇴)
	@DeleteMapping("/members/{id}")
	@ResponseBody
	public Long deleteMember(@PathVariable("id") final Long id) {
		return memberService.deleteMember(id);
	}

	// 회원 수 카운팅 (ID 중복 체크)
	@GetMapping("/member-count")
	@ResponseBody
	public int countMemberByLoginId(@RequestParam("loginId") final String loginId) {
		return memberService.countMemberByLoingId(loginId);
	}

	// 로그인
	@PostMapping("/login")
	@ResponseBody
	public MemberResponse login(HttpServletRequest request) {

		// 1. 회원 정보 조회
		String loginId = request.getParameter("loginId");
		String password = request.getParameter("password");

		log.debug(" loginId : " + loginId);
		log.debug(" password : " + password);
		MemberResponse member = memberService.login(loginId, password);

		// 2. 세션에 회원 정보 저장 & 세션 유지 시간 설정
		if(member != null) {
			HttpSession session = request.getSession();
			session.setAttribute("loginMember", member);
			session.setMaxInactiveInterval(60*30); // 30분
		}

		return member;
	}

	// 로그아웃
	@PostMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/login.do";
	}

}
