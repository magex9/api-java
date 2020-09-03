package ca.magex.crm.api.policies.basic;

import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.policies.CrmLocationPolicy;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;

public class BasicLocationPolicy implements CrmLocationPolicy {

	private CrmOrganizationService organizations;
	
	private CrmLocationService locations;
	
	/**
	 * Basic Location Policy handles presence and status checks require for policy approval
	 * 
	 * @param organizations
	 * @param locations
	 */
	public BasicLocationPolicy(CrmOrganizationService organizations, CrmLocationService locations) {
		this.organizations = organizations;
		this.locations = locations;
	}
	
	@Override
	public boolean canCreateLocationForOrganization(OrganizationIdentifier organizationId) {
		/* can only create a location for the organization, if the organization exists, and is active */
		OrganizationSummary summary = organizations.findOrganizationSummary(organizationId);
		if (summary == null) {
			throw new ItemNotFoundException("Organization ID '" + organizationId + "'");
		}
		return summary.getStatus() == Status.ACTIVE;
	}

	@Override
	public boolean canViewLocation(LocationIdentifier locationId) {
		/* can only view a location if it exists */
		if (locations.findLocationSummary(locationId) == null) {
			throw new ItemNotFoundException("Location ID '" + locationId + "'");
		}
		return true;
	}

	@Override
	public boolean canUpdateLocation(LocationIdentifier locationId) {
		/* can only update a location if it exists, and is active */
		LocationSummary summary = locations.findLocationSummary(locationId);
		if (summary == null) {
			throw new ItemNotFoundException("Location ID '" + locationId + "'");
		}
		return summary.getStatus() == Status.ACTIVE;
	}

	@Override
	public boolean canEnableLocation(LocationIdentifier locationId) {
		/* can only enable a location if it exists */
		LocationSummary summary = locations.findLocationSummary(locationId);
		if (summary == null) {
			throw new ItemNotFoundException("Location ID '" + locationId + "'");
		}
		return !summary.getStatus().equals(Status.ACTIVE) &&
			organizations.findOrganizationDetails(summary.getOrganizationId()).getStatus().equals(Status.ACTIVE);
	}

	@Override
	public boolean canDisableLocation(LocationIdentifier locationId) {
		/* can only disable a location if it exists */
		LocationSummary summary = locations.findLocationSummary(locationId);
		if (summary == null) {
			throw new ItemNotFoundException("Location ID '" + locationId + "'");
		}
		return summary.getStatus().equals(Status.ACTIVE) && (
			organizations.findOrganizationDetails(summary.getOrganizationId()).getMainLocationId() == null ||
			!organizations.findOrganizationDetails(summary.getOrganizationId()).getMainLocationId().equals(locationId)
		);
	}
}