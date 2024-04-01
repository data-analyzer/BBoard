package com.ose.p.util;

import java.util.HashMap;
import java.util.Map;

public class ConsonantVowelUtil {

	// 초성 19개
	private final Map<Character, Integer> FIRST_CONSONANT_MAP = new HashMap<>(19);
	private final char[] FIRST_CONSONANT_LIST = {'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ',
            'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
	};


	// 중성 21개
	private final Map<Character, Integer> MIDDLE_VOWEL_MAP = new HashMap<>(21);
	private final char[] MIDDLE_VOWEL_LIST = {'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ', 'ㅙ',
            'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ', 'ㅣ'
	};


	// 종성 28개(없는 경우 - 공백 포함)
	private final Map<Character, Integer> LAST_CONSONANT_MAP = new HashMap<>(28);
	private final char[] LAST_CONSONANT_LIST = {' ', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ', 'ㄻ',
            'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
	};

	private final Map<String, String> DUAL_MIDDLE_VOWEL_MAP = new HashMap<>(7);
    private final Map<String, String> DUAL_LAST_CONSONANT_MAP = new HashMap<>(9);

    private final Map<Character, String> LAYER_CHARACTER_MAP = new HashMap<>(16);

    public ConsonantVowelUtil() {
    	initialize();
    }

    private void initialize() {
    	initializeFirstConsonantMap();
    	initializeMiddleConsonantMap();
    	initializeLastConsonantMap();
    	initializeDualMiddleConsonantMap();
    	initializeDualLastConsonantMap();
    	initializeLayerCharacterMap();

    }

    private void initializeLayerCharacterMap() {
        LAYER_CHARACTER_MAP.put('ㄳ', "ㄱㅅ");
        LAYER_CHARACTER_MAP.put('ㄵ', "ㄴㅈ");
        LAYER_CHARACTER_MAP.put('ㄶ', "ㄴㅎ");
        LAYER_CHARACTER_MAP.put('ㄺ', "ㄹㄱ");
        LAYER_CHARACTER_MAP.put('ㄻ', "ㄹㅁ");
        LAYER_CHARACTER_MAP.put('ㄼ', "ㄹㅂ");
        LAYER_CHARACTER_MAP.put('ㄽ', "ㄹㅅ");
        LAYER_CHARACTER_MAP.put('ㄾ', "ㄹㅌ");
        LAYER_CHARACTER_MAP.put('ㅀ', "ㄹㅎ");
        LAYER_CHARACTER_MAP.put('ㅄ', "ㅂㅅ");
        LAYER_CHARACTER_MAP.put('ㅘ', "ㅗㅏ");
        LAYER_CHARACTER_MAP.put('ㅙ', "ㅗㅐ");
        LAYER_CHARACTER_MAP.put('ㅚ', "ㅗㅣ");
        LAYER_CHARACTER_MAP.put('ㅝ', "ㅜㅓ");
        LAYER_CHARACTER_MAP.put('ㅞ', "ㅜㅔ");
        LAYER_CHARACTER_MAP.put('ㅟ', "ㅜㅣ");
        LAYER_CHARACTER_MAP.put('ㅢ', "ㅡㅣ");
    }

    private void initializeFirstConsonantMap() {
    	FIRST_CONSONANT_MAP.put('ㄱ', 0);
    	FIRST_CONSONANT_MAP.put('ㄲ', 1);
    	FIRST_CONSONANT_MAP.put('ㄴ', 2);
    	FIRST_CONSONANT_MAP.put('ㄷ', 3);
    	FIRST_CONSONANT_MAP.put('ㄸ', 4);
    	FIRST_CONSONANT_MAP.put('ㄹ', 5);
    	FIRST_CONSONANT_MAP.put('ㅁ', 6);
    	FIRST_CONSONANT_MAP.put('ㅂ', 7);
    	FIRST_CONSONANT_MAP.put('ㅃ', 8);
    	FIRST_CONSONANT_MAP.put('ㅅ', 9);
    	FIRST_CONSONANT_MAP.put('ㅆ', 10);
    	FIRST_CONSONANT_MAP.put('ㅇ', 11);
    	FIRST_CONSONANT_MAP.put('ㅈ', 12);
    	FIRST_CONSONANT_MAP.put('ㅉ', 13);
    	FIRST_CONSONANT_MAP.put('ㅊ', 14);
    	FIRST_CONSONANT_MAP.put('ㅋ', 15);
    	FIRST_CONSONANT_MAP.put('ㅌ', 16);
    	FIRST_CONSONANT_MAP.put('ㅍ', 17);
    	FIRST_CONSONANT_MAP.put('ㅎ', 18);
    }

    private void initializeMiddleConsonantMap() {
    	MIDDLE_VOWEL_MAP.put('ㅏ', 0);
    	MIDDLE_VOWEL_MAP.put('ㅐ', 1);
    	MIDDLE_VOWEL_MAP.put('ㅑ', 2);
    	MIDDLE_VOWEL_MAP.put('ㅒ', 3);
    	MIDDLE_VOWEL_MAP.put('ㅓ', 4);
    	MIDDLE_VOWEL_MAP.put('ㅔ', 5);
    	MIDDLE_VOWEL_MAP.put('ㅕ', 6);
    	MIDDLE_VOWEL_MAP.put('ㅖ', 7);
    	MIDDLE_VOWEL_MAP.put('ㅗ', 8);
    	MIDDLE_VOWEL_MAP.put('ㅘ', 9);
    	MIDDLE_VOWEL_MAP.put('ㅙ', 10);
    	MIDDLE_VOWEL_MAP.put('ㅚ', 11);
    	MIDDLE_VOWEL_MAP.put('ㅛ', 12);
    	MIDDLE_VOWEL_MAP.put('ㅜ', 13);
    	MIDDLE_VOWEL_MAP.put('ㅝ', 14);
    	MIDDLE_VOWEL_MAP.put('ㅞ', 15);
    	MIDDLE_VOWEL_MAP.put('ㅟ', 16);
    	MIDDLE_VOWEL_MAP.put('ㅠ', 17);
    	MIDDLE_VOWEL_MAP.put('ㅡ', 18);
    	MIDDLE_VOWEL_MAP.put('ㅢ', 19);
    	MIDDLE_VOWEL_MAP.put('ㅣ', 20);
    }

    private void initializeLastConsonantMap() {
    	LAST_CONSONANT_MAP.put(' ', 0);
    	LAST_CONSONANT_MAP.put('ㄱ', 1);
    	LAST_CONSONANT_MAP.put('ㄲ', 2);
    	LAST_CONSONANT_MAP.put('ㄳ', 3);
    	LAST_CONSONANT_MAP.put('ㄴ', 4);
    	LAST_CONSONANT_MAP.put('ㄵ', 5);
    	LAST_CONSONANT_MAP.put('ㄶ', 6);
    	LAST_CONSONANT_MAP.put('ㄷ', 7);
    	LAST_CONSONANT_MAP.put('ㄹ', 8);
    	LAST_CONSONANT_MAP.put('ㄺ', 9);
    	LAST_CONSONANT_MAP.put('ㄻ', 10);
    	LAST_CONSONANT_MAP.put('ㄼ', 11);
    	LAST_CONSONANT_MAP.put('ㄽ', 12);
    	LAST_CONSONANT_MAP.put('ㄾ', 13);
    	LAST_CONSONANT_MAP.put('ㄿ', 14);
    	LAST_CONSONANT_MAP.put('ㅀ', 15);
    	LAST_CONSONANT_MAP.put('ㅁ', 16);
    	LAST_CONSONANT_MAP.put('ㅂ', 17);
    	LAST_CONSONANT_MAP.put('ㅄ', 18);
    	LAST_CONSONANT_MAP.put('ㅅ', 19);
    	LAST_CONSONANT_MAP.put('ㅆ', 20);
    	LAST_CONSONANT_MAP.put('ㅇ', 21);
    	LAST_CONSONANT_MAP.put('ㅈ', 22);
    	LAST_CONSONANT_MAP.put('ㅊ', 23);
    	LAST_CONSONANT_MAP.put('ㅋ', 24);
    	LAST_CONSONANT_MAP.put('ㅌ', 25);
    	LAST_CONSONANT_MAP.put('ㅍ', 26);
    	LAST_CONSONANT_MAP.put('ㅎ', 27);
    }

    private void initializeDualMiddleConsonantMap() {
    	DUAL_MIDDLE_VOWEL_MAP.put("ㅜㅣ", "ㅟ");
    	DUAL_MIDDLE_VOWEL_MAP.put("ㅡㅣ", "ㅢ");
    	DUAL_MIDDLE_VOWEL_MAP.put("ㅗㅏ", "ㅘ");
    	DUAL_MIDDLE_VOWEL_MAP.put("ㅜㅓ", "ㅝ");
    	DUAL_MIDDLE_VOWEL_MAP.put("ㅜㅔ", "ㅞ");
    	DUAL_MIDDLE_VOWEL_MAP.put("ㅗㅣ", "ㅚ");
    	DUAL_MIDDLE_VOWEL_MAP.put("ㅗㅐ", "ㅙ");
    }

    private void initializeDualLastConsonantMap() {
    	DUAL_LAST_CONSONANT_MAP.put("ㄱㅅ", "ㄳ");
    	DUAL_LAST_CONSONANT_MAP.put("ㄴㅈ", "ㄵ");
    	DUAL_LAST_CONSONANT_MAP.put("ㄴㅎ", "ㄶ");
    	DUAL_LAST_CONSONANT_MAP.put("ㄹㄱ", "ㄺ");
    	DUAL_LAST_CONSONANT_MAP.put("ㄹㅁ", "ㄻ");
    	DUAL_LAST_CONSONANT_MAP.put("ㄹㅎ", "ㅀ");
    	DUAL_LAST_CONSONANT_MAP.put("ㄹㅂ", "ㄼ");
    	DUAL_LAST_CONSONANT_MAP.put("ㄹㅌ", "ㄾ");
    	DUAL_LAST_CONSONANT_MAP.put("ㅂㅅ", "ㅄ");
    }

    // 문자열 분해
    public String decompose(String koString, boolean delayer) {
    	StringBuilder builder = new StringBuilder();
    	for(char c: koString.toCharArray()) {
    		String cv = decompose(c);

    		if(delayer) {
    			cv = deLayer(cv);
    		}
    		builder.append(cv);
    	}
    	return builder.toString();
    }

    // 한글자 분해
    private String decompose(char c) {
    	if (c < '가'  || c > '힣') {
    		return String.valueOf(c);
    	}

    	StringBuilder builder = new StringBuilder();
    	int GA = '\uAC00';
    	int diff = c - GA;

    	final int firstIndex = diff / (MIDDLE_VOWEL_MAP.size() * LAST_CONSONANT_MAP.size());
    	builder.append(FIRST_CONSONANT_LIST[firstIndex]);

    	final int middleIndex = (diff - ((LAST_CONSONANT_MAP.size() * MIDDLE_VOWEL_MAP.size()) * firstIndex)) / LAST_CONSONANT_MAP.size();
    	builder.append(MIDDLE_VOWEL_LIST[middleIndex]);

    	final int lastIndex = (diff - ((LAST_CONSONANT_MAP.size() * MIDDLE_VOWEL_MAP.size()) * firstIndex) - (LAST_CONSONANT_MAP.size() * middleIndex));

    	if(lastIndex > 0) {
    		builder.append(LAST_CONSONANT_LIST[lastIndex]);
    	}

    	return builder.toString();
    }

    // 겹받침('ㄺ', 'ㄼ', ...), 이중모음('ㅘ', 'ㅞ', ...) 분리
    private String deLayer(String str) {
    	StringBuilder builder = new StringBuilder();

    	for(char c: str.toCharArray()) {
    		if (LAYER_CHARACTER_MAP.containsKey(c)) {
    			builder.append(LAYER_CHARACTER_MAP.get(c));
    		} else {
    			builder.append(c);
    		}
    	}

    	return builder.toString();
    }

    public String compose (String str) {
    	//int start = 0;
    	StringBuilder result = new StringBuilder();

    	for (int start = 0; start < str.length();) {
    		int result_char = 0xAC00;

    		// 초성 여부 확인
    		if (FIRST_CONSONANT_MAP.containsKey(str.charAt(start))) {
    			int firstIndex = FIRST_CONSONANT_MAP.get(str.charAt(start));
    			result_char += firstIndex * MIDDLE_VOWEL_MAP.size() * LAST_CONSONANT_MAP.size();
    			start++;

    			// 중성 여부 확인
    			if(start < str.length() && MIDDLE_VOWEL_MAP.containsKey(str.charAt(start))) {
    				int middleIndex = MIDDLE_VOWEL_MAP.get(str.charAt(start));
    				result_char += LAST_CONSONANT_MAP.size() * middleIndex;
    				start++;

    				// 종성 여부 확인
    				if(start < str.length() && LAST_CONSONANT_MAP.containsKey(str.charAt(start))
    						&& !(start + 1 < str.length() && MIDDLE_VOWEL_MAP.containsKey(str.charAt(start + 1)))) {
    					int lastIndex = LAST_CONSONANT_MAP.get(str.charAt(start));
    					result_char += lastIndex;

    					if (str.charAt(start) != ' ') {
    						start++;
    						// 종성의 겹받침 여부 확인
    						if (start < str.length() && LAST_CONSONANT_MAP.containsKey(str.charAt(start))
    								&& !(start + 1 < str.length() && MIDDLE_VOWEL_MAP.containsKey(str.charAt(start + 1)))) {
    	    					String dualLastConsonant = String.valueOf(str.charAt(start - 1)) + str.charAt(start);
    	    					if (DUAL_LAST_CONSONANT_MAP.containsKey(dualLastConsonant)) {
    	    						result_char -= lastIndex;
    	    						lastIndex = LAST_CONSONANT_MAP.get(DUAL_LAST_CONSONANT_MAP.get(dualLastConsonant).charAt(0));
    	    						result_char += lastIndex;
    	    						start++;
    	    					}
    						}
    					}
    				}
    				// 중성의 이중모음 여부 확인
    				else if (start < str.length() && MIDDLE_VOWEL_MAP.containsKey(str.charAt(start))){
    					String dualMiddleVowel = String.valueOf(str.charAt(start - 1)) + str.charAt(start);

    					if (DUAL_MIDDLE_VOWEL_MAP.containsKey(dualMiddleVowel)) {
    						result_char -= LAST_CONSONANT_MAP.size() * middleIndex;
    						middleIndex = MIDDLE_VOWEL_MAP.get(DUAL_MIDDLE_VOWEL_MAP.get(dualMiddleVowel).charAt(0));
    						result_char += LAST_CONSONANT_MAP.size() * middleIndex;
    						start++;

    						// 이중모음 이후 종성 존재 여부 확인
    						if (start < str.length() && LAST_CONSONANT_MAP.containsKey(str.charAt(start))
    								&& !(start + 1 < str.length() && MIDDLE_VOWEL_MAP.containsKey(str.charAt(start + 1)))) {
    							int lastConsonantIndex = LAST_CONSONANT_MAP.get(str.charAt(start));
	    						result_char += lastConsonantIndex;
	    						start++;

	    						// 이중모음 + 겹받침 종성 여부 확인
	    						if (start < str.length() && LAST_CONSONANT_MAP.containsKey(str.charAt(start))
	    								&& !(start + 1 < str.length() && MIDDLE_VOWEL_MAP.containsKey(str.charAt(start + 1)))) {
	    							String dualLastConsonant = String.valueOf(str.charAt(start - 1)) + str.charAt(start);

	    							if(DUAL_LAST_CONSONANT_MAP.containsKey(dualLastConsonant)) {
	    								result_char -= lastConsonantIndex;
	    								lastConsonantIndex = LAST_CONSONANT_MAP.get(DUAL_LAST_CONSONANT_MAP.get(dualLastConsonant).charAt(0));
	    								result_char += lastConsonantIndex;
	    								start++;
	    							}
	    						}
    						}
    					}
    				}

    				result.append((char) result_char);
    			} else {
    				result.append(str.charAt(start - 1));
    			}
    		} else if (MIDDLE_VOWEL_MAP.containsKey(str.charAt(start)) && (start + 1 < str.length() && MIDDLE_VOWEL_MAP.containsKey(str.charAt(start + 1)))) {
    			String dualMiddleVowel = String.valueOf(str.charAt(start)) + str.charAt(start + 1);

    			if(DUAL_MIDDLE_VOWEL_MAP.containsKey(dualMiddleVowel)) {
    				result.append(DUAL_MIDDLE_VOWEL_MAP.get(dualMiddleVowel));
    				start += 2;
    			} else {
    				result.append(str.charAt(start));
    				start++;
    			}
    		} else {
    			result.append(str.charAt(start));
    			start++;
    		}
    	}

    	return result.toString();
    }

    public String firstConsonant(String str) {
    	StringBuilder builder = new StringBuilder();
    	for (char c: str.toCharArray()) {
    		builder.append(decompose(c).charAt(0));
    	}

    	return builder.toString();
    }

}
