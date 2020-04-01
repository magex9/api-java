package ca.magex.crm.api.filters;

import java.io.Serializable;
import java.util.Comparator;

import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.system.Status;

public class PersonsFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	private String displayName;
	
	private Status status;
	
	public PersonsFilter(String displayName, Status status) {
		this.displayName = displayName;
		this.status = status;
	}
	
	public PersonsFilter() {
		this(null, null);
	}
	
	public Status getStatus() {
		return status;
	}
	
	public String getDisplayName() {
		return displayName;
	}	
	
	public Comparator<PersonSummary> getComparator(Paging paging) {
		return paging.new PagingComparator<PersonSummary>();
	}
	
}
