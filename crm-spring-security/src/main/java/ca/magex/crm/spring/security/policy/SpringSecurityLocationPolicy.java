package ca.magex.crm.spring.security.policy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.common.User;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.policies.CrmLocationPolicy;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.system.Identifier;

@Component
public class SpringSecurityLocationPolicy extends AbstractSpringSecurityPolicy implements CrmLocationPolicy {

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
		if (currentUser.getOrganizationId().equals(organizationId)) {
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
		return currentUser.getOrganizationId().equals(location.getOrganizationId());
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
		if (currentUser.getOrganizationId().equals(location.getOrganizationId())) {
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
		if (currentUser.getOrganizationId().equals(location.getOrganizationId())) {
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
		if (currentUser.getOrganizationId().equals(location.getOrganizationId())) {
			return isReAdmin(currentUser);
		}
		/* the person doesn't belong to the organization that owns this location */
		return false;
	}
}
