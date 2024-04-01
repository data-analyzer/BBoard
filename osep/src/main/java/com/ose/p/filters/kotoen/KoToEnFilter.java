package com.ose.p.filters.kotoen;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import com.ose.p.util.ConsonantVowelUtil;
import com.ose.p.util.KoEnUtil;

public class KoToEnFilter extends TokenFilter {
	private final CharTermAttribute charAttr;
	private final ConsonantVowelUtil util;
	private final KoEnUtil koEnUtil;

	public KoToEnFilter(TokenStream input) {
		super(input);
		util = new ConsonantVowelUtil();
		koEnUtil = new KoEnUtil();
		charAttr = addAttribute(CharTermAttribute.class);
	}

	@Override
	public final boolean incrementToken() throws IOException {
		if(input.incrementToken()) {
			String koToEn = koEnUtil.convertKoToEn(util.decompose(charAttr.toString(), true));
			charAttr.setEmpty().append(koToEn);
			return true;
		}

		return false;
	}
}