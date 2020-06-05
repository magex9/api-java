package ca.magex.crm.api.policies.basic;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.policies.CrmPermissionPolicy;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

@Component
@Primary
@Profile(MagexCrmProfiles.CRM_NO_AUTH)
public class BasicPermissionPolicy implements CrmPermissionPolicy {

	private CrmPermissionService permissions;

	/**
	 * Basic Permission Policy handles presence and status checks require for policy approval
	 * 
	 * @param permissions
	 */
	public BasicPermissionPolicy(CrmPermissionService permissions) {
		this.permissions = permissions;
	}

	@Override
	public boolean canCreateGroup() {
		/* always return true */
		return true;
	}

	@Override
	public boolean canViewGroup(String group) {
		/* can view a group if it exists */
		if (permissions.findGroupByCode(group) == null) {
			throw new ItemNotFoundException("Group Code '" + group + "'");
		}
		return true;
	}

	@Override
	public boolean canViewGroup(Identifier groupId) {
		/* can view a group if it exists */
		if (permissions.findGroup(groupId) == null) {
			throw new ItemNotFoundException("Group ID '" + groupId + "'");
		}
		return true;
	}

	@Override
	public boolean canUpdateGroup(Identifier groupId) {
		/* can update a group if it exists and is active */
		Group group = permissions.findGroup(groupId);
		if (group == null) {
			throw new ItemNotFoundException("Group ID '" + groupId + "'");
		}
		return group.getStatus() == Status.ACTIVE;
	}

	@Override
	public boolean canEnableGroup(Identifier groupId) {
		/* can enable a group if it exists */
		if (permissions.findGroup(groupId) == null) {
			throw new ItemNotFoundException("Group ID '" + groupId + "'");
		}
		return true;
	}

	@Override
	public boolean canDisableGroup(Identifier groupId) {
		/* can disable a group if it exists */
		if (permissions.findGroup(groupId) == null) {
			throw new ItemNotFoundException("Group ID '" + groupId + "'");
		}
		return true;
	}

	@Override
	public boolean canCreateRole(Identifier groupId) {
		/* can create a role for this group if it exists */
		if (permissions.findGroup(groupId) == null) {
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
		if (permissions.findRoleByCode(code) == null) {
			throw new ItemNotFoundException("Role Code '" + code + "'");
		}
		return true;
	}

	@Override
	public boolean canViewRole(Identifier roleId) {
		/* can view a specific role if it exists */
		if (permissions.findRole(roleId) == null) {
			throw new ItemNotFoundException("Role ID '" + roleId + "'");
		}
		return true;
	}

	@Override
	public boolean canUpdateRole(Identifier roleId) {
		/* can view a specific role if it exists and is active */
		Role role = permissions.findRole(roleId);
		if (role == null) {
			throw new ItemNotFoundException("Role ID '" + roleId + "'");
		}
		return permissions.findRole(roleId).getStatus() == Status.ACTIVE;
	}

	@Override
	public boolean canEnableRole(Identifier roleId) {
		/* can enable a specific role if it exists */
		if (permissions.findRole(roleId) == null) {
			throw new ItemNotFoundException("Role ID '" + roleId + "'");
		}
		return true;
	}

	@Override
	public boolean canDisableRole(Identifier roleId) {
		/* can disable a specific role if it exists */
		if (permissions.findRole(roleId) == null) {
			throw new ItemNotFoundException("Role ID '" + roleId + "'");
		}
		return true;
	}

}