package com.ose.p.filters.entoko;

import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;

public class EnToKoFilterFactory extends AbstractTokenFilterFactory {
	public EnToKoFilterFactory (IndexSettings indexSettings, Environment env, String name, Settings settings) {
		super(indexSettings, name, settings);
	}

	@Override
	public TokenStream create(TokenStream input) {
		return new EnToKoFilter(input);
	}
}
