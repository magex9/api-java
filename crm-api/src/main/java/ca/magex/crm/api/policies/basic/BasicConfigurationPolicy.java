package ca.magex.crm.api.policies.basic;

import ca.magex.crm.api.policies.CrmConfigurationPolicy;
import ca.magex.crm.api.services.CrmConfigurationService;

public class BasicConfigurationPolicy implements CrmConfigurationPolicy {

	private CrmConfigurationService configs;

	public BasicConfigurationPolicy(CrmConfigurationService configs) {
		this.configs = configs;
	}

	@Override
	public boolean canInitialize() {
		return !configs.isInitialized();
	}

	@Override
	public boolean canReset() {
		return configs.isInitialized();
	}
	
}