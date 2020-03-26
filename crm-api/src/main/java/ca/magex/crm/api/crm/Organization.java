package ca.magex.crm.api.crm;

import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class Organization {

	private Identifier organizationId;
	
	private Status status;
	
	private String displayName;
	
	private Identifier mainLocationId;

	public Organization(Identifier organizationId, Status status, String displayName, Identifier mainLocationId) {
		super();
		this.organizationId = organizationId;
		this.status = status;
		this.displayName = displayName;
		this.mainLocationId = mainLocationId;
	}

	public Identifier getOrganizationId() {
		return organizationId;
	}

	public Status getStatus() {
		return status;
	}
	
	public Organization withStatus(Status status) {
		return new Organization(organizationId, status, displayName, mainLocationId);
	}

	public String getDisplayName() {
		return displayName;
	}
	
	public Organization withDisplayName(String displayName) {
		return new Organization(organizationId, status, displayName, mainLocationId);
	}

	public Identifier getMainLocationId() {
		return mainLocationId;
	}

	public Organization withMainLocation(Identifier mainLocation) {
		return new Organization(organizationId, status, displayName, mainLocationId);
	}

}
