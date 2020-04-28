package ca.magex.crm.spring.security.policy;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.common.User;
import ca.magex.crm.api.policies.CrmOrganizationPolicy;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.spring.security.MagexSecurityProfile;

@Component
@Profile({MagexSecurityProfile.EMBEDDED_JWT, MagexSecurityProfile.REMOTE_JWT})
public class SpringSecurityOrganizationPolicy extends AbstractSpringSecurityPolicy implements CrmOrganizationPolicy {

	@Override
	public boolean canCreateOrganization() {
		return getCurrentUser().getRoles().stream().filter((r) -> r.contentEquals("CRM_ADMIN")).findAny().isPresent();
	}

	@Override
	public boolean canViewOrganization(Identifier organizationId) {
		User currentUser = getCurrentUser();
		/* if the currentUser is a CRM_ADMIN then return true */
		if (isCrmAdmin(currentUser)) {
			return true;
		}
		/* return true if the person belongs to the organization */
		return currentUser.getOrganizationId().equals(organizationId);
	}

	@Override
	public boolean canUpdateOrganization(Identifier organizationId) {
		User currentUser = getCurrentUser();
		/* if the currentUser is a CRM_ADMIN then return true */
		if (isCrmAdmin(currentUser)) {
			return true;
		}
		/*
		 * ensure the organization is the same organization as the current currentUser
		 */
		if (currentUser.getOrganizationId().equals(organizationId)) {
			return isReAdmin(currentUser);
		}
		/* the person doesn't belong to the organization */
		return false;
	}

	@Override
	public boolean canEnableOrganization(Identifier organizationId) {
		User currentUser = getCurrentUser();
		/* only CRM_ADMIN can enable an organization */
		return isCrmAdmin(currentUser);
	}

	@Override
	public boolean canDisableOrganization(Identifier organizationId) {
		User currentUser = getCurrentUser();
		/* only CRM_ADMIN can disable an organization */
		return isCrmAdmin(currentUser);
	}
}