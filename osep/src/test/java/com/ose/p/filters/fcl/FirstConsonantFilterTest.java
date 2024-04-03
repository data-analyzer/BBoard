package com.ose.p.filters.fcl;

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

public class FirstConsonantFilterTest {
	private Analyzer analyzer;

	public String getFirstConsonant(String text) throws IOException {
		TokenStream tokenStream = analyzer.tokenStream("field", text);

		CharTermAttribute charAttr = tokenStream.addAttribute(CharTermAttribute.class);

		tokenStream.reset();

		List <String> tokenList = new ArrayList<>();
		while (tokenStream.incrementToken()) {
			tokenList.add(charAttr.toString());
		}

		tokenStream.close();

		String result = String.join(" ", tokenList);
		System.out.println("result :" + result);

		return result;
	}

	@BeforeEach
	public void setup() {
		analyzer = new Analyzer(Analyzer.PER_FIELD_REUSE_STRATEGY) {
			@Override
			protected TokenStreamComponents createComponents(String fieldName) {
				Tokenizer tokenizer = new KeywordTokenizer();
				TokenStream tokenFilter = new FirstConsonantFilter(tokenizer);
				return new TokenStreamComponents(tokenizer, tokenFilter);
			}
		};
	}

	@Test
	void testOnlyKo() throws IOException {
		assertEquals("ㅇㄹㅅㅌ ㅅㅊ", getFirstConsonant("엘라스틱 서치"));
	}

	@Test
	void testIncludeEn() throws IOException {
		assertEquals("ㅇㄹㅅㅌ Search", getFirstConsonant("엘라스틱 Search"));
	}

	@Test
	void testIncludeEn2() throws IOException {
		assertEquals("ㅇㄹㅅㅌ search", getFirstConsonant("엘라스틱 search"));
	}

	@Test
	void testContainsSpecialCharacters() throws IOException {
		assertEquals("([]ㅇㄹㅅㅌ!@#ㅅㅊ", getFirstConsonant("([]엘라스틱!@#서치"));
	}

	@Test
	void testReturnOriginalJamoIfContainsJamo() throws IOException {
		assertEquals("ㅇㄹㅅㅌㅣㄱ ㅅㅓㅊ", getFirstConsonant("엘라스ㅌㅣㄱ ㅅㅓ치"));
	}

	@Test
	void testContainsStacking() throws IOException {
		assertEquals("ㄱㅈㄷ", getFirstConsonant("값지다"));
		assertEquals("ㅇㄷ", getFirstConsonant("앉다"));
	}
}
