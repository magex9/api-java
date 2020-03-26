package ca.magex.crm.api.filters;

import org.springframework.data.domain.Sort;

public class OrganizationsFilter {

	private String displayName;
	
	private Paging paging;
	
	public OrganizationsFilter(String displayName, Paging paging) {
		this.displayName = displayName;
		this.paging = paging;
	}
	
	public OrganizationsFilter() {
		this(null, new Paging(0, 10, Sort.by("displayName")));
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public Paging getPaging() {
		return paging;
	}	
}