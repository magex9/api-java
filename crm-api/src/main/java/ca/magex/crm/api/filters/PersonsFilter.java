package ca.magex.crm.api.filters;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.system.Status;

public class PersonsFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	private String displayName;
	
	private Status status;
	
	public PersonsFilter(String displayName, Status status) {
		this.displayName = displayName;
		this.status = status;
	}
	
	public PersonsFilter(Map<String, Object> filter) {
		this.displayName = (String) filter.get("displayName");
		if (filter.containsKey("status") && StringUtils.isNotBlank((String) filter.get("status"))) {
			try {
				this.status = Status.valueOf((String) filter.get("status"));
			}
			catch(IllegalArgumentException e) {
				throw new ApiException("Invalid status value '" + filter.get("status") + "' expected one of {" + StringUtils.join(Status.values(), ",") + "}");
			}
		}
	}
	
	public PersonsFilter() {
		this(Collections.emptyMap());
	}
	
	public Status getStatus() {
		return status;
	}
	
	public String getDisplayName() {
		return displayName;
	}	
	
	public Comparator<PersonSummary> getComparator(Paging paging) {
		// TODO make the filtering based on the paging information
		return Comparator.comparing(PersonSummary::getDisplayName);
	}
	
}
