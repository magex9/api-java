package ca.magex.crm.api.policies;

import ca.magex.crm.api.system.Identifier;

public class CrmPolicyDelegate implements CrmPolicies {
	
	private CrmPermissionPolicy permissionPolicy;
	
	private CrmOrganizationPolicy organizationPolicy;
	
	private CrmLocationPolicy locationPolicy;
	
	private CrmPersonPolicy personPolicy;
	
	private CrmUserPolicy userPolicy;
	
	public CrmPolicyDelegate(
			CrmPermissionPolicy permissionPolicy, 
			CrmOrganizationPolicy organizationPolicy,
			CrmLocationPolicy locationPolicy, 
			CrmPersonPolicy personPolicy, 
			CrmUserPolicy userPolicy) {
		this.permissionPolicy = permissionPolicy;
		this.organizationPolicy = organizationPolicy;
		this.locationPolicy = locationPolicy;
		this.personPolicy = personPolicy;
		this.userPolicy = userPolicy;
	}

	@Override
	public boolean canCreateOrganization() {
		return organizationPolicy.canCreateOrganization();
	}

	@Override
	public boolean canViewOrganization(Identifier organizationId) {
		return organizationPolicy.canViewOrganization(organizationId);
	}

	@Override
	public boolean canUpdateOrganization(Identifier organizationId) {
		return organizationPolicy.canUpdateOrganization(organizationId);
	}

	@Override
	public boolean canEnableOrganization(Identifier organizationId) {
		return organizationPolicy.canEnableOrganization(organizationId);
	}

	@Override
	public boolean canDisableOrganization(Identifier organizationId) {
		return organizationPolicy.canDisableOrganization(organizationId);
	}

	@Override
	public boolean canCreateLocationForOrganization(Identifier organizationId) {
		return locationPolicy.canCreateLocationForOrganization(organizationId);
	}

	@Override
	public boolean canViewLocation(Identifier locationId) {
		return locationPolicy.canViewLocation(locationId);
	}

	@Override
	public boolean canUpdateLocation(Identifier locationId) {
		return locationPolicy.canUpdateLocation(locationId);
	}

	@Override
	public boolean canEnableLocation(Identifier locationId) {
		return locationPolicy.canEnableLocation(locationId);
	}

	@Override
	public boolean canDisableLocation(Identifier locationId) {
		return locationPolicy.canDisableLocation(locationId);
	}

	@Override
	public boolean canCreatePersonForOrganization(Identifier organizationId) {
		return personPolicy.canCreatePersonForOrganization(organizationId);
	}

	@Override
	public boolean canViewPerson(Identifier personId) {
		return personPolicy.canViewPerson(personId);
	}

	@Override
	public boolean canUpdatePerson(Identifier personId) {
		return personPolicy.canUpdatePerson(personId);
	}

	@Override
	public boolean canEnablePerson(Identifier personId) {
		return personPolicy.canEnablePerson(personId);
	}

	@Override
	public boolean canDisablePerson(Identifier personId) {
		return personPolicy.canDisablePerson(personId);
	}

	@Override
	public boolean canCreateUserForPerson(Identifier personId) {
		return userPolicy.canCreateUserForPerson(personId);
	}

	@Override
	public boolean canViewUser(Identifier userId) {
		return userPolicy.canViewUser(userId);
	}

	@Override
	public boolean canUpdateUserPassword(Identifier userId) {
		return userPolicy.canUpdateUserPassword(userId);
	}

	@Override
	public boolean canUpdateUserRole(Identifier userId) {
		return userPolicy.canUpdateUserRole(userId);
	}

	@Override
	public boolean canEnableUser(Identifier userId) {
		return userPolicy.canEnableUser(userId);
	}

	@Override
	public boolean canDisableUser(Identifier userId) {
		return userPolicy.canDisableUser(userId);
	}

	@Override
	public boolean canCreateGroup() {
		return permissionPolicy.canCreateGroup();
	}

	@Override
	public boolean canViewGroup(String group) {
		return permissionPolicy.canViewGroup(group);
	}

	@Override
	public boolean canViewGroup(Identifier groupId) {
		return permissionPolicy.canViewGroup(groupId);
	}

	@Override
	public boolean canUpdateGroup(Identifier groupId) {
		return permissionPolicy.canUpdateGroup(groupId);
	}

	@Override
	public boolean canEnableGroup(Identifier groupId) {
		return permissionPolicy.canEnableGroup(groupId);
	}

	@Override
	public boolean canDisableGroup(Identifier groupId) {
		return permissionPolicy.canDisableGroup(groupId);
	}

	@Override
	public boolean canCreateRole(Identifier groupId) {
		return permissionPolicy.canCreateRole(groupId);
	}

	@Override
	public boolean canViewRoles() {
		return permissionPolicy.canViewRoles();
	}

	@Override
	public boolean canViewRole(String code) {
		return permissionPolicy.canViewRole(code);
	}

	@Override
	public boolean canViewRole(Identifier roleId) {
		return permissionPolicy.canViewRole(roleId);
	}

	@Override
	public boolean canUpdateRole(Identifier roleId) {
		return permissionPolicy.canUpdateRole(roleId);
	}

	@Override
	public boolean canEnableRole(Identifier roleId) {
		return permissionPolicy.canEnableRole(roleId);
	}

	@Override
	public boolean canDisableRole(Identifier roleId) {
		return permissionPolicy.canDisableRole(roleId);
	}

}
