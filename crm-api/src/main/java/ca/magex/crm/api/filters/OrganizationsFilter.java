package ca.magex.crm.api.filters;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.system.Status;

public class OrganizationsFilter implements Serializable {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	public static final List<Sort> SORT_OPTIONS = List.of(
		Sort.by(Direction.ASC, "displayName"),
		Sort.by(Direction.DESC, "displayName"),
		Sort.by(Direction.ASC, "status"),
		Sort.by(Direction.DESC, "status")
	);

	private String displayName;

	private Status status;

	public OrganizationsFilter(String displayName, Status status) {
		this.displayName = displayName;
		this.status = status;
	}
	
	public OrganizationsFilter(Map<String, Object> filterCriteria) {
		this.displayName = (String) filterCriteria.get("displayName");
		this.status = null;
		if (filterCriteria.containsKey("status") && StringUtils.isNotBlank((String) filterCriteria.get("status"))) {
			try {
				this.status = Status.valueOf(StringUtils.upperCase((String) filterCriteria.get("status")));
			} catch (IllegalArgumentException e) {
				throw new ApiException("Invalid status value '" + filterCriteria.get("status") + "' expected one of {" + StringUtils.join(Status.values(), ",") + "}");
			}
		}
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
	
	public static Sort getDefaultSort() {
		return Sort.by(Direction.ASC, "displayName");
	}
	
	public static Paging getDefaultPaging() {
		return new Paging(getDefaultSort());
	}
	
	public Comparator<OrganizationSummary> getComparator(Paging paging) {
		return paging.new PagingComparator<OrganizationSummary>();		
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