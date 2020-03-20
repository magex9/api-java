package ca.magex.crm.api.crm;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class Location {

	private Identifier locationId;
	
	private Identifier organizationId;
	
	private Status status;
	
	private String reference;
	
	private String displayName;
	
	private MailingAddress address;

	public Location(Identifier locationId, Identifier organizationId, Status status, String reference,
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

	public String getReference() {
		return reference;
	}

	public String getDisplayName() {
		return displayName;
	}

	public MailingAddress getAddress() {
		return address;
	}
	
}
