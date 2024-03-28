package com.se.board.domain.es;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EsBulkData {
	public enum Type {
		CREATE, UPDATE, DELETE
	}
	private String indexName;
	private String id;
	private Type actionType;
	private Map<String, Object> mapDoc;
}
