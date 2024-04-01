package com.ose.p.filters.fcl;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import com.ose.p.util.ConsonantVowelUtil;

public class FirstConsonantFilter extends TokenFilter {
	private final CharTermAttribute charAttr;
	private final ConsonantVowelUtil consonantVowelUtil;

	public FirstConsonantFilter(TokenStream input) {
		super(input);
		consonantVowelUtil = new ConsonantVowelUtil();
		charAttr = addAttribute(CharTermAttribute.class);
	}

	@Override
	public final boolean incrementToken() throws IOException {
		if (input.incrementToken()) {
			String firstConsonant = consonantVowelUtil.firstConsonant(charAttr.toString());
			charAttr.setEmpty().append(firstConsonant);
			return true;
		}

		return false;
	}


}
