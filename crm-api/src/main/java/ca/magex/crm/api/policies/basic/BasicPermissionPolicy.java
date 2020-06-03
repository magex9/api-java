package ca.magex.crm.api.policies.basic;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.policies.CrmPermissionPolicy;
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
		permissions.findGroupByCode(group);
		return true;
	}

	@Override
	public boolean canViewGroup(Identifier groupId) {
		/* can view a group if it exists */
		permissions.findGroup(groupId);
		return true;
	}

	@Override
	public boolean canUpdateGroup(Identifier groupId) {
		/* can update a group if it exists and is active */
		return permissions.findGroup(groupId).getStatus() == Status.ACTIVE;
	}

	@Override
	public boolean canEnableGroup(Identifier groupId) {
		/* can enable a group if it exists */
		permissions.findGroup(groupId);
		return true;
	}

	@Override
	public boolean canDisableGroup(Identifier groupId) {
		/* can disable a group if it exists */
		permissions.findGroup(groupId);
		return true;
	}

	@Override
	public boolean canCreateRole(Identifier groupId) {
		/* can create a role for this group if it exists */
		permissions.findGroup(groupId);
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
		permissions.findRoleByCode(code);
		return true;
	}

	@Override
	public boolean canViewRole(Identifier roleId) {
		/* can view a specific role if it exists */
		permissions.findRole(roleId);
		return true;
	}

	@Override
	public boolean canUpdateRole(Identifier roleId) {
		/* can view a specific role if it exists and is active */
		return permissions.findRole(roleId).getStatus() == Status.ACTIVE;
	}

	@Override
	public boolean canEnableRole(Identifier roleId) {
		/* can enable a specific role if it exists */
		permissions.findRole(roleId);
		return true;
	}

	@Override
	public boolean canDisableRole(Identifier roleId) {
		/* can disable a specific role if it exists */
		permissions.findRole(roleId);
		return true;
	}
	
}