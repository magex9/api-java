package ca.magex.crm.api.repositories.basic;

import java.time.LocalDateTime;

import ca.magex.crm.api.repositories.CrmConfigurationRepository;
import ca.magex.crm.api.store.CrmStore;
import ca.magex.crm.api.system.Configuration;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.ConfigurationIdentifier;

public class BasicConfigurationRepository implements CrmConfigurationRepository {

	private Configuration latest;
	
	private CrmStore store;
	
	public BasicConfigurationRepository(CrmStore store) {
		this.store = store;
		this.latest = null;
	}

	@Override
	public boolean isInitialized() {
		if (latest == null || !store.getConfigurations().containsKey(latest.getConfigurationId())) {
			return false;
		}
		return store.getConfigurations().get(latest.getConfigurationId()).getStatus().equals(Status.ACTIVE);
	}
	
	@Override
	public boolean prepareInitialize() {	
		return !isInitialized();
	}
	
	@Override
	public void setInitialized() {
		if (latest == null) {
			latest = new Configuration(new ConfigurationIdentifier(CrmStore.generateId()), Status.ACTIVE, LocalDateTime.now());
		} else {
			latest = latest.withStatus(Status.ACTIVE);
		}
		store.getConfigurations().put(latest.getConfigurationId(), latest);
	}
	
}