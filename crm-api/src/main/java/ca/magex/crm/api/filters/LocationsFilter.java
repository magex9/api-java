package ca.magex.crm.api.filters;

import java.util.Collections;
import java.util.Map;

public class LocationsFilter {

	private String displayName;

	public LocationsFilter(String displayName) {
		this.displayName = displayName;
	}

	public LocationsFilter(Map<String, Object> filter) {
		this.displayName = (String) filter.get("displayName");
	}

	public LocationsFilter() {
		this(Collections.emptyMap());
	}

	public String getDisplayName() {
		return displayName;
	}

}