package ca.magex.crm.api.crm;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class LocationDetails {

	private Identifier locationId;
	
	private Identifier organizationId;
	
	private Status status;
	
	private String reference;
	
	private String displayName;
	
	private MailingAddress address;

	public LocationDetails(Identifier locationId, Identifier organizationId, Status status, String reference,
			String displayName, MailingAddress address) {
		super();
		this.locationId = locationId;
		this.organizationId = organizationId;
		this.status = status;
		this.reference = reference;
		this.displayName = displayName;
		this.address = address;
	}

	public Identifier getLocationId() {
		return locationId;
	}

	public Identifier getOrganizationId() {
		return organizationId;
	}

	public Status getStatus() {
		return status;
	}
	
	public LocationDetails withStatus(Status status) {
		return new LocationDetails(locationId, organizationId, status, reference, displayName, address);
	}

	public String getReference() {
		return reference;
	}
	
	public LocationDetails withReference(String reference) {
		return new LocationDetails(locationId, organizationId, status, reference, displayName, address);
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public LocationDetails withDisplayName(String displayName) {
		return new LocationDetails(locationId, organizationId, status, reference, displayName, address);
	}

	public MailingAddress getAddress() {
		return address;
	}
	
	public LocationDetails withAddress(MailingAddress address) {
		return new LocationDetails(locationId, organizationId, status, reference, displayName, address);
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
