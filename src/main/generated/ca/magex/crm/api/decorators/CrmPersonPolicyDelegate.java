package ca.magex.crm.api.decorators;

import ca.magex.crm.api.policies.CrmPersonPolicy;

import ca.magex.crm.api.system.Identifier;

public class CrmPersonPolicyDelegate implements CrmPersonPolicy {
	
	private CrmPersonPolicy delegate;
	
	public CrmPersonPolicyDelegate(CrmPersonPolicy delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public boolean canCreatePersonForOrganization(Identifier organizationId) {
		return delegate.canCreatePersonForOrganization(organizationId);
	}
	
	@Override
	public boolean canViewPerson(Identifier personId) {
		return delegate.canViewPerson(personId);
	}
	
	@Override
	public boolean canUpdatePerson(Identifier personId) {
		return delegate.canUpdatePerson(personId);
	}
	
	@Override
	public boolean canEnablePerson(Identifier personId) {
		return delegate.canEnablePerson(personId);
	}
	
	@Override
	public boolean canDisablePerson(Identifier personId) {
		return delegate.canDisablePerson(personId);
	}
	
}
