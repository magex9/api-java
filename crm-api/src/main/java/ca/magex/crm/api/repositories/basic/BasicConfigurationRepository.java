package ca.magex.crm.api.repositories.basic;

import ca.magex.crm.api.repositories.CrmConfigurationRepository;
import ca.magex.crm.api.store.CrmStore;
import ca.magex.crm.api.system.Identifier;

public class BasicConfigurationRepository implements CrmConfigurationRepository {

	private CrmStore store;
	
	public BasicConfigurationRepository(CrmStore store) {
		this.store = store;
	}

	@Override
	public boolean isInitialized() {
		return store.getConfigurations().containsKey(new Identifier("initialized"));
	}
}