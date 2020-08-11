package ca.magex.crm.graphql.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MapBuilder {

	private Map<String, Object> map = new HashMap<>();

	public MapBuilder withEntry(String key, Object value) {
		map.put(key, value);
		return this;
	}
	
	public MapBuilder withOptionalEntry(String key, Optional<?> value) {
		if (value.isPresent() && value.get() != null) {
			map.put(key,  value.get().toString());
		}
		return this;
	}

	public Map<String, Object> build() {
		return map;
	}
}
