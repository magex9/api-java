package ca.magex.crm.api.filters;

import java.io.Serializable;

public class PersonsFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	private String displayName;
	
	public PersonsFilter(String displayName) {
		this.displayName = displayName;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
}
