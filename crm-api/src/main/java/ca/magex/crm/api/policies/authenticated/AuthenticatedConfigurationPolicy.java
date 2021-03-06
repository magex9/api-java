package ca.magex.crm.api.policies.authenticated;

import static ca.magex.crm.api.authentication.CrmAuthenticationService.CRM_ADMIN;

import ca.magex.crm.api.authentication.CrmAuthenticationService;
import ca.magex.crm.api.policies.CrmConfigurationPolicy;
import ca.magex.crm.api.policies.basic.BasicConfigurationPolicy;
import ca.magex.crm.api.services.CrmConfigurationService;

public class AuthenticatedConfigurationPolicy implements CrmConfigurationPolicy {
	
	private CrmAuthenticationService auth;

	private CrmConfigurationPolicy delegate;
	
	public AuthenticatedConfigurationPolicy(
			CrmAuthenticationService auth,
			CrmConfigurationService init) {
		this.auth = auth;
		this.delegate = new BasicConfigurationPolicy(init);
	}

	@Override
	public boolean canInitialize() {
		return delegate.canInitialize();
	}

	@Override
	public boolean canReset() {
		if (!delegate.canReset()) {
			return false;
		}
		return auth.isUserInRole(CRM_ADMIN);
	}

}