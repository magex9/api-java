package ca.magex.crm.api.crm;

import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.common.IdentifierList;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.AuthenticationRoleIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;

/**
 * Contains the information for a User within the System
 * 
 * @author Jonny
 */
public class UserDetails extends UserSummary {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	/** the person summary this user is associated with */
	@NotNull
	private PersonIdentifier personId;
	
	/** the authentication roles associated with this user */
	@NotEmpty
	private IdentifierList<AuthenticationRoleIdentifier> authenticationRoleIds;
	
	/**
	 * Constructs a new user from the information provided
	 * @param userId
	 * @param personId
	 * @param username
	 * @param status
	 * @param authenticationRoleIds
	 */
	public UserDetails(UserIdentifier userId, OrganizationIdentifier organizationId, PersonIdentifier personId, String username, Status status, List<AuthenticationRoleIdentifier> authenticationRoleIds) {
		super(userId, organizationId, username, status);
		this.personId = personId;
		this.authenticationRoleIds = authenticationRoleIds == null ? new IdentifierList<>() : new IdentifierList<>(authenticationRoleIds);
	}
	
	/** 
	 * returns the unique identifier of the person associated with the user
	 * @return
	 */
	public PersonIdentifier getPersonId() {
		return personId;
	}
	
	/**
	 * returns the authentication role identifiers associated to the user
	 * @return
	 */
	public List<AuthenticationRoleIdentifier> getAuthenticationRoleIds() {
		return Collections.unmodifiableList(authenticationRoleIds);
	}
	
	/**
	 * returns a copy of the user with the new status
	 * @param status
	 * @return
	 */
	public UserDetails withStatus(Status status) {
		return new UserDetails(userId, organizationId, personId, username, status, authenticationRoleIds);
	}	
	
	/**
	 * returns a copy of the user with the new roles
	 * @param roles
	 * @return
	 */
	public UserDetails withAuthenticationRoleIds(List<AuthenticationRoleIdentifier> authenticationRoleIds) {
		return new UserDetails(userId, organizationId, personId, username, status, authenticationRoleIds);
	}
	
	/**
	 * returns whether or not the current user has the associated roleId
	 * @param roleId
	 * @return
	 */
	public boolean isInRole(AuthenticationRoleIdentifier roleId) {
		return authenticationRoleIds.contains(roleId);
	}
	
	/**
	 * Get the summary information of the user
	 * @return
	 */
	public UserSummary asSummary() {
		return new UserSummary(userId, organizationId, username, status);
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