package ca.magex.crm.api.decorators;

import ca.magex.crm.api.policies.CrmOrganizationPolicy;

import ca.magex.crm.api.system.Identifier;

public class CrmOrganizationPolicyDelegate implements CrmOrganizationPolicy {
	
	private CrmOrganizationPolicy delegate;
	
	public CrmOrganizationPolicyDelegate(CrmOrganizationPolicy delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public boolean canCreateOrganization() {
		return delegate.canCreateOrganization();
	}
	
	@Override
	public boolean canViewOrganization(Identifier organizationId) {
		return delegate.canViewOrganization(organizationId);
	}
	
	@Override
	public boolean canUpdateOrganization(Identifier organizationId) {
		return delegate.canUpdateOrganization(organizationId);
	}
	
	@Override
	public boolean canEnableOrganization(Identifier organizationId) {
		return delegate.canEnableOrganization(organizationId);
	}
	
	@Override
	public boolean canDisableOrganization(Identifier organizationId) {
		return delegate.canDisableOrganization(organizationId);
	}
	
}
