package ca.magex.crm.api.policies.basic;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.policies.CrmPermissionPolicy;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

@Component
@Primary
@Profile(MagexCrmProfiles.CRM_NO_AUTH)
public class BasicPermissionPolicy implements CrmPermissionPolicy {

	private CrmPermissionService permissionService;
	
	/**
	 * Basic Permission Policy handles presence and status checks require for policy approval
	 * 
	 * @param permissionService
	 */
	public BasicPermissionPolicy(
			CrmPermissionService permissionService) {
		this.permissionService = permissionService;
	}
	
	@Override
	public boolean canCreateGroup() {
		/* always return true */
		return true;
	}

	@Override
	public boolean canViewGroup(String group) {
		try {
			/* can view a group if it exists */
			permissionService.findGroupByCode(group);
			return true;
		}
		catch(ItemNotFoundException e) {
			return false;
		}
	}

	@Override
	public boolean canViewGroup(Identifier groupId) {
		try {
			/* can view a group if it exists */
			permissionService.findGroup(groupId);
			return true;
		}
		catch(ItemNotFoundException e) {
			return false;
		}
	}

	@Override
	public boolean canUpdateGroup(Identifier groupId) {
		try {
			/* can update a group if it exists and is active */
			return permissionService.findGroup(groupId).getStatus() == Status.ACTIVE;
		}
		catch(ItemNotFoundException e) {
			return false;
		}
	}

	@Override
	public boolean canEnableGroup(Identifier groupId) {
		try {
			/* can enable a group if it exists */
			permissionService.findGroup(groupId);
			return true;
		} catch (ItemNotFoundException e) {
			return false;
		}
	}

	@Override
	public boolean canDisableGroup(Identifier groupId) {
		try {
			/* can disable a group if it exists */
			permissionService.findGroup(groupId);
			return true;
		} catch (ItemNotFoundException e) {
			return false;
		}
	}

	@Override
	public boolean canCreateRole(Identifier groupId) {
		try {
			/* can create a role for this group if it exists */
			permissionService.findGroup(groupId);
			return true;
		} catch (ItemNotFoundException e) {
			return false;
		}
	}

	@Override
	public boolean canViewRoles() {
		/* can always view roles */
		return true;
	}

	@Override
	public boolean canViewRole(String code) {		
		try {
			/* can view a specific role if it exists */
			permissionService.findRoleByCode(code);
			return true;
		} catch (ItemNotFoundException e) {
			return false;
		}
	}

	@Override
	public boolean canViewRole(Identifier roleId) {
		try {
			/* can view a specific role if it exists */
			permissionService.findRole(roleId);
			return true;
		} catch (ItemNotFoundException e) {
			return false;
		}
	}

	@Override
	public boolean canUpdateRole(Identifier roleId) {
		try {
			/* can view a specific role if it exists and is active */
			return permissionService.findRole(roleId).getStatus() == Status.ACTIVE;
		} catch (ItemNotFoundException e) {
			return false;
		}
	}

	@Override
	public boolean canEnableRole(Identifier roleId) {
		try {
			/* can enable a specific role if it exists */
			permissionService.findRole(roleId);
			return true;
		} catch (ItemNotFoundException e) {
			return false;
		}
	}

	@Override
	public boolean canDisableRole(Identifier roleId) {
		try {
			/* can disable a specific role if it exists */
			permissionService.findRole(roleId);
			return true;
		} catch (ItemNotFoundException e) {
			return false;
		}
	}
}