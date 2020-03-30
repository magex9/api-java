package ca.magex.crm.api.filters;

public class PersonsFilter {

	private String displayName;
	
	public PersonsFilter(String displayName) {
		this.displayName = displayName;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
}
