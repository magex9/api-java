package ca.magex.crm.api.crm;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

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
public class User implements Serializable {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	/** a unique identifier for the user within the system */
	private UserIdentifier userId;
	
	/** the unique username within the system for this user */
	private String username;

	/** the person summary this user is associated with */
	private PersonIdentifier personId;
	
	/** the organization this user is associated with */
	private OrganizationIdentifier organizationId;
	
	/** the current status of the user */
	private Status status;
	
	/** the authentication roles associated with this user */
	private IdentifierList<AuthenticationRoleIdentifier> authenticationRoleIds;
	
	/**
	 * Constructs a new user from the information provided
	 * @param userId
	 * @param personId
	 * @param username
	 * @param status
	 * @param authenticationRoleIds
	 */
	public User(UserIdentifier userId, OrganizationIdentifier organizationId, PersonIdentifier personId, String username, Status status, List<AuthenticationRoleIdentifier> authenticationRoleIds) {
		super();
		this.userId = userId;
		this.organizationId = organizationId;
		this.personId = personId;
		this.username = username;		
		this.status = status;
		this.authenticationRoleIds = authenticationRoleIds == null ? new IdentifierList<>() : new IdentifierList<>(authenticationRoleIds);
	}
	
	/**
	 * returns the unique identifier for the user
	 * @return
	 */
	public UserIdentifier getUserId() {
		return userId;
	}
	
	/**
	 * returns the unique identifier for the organization
	 * @return
	 */
	public OrganizationIdentifier getOrganizationId() {
		return organizationId;
	}	
	
	/** 
	 * returns the unique identifier of the person associated with the user
	 * @return
	 */
	public PersonIdentifier getPersonId() {
		return personId;
	}
	
	/**
	 * returns the unique username for the user
	 * @return
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * returns the current status of the user
	 * @return
	 */
	public Status getStatus() {
		return status;
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
	public User withStatus(Status status) {
		return new User(userId, organizationId, personId, username, status, authenticationRoleIds);
	}	
	
	/**
	 * returns a copy of the user with the new roles
	 * @param roles
	 * @return
	 */
	public User withAuthenticationRoleIds(List<AuthenticationRoleIdentifier> authenticationRoleIds) {
		return new User(userId, organizationId, personId, username, status, authenticationRoleIds);
	}
	
	/**
	 * returns whether or not the current user has the associated roleId
	 * @param roleId
	 * @return
	 */
	public boolean isInRole(AuthenticationRoleIdentifier roleId) {
		return authenticationRoleIds.contains(roleId);
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