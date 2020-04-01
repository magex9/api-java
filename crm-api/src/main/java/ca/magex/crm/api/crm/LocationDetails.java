package ca.magex.crm.api.crm;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class LocationDetails extends LocationSummary {
	
	private static final long serialVersionUID = 1L;
	
	private MailingAddress address;

	public LocationDetails(Identifier locationId, Identifier organizationId, Status status, String reference, String displayName, MailingAddress address) {
		super(locationId, organizationId, status, reference, displayName);		
		this.address = address;
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

	public MailingAddress getAddress() {
		return address;
	}
	
	public LocationDetails withAddress(MailingAddress address) {
		return new LocationDetails(locationId, organizationId, status, reference, displayName, address);
	}

	public LocationSummary toSummary() {
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