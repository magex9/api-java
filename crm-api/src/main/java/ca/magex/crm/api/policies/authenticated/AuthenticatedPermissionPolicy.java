package ca.magex.crm.api.policies.authenticated;

import static ca.magex.crm.api.services.CrmAuthenticationService.CRM_ADMIN;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.policies.CrmPermissionPolicy;
import ca.magex.crm.api.policies.basic.BasicPermissionPolicy;
import ca.magex.crm.api.services.CrmAuthenticationService;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.system.Identifier;

@Component
@Primary
@Profile(MagexCrmProfiles.CRM_AUTH)
public class AuthenticatedPermissionPolicy implements CrmPermissionPolicy {
	
	private CrmAuthenticationService auth;

	private CrmPermissionPolicy delegate;
	
	/**
	 * Authenticated Permission Policy handles roles and association checks required for policy approval
	 * 
	 * @param auth
	 * @param permissions
	 * @param users
	 */
	public AuthenticatedPermissionPolicy(
			CrmAuthenticationService auth,
			CrmPermissionService permissions) {
		this.auth = auth;
		this.delegate = new BasicPermissionPolicy(permissions);
	}

	@Override
	public boolean canCreateGroup() {
		if (!delegate.canCreateGroup()) {
			return false;
		}
		/* only a CRM Admin can create a Group */
		return auth.isUserInRole(CRM_ADMIN);
	}

	@Override
	public boolean canViewGroup(String group) {
		if (!delegate.canViewGroup(group)) {
			return false;
		}
		/* anybody can view a group */
		return true;
	}

	@Override
	public boolean canViewGroup(Identifier groupId) {
		if (!delegate.canViewGroup(groupId)) {
			return false;
		}
		/* anybody can view a group */
		return true;
	}

	@Override
	public boolean canUpdateGroup(Identifier groupId) {
		if (!delegate.canUpdateGroup(groupId)) {
			return false;
		}
		/* only a CRM Admin can update a Group */
		return auth.isUserInRole(CRM_ADMIN);
	}

	@Override
	public boolean canEnableGroup(Identifier groupId) {
		if (!delegate.canEnableGroup(groupId)) {
			return false;
		}
		/* only a CRM Admin can enable a Group */
		return auth.isUserInRole(CRM_ADMIN);
	}

	@Override
	public boolean canDisableGroup(Identifier groupId) {
		if (!delegate.canDisableGroup(groupId)) {
			return false;
		}
		/* only a CRM Admin can disable a Group */
		return auth.isUserInRole(CRM_ADMIN);
	}

	@Override
	public boolean canCreateRole(Identifier groupId) {
		if (!delegate.canCreateRole(groupId)) {
			return false;
		}
		/* only a CRM Admin can create a Role */
		return auth.isUserInRole(CRM_ADMIN);
	}

	@Override
	public boolean canViewRoles() {
		if (!delegate.canViewRoles()) {
			return false;
		}
		/* anybody can view roles */
		return true;
	}

	@Override
	public boolean canViewRole(String code) {
		if (!delegate.canViewRole(code)) {
			return false;
		}
		/* anybody can view a role */
		return true;
	}

	@Override
	public boolean canViewRole(Identifier roleId) {
		if (!delegate.canViewRole(roleId)) {
			return false;
		}
		/* anybody can view a role */
		return true;
	}

	@Override
	public boolean canUpdateRole(Identifier roleId) {
		if (!delegate.canUpdateRole(roleId)) {
			return false;
		}
		/* only a CRM Admin can update a Role */
		return auth.isUserInRole(CRM_ADMIN);
	}

	@Override
	public boolean canEnableRole(Identifier roleId) {
		if (!delegate.canEnableRole(roleId)) {
			return false;
		}
		/* only a CRM Admin can enable a Role */
		return auth.isUserInRole(CRM_ADMIN);
	}

	@Override
	public boolean canDisableRole(Identifier roleId) {
		if (!delegate.canDisableRole(roleId)) {
			return false;
		}
		/* only a CRM Admin can disable a Role */
		return auth.isUserInRole(CRM_ADMIN);
	}
}