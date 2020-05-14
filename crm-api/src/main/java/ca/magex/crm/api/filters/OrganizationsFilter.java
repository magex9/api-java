package ca.magex.crm.api.filters;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.system.Status;

public class OrganizationsFilter implements Serializable {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	public static final List<Sort> SORT_OPTIONS = List.of(
		Sort.by(Order.asc("displayName")),
		Sort.by(Order.desc("displayName")),
		Sort.by(Order.asc("status")),
		Sort.by(Order.desc("status"))
	);

	private String displayName;

	private Status status;

	public OrganizationsFilter(String displayName, Status status) {
		this.displayName = displayName;
		this.status = status;
	}

	public OrganizationsFilter() {
		this(null, null);
	}

	public Status getStatus() {
		return status;
	}
	
	public OrganizationsFilter withStatus(Status status) {
		return new OrganizationsFilter(displayName, status);
	}

	public String getDisplayName() {
		return displayName;
	}
	
	public OrganizationsFilter withDisplayName(String displayName) {
		return new OrganizationsFilter(displayName, status);
	}

	public static List<Sort> getSortOptions() {
		return SORT_OPTIONS;
	}

	public Comparator<OrganizationSummary> getComparator(Paging paging) {
		return paging.new PagingComparator<OrganizationSummary>();		
	}
	
}