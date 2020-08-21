package ca.magex.crm.api.crm;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;

/**
 * Contains the tombstone information for a Location within the System
 * 
 * @author Jonny
 */
public class LocationSummary implements Serializable {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	/** a unique identifier for the location within the system */
	@NotNull
	protected LocationIdentifier locationId;
	
	/** the unique organization this location belongs to */
	@NotNull
	protected OrganizationIdentifier organizationId;
	
	/** current status of the organization */
	@NotNull
	protected Status status;
	
	/** a unique reference for the location within the organization */
	@NotBlank
	@Pattern(regexp = "[A-Z0-9]+")
	@Size(min = 5, max = 60)
	protected String reference;
	
	/** name of the location for display purposes */
	@NotBlank
	@Size(max = 60)
	protected String displayName;
		
	/** last modified timestamp provided by the backing datastore */
	protected Long lastModified;
	
	/**
	 * Constructs a new Organization Summary from the provided information
	 * 
	 * @param locationId
	 * @param organizationId
	 * @param status
	 * @param reference
	 * @param displayName
	 * @param lastModified
	 */
	public LocationSummary(LocationIdentifier locationId, OrganizationIdentifier organizationId, Status status, String reference, String displayName, Long lastModified) {
		super();
		this.locationId = locationId;
		this.organizationId = organizationId;
		this.status = status;
		this.reference = reference;
		this.displayName = displayName;
		this.lastModified = lastModified;
	}

	/**
	 * returns the unique identifier for the location
	 * @return
	 */
	public LocationIdentifier getLocationId() {
		return locationId;
	}
	
	/**
	 * returns the unique identifier for the associated organization
	 * @return
	 */
	public OrganizationIdentifier getOrganizationId() {
		return organizationId;
	}
	
	/**
	 * returns the reference for this location
	 * @return
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * returns the current status of this location
	 * @return
	 */
	public Status getStatus() {
		return status;
	}
	
	/**
	 * returns the display name associated with the location
	 * @return
	 */
	public String getDisplayName() {
		return displayName;
	}
	
	/**
	 * returns the last modified timestamp for the instance
	 * @return
	 */
	public Long getLastModified() {
		return lastModified;
	}
	
	/**
	 * returns a copy of the location with the new status provided
	 * @param status
	 * @return
	 */
	public LocationSummary withStatus(Status status) {
		return new LocationSummary(locationId, organizationId, status, reference, displayName, lastModified);
	}
	
	/**
	 * returns a copy of the location with the new reference provided
	 * @param reference
	 * @return
	 */
	public LocationSummary withReference(String reference) {
		return new LocationSummary(locationId, organizationId, status, reference, displayName, lastModified);
	}

	/**
	 * returns a copy of the location with the new display name provided
	 * @param displayName
	 * @return
	 */
	public LocationSummary withDisplayName(String displayName) {
		return new LocationSummary(locationId, organizationId, status, reference, displayName, lastModified);
	}
	
	/**
	 * returns a copy of the location with the new last modified provided
	 * @param lastModified
	 * @return
	 */
	public LocationSummary withLastModified(Long lastModified) {
		return new LocationSummary(locationId, organizationId, status, reference, displayName, lastModified);
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