package ca.magex.crm.api.filters;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class PersonsFilter implements Serializable {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	public static final List<Sort> SORT_OPTIONS = List.of(
		Sort.by(Order.asc("displayName")),
		Sort.by(Order.desc("displayName")),
		Sort.by(Order.asc("status")),
		Sort.by(Order.desc("status"))
	);

	private Identifier organizationId;

	private String displayName;

	private Status status;

	public PersonsFilter(Identifier organizationId, String displayName, Status status) {
		this.organizationId = organizationId;
		this.displayName = displayName;
		this.status = status;
	}

	public PersonsFilter() {
		this(null, null, null);
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

	public static List<Sort> getSortOptions() {
		return SORT_OPTIONS;
	}
	
	public static Sort getDefaultSort() {
		return Sort.by(Direction.ASC, "displayName");
	}

	public static Paging getDefaultPaging() {
		return new Paging(getDefaultSort());
	}

	public Comparator<PersonSummary> getComparator(Paging paging) {
		return paging.new PagingComparator<PersonSummary>();
	}

}
