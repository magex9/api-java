package ca.magex.crm.api.decorators;

import ca.magex.crm.api.policies.CrmPermissionPolicy;

import ca.magex.crm.api.system.Identifier;

public class CrmPermissionPolicyDelegate implements CrmPermissionPolicy {
	
	private CrmPermissionPolicy delegate;
	
	public CrmPermissionPolicyDelegate(CrmPermissionPolicy delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public boolean canCreateGroup() {
		return delegate.canCreateGroup();
	}
	
	@Override
	public boolean canViewGroup(String group) {
		return delegate.canViewGroup(group);
	}
	
	@Override
	public boolean canViewGroup(Identifier groupId) {
		return delegate.canViewGroup(groupId);
	}
	
	@Override
	public boolean canUpdateGroup(Identifier groupId) {
		return delegate.canUpdateGroup(groupId);
	}
	
	@Override
	public boolean canEnableGroup(Identifier groupId) {
		return delegate.canEnableGroup(groupId);
	}
	
	@Override
	public boolean canDisableGroup(Identifier groupId) {
		return delegate.canDisableGroup(groupId);
	}
	
	@Override
	public boolean canCreateRole(Identifier groupId) {
		return delegate.canCreateRole(groupId);
	}
	
	@Override
	public boolean canViewRoles() {
		return delegate.canViewRoles();
	}
	
	@Override
	public boolean canViewRole(String code) {
		return delegate.canViewRole(code);
	}
	
	@Override
	public boolean canViewRole(Identifier roleId) {
		return delegate.canViewRole(roleId);
	}
	
	@Override
	public boolean canUpdateRole(Identifier roleId) {
		return delegate.canUpdateRole(roleId);
	}
	
	@Override
	public boolean canEnableRole(Identifier roleId) {
		return delegate.canEnableRole(roleId);
	}
	
	@Override
	public boolean canDisableRole(Identifier roleId) {
		return delegate.canDisableRole(roleId);
	}
	
}
