package com.ose.p.filters.cv;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CVDecompositionFilterTest {
	private Analyzer analyzer;

	private String getFirstConsonantString(String text) throws IOException {
		TokenStream tokenStream = analyzer.tokenStream("field", text);

		CharTermAttribute charAttr = tokenStream.addAttribute(CharTermAttribute.class);

		tokenStream.reset();

		List<String> tokenList = new ArrayList<>();

		while (tokenStream.incrementToken()) {
			tokenList.add(charAttr.toString());
		}

		tokenStream.close();

		String result = String.join(" ", tokenList);
		System.out.println(result);

		return result;
	}

	@BeforeEach
	public void setup() {
		analyzer = new Analyzer(Analyzer.PER_FIELD_REUSE_STRATEGY) {
			@Override
			protected TokenStreamComponents createComponents(String fieldName) {
				Tokenizer tokenizer = new KeywordTokenizer();
				TokenStream tokenFilter = new CVDecomposionFilter(tokenizer);
				return new TokenStreamComponents(tokenizer, tokenFilter);

			}
		};
	}

	@Test
	void testOnlyKo() throws IOException {
		assertEquals("ㅇㅔㄹㄹㅏㅅㅡㅌㅣㄱ ㅅㅓㅊㅣ", getFirstConsonantString("엘라스틱 서치"));
	}

	@Test
	void testIncludeEn() throws IOException {
		assertEquals("ㅇㅔㄹㄹㅏㅅㅡㅌㅣㄱ search", getFirstConsonantString("엘라스틱 search"));
	}

	@Test
	void testIncludeSpecialCharacters() throws IOException {
		assertEquals("ㅇㅔㄹㄹㅏㅅㅡㅌㅣㄱ!@# ㅅㅓㅊㅣ(%^&*)", getFirstConsonantString("엘라스틱!@# 서치(%^&*)"));
	}

	@Test
	void testIncludeConsonantVowel() throws IOException {
		assertEquals("ㅇㅔㄹㄹㅏㅅㅡㅌㅣㄱ ㅅㅓㅊㅣ", getFirstConsonantString("엘라스ㅌㅣㄱ ㅅㅓ치"));
	}

	@Test
	void testIncludeStacking() throws IOException {
		assertEquals("ㄱㅏㅂㅅㅈㅣㄷㅏ", getFirstConsonantString("값지다"));
		assertEquals("ㅇㅏㄴㅈㄷㅏ", getFirstConsonantString("앉다"));
	}

}
