package ca.magex.crm.api.decorators;

import ca.magex.crm.api.policies.CrmUserPolicy;

import ca.magex.crm.api.system.Identifier;

public class CrmUserPolicyDelegate implements CrmUserPolicy {
	
	private CrmUserPolicy delegate;
	
	public CrmUserPolicyDelegate(CrmUserPolicy delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public boolean canCreateUserForPerson(Identifier personId) {
		return delegate.canCreateUserForPerson(personId);
	}
	
	@Override
	public boolean canViewUser(String username) {
		return delegate.canViewUser(username);
	}
	
	@Override
	public boolean canViewUser(Identifier userId) {
		return delegate.canViewUser(userId);
	}
	
	@Override
	public boolean canUpdateUserPassword(Identifier userId) {
		return delegate.canUpdateUserPassword(userId);
	}
	
	@Override
	public boolean canUpdateUserRole(Identifier userId) {
		return delegate.canUpdateUserRole(userId);
	}
	
	@Override
	public boolean canEnableUser(Identifier userId) {
		return delegate.canEnableUser(userId);
	}
	
	@Override
	public boolean canDisableUser(Identifier userId) {
		return delegate.canDisableUser(userId);
	}
	
}
