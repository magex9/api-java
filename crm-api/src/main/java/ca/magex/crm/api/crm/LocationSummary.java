package ca.magex.crm.api.crm;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class LocationSummary implements Serializable {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	protected Identifier locationId;
	
	protected Identifier organizationId;
	
	protected Status status;
	
	protected String reference;
	
	protected String displayName;
	
	public LocationSummary(Identifier locationId, Identifier organizationId, Status status, String reference,
			String displayName) {
		super();
		this.locationId = locationId;
		this.organizationId = organizationId;
		this.status = status;
		this.reference = reference;
		this.displayName = displayName;
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
	
	public LocationSummary withStatus(Status status) {
		return new LocationSummary(locationId, organizationId, status, reference, displayName);
	}

	public String getReference() {
		return reference;
	}
	
	public LocationSummary withReference(String reference) {
		return new LocationSummary(locationId, organizationId, status, reference, displayName);
	}

	public String getDisplayName() {
		return displayName;
	}
	
	public LocationSummary withDisplayName(String displayName) {
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
