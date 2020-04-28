package ca.magex.crm.policy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.policies.CrmLocationPolicy;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

@Component
@Profile(MagexCrmProfiles.CRM_NO_AUTH)
public class DefaultCrmLocationPolicy implements CrmLocationPolicy {

	@Autowired private CrmLocationService locationService;

	@Override
	public boolean canCreateLocationForOrganization(Identifier organizationId) {
		return true;
	}

	@Override
	public boolean canViewLocation(Identifier locationId) {
		try {
			locationService.findLocationSummary(locationId);
		} catch (ItemNotFoundException i) {
			return false;
		}
		return true;
	}

	@Override
	public boolean canUpdateLocation(Identifier locationId) {
		try {
			locationService.findLocationSummary(locationId);
		} catch (ItemNotFoundException i) {
			return false;
		}
		return true;
	}

	@Override
	public boolean canEnableLocation(Identifier locationId) {
		try {
			return locationService.findLocationSummary(locationId).getStatus() != Status.ACTIVE;
		} catch (ItemNotFoundException i) {
			return false;
		}
	}

	@Override
	public boolean canDisableLocation(Identifier locationId) {
		try {
			return locationService.findLocationSummary(locationId).getStatus() != Status.INACTIVE;
		} catch (ItemNotFoundException i) {
			return false;
		}
	}
}
