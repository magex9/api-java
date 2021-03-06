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
import ca.magex.crm.api.crm.UserDetails;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.AuthenticationRoleIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;

public class UsersFilter implements CrmFilter<UserDetails> {

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
	
	private PersonIdentifier personId;
	
	private OrganizationIdentifier organizationId;

	private Status status;

	private String username;

	private AuthenticationRoleIdentifier authenticationRoleId;

	public UsersFilter() {
		this(null, null, null, null, null);
	}
	
	public UsersFilter(OrganizationIdentifier organizationId, PersonIdentifier personId, Status status, String username, AuthenticationRoleIdentifier authenticationRoleId) {
		this.organizationId = organizationId;
		this.personId = personId;
		this.status = status;
		this.username = username;
		this.authenticationRoleId = authenticationRoleId;
	}
	
	public UsersFilter(Map<String, Object> filterCriteria) {
		try {
			this.organizationId = filterCriteria.containsKey("organizationId") ? new OrganizationIdentifier((CharSequence) filterCriteria.get("organizationId")) : null;
			this.personId = filterCriteria.containsKey("personId") ? new PersonIdentifier((CharSequence) filterCriteria.get("personId")) : null;
			this.authenticationRoleId = filterCriteria.get("authenticationRoleId") != null ? new AuthenticationRoleIdentifier((CharSequence) filterCriteria.get("authenticationRoleId")) : null;
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

	public OrganizationIdentifier getOrganizationId() {
		return organizationId;
	}
	
	public UsersFilter withOrganizationId(OrganizationIdentifier organizationId) {
		return new UsersFilter(organizationId, personId, status, username, authenticationRoleId);
	}
	
	public Identifier getPersonId() {
		return personId;
	}		
	
	public UsersFilter withPersonId(PersonIdentifier personId) {
		return new UsersFilter(organizationId, personId, status, username, authenticationRoleId);
	}

	public Status getStatus() {
		return status;
	}
	
	public String getStatusCode() {
		return status == null ? null : status.getCode();
	}
	
	public UsersFilter withStatus(Status status) {
		return new UsersFilter(organizationId, personId, status, username, authenticationRoleId);
	}
	
	public String getUsername() {
		return username;
	}
	
	public UsersFilter withUsername(String username) {
		return new UsersFilter(organizationId, personId, status, username, authenticationRoleId);
	}

	public Identifier getAuthenticationRoleId() {
		return authenticationRoleId;
	}
	
	public UsersFilter withAuthenticationRoleId(AuthenticationRoleIdentifier authenticationRoleId) {
		return new UsersFilter(organizationId, personId, status, username, authenticationRoleId);
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
	public boolean apply(UserDetails instance) {
		return List.of(instance)
			.stream()
			.filter(u -> this.getOrganizationId() == null || this.getOrganizationId().equals(u.getOrganizationId()))
			.filter(u -> this.getPersonId() == null || this.getPersonId().equals(u.getPersonId()))
			.filter(u -> this.getUsername() == null || StringUtils.containsIgnoreCase(u.getUsername(), this.getUsername()))
			.filter(u -> this.getAuthenticationRoleId() == null || u.getAuthenticationRoleIds().contains(this.getAuthenticationRoleId()))
			.filter(u -> this.getStatus() == null || this.getStatus().equals(u.getStatus()))
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
