package ca.magex.crm.api.policies;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

@Component
@Primary
@Profile(MagexCrmProfiles.CRM_NO_AUTH)
public class BasicPermissionPolicy implements CrmPermissionPolicy {

	private CrmPermissionService permissionService;
	
	public BasicPermissionPolicy(CrmPermissionService permissionService) {
		super();
		this.permissionService = permissionService;
	}

	@Override
	public boolean canCreateGroup() {
		return true;
	}

	@Override
	public boolean canViewGroup(String group) {
		return true;
	}

	@Override
	public boolean canViewGroup(Identifier groupId) {
		return true;
	}

	@Override
	public boolean canUpdateGroup(Identifier groupId) {
		return true;
	}

	@Override
	public boolean canEnableGroup(Identifier groupId) {
		try {
			return permissionService.findGroup(groupId).getStatus() != Status.ACTIVE;
		} catch (ItemNotFoundException e) {
			return false;
		}
	}

	@Override
	public boolean canDisableGroup(Identifier groupId) {
		try {
			return permissionService.findGroup(groupId).getStatus() != Status.INACTIVE;
		} catch (ItemNotFoundException e) {
			return false;
		}
	}

	@Override
	public boolean canCreateRole() {
		return true; 
	}

	@Override
	public boolean canViewRoles() {
		return true;
	}

	@Override
	public boolean canViewRole(String code) {
		return true;
	}

	@Override
	public boolean canViewRole(Identifier roleId) {
		return true;
	}

	@Override
	public boolean canUpdateRole(Identifier roleId) {
		return true;
	}

	@Override
	public boolean canEnableRole(Identifier roleId) {
		return true;
	}

	@Override
	public boolean canDisableRole(Identifier roleId) {
		return true;
	}

	@Override
	public boolean canViewPermissions() {
		return true;
	}

}
