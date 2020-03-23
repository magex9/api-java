package ca.magex.crm.api.filters;

public class LocationsFilter {

	private String displayName;
	
	private Paging paging;
	
	public LocationsFilter(String displayName, Paging paging) {
		this.displayName = displayName;
		this.paging = paging;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public Paging getPaging() {
		return paging;
	}
	
}
