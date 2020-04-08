package ca.magex.crm.api.filters;

import java.io.Serializable;
import java.util.Comparator;

import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class LocationsFilter implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Identifier organizationId;

	private String displayName;
	
	private Status status;

	public LocationsFilter(Identifier organizationId, String displayName, Status status) {
		this.organizationId = organizationId;
		this.displayName = displayName;
		this.status = status;
	}

	public LocationsFilter() {
		this(null, null, null);
	}
	
	public Identifier getOrganizationId() {
		return organizationId;
	}
	
	public Status getStatus() {
		return status;
	}

	public String getDisplayName() {
		return displayName;
	}	
	
	public Comparator<LocationSummary> getComparator(Paging paging) {
		return paging.new PagingComparator<LocationSummary>();
	}
	
}