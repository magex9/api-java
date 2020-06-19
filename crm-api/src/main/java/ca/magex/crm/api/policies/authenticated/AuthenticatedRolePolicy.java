package ca.magex.crm.api.policies.authenticated;

import static ca.magex.crm.api.services.CrmAuthenticationService.CRM_ADMIN;

import ca.magex.crm.api.policies.CrmRolePolicy;
import ca.magex.crm.api.policies.basic.BasicRolePolicy;
import ca.magex.crm.api.services.CrmAuthenticationService;
import ca.magex.crm.api.services.CrmGroupService;
import ca.magex.crm.api.services.CrmRoleService;
import ca.magex.crm.api.system.Identifier;

public class AuthenticatedRolePolicy implements CrmRolePolicy {
	
	private CrmAuthenticationService auth;

	private CrmRolePolicy delegate;
	
	/**
	 * Authenticated Permission Policy handles roles and association checks required for policy approval
	 * 
	 * @param auth
	 * @param permissions
	 * @param users
	 */
	public AuthenticatedRolePolicy(
			CrmAuthenticationService auth,
			CrmGroupService groups,
			CrmRoleService roles) {
		this.auth = auth;
		this.delegate = new BasicRolePolicy(groups, roles);
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