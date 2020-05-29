package ca.magex.crm.api.policies.authenticated;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.policies.CrmOrganizationPolicy;
import ca.magex.crm.api.policies.basic.BasicOrganizationPolicy;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmAuthenticationService;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.Identifier;

@Component
@Primary
@Profile(MagexCrmProfiles.CRM_AUTH)
public class AuthenticatedOrganizationPolicy extends BaseAuthenticatedPolicy implements CrmOrganizationPolicy {

	private CrmOrganizationPolicy basicPolicy;
	
	/**
	 * Authenticated Organization Policy handles roles and association checks required for policy approval
	 * 
	 * @param authenticationService
	 * @param organizationService
	 * @param locationService
	 * @param userService
	 */
	public AuthenticatedOrganizationPolicy(
			CrmAuthenticationService authenticationService,
			CrmOrganizationService organizationService,
			CrmLocationService locationService,
			CrmUserService userService) {
		super(authenticationService, userService);
		this.basicPolicy = new BasicOrganizationPolicy(organizationService);
	}
	
	@Override
	public boolean canCreateOrganization() {
		if (!basicPolicy.canCreateOrganization()) {
			return false;
		}
		User currentUser = getCurrentUser();
		/* if the user is a CRM Admin then return true */
		return isCrmAdmin(currentUser);
	}

	@Override
	public boolean canViewOrganization(Identifier organizationId) {
		if (!basicPolicy.canViewOrganization(organizationId)) {
			return false;
		}
		User currentUser = getCurrentUser();
		/* if the currentUser is a CRM_ADMIN then return true */
		if (isCrmAdmin(currentUser)) {
			return true;
		}
		/* return true if the person is associated with the organization */
		return currentUser.getPerson().getOrganizationId().equals(organizationId);
	}

	@Override
	public boolean canUpdateOrganization(Identifier organizationId) {
		if (!basicPolicy.canUpdateOrganization(organizationId)) {
			return false;
		}
		User currentUser = getCurrentUser();
		/* if the currentUser is a CRM_ADMIN then return true */
		if (isCrmAdmin(currentUser)) {
			return true;
		}
		/* ensure the current user is associated with the organization, and return true if they are an RE Admin */
		if (currentUser.getPerson().getOrganizationId().equals(organizationId)) {
			return isReAdmin(currentUser);
		}
		/* the current user is not associated with the organization */
		return false;
	}

	@Override
	public boolean canEnableOrganization(Identifier organizationId) {
		if (!basicPolicy.canEnableOrganization(organizationId)) {
			return false;
		}
		User currentUser = getCurrentUser();
		/* only CRM_ADMIN can enable an organization */
		return isCrmAdmin(currentUser);
	}

	@Override
	public boolean canDisableOrganization(Identifier organizationId) {
		if (!basicPolicy.canDisableOrganization(organizationId)) {
			return false;
		}
		User currentUser = getCurrentUser();
		/* only CRM_ADMIN can disable an organization */
		return isCrmAdmin(currentUser);
	}
}