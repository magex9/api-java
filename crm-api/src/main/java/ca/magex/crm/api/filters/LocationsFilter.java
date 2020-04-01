package ca.magex.crm.api.filters;

import java.io.Serializable;
import java.util.Comparator;

import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.system.Status;

public class LocationsFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	private String displayName;
	
	private Status status;

	public LocationsFilter(String displayName, Status status) {
		this.displayName = displayName;
		this.status = status;
	}

	public LocationsFilter() {
		this(null, null);
	}
	
	public Status getStatus() {
		return status;
	}

	public String getDisplayName() {
		return displayName;
	}	
	
	public Comparator<LocationSummary> getComparator(Paging paging) {
		// TODO make the filtering based on the paging information
		return Comparator.comparing(LocationSummary::getDisplayName);
	}
	
}