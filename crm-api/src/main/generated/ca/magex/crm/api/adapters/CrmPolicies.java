package ca.magex.crm.api.adapters;

public class CrmPolicies implements ca.magex.crm.api.policies.CrmPermissionPolicy, ca.magex.crm.api.policies.CrmOrganizationPolicy, ca.magex.crm.api.policies.CrmPersonPolicy, ca.magex.crm.api.policies.CrmUserPolicy, ca.magex.crm.api.policies.CrmLocationPolicy {
	
	private ca.magex.crm.api.policies.CrmPermissionPolicy crmPermissionPolicy;
	
	private ca.magex.crm.api.policies.CrmOrganizationPolicy crmOrganizationPolicy;
	
	private ca.magex.crm.api.policies.CrmPersonPolicy crmPersonPolicy;
	
	private ca.magex.crm.api.policies.CrmUserPolicy crmUserPolicy;
	
	private ca.magex.crm.api.policies.CrmLocationPolicy crmLocationPolicy;
	
	public CrmPolicies(ca.magex.crm.api.policies.CrmPermissionPolicy crmPermissionPolicy, ca.magex.crm.api.policies.CrmOrganizationPolicy crmOrganizationPolicy, ca.magex.crm.api.policies.CrmPersonPolicy crmPersonPolicy, ca.magex.crm.api.policies.CrmUserPolicy crmUserPolicy, ca.magex.crm.api.policies.CrmLocationPolicy crmLocationPolicy) {
		this.crmPermissionPolicy = crmPermissionPolicy;
		this.crmOrganizationPolicy = crmOrganizationPolicy;
		this.crmPersonPolicy = crmPersonPolicy;
		this.crmUserPolicy = crmUserPolicy;
		this.crmLocationPolicy = crmLocationPolicy;
	}
	
	@Override
	public boolean canCreateGroup() {
		return crmPermissionPolicy.canCreateGroup();
	}
	
	@Override
	public boolean canViewGroup(String group) {
		return crmPermissionPolicy.canViewGroup(group);
	}
	
	@Override
	public boolean canViewGroup(ca.magex.crm.api.system.Identifier groupId) {
		return crmPermissionPolicy.canViewGroup(groupId);
	}
	
	@Override
	public boolean canUpdateGroup(ca.magex.crm.api.system.Identifier groupId) {
		return crmPermissionPolicy.canUpdateGroup(groupId);
	}
	
	@Override
	public boolean canEnableGroup(ca.magex.crm.api.system.Identifier groupId) {
		return crmPermissionPolicy.canEnableGroup(groupId);
	}
	
	@Override
	public boolean canDisableGroup(ca.magex.crm.api.system.Identifier groupId) {
		return crmPermissionPolicy.canDisableGroup(groupId);
	}
	
	@Override
	public boolean canCreateRole(ca.magex.crm.api.system.Identifier groupId) {
		return crmPermissionPolicy.canCreateRole(groupId);
	}
	
	@Override
	public boolean canViewRoles() {
		return crmPermissionPolicy.canViewRoles();
	}
	
	@Override
	public boolean canViewRole(String code) {
		return crmPermissionPolicy.canViewRole(code);
	}
	
	@Override
	public boolean canViewRole(ca.magex.crm.api.system.Identifier roleId) {
		return crmPermissionPolicy.canViewRole(roleId);
	}
	
	@Override
	public boolean canUpdateRole(ca.magex.crm.api.system.Identifier roleId) {
		return crmPermissionPolicy.canUpdateRole(roleId);
	}
	
	@Override
	public boolean canEnableRole(ca.magex.crm.api.system.Identifier roleId) {
		return crmPermissionPolicy.canEnableRole(roleId);
	}
	
	@Override
	public boolean canDisableRole(ca.magex.crm.api.system.Identifier roleId) {
		return crmPermissionPolicy.canDisableRole(roleId);
	}
	
	@Override
	public boolean canCreateOrganization() {
		return crmOrganizationPolicy.canCreateOrganization();
	}
	
	@Override
	public boolean canViewOrganization(ca.magex.crm.api.system.Identifier organizationId) {
		return crmOrganizationPolicy.canViewOrganization(organizationId);
	}
	
	@Override
	public boolean canUpdateOrganization(ca.magex.crm.api.system.Identifier organizationId) {
		return crmOrganizationPolicy.canUpdateOrganization(organizationId);
	}
	
	@Override
	public boolean canEnableOrganization(ca.magex.crm.api.system.Identifier organizationId) {
		return crmOrganizationPolicy.canEnableOrganization(organizationId);
	}
	
	@Override
	public boolean canDisableOrganization(ca.magex.crm.api.system.Identifier organizationId) {
		return crmOrganizationPolicy.canDisableOrganization(organizationId);
	}
	
	@Override
	public boolean canCreatePersonForOrganization(ca.magex.crm.api.system.Identifier organizationId) {
		return crmPersonPolicy.canCreatePersonForOrganization(organizationId);
	}
	
	@Override
	public boolean canViewPerson(ca.magex.crm.api.system.Identifier personId) {
		return crmPersonPolicy.canViewPerson(personId);
	}
	
	@Override
	public boolean canUpdatePerson(ca.magex.crm.api.system.Identifier personId) {
		return crmPersonPolicy.canUpdatePerson(personId);
	}
	
	@Override
	public boolean canEnablePerson(ca.magex.crm.api.system.Identifier personId) {
		return crmPersonPolicy.canEnablePerson(personId);
	}
	
	@Override
	public boolean canDisablePerson(ca.magex.crm.api.system.Identifier personId) {
		return crmPersonPolicy.canDisablePerson(personId);
	}
	
	@Override
	public boolean canCreateUserForPerson(ca.magex.crm.api.system.Identifier personId) {
		return crmUserPolicy.canCreateUserForPerson(personId);
	}
	
	@Override
	public boolean canViewUser(String username) {
		return crmUserPolicy.canViewUser(username);
	}
	
	@Override
	public boolean canViewUser(ca.magex.crm.api.system.Identifier userId) {
		return crmUserPolicy.canViewUser(userId);
	}
	
	@Override
	public boolean canUpdateUserPassword(ca.magex.crm.api.system.Identifier userId) {
		return crmUserPolicy.canUpdateUserPassword(userId);
	}
	
	@Override
	public boolean canUpdateUserRole(ca.magex.crm.api.system.Identifier userId) {
		return crmUserPolicy.canUpdateUserRole(userId);
	}
	
	@Override
	public boolean canEnableUser(ca.magex.crm.api.system.Identifier userId) {
		return crmUserPolicy.canEnableUser(userId);
	}
	
	@Override
	public boolean canDisableUser(ca.magex.crm.api.system.Identifier userId) {
		return crmUserPolicy.canDisableUser(userId);
	}
	
	@Override
	public boolean canCreateLocationForOrganization(ca.magex.crm.api.system.Identifier organizationId) {
		return crmLocationPolicy.canCreateLocationForOrganization(organizationId);
	}
	
	@Override
	public boolean canViewLocation(ca.magex.crm.api.system.Identifier locationId) {
		return crmLocationPolicy.canViewLocation(locationId);
	}
	
	@Override
	public boolean canUpdateLocation(ca.magex.crm.api.system.Identifier locationId) {
		return crmLocationPolicy.canUpdateLocation(locationId);
	}
	
	@Override
	public boolean canEnableLocation(ca.magex.crm.api.system.Identifier locationId) {
		return crmLocationPolicy.canEnableLocation(locationId);
	}
	
	@Override
	public boolean canDisableLocation(ca.magex.crm.api.system.Identifier locationId) {
		return crmLocationPolicy.canDisableLocation(locationId);
	}
	
}
