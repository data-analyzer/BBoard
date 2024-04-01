package com.ose.p.filters.kotoen;

import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;

public class KoToEnFilterFactory extends AbstractTokenFilterFactory {
	@Inject
	public KoToEnFilterFactory (IndexSettings indexSettings, Environment env, String name, Settings settings) {
		super(indexSettings, name, settings);
	}

	@Override
	public TokenStream create(TokenStream input) {
		return new KoToEnFilter(input);
	}
}
