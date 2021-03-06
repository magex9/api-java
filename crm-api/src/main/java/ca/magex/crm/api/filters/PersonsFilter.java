package ca.magex.crm.api.filters;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.OrganizationIdentifier;

public class PersonsFilter implements CrmFilter<PersonSummary> {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	public static final List<Sort> SORT_OPTIONS = List.of(
		Sort.by(Order.asc("displayName")),
		Sort.by(Order.desc("displayName")),
		Sort.by(Order.asc("status")),
		Sort.by(Order.desc("status"))
	);

	private OrganizationIdentifier organizationId;

	private String displayName;

	private Status status;

	public PersonsFilter() {
		this(null, null, null);
	}
	
	public PersonsFilter(OrganizationIdentifier organizationId, String displayName, Status status) {
		this.organizationId = organizationId;
		this.displayName = displayName;
		this.status = status;
	}
	
	public PersonsFilter(Map<String, Object> filterCriteria) {
		try {
			this.displayName = (String) filterCriteria.get("displayName");
			this.organizationId = filterCriteria.containsKey("organizationId") ? new OrganizationIdentifier((CharSequence) filterCriteria.get("organizationId")) : null;
			this.status = null;
			if (filterCriteria.containsKey("status") && StringUtils.isNotBlank((String) filterCriteria.get("status"))) {
				try {
					this.status = Status.valueOf(StringUtils.upperCase((String) filterCriteria.get("status")));
				}
				catch(IllegalArgumentException e) {
					throw new ApiException("Invalid status value '" + filterCriteria.get("status") + "' expected one of {" + StringUtils.join(Status.values(), ",") + "}");
				}
			}
		}
		catch(ClassCastException cce) {
			throw new ApiException("Unable to instantiate persons filter", cce);
		}
	}

	public Identifier getOrganizationId() {
		return organizationId;
	}

	public Status getStatus() {
		return status;
	}
	
	public String getStatusCode() {
		return status == null ? null : status.getCode();
	}

	public String getDisplayName() {
		return displayName;
	}
	
	public PersonsFilter withOrganizationId(OrganizationIdentifier organizationId) {
		return new PersonsFilter(organizationId, displayName, status);
	}

	public PersonsFilter withDisplayName(String displayName) {
		return new PersonsFilter(organizationId, displayName, status);
	}

	public PersonsFilter withStatus(Status status) {
		return new PersonsFilter(organizationId, displayName, status);
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

	@Override
	public boolean apply(PersonSummary instance) {
		return List.of(instance)
				.stream()
				.filter(p -> this.getDisplayName() == null || StringUtils.containsIgnoreCase(p.getDisplayName(), this.getDisplayName()))
				.filter(p -> this.getStatus() == null || this.getStatus().equals(p.getStatus()))
				.filter(p -> this.getOrganizationId() == null || this.getOrganizationId().equals(p.getOrganizationId()))
				.findAny()
				.isPresent();
	}
	
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}
}
