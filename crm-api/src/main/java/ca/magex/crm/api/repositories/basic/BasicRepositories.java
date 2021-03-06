package ca.magex.crm.api.repositories.basic;

import java.io.OutputStream;

import ca.magex.crm.api.adapters.CrmRepositoriesAdapter;
import ca.magex.crm.api.event.CrmEventNotifier;
import ca.magex.crm.api.event.CrmEventObserver;
import ca.magex.crm.api.repositories.CrmRepositories;
import ca.magex.crm.api.store.CrmStore;

public class BasicRepositories extends CrmRepositoriesAdapter implements CrmRepositories {
	
	private CrmStore store;
	
	public BasicRepositories(CrmStore store, CrmEventObserver observer) {
		super(
			new BasicConfigurationRepository(store),
			new BasicOptionRepository(store),
			new BasicOrganizationRepository(store),
			new BasicLocationRepository(store),
			new BasicPersonRepository(store),
			new BasicUserRepository(store)
		);
		this.store = store;
		store.getNotifier().register(observer);
	}

	@Override
	public void reset() {
		store.reset();
	}
	
	public CrmStore getStore() {
		return store;
	}
	
	public CrmEventNotifier getNotifier() {
		return store.getNotifier();
	}

	@Override
	public void dump(OutputStream os) {
		store.dump(os);
	}
	
}
