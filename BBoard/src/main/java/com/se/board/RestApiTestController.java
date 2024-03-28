package com.se.board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
// Spring v4부터는 @RestController 제공 (기본적으로 @ResponseBody가 모든 메소드에 기본 적용됨)
@Controller
public class RestApiTestController {

	@GetMapping("/members")
	@ResponseBody // public @ResponseBody List<Map<String, Object>> findAllMember 와 같이 리턴 타입 앞에도 선언 가능 (Spring v3부터)
	// 컨트롤러 메서드에 @ResponseBody가 붙으면, 스프링의 메시지 컨버터(Message Converter)에 의해 화면(HTML)이 아닌 리턴 타입에 해당하는 데이터 자체를 리턴
	public List<Map<String, Object>> findAllMember() {

		List<Map<String, Object>> members = new ArrayList<>();

		for (int i = 1; i <= 20; i++) {
			Map<String, Object> member = new HashMap<>();
			member.put("id", i);
			member.put("name", i + "번 개발자");
			member.put("age", 10 + i);
			members.add(member);
		}

		return members;
	}

}
