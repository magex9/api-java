package ca.magex.crm.api.repositories.basic;

import java.io.OutputStream;

import ca.magex.crm.api.adapters.CrmRepositoriesAdapter;
import ca.magex.crm.api.observer.CrmUpdateNotifier;
import ca.magex.crm.api.repositories.CrmRepositories;
import ca.magex.crm.api.repositories.CrmStore;

public class BasicRepositories extends CrmRepositoriesAdapter implements CrmRepositories {
	
	private CrmStore store;
	
	private CrmUpdateNotifier notifier;
	
	public BasicRepositories(CrmStore store, CrmUpdateNotifier notifier) {
		super(
			new BasicConfigurationRepository(store),
			new BasicLookupRepository(store, notifier),
			new BasicOptionRepository(store, notifier),
			new BasicGroupRepository(store, notifier),
			new BasicRoleRepository(store, notifier),
			new BasicOrganizationRepository(store, notifier),
			new BasicLocationRepository(store, notifier),
			new BasicPersonRepository(store, notifier),
			new BasicUserRepository(store, notifier)
		);
	}

	@Override
	public void reset() {
		store.reset();
	}
	
	public CrmStore getStore() {
		return store;
	}
	
	public CrmUpdateNotifier getNotifier() {
		return notifier;
	}

	@Override
	public void dump(OutputStream os) {
		store.dump(os);
	}
	
}
