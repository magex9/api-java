package ca.magex.crm.policy.secure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.policies.CrmLocationPolicy;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.system.Identifier;

@Component
@Primary
@Profile(value = {
		MagexCrmProfiles.CRM_AUTH_EMBEDDED,
		MagexCrmProfiles.CRM_AUTH_REMOTE
})
public class SecureCrmLocationPolicy extends AbstractSecureCrmPolicy implements CrmLocationPolicy {

	@Autowired private CrmLocationService locationService;

	@Override
	public boolean canCreateLocationForOrganization(Identifier organizationId) {
		User currentUser = getCurrentUser();
		/* if the user is a CRM Admin then return true */
		if (isCrmAdmin(currentUser)) {
			return true;
		}
		/*
		 * if the person belongs to the organization, then return true if they are an RE
		 * Admin
		 */
		if (currentUser.getPerson().getOrganizationId().equals(organizationId)) {
			return isReAdmin(currentUser);
		}
		/* the person doesn't belong to the organization that owns this location */
		return false;
	}

	@Override
	public boolean canViewLocation(Identifier locationId) {
		User currentUser = getCurrentUser();
		/* if the user is a CRM_ADMIN then return true */
		if (isCrmAdmin(currentUser)) {
			return true;
		}
		/* ensure this location belongs to the same organization as the current user */
		LocationDetails location = locationService.findLocationDetails(locationId);
		return currentUser.getPerson().getOrganizationId().equals(location.getOrganizationId());
	}

	@Override
	public boolean canUpdateLocation(Identifier locationId) {
		User currentUser = getCurrentUser();
		/* if the user is a CRM_ADMIN then return true */
		if (isCrmAdmin(currentUser)) {
			return true;
		}
		/* ensure this location belongs to the same organization as the current user */
		LocationDetails location = locationService.findLocationDetails(locationId);
		if (currentUser.getPerson().getOrganizationId().equals(location.getOrganizationId())) {
			return isReAdmin(currentUser);
		}
		/* the person doesn't belong to the organization that owns this location */
		return false;
	}

	@Override
	public boolean canEnableLocation(Identifier locationId) {
		User currentUser = getCurrentUser();
		/* if the user is a CRM_ADMIN then return true */
		if (isCrmAdmin(currentUser)) {
			return true;
		}
		/* ensure this location belongs to the same organization as the current user */
		LocationDetails location = locationService.findLocationDetails(locationId);
		if (currentUser.getPerson().getOrganizationId().equals(location.getOrganizationId())) {
			return isReAdmin(currentUser);
		}
		/* the person doesn't belong to the organization that owns this location */
		return false;
	}

	@Override
	public boolean canDisableLocation(Identifier locationId) {
		User currentUser = getCurrentUser();
		/* if the user is a CRM_ADMIN then return true */
		if (isCrmAdmin(currentUser)) {
			return true;
		}
		/* ensure this location belongs to the same organization as the current user */
		LocationDetails location = locationService.findLocationDetails(locationId);
		if (currentUser.getPerson().getOrganizationId().equals(location.getOrganizationId())) {
			return isReAdmin(currentUser);
		}
		/* the person doesn't belong to the organization that owns this location */
		return false;
	}
}
