package ca.magex.crm.api.policies.basic;

import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.policies.CrmRolePolicy;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.services.CrmGroupService;
import ca.magex.crm.api.services.CrmRoleService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class BasicRolePolicy implements CrmRolePolicy {

	private CrmGroupService groups;
	
	private CrmRoleService roles;

	/**
	 * Basic Role Policy handles presence and status checks require for policy approval
	 * 
	 * @param roles
	 */
	public BasicRolePolicy(CrmGroupService groups, CrmRoleService roles) {
		this.groups = groups;
		this.roles = roles;
	}

	@Override
	public boolean canCreateRole(Identifier groupId) {
		/* can create a role for this group if it exists */
		if (groups.findGroup(groupId) == null) {
			throw new ItemNotFoundException("Group ID '" + groupId + "'");
		}
		return true;
	}

	@Override
	public boolean canViewRoles() {
		/* can always view roles */
		return true;
	}

	@Override
	public boolean canViewRole(String code) {
		/* can view a specific role if it exists */
		if (roles.findRoleByCode(code) == null) {
			throw new ItemNotFoundException("Role Code '" + code + "'");
		}
		return true;
	}

	@Override
	public boolean canViewRole(Identifier roleId) {
		/* can view a specific role if it exists */
		if (roles.findRole(roleId) == null) {
			throw new ItemNotFoundException("Role ID '" + roleId + "'");
		}
		return true;
	}

	@Override
	public boolean canUpdateRole(Identifier roleId) {
		/* can view a specific role if it exists and is active */
		Role role = roles.findRole(roleId);
		if (role == null) {
			throw new ItemNotFoundException("Role ID '" + roleId + "'");
		}
		return roles.findRole(roleId).getStatus() == Status.ACTIVE;
	}

	@Override
	public boolean canEnableRole(Identifier roleId) {
		/* can enable a specific role if it exists */
		if (roles.findRole(roleId) == null) {
			throw new ItemNotFoundException("Role ID '" + roleId + "'");
		}
		return true;
	}

	@Override
	public boolean canDisableRole(Identifier roleId) {
		/* can disable a specific role if it exists */
		if (roles.findRole(roleId) == null) {
			throw new ItemNotFoundException("Role ID '" + roleId + "'");
		}
		return true;
	}

}