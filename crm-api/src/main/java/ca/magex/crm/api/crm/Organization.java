package ca.magex.crm.api.crm;

import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class Organization {

	private Identifier organizationId;
	
	private Status status;
	
	private String displayName;
	
	private Identifier mainLocation;

	public Organization(Identifier organizationId, Status status, String displayName, Identifier mainLocation) {
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
	
	public Organization withStatus(Status status) {
		return new Organization(organizationId, status, displayName, mainLocation);
	}

	public String getDisplayName() {
		return displayName;
	}
	
	public Organization withDisplayName(String displayName) {
		return new Organization(organizationId, status, displayName, mainLocation);
	}

	public Identifier getMainLocation() {
		return mainLocation;
	}

	public Organization withMainLocation(Identifier mainLocation) {
		return new Organization(organizationId, status, displayName, mainLocation);
	}

}
