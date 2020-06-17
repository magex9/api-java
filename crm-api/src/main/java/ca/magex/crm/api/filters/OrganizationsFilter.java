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

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.system.Status;

public class OrganizationsFilter implements CrmFilter<OrganizationDetails> {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	public static final List<Sort> SORT_OPTIONS = List.of(
		Sort.by(Direction.ASC, "displayName"),
		Sort.by(Direction.DESC, "displayName"),
		Sort.by(Direction.ASC, "status"),
		Sort.by(Direction.DESC, "status")
	);

	private String displayName;

	private Status status;
	
	private String group;

	public OrganizationsFilter() {
		this(null, null, null);
	}
	
	public OrganizationsFilter(String displayName, Status status, String group) {
		this.displayName = displayName;
		this.status = status;
		this.group = group;
	}
	
	public OrganizationsFilter(Map<String, Object> filterCriteria) {
		try {
			this.displayName = (String) filterCriteria.get("displayName");
			this.status = null;
			if (filterCriteria.containsKey("status") && StringUtils.isNotBlank((String) filterCriteria.get("status"))) {
				try {
					this.status = Status.valueOf(StringUtils.upperCase((String) filterCriteria.get("status")));
				} catch (IllegalArgumentException e) {
					throw new ApiException("Invalid status value '" + filterCriteria.get("status") + "' expected one of {" + StringUtils.join(Status.values(), ",") + "}");
				}
			}
			this.group = (String) filterCriteria.get("group");
		}
		catch(ClassCastException cce) {
			throw new ApiException("Unable to instantiate organizations filter", cce);
		}
	}

	public Status getStatus() {
		return status;
	}
	
	public OrganizationsFilter withStatus(Status status) {
		return new OrganizationsFilter(displayName, status, group);
	}

	public String getDisplayName() {
		return displayName;
	}
	
	public OrganizationsFilter withDisplayName(String displayName) {
		return new OrganizationsFilter(displayName, status, group);
	}
	
	public String getGroup() {
		return group;
	}
	
	public OrganizationsFilter withGroup(String group) {
		return new OrganizationsFilter(displayName, status, group);
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
	public boolean apply(OrganizationDetails instance) {
		return List.of(instance)
			.stream()
			.filter(g -> this.getGroup() == null || g.getGroups().contains(this.getGroup()))
			.filter(g -> this.getDisplayName() == null || containsIgnoreCaseAndAccent(g.getDisplayName(), this.getDisplayName()))				
			.filter(g -> this.getStatus() == null || this.getStatus().equals(g.getStatus()))
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