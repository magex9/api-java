package ca.magex.crm.api.decorators;

import ca.magex.crm.api.policies.CrmLocationPolicy;

import ca.magex.crm.api.system.Identifier;

public class CrmLocationPolicyDelegate implements CrmLocationPolicy {
	
	private CrmLocationPolicy delegate;
	
	public CrmLocationPolicyDelegate(CrmLocationPolicy delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public boolean canCreateLocationForOrganization(Identifier organizationId) {
		return delegate.canCreateLocationForOrganization(organizationId);
	}
	
	@Override
	public boolean canViewLocation(Identifier locationId) {
		return delegate.canViewLocation(locationId);
	}
	
	@Override
	public boolean canUpdateLocation(Identifier locationId) {
		return delegate.canUpdateLocation(locationId);
	}
	
	@Override
	public boolean canEnableLocation(Identifier locationId) {
		return delegate.canEnableLocation(locationId);
	}
	
	@Override
	public boolean canDisableLocation(Identifier locationId) {
		return delegate.canDisableLocation(locationId);
	}
	
}
