package com.ose.p.plugin;

import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.indices.analysis.AnalysisModule.AnalysisProvider;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;

import com.ose.p.filters.cv.CVDecomposionFilterFactory;
import com.ose.p.filters.entoko.EnToKoFilterFactory;
import com.ose.p.filters.fcl.FirstConsonantFilterFactory;
import com.ose.p.filters.kotoen.KoToEnFilterFactory;

public class OsePlugin extends Plugin implements AnalysisPlugin {

	@Override
	public Map<String, AnalysisProvider<TokenFilterFactory>> getTokenFilters() {
		Map<String, AnalysisProvider<TokenFilterFactory>> filterMap = new HashMap<>();
		filterMap.put("ose_cv", CVDecomposionFilterFactory::new);
		filterMap.put("ose_fc", FirstConsonantFilterFactory::new);
		filterMap.put("ose_entoko", EnToKoFilterFactory::new);
		filterMap.put("ose_kotoen", KoToEnFilterFactory::new);

		return filterMap;
	}
}
