package ca.magex.crm.api.filters;

import java.util.Collections;
import java.util.Map;

public class OrganizationsFilter {

	private String displayName;

	public OrganizationsFilter(String displayName) {
		this.displayName = displayName;
	}

	public OrganizationsFilter(Map<String, Object> filter) {
		this.displayName = (String) filter.get("displayName");
	}

	public OrganizationsFilter() {
		this(Collections.emptyMap());
	}

	public String getDisplayName() {
		return displayName;
	}

}