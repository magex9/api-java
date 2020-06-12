package ca.magex.crm.api.decorators;

/**
 * AUTO-GENERATED: This file is auto-generated by ca.magex.json.javadoc.JavadocDelegationBuilder
 * 
 * Logging and delegate decorators for the CRM services and policies
 * 
 * This delegate may be extended so that implementations can be kept clean if they dont need to implement every single field.
 * 
 * @author magex
 */
public class CrmUserPolicyDelegate implements ca.magex.crm.api.policies.CrmUserPolicy {
	
	private ca.magex.crm.api.policies.CrmUserPolicy delegate;
	
	public CrmUserPolicyDelegate(ca.magex.crm.api.policies.CrmUserPolicy delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public boolean canCreateUserForPerson(ca.magex.crm.api.system.Identifier personId) {
		return delegate.canCreateUserForPerson(personId);
	}
	
	@Override
	public boolean canViewUser(String username) {
		return delegate.canViewUser(username);
	}
	
	@Override
	public boolean canViewUser(ca.magex.crm.api.system.Identifier userId) {
		return delegate.canViewUser(userId);
	}
	
	@Override
	public boolean canUpdateUserPassword(ca.magex.crm.api.system.Identifier userId) {
		return delegate.canUpdateUserPassword(userId);
	}
	
	@Override
	public boolean canUpdateUserRole(ca.magex.crm.api.system.Identifier userId) {
		return delegate.canUpdateUserRole(userId);
	}
	
	@Override
	public boolean canEnableUser(ca.magex.crm.api.system.Identifier userId) {
		return delegate.canEnableUser(userId);
	}
	
	@Override
	public boolean canDisableUser(ca.magex.crm.api.system.Identifier userId) {
		return delegate.canDisableUser(userId);
	}
	
}
