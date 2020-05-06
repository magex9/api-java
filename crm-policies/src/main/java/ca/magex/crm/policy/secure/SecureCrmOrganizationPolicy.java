package ca.magex.crm.policy.secure;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.policies.CrmOrganizationPolicy;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.system.Identifier;

@Component
@Primary
@Profile(value = {
		MagexCrmProfiles.CRM_AUTH_EMBEDDED,
		MagexCrmProfiles.CRM_AUTH_REMOTE
})
public class SecureCrmOrganizationPolicy extends AbstractSecureCrmPolicy implements CrmOrganizationPolicy {

	@Override
	public boolean canCreateOrganization() {
		return userService.getRoles(getCurrentUser().getUserId()).stream().filter((r) -> r.toString().equals("CRM_ADMIN")).findAny().isPresent();
	}

	@Override
	public boolean canViewOrganization(Identifier organizationId) {
		User currentUser = getCurrentUser();
		/* if the currentUser is a CRM_ADMIN then return true */
		if (isCrmAdmin(currentUser)) {
			return true;
		}
		/* return true if the person belongs to the organization */
		return currentUser.getPerson().getOrganizationId().equals(organizationId);
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
		if (currentUser.getPerson().getOrganizationId().equals(organizationId)) {
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