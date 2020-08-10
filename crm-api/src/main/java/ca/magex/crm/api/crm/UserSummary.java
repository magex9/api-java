package ca.magex.crm.api.crm;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;

/**
 * Contains the tombstone information for a User within the System
 * 
 * @author Jonny
 */
public class UserSummary implements Serializable {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	/** a unique identifier for the user within the system */
	@NotNull
	protected UserIdentifier userId;
	
	/** the unique username within the system for this user */
	@NotBlank
	@Size(max = 32)
	protected String username;

	/** the organization this user is associated with */
	@NotNull
	protected OrganizationIdentifier organizationId;
	
	/** the current status of the user */
	@NotNull
	protected Status status;
	
	/**
	 * Constructs a new user from the information provided
	 * @param userId
	 * @param personId
	 * @param username
	 * @param status
	 * @param authenticationRoleIds
	 */
	public UserSummary(UserIdentifier userId, OrganizationIdentifier organizationId, String username, Status status) {
		super();
		this.userId = userId;
		this.organizationId = organizationId;
		this.username = username;		
		this.status = status;
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
	 * returns a copy of the user with the new status
	 * @param status
	 * @return
	 */
	public UserSummary withStatus(Status status) {
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