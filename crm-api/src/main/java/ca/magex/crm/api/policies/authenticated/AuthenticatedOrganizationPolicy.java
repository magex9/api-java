package ca.magex.crm.api.policies.authenticated;

import static ca.magex.crm.api.services.CrmAuthenticationService.CRM_ADMIN;
import static ca.magex.crm.api.services.CrmAuthenticationService.ORG_ADMIN;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.policies.CrmOrganizationPolicy;
import ca.magex.crm.api.policies.basic.BasicOrganizationPolicy;
import ca.magex.crm.api.services.CrmAuthenticationService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.system.Identifier;

@Component
@Primary
@Profile(MagexCrmProfiles.CRM_AUTH)
public class AuthenticatedOrganizationPolicy implements CrmOrganizationPolicy {
	
	private CrmAuthenticationService auth;

	private CrmOrganizationPolicy delegate;
	
	/**
	 * Authenticated Organization Policy handles roles and association checks required for policy approval
	 * 
	 * @param auth
	 * @param organizations
	 * @param locationService
	 * @param userService
	 */
	public AuthenticatedOrganizationPolicy(
			CrmAuthenticationService auth,
			CrmOrganizationService organizations) {
		this.auth = auth;
		this.delegate = new BasicOrganizationPolicy(organizations);
	}
	
	@Override
	public boolean canCreateOrganization() {
		if (!delegate.canCreateOrganization()) {
			return false;
		}
		/* if the user is a CRM Admin then return true */
		return auth.isUserInRole(CRM_ADMIN);
	}

	@Override
	public boolean canViewOrganization(Identifier organizationId) {
		if (!delegate.canViewOrganization(organizationId)) {
			return false;
		}
		/* if the currentUser is a CRM_ADMIN then return true */
		if (auth.isUserInRole(CRM_ADMIN)) {
			return true;
		}
		/* return true if the person is associated with the organization */
		return auth.getOrganizationId().equals(organizationId);
	}

	@Override
	public boolean canUpdateOrganization(Identifier organizationId) {
		if (!delegate.canUpdateOrganization(organizationId)) {
			return false;
		}
		/* if the currentUser is a CRM_ADMIN then return true */
		if (auth.isUserInRole(CRM_ADMIN)) {
			return true;
		}
		/* ensure the current user is associated with the organization, and return true if they are an RE Admin */
		if (auth.getOrganizationId().equals(organizationId)) {
			return auth.isUserInRole(ORG_ADMIN);
		}
		/* the current user is not associated with the organization */
		return false;
	}

	@Override
	public boolean canEnableOrganization(Identifier organizationId) {
		if (!delegate.canEnableOrganization(organizationId)) {
			return false;
		}
		/* only CRM_ADMIN can enable an organization */
		return auth.isUserInRole(CRM_ADMIN);
	}

	@Override
	public boolean canDisableOrganization(Identifier organizationId) {
		if (!delegate.canDisableOrganization(organizationId)) {
			return false;
		}
		/* only CRM_ADMIN can disable an organization */
		return auth.isUserInRole(CRM_ADMIN);
	}
}