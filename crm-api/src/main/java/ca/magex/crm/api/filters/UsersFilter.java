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
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class UsersFilter implements CrmFilter<User> {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	public static final List<Sort> SORT_OPTIONS = List.of(
		Sort.by(Order.asc("username")),
		Sort.by(Order.desc("username")),
		Sort.by(Order.asc("personName")),
		Sort.by(Order.desc("personName")),
		Sort.by(Order.asc("organizationName")),
		Sort.by(Order.desc("organizationName")),
		Sort.by(Order.asc("status")),
		Sort.by(Order.desc("status"))
	);

	private Identifier organizationId;
	
	private Identifier personId;

	private Status status;

	private String username;

	private String role;

	public UsersFilter() {
		this(null, null, null, null, null);
	}
	
	public UsersFilter(Identifier organizationId, Identifier personId, Status status, String username, String role) {
		this.organizationId = organizationId;
		this.personId = personId;
		this.status = status;
		this.username = username;
		this.role = role;
	}
	
	public UsersFilter(Map<String, Object> filterCriteria) {
		try {
			this.personId = filterCriteria.containsKey("personId") ? new Identifier((String) filterCriteria.get("personId")) : null;
			this.organizationId = filterCriteria.containsKey("organizationId") ? new Identifier((String) filterCriteria.get("organizationId")) : null;
			this.role = (String) filterCriteria.get("role");
			this.username = (String) filterCriteria.get("username");		
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
			throw new ApiException("Unable to instantiate users filter", cce);
		}
	}

	public Identifier getPersonId() {
		return personId;
	}
	
	public UsersFilter withPersonId(Identifier personId) {
		return new UsersFilter(organizationId, personId, status, username, role);
	}

	public Identifier getOrganizationId() {
		return organizationId;
	}
	
	public UsersFilter withOrganizationId(Identifier organizationId) {
		return new UsersFilter(organizationId, personId, status, username, role);
	}

	public Status getStatus() {
		return status;
	}
	
	public UsersFilter withStatus(Status status) {
		return new UsersFilter(organizationId, personId, status, username, role);
	}
	
	public String getUsername() {
		return username;
	}
	
	public UsersFilter withUsername(String username) {
		return new UsersFilter(organizationId, personId, status, username, role);
	}

	public String getRole() {
		return role;
	}
	
	public UsersFilter withRole(String role) {
		return new UsersFilter(organizationId, personId, status, username, role);
	}

	public static List<Sort> getSortOptions() {
		return SORT_OPTIONS;
	}
	
	public static Sort getDefaultSort() {
		return Sort.by(Direction.ASC, "username");
	}

	public static Paging getDefaultPaging() {
		return new Paging(getDefaultSort());
	}

	@Override
	public boolean apply(User instance) {
		return List.of(instance)
			.stream()
			.filter(u -> this.getUsername() == null || StringUtils.containsIgnoreCase(u.getUsername(), this.getUsername()))
			.filter(u -> this.getRole() == null || u.getRoles().contains(this.getRole()))
			.filter(u -> this.getStatus() == null || this.getStatus().equals(u.getStatus()))
			.filter(u -> this.getPersonId() == null || this.getPersonId().equals(u.getPerson().getPersonId()))
			.filter(u -> this.getOrganizationId() == null || this.getOrganizationId().equals(u.getPerson().getOrganizationId()))
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
