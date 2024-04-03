package com.se.board.common.book;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EsKoUtil {

	private EsKoUtil() {}

	private static final Map<Character, String> dualConsonantMap = new HashMap<>() {
		{
			put('ㄳ', "ㄱㅅ");
			put('ㄵ', "ㄴㅈ");
			put('ㄶ', "ㄴㅎ");
			put('ㄺ', "ㄹㄱ");
			put('ㄻ', "ㄹㅁ");
			put('ㄼ', "ㄹㅂ");
			put('ㄽ', "ㄹㅅ");
			put('ㄾ', "ㄹㅌ");
			put('ㄿ', "ㄹㅍ");
			put('ㅀ', "ㄹㅎ");
			put('ㅄ', "ㅂㅅ");
		}
		private static final long serialVersionUID = 1L;
	};

	public static String decomposeDualConsonant(String query) {
		StringBuilder queryBuilder = new StringBuilder();
		for (char c : query.toCharArray()) {
			if (dualConsonantMap.containsKey(c)) {
				queryBuilder.append(dualConsonantMap.get(c));
			} else {
				queryBuilder.append(c);
			}
		}

		return queryBuilder.toString();
	}

	public static boolean isCompleteKoQuery(String query) {
		int cnt = 0;
		for (char c : query.toCharArray()) {
			if (c >= '가' && c <= '힣') {
				cnt++;
			}
		}

		float completeKoRatio = (float) cnt / query.length();

		return completeKoRatio > 0.3;  // num, space, special char 가 포함되어 보다 정확히 할 필요가 있음 (일단 낮춤)
	}

	public static final Set<Character> fcSet = new HashSet<>() {
		{
			add(' ');
			add('ㄱ');
			add('ㄴ');
			add('ㄷ');
			add('ㄹ');
			add('ㅁ');
			add('ㅂ');
			add('ㅅ');
			add('ㅇ');
			add('ㅈ');
			add('ㅊ');
			add('ㅋ');
			add('ㅌ');
			add('ㅍ');
			add('ㅎ');
			add('ㄲ');
			add('ㄸ');
			add('ㅃ');
			add('ㅆ');
			add('ㅉ');
			add('ㄳ');
			add('ㄵ');
			add('ㄶ');
			add('ㄺ');
			add('ㄻ');
			add('ㄼ');
			add('ㄽ');
			add('ㄾ');
			add('ㄿ');
			add('ㅀ');
			add('ㅄ');
		}
		private static final long serialVersionUID = 1L;
	};

	public static boolean isFcQuery(String query) {
		for (char c : query.toCharArray()) {
			if (!fcSet.contains(c)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isEnQuery(String query) {
		for (char c : query.toCharArray()) {
			if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))) {
				return false;
			}
		}
		return true;
	}
}
