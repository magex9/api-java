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

import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class LocationsFilter implements CrmFilter<LocationSummary> {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;

	public static final List<Sort> SORT_OPTIONS = List.of(
			Sort.by(Order.asc("displayName")),
			Sort.by(Order.desc("displayName")),
			Sort.by(Order.asc("reference")),
			Sort.by(Order.desc("reference")),
			Sort.by(Order.asc("country")),
			Sort.by(Order.desc("country")),
			Sort.by(Order.asc("status")),
			Sort.by(Order.desc("status")));

	private Identifier organizationId;

	private String displayName;

	private String reference;

	private Status status;

	public LocationsFilter() {
		this(null, null, null, null);
	}

	public LocationsFilter(Identifier organizationId, String displayName, String reference, Status status) {
		this.organizationId = organizationId;
		this.displayName = displayName;
		this.reference = reference;
		this.status = status;
	}

	public LocationsFilter(Map<String, Object> filterCriteria) {
		this.displayName = (String) filterCriteria.get("displayName");
		this.reference = (String) filterCriteria.get("reference");
		this.organizationId = filterCriteria.keySet().contains("organizationId") ? new Identifier((String) filterCriteria.get("organizationId")) : null;
		this.status = null;
		if (filterCriteria.containsKey("status") && StringUtils.isNotBlank((String) filterCriteria.get("status"))) {
			try {
				this.status = Status.valueOf(StringUtils.upperCase((String) filterCriteria.get("status")));
			} catch (IllegalArgumentException e) {
				throw new ApiException("Invalid status value '" + filterCriteria.get("status") + "' expected one of {" + StringUtils.join(Status.values(), ",") + "}");
			}
		}
	}

	public Identifier getOrganizationId() {
		return organizationId;
	}

	public LocationsFilter withOrganizationId(Identifier organizationId) {
		return new LocationsFilter(organizationId, displayName, reference, status);
	}

	public String getDisplayName() {
		return displayName;
	}

	public LocationsFilter withDisplayName(String displayName) {
		return new LocationsFilter(organizationId, displayName, reference, status);
	}

	public String getReference() {
		return reference;
	}

	public LocationsFilter setReference(String reference) {
		return new LocationsFilter(organizationId, displayName, reference, status);
	}

	public Status getStatus() {
		return status;
	}

	public LocationsFilter withStatus(Status status) {
		return new LocationsFilter(organizationId, displayName, reference, status);
	}

	public static List<Sort> getSortOptions() {
		return SORT_OPTIONS;
	}

	public static Sort getDefaultSort() {
		return Sort.by(Direction.ASC, "displayName");
	}

	public static Paging defaultPaging() {
		return new Paging(getDefaultSort());
	}

	@Override
	public boolean apply(LocationSummary instance) {
		return List.of(instance)
				.stream()
				.filter(l -> this.getDisplayName() == null || StringUtils.equalsIgnoreCase(this.getDisplayName(), l.getDisplayName()))
				.filter(l -> this.getStatus() == null || this.getStatus().equals(l.getStatus()))
				.filter(l -> this.getOrganizationId() == null || this.getOrganizationId().equals(l.getOrganizationId()))
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