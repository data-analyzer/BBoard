package com.ose.p.filters.cv;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import com.ose.p.util.ConsonantVowelUtil;

public class CVDecomposionFilter extends TokenFilter {
	private final CharTermAttribute charAttr;
	private final ConsonantVowelUtil util;

	public CVDecomposionFilter(TokenStream input) {
		super(input);
		util = new ConsonantVowelUtil();
		charAttr = addAttribute(CharTermAttribute.class);
	}

	@Override
	public final boolean incrementToken() throws IOException {
		if(input.incrementToken()) {
			String consonantVowel = util.decompose(charAttr.toString(), true);
			charAttr.setEmpty().append(consonantVowel);
			return true;
		}
		return false;
	}
}
