package com.se.board;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.se.board.domain.es.EsBulkData;
import com.se.board.domain.es.EsBulkData.Type;
import com.se.board.domain.es.EsService;

@SpringBootTest
public class EsServiceTest {

	@Autowired
	private EsService esService;

	@Test
	public void bulkDocument ()  {
		ArrayList<EsBulkData> bulkList = new ArrayList<>();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.KOREAN);
		for(int i = 1; i < 11 ; i++) {
			EsBulkData data = new EsBulkData();
			data.setActionType(Type.CREATE);
			data.setIndexName("ingest-test-v3");
			data.setId(String.valueOf(i));
			Map<String, Object> mapDoc = new HashMap<>();
			mapDoc.put("code", String.valueOf(i));
			mapDoc.put("title", "제목 : " + String.valueOf(i));
			mapDoc.put("date", format.format(new Date()));
			data.setMapDoc(mapDoc);
			bulkList.add(data);
		}

		esService.bulkDocument(bulkList, null);
	}
}
