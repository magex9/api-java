package ca.magex.crm.api.policies.authenticated;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.policies.CrmPermissionPolicy;
import ca.magex.crm.api.policies.basic.BasicPermissionPolicy;
import ca.magex.crm.api.services.CrmAuthenticationService;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.Identifier;

@Component
@Primary
@Profile(MagexCrmProfiles.CRM_AUTH)
public class AuthenticatedPermissionPolicy extends BaseAuthenticatedPolicy implements CrmPermissionPolicy {

	private CrmPermissionPolicy basicPolicy;
	
	/**
	 * Authenticated Permission Policy handles roles and association checks required for policy approval
	 * 
	 * @param authenticationService
	 * @param permissionService
	 * @param userService
	 */
	public AuthenticatedPermissionPolicy(
			CrmAuthenticationService authenticationService,
			CrmPermissionService permissionService,
			CrmUserService userService) {
		super(authenticationService, userService);
		this.basicPolicy = new BasicPermissionPolicy(permissionService);
	}

	@Override
	public boolean canCreateGroup() {
		if (!basicPolicy.canCreateGroup()) {
			return false;
		}
		/* only a CRM Admin can create a Group */
		return isCrmAdmin(getCurrentUser());
	}

	@Override
	public boolean canViewGroup(String group) {
		if (!basicPolicy.canViewGroup(group)) {
			return false;
		}
		/* anybody can view a group */
		return true;
	}

	@Override
	public boolean canViewGroup(Identifier groupId) {
		if (!basicPolicy.canViewGroup(groupId)) {
			return false;
		}
		/* anybody can view a group */
		return true;
	}

	@Override
	public boolean canUpdateGroup(Identifier groupId) {
		if (!basicPolicy.canUpdateGroup(groupId)) {
			return false;
		}
		/* only a CRM Admin can update a Group */
		return isCrmAdmin(getCurrentUser());
	}

	@Override
	public boolean canEnableGroup(Identifier groupId) {
		if (!basicPolicy.canEnableGroup(groupId)) {
			return false;
		}
		/* only a CRM Admin can enable a Group */
		return isCrmAdmin(getCurrentUser());
	}

	@Override
	public boolean canDisableGroup(Identifier groupId) {
		if (!basicPolicy.canDisableGroup(groupId)) {
			return false;
		}
		/* only a CRM Admin can disable a Group */
		return isCrmAdmin(getCurrentUser());
	}

	@Override
	public boolean canCreateRole(Identifier groupId) {
		if (!basicPolicy.canCreateRole(groupId)) {
			return false;
		}
		/* only a CRM Admin can create a Role */
		return isCrmAdmin(getCurrentUser());
	}

	@Override
	public boolean canViewRoles() {
		if (!basicPolicy.canViewRoles()) {
			return false;
		}
		/* anybody can view roles */
		return true;
	}

	@Override
	public boolean canViewRole(String code) {
		if (!basicPolicy.canViewRole(code)) {
			return false;
		}
		/* anybody can view a role */
		return true;
	}

	@Override
	public boolean canViewRole(Identifier roleId) {
		if (!basicPolicy.canViewRole(roleId)) {
			return false;
		}
		/* anybody can view a role */
		return true;
	}

	@Override
	public boolean canUpdateRole(Identifier roleId) {
		if (!basicPolicy.canUpdateRole(roleId)) {
			return false;
		}
		/* only a CRM Admin can update a Role */
		return isCrmAdmin(getCurrentUser());
	}

	@Override
	public boolean canEnableRole(Identifier roleId) {
		if (!basicPolicy.canEnableRole(roleId)) {
			return false;
		}
		/* only a CRM Admin can enable a Role */
		return isCrmAdmin(getCurrentUser());
	}

	@Override
	public boolean canDisableRole(Identifier roleId) {
		if (!basicPolicy.canDisableRole(roleId)) {
			return false;
		}
		/* only a CRM Admin can disable a Role */
		return isCrmAdmin(getCurrentUser());
	}
}