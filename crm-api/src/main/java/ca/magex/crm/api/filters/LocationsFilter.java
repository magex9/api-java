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
	
	private String reference;
	
	private Status status;

	public LocationsFilter(Identifier organizationId, String displayName, String reference, Status status) {
		this.organizationId = organizationId;
		this.displayName = displayName;
		this.reference = reference;
		this.status = status;
	}

	public LocationsFilter() {
		this(null, null, null, null);
	}
	
	public Identifier getOrganizationId() {
		return organizationId;
	}
	
	public LocationsFilter withOrganizationId(Identifier organizationId) {
		return new LocationsFilter(organizationId, displayName, reference, status);
	}
	
	public String getDisplayName() {
		return displayName;
	}	
	
	public LocationsFilter withDisplayName(String displayName) {
		return new LocationsFilter(organizationId, displayName, reference, status);
	}
	
	public Status getStatus() {
		return status;
	}

	public LocationsFilter withStatus(Status status) {
		return new LocationsFilter(organizationId, displayName, reference, status);
	}
	
	public Comparator<LocationSummary> getComparator(Paging paging) {
		return paging.new PagingComparator<LocationSummary>();
	}
	
}