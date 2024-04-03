package com.ose.p.filters.entoko;

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

public class EnToKoFilterTest {
	private Analyzer analyzer;

	private String getEnToKo(String text) throws IOException {
		TokenStream tokenStream = analyzer.tokenStream("field", text);

		CharTermAttribute charAttr = tokenStream.addAttribute(CharTermAttribute.class);

		tokenStream.reset();

		List<String> tokenList = new ArrayList<>();

		while(tokenStream.incrementToken() ) {
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
				TokenStream tokenFilter = new EnToKoFilter(tokenizer);
				return new TokenStreamComponents(tokenizer, tokenFilter);
			}
		};
	}

	@Test
	void testOnlyEn() throws IOException {
		assertEquals("엘라스틱 서치", getEnToKo("dpffktmxlr tjcl"));
	}

	@Test
	void testIncludeKo() throws IOException {
		assertEquals("엘라스틱 서치", getEnToKo("dpffktmxlr 서치"));
	}

	@Test
	void testIncludeSpecialCharacters() throws IOException {
		assertEquals("엘라스틱#$%$ 서치~!@", getEnToKo("dpffktmxlr#$%$ tjcl~!@"));
	}

	@Test
	void testIncludeSpecialCharacters2() throws IOException {
		assertEquals("엘라스틱~!@\" 서치#$%$", getEnToKo("dpffktmxlr~!@\" 서치#$%$"));
	}

	@Test
	void testIncludeStacking() throws IOException {
		assertEquals("값지다", getEnToKo("rkqtwlek"));
		assertEquals("앉다", getEnToKo("dkswek"));
	}

	@Test
	void testIncludeStacking2() throws IOException {
		assertEquals("값어치가 있다", getEnToKo("rkqtdjclrk dlTek"));
		assertEquals("맛있다", getEnToKo("aktdlTek"));
	}

}
