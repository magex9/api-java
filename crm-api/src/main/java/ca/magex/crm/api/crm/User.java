package ca.magex.crm.api.crm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.AuthenticationRoleIdentifier;
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
	
	/** the current status of the user */
	private Status status;
	
	/** the authentication roles associated with this user */
	private List<AuthenticationRoleIdentifier> roleIds;
	
	/**
	 * Constructs a new user from the information provided
	 * @param userId
	 * @param personId
	 * @param username
	 * @param status
	 * @param roleIds
	 */
	public User(UserIdentifier userId, PersonIdentifier personId, String username, Status status, List<AuthenticationRoleIdentifier> roleIds) {
		super();
		this.userId = userId;
		this.personId = personId;
		this.username = username;		
		this.status = status;
		this.roleIds = new ArrayList<>(roleIds);
	}
	
	/**
	 * returns the unique identifier for the user
	 * @return
	 */
	public UserIdentifier getUserId() {
		return userId;
	}
	
	/**
	 * returns the unique username for the user
	 * @return
	 */
	public String getUsername() {
		return username;
	}
	
	/** 
	 * returns the unique identifier of the person associated with the user
	 * @return
	 */
	public PersonIdentifier getPersonId() {
		return personId;
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
	public List<AuthenticationRoleIdentifier> getRoles() {
		return roleIds;
	}
	
	/**
	 * returns a copy of the user with the new status
	 * @param status
	 * @return
	 */
	public User withStatus(Status status) {
		return new User(userId, personId, username, status, roleIds);
	}	
	
	/**
	 * returns a copy of the user with the new roles
	 * @param roles
	 * @return
	 */
	public User withRoles(List<AuthenticationRoleIdentifier> roleIds) {
		return new User(userId, personId, username, status, roleIds);
	}
	
	/**
	 * returns whether or not the current user has the associated roleId
	 * @param roleId
	 * @return
	 */
	public boolean isInRole(AuthenticationRoleIdentifier roleId) {
		return roleIds.contains(roleId);
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