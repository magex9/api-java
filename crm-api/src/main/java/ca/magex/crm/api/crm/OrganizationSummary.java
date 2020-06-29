package ca.magex.crm.api.crm;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.OrganizationIdentifier;

/**
 * Contains the tombstone information for an Organization within the System
 * 
 * @author Jonny
 */
public class OrganizationSummary implements Serializable {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;

	/** a unique identifier for the organization within the system */
	protected OrganizationIdentifier organizationId;
	
	/** current status of the organization */
	protected Status status;
	
	/** name of the organization for display purposes */
	protected String displayName;
	
	/**
	 * Constructs a new Organization Summary from the provided information
	 * 
	 * @param organizationId
	 * @param status
	 * @param displayName
	 */
	public OrganizationSummary(OrganizationIdentifier organizationId, Status status, String displayName) {
		super();
		this.organizationId = organizationId;
		this.status = status;
		this.displayName = displayName;
	}

	/**
	 * returns the unique identifier for the organization
	 * @return
	 */
	public OrganizationIdentifier getOrganizationId() {
		return organizationId;
	}

	/**
	 * returns the current status of the organization
	 * @return
	 */
	public Status getStatus() {
		return status;
	}
	
	/**
	 * returns the display name associated with the organization
	 * @return
	 */
	public String getDisplayName() {
		return displayName;
	}
	
	/**
	 * returns a copy of the organization with the new status provided
	 * @param status
	 * @return
	 */
	public OrganizationSummary withStatus(Status status) {
		return new OrganizationSummary(organizationId, status, displayName);
	}

	/**
	 * returns a copy of the organization with the new display name provided
	 * @param displayName
	 * @return
	 */
	public OrganizationSummary withDisplayName(String displayName) {
		return new OrganizationSummary(organizationId, status, displayName);
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