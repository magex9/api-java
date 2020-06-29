package ca.magex.crm.api.crm;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;

/**
 * An extension to the Location Summary with additional details associated to a location
 * 
 * @author Jonny
 */
public class LocationDetails extends LocationSummary {
	
	private static final long serialVersionUID = 1L;
	
	/** the address associated with this location */
	private MailingAddress address;
	
	/**
	 * Creates a full location details from the information provided
	 * @param locationId
	 * @param organizationId
	 * @param status
	 * @param reference
	 * @param displayName
	 * @param address
	 */
	public LocationDetails(LocationIdentifier locationId, OrganizationIdentifier organizationId, Status status, String reference, String displayName, MailingAddress address) {
		super(locationId, organizationId, status, reference, displayName);		
		this.address = address;
	}
	
	/**
	 * returns the associated address
	 * @return
	 */
	public MailingAddress getAddress() {
		return address;
	}

	@Override
	public LocationDetails withStatus(Status status) {
		return new LocationDetails(locationId, organizationId, status, reference, displayName, address);
	}

	@Override
	public LocationDetails withReference(String reference) {
		return new LocationDetails(locationId, organizationId, status, reference, displayName, address);
	}
	
	@Override
	public LocationDetails withDisplayName(String displayName) {
		return new LocationDetails(locationId, organizationId, status, reference, displayName, address);
	}

	/**
	 * returns a copy of the location with the associated address
	 * @param address
	 * @return
	 */
	public LocationDetails withAddress(MailingAddress address) {
		return new LocationDetails(locationId, organizationId, status, reference, displayName, address);
	}

	/**
	 * returns the summary information for this location
	 * @return
	 */
	public LocationSummary asSummary() {
		return new LocationSummary(locationId, organizationId, status, reference, displayName);
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