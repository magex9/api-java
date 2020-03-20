package ca.magex.crm.api.crm;

import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class Organization {

	private Identifier organizationId;
	
	private Status status;
	
	private String displayName;
	
	private Location mainLocation;

	public Organization(Identifier organizationId, Status status, String displayName, Location mainLocation) {
		super();
		this.organizationId = organizationId;
		this.status = status;
		this.displayName = displayName;
		this.mainLocation = mainLocation;
	}

	public Identifier getOrganizationId() {
		return organizationId;
	}

	public Status getStatus() {
		return status;
	}

	public String getDisplayName() {
		return displayName;
	}

	public Location getMainLocation() {
		return mainLocation;
	}

}
