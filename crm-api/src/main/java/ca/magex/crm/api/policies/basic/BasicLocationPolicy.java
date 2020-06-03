package ca.magex.crm.api.policies.basic;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.policies.CrmLocationPolicy;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

@Component
@Primary
@Profile(MagexCrmProfiles.CRM_NO_AUTH)
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
	public boolean canCreateLocationForOrganization(Identifier organizationId) {
		/* can only create a location for the organization, if the organization exists, and is active */
		return organizations.findOrganizationSummary(organizationId).getStatus() == Status.ACTIVE;
	}

	@Override
	public boolean canViewLocation(Identifier locationId) {
		/* can only view a location if it exists */
		locations.findLocationSummary(locationId);
		return true;
	}

	@Override
	public boolean canUpdateLocation(Identifier locationId) {
		/* can only update a location if it exists, and is active */
		return locations.findLocationSummary(locationId).getStatus() == Status.ACTIVE;
	}

	@Override
	public boolean canEnableLocation(Identifier locationId) {
		/* can only enable a location if it exists */
		locations.findLocationSummary(locationId);
		return true;
	}

	@Override
	public boolean canDisableLocation(Identifier locationId) {
		/* can only disable a location if it exists */
		locations.findLocationSummary(locationId);
		return true;
	}
}