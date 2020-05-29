package ca.magex.crm.api.policies.basic;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.policies.CrmLocationPolicy;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

@Component
@Primary
@Profile(MagexCrmProfiles.CRM_NO_AUTH)
public class BasicLocationPolicy implements CrmLocationPolicy {

	private CrmLocationService locationService;
	private CrmOrganizationService organizationService;
	
	/**
	 * Basic Location Policy handles presence and status checks require for policy approval
	 * 
	 * @param organizationService
	 * @param locationService
	 */
	public BasicLocationPolicy(CrmOrganizationService organizationService, CrmLocationService locationService) {
		this.locationService = locationService;
	}
	
	@Override
	public boolean canCreateLocationForOrganization(Identifier organizationId) {
		try {
			/* can only create a location for the organization, if the organization exists, and is active */
			return organizationService.findOrganizationSummary(organizationId).getStatus() == Status.ACTIVE;
		} catch (ItemNotFoundException i) {
			return false;
		}
	}

	@Override
	public boolean canViewLocation(Identifier locationId) {
		try {
			/* can only view a location if it exists */
			locationService.findLocationSummary(locationId);
		} catch (ItemNotFoundException i) {
			return false;
		}
		return true;
	}

	@Override
	public boolean canUpdateLocation(Identifier locationId) {
		try {
			/* can only update a location if it exists, and is active */
			return locationService.findLocationSummary(locationId).getStatus() == Status.ACTIVE;
		} catch (ItemNotFoundException i) {
			return false;
		}
	}

	@Override
	public boolean canEnableLocation(Identifier locationId) {
		try {
			/* can only enable a location if it exists */
			locationService.findLocationSummary(locationId);
			return true;
		} catch (ItemNotFoundException i) {
			return false;
		}
	}

	@Override
	public boolean canDisableLocation(Identifier locationId) {
		try {
			/* can only disable a location if it exists */
			locationService.findLocationSummary(locationId);
			return true;
		} catch (ItemNotFoundException i) {
			return false;
		}
	}
}