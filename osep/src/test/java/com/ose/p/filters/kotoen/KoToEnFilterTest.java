package com.ose.p.filters.kotoen;

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

public class KoToEnFilterTest {
	private Analyzer analyzer;

	private String getKoToEn(String text) throws IOException {

		TokenStream tokenStream = analyzer.tokenStream("field", text);

		CharTermAttribute charAttr = tokenStream.addAttribute(CharTermAttribute.class);

		tokenStream.reset();

		List<String> tokenList = new ArrayList<>();
		while (tokenStream.incrementToken() ) {
			tokenList.add(charAttr.toString());
		}
		tokenStream.close();

		String result = String.join(" ", tokenList);
		System.out.println("result :"+ result);

		return result;
	}

	@BeforeEach
	public void setup() {
		analyzer = new Analyzer(Analyzer.PER_FIELD_REUSE_STRATEGY) {
			@Override
			protected TokenStreamComponents createComponents(String filedName) {
				Tokenizer tokenizer = new KeywordTokenizer();
				TokenStream tokenFilter = new KoToEnFilter(tokenizer);

				return new TokenStreamComponents(tokenizer, tokenFilter);
			}
		};
	}

	@Test
	void testOnlyHangul() throws IOException {
		assertEquals("elasticsearch", getKoToEn("딤ㄴ샻ㄴㄷㅁㄱ초"));
	}

	@Test
	void testOnlyHangul2() throws IOException {
		assertEquals("elastic search", getKoToEn("딤ㄴ샻 ㄴㄷㅁㄱ초"));
	}

	@Test
	void testIncludeEn() throws IOException {
		assertEquals("naver.com", getKoToEn("ㅜㅁㅍㄷㄱ.com"));
	}

	@Test
	void testIncludeSpecialCharacters() throws IOException {
		assertEquals("elastic~`+) search=0$$#", getKoToEn("딤ㄴ샻~`+) ㄴㄷㅁㄱ초=0$$#"));
	}

	@Test
	void testIncludeStacks() throws IOException {
		assertEquals("sword", getKoToEn("ㄴ잭ㅇ"));
	}

	@Test
	void testIncludeStacks2() throws IOException {
		assertEquals("javascript", getKoToEn("ㅓㅁㅍㅁㄴㅊ갸ㅔㅅ"));
	}

	@Test
	void testOnlyHangul3() throws IOException {
		assertEquals("pdf", getKoToEn("ㅔㅇㄹ"));
	}

}
