package ca.magex.crm.api.policies.authenticated;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.policies.CrmLocationPolicy;
import ca.magex.crm.api.policies.basic.BasicLocationPolicy;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmAuthenticationService;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.Identifier;

@Component
@Primary
@Profile(MagexCrmProfiles.CRM_AUTH)
public class AuthenticatedLocationPolicy extends BaseAuthenticatedPolicy implements CrmLocationPolicy {

	private CrmLocationPolicy basicPolicy;
	private CrmLocationService locationService;

	/**
	 * Authenticated Location Policy handles roles and association checks required for policy approval
	 * 
	 * @param authenticationService
	 * @param organizationService
	 * @param locationService
	 * @param userService
	 */
	public AuthenticatedLocationPolicy(
			CrmAuthenticationService authenticationService,
			CrmOrganizationService organizationService,
			CrmLocationService locationService,
			CrmUserService userService) {
		super(authenticationService, userService);
		this.basicPolicy = new BasicLocationPolicy(organizationService, locationService);
		this.locationService = locationService;
	}

	@Override
	public boolean canCreateLocationForOrganization(Identifier organizationId) {
		if (!basicPolicy.canCreateLocationForOrganization(organizationId)) {
			return false;
		}
		User currentUser = getCurrentUser();
		/* if the user is a CRM Admin then return true */
		if (isCrmAdmin(currentUser)) {
			return true;
		}
		/* if the current user is associated with the organization, then return true if they are an RE Admin */
		if (currentUser.getPerson().getOrganizationId().equals(organizationId)) {
			return isReAdmin(currentUser);
		}
		/* the current user doesn't belong to the organization that this location is associated with */
		return false;
	}

	@Override
	public boolean canViewLocation(Identifier locationId) {
		if (!basicPolicy.canViewLocation(locationId)) {
			return false;
		}
		User currentUser = getCurrentUser();
		/* if the user is a CRM_ADMIN then return true */
		if (isCrmAdmin(currentUser)) {
			return true;
		}
		/* ensure this location is associated with the organization the current user is associated with */
		return currentUser.getPerson().getOrganizationId().equals(locationService.findLocationSummary(locationId).getOrganizationId());
	}

	@Override
	public boolean canUpdateLocation(Identifier locationId) {
		if (!basicPolicy.canUpdateLocation(locationId)) {
			return false;
		}
		User currentUser = getCurrentUser();
		/* if the user is a CRM_ADMIN then return true */
		if (isCrmAdmin(currentUser)) {
			return true;
		}
		/* ensure this location is associated with the organization the current user is associated with, then return true if they are an RE Admin */
		if (currentUser.getPerson().getOrganizationId().equals(locationService.findLocationSummary(locationId).getOrganizationId())) {
			return isReAdmin(currentUser);
		}
		/* the current user doesn't belong to the organization that this location is associated with */
		return false;
	}

	@Override
	public boolean canEnableLocation(Identifier locationId) {
		if (!basicPolicy.canEnableLocation(locationId)) {
			return false;
		}
		User currentUser = getCurrentUser();
		/* if the user is a CRM_ADMIN then return true */
		if (isCrmAdmin(currentUser)) {
			return true;
		}
		/* ensure this location is associated with the organization the current user is associated with, then return true if they are an RE Admin */
		if (currentUser.getPerson().getOrganizationId().equals(locationService.findLocationSummary(locationId).getOrganizationId())) {
			return isReAdmin(currentUser);
		}
		/* the current user doesn't belong to the organization that this location is associated with */
		return false;
	}

	@Override
	public boolean canDisableLocation(Identifier locationId) {
		if (!basicPolicy.canDisableLocation(locationId)) {
			return false;
		}
		User currentUser = getCurrentUser();
		/* if the user is a CRM_ADMIN then return true */
		if (isCrmAdmin(currentUser)) {
			return true;
		}
		/* ensure this location is associated with the organization the current user is associated with, then return true if they are an RE Admin */
		if (currentUser.getPerson().getOrganizationId().equals(locationService.findLocationSummary(locationId).getOrganizationId())) {
			return isReAdmin(currentUser);
		}
		/* the current user doesn't belong to the organization that this location is associated with */
		return false;
	}
}
