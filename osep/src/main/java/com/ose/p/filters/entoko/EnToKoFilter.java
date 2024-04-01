package com.ose.p.filters.entoko;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import com.ose.p.util.ConsonantVowelUtil;
import com.ose.p.util.KoEnUtil;

public class EnToKoFilter extends TokenFilter {
	private final CharTermAttribute charAttr;
	private final ConsonantVowelUtil util;
	private final KoEnUtil koEnUtil;

	public EnToKoFilter (TokenStream input) {
		super(input);
		util = new ConsonantVowelUtil();
		koEnUtil = new KoEnUtil();
		charAttr = addAttribute(CharTermAttribute.class);
	}

	@Override
	public final boolean incrementToken() throws IOException {
		if(input.incrementToken()) {
			String enToKo = util.compose(koEnUtil.convertEnToKo(charAttr.toString()));
			charAttr.setEmpty().append(enToKo);
			return true;
		}

		return false;
	}
}
