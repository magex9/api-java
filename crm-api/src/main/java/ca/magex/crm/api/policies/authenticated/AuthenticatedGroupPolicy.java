package ca.magex.crm.api.policies.authenticated;

import static ca.magex.crm.api.services.CrmAuthenticationService.CRM_ADMIN;

import ca.magex.crm.api.policies.CrmGroupPolicy;
import ca.magex.crm.api.policies.basic.BasicGroupPolicy;
import ca.magex.crm.api.services.CrmAuthenticationService;
import ca.magex.crm.api.services.CrmGroupService;
import ca.magex.crm.api.system.Identifier;

public class AuthenticatedGroupPolicy implements CrmGroupPolicy {
	
	private CrmAuthenticationService auth;

	private CrmGroupPolicy delegate;
	
	/**
	 * Authenticated Group Policy handles roles and association checks required for policy approval
	 * 
	 * @param auth
	 * @param permissions
	 * @param users
	 */
	public AuthenticatedGroupPolicy(
			CrmAuthenticationService auth,
			CrmGroupService groups) {
		this.auth = auth;
		this.delegate = new BasicGroupPolicy(groups);
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

}