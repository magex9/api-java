package ca.magex.crm.graphql.util;

import java.util.HashMap;
import java.util.Map;

public class MapBuilder {

	private Map<String, Object> map = new HashMap<>();
	
	public MapBuilder withEntry(String key, Object value) {
		map.put(key, value);
		return this;
	}
	
	public Map<String, Object> build() {
		return map;
	}
}
