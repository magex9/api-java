package ca.magex.crm.api.filters;

import java.io.Serializable;
import java.util.Comparator;

import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class PersonsFilter implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Identifier organizationId;

	private String displayName;
	
	private Status status;
	
	private String userName;
	
	public PersonsFilter(Identifier organizationId, String displayName, Status status) {
		this(organizationId, displayName, status, null);
	}
	
	public PersonsFilter(Identifier organizationId, String displayName, Status status, String userName) {
		this.organizationId = organizationId;
		this.displayName = displayName;
		this.status = status;
		this.userName = userName;
	}
	
	public PersonsFilter() {
		this(null, null, null, null);
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
	
	public String getUserName() {
		return userName;
	}
	
	public Comparator<PersonSummary> getComparator(Paging paging) {
		return paging.new PagingComparator<PersonSummary>();
	}
	
}
