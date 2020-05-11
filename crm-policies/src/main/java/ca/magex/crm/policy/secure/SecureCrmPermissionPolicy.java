package ca.magex.crm.policy.secure;

import org.springframework.beans.factory.annotation.Autowired;
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
@Profile(MagexCrmProfiles.CRM_AUTH)
public class SecureCrmPermissionPolicy extends AbstractSecureCrmPolicy implements CrmPermissionPolicy {

	@Autowired private CrmPermissionService permissionService;

	@Override
	public boolean canCreateGroup() {
		return isCrmAdmin(getCurrentUser());
	}

	@Override
	public boolean canViewGroup(String group) {
		return isCrmAdmin(getCurrentUser());
	}

	@Override
	public boolean canViewGroup(Identifier groupId) {
		return isCrmAdmin(getCurrentUser());
	}

	@Override
	public boolean canUpdateGroup(Identifier groupId) {
		return isCrmAdmin(getCurrentUser());
	}

	@Override
	public boolean canEnableGroup(Identifier groupId) {
		try {
			return permissionService.findGroup(groupId).getStatus().equals(Status.INACTIVE);
		} catch (ItemNotFoundException e) {
			return false;
		}
	}

	@Override
	public boolean canDisableGroup(Identifier groupId) {
		try {
			return permissionService.findGroup(groupId).getStatus().equals(Status.ACTIVE);
		} catch (ItemNotFoundException e) {
			return false;
		}
	}

	@Override
	public boolean canCreateRole() {
		return isCrmAdmin(getCurrentUser());
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
		return isCrmAdmin(getCurrentUser());
	}

	@Override
	public boolean canEnableRole(Identifier roleId) {
		try {
			return permissionService.findRole(roleId).getStatus().equals(Status.INACTIVE);
		} catch (ItemNotFoundException e) {
			return false;
		}
	}

	@Override
	public boolean canDisableRole(Identifier roleId) {
		try {
			return permissionService.findRole(roleId).getStatus().equals(Status.INACTIVE);
		} catch (ItemNotFoundException e) {
			return false;
		}
	}

	@Override
	public boolean canViewPermissions() {
		return isCrmAdmin(getCurrentUser());
	}

}