package ca.magex.crm.policy;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.policies.CrmPermissionPolicy;
import ca.magex.crm.api.system.Identifier;

@Component
@Primary
@Profile(MagexCrmProfiles.CRM_NO_AUTH)
public class DefaultCrmPermissionPolicy implements CrmPermissionPolicy {

	@Override
	public boolean canCreateGroup() {
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
		return 1 == 1 ? true : true;
	}

	@Override
	public boolean canDisableGroup(Identifier groupId) {
		return true;
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
