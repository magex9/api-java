package ca.magex.crm.api.filters;

import java.util.Collections;
import java.util.Map;

import ca.magex.crm.api.system.Status;

public class OrganizationsFilter {

	private String displayName;
	
	private Status status;

	public OrganizationsFilter(String displayName, Status status) {
		this.displayName = displayName;
		this.status = status;
	}

	public OrganizationsFilter(Map<String, Object> filter) {
		this.displayName = (String) filter.get("displayName");
		this.status = Status.valueOf((String) filter.get("status"));
	}

	public OrganizationsFilter() {
		this(Collections.emptyMap());
	}
	
	public Status getStatus() {
		return status;
	}

	public String getDisplayName() {
		return displayName;
	}

}