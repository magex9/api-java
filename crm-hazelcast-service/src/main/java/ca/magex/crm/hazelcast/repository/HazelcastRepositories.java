package ca.magex.crm.hazelcast.repository;

import java.io.OutputStream;

import ca.magex.crm.api.adapters.CrmRepositoriesAdapter;
import ca.magex.crm.api.repositories.CrmRepositories;
import ca.magex.crm.hazelcast.xa.XATransactionAwareHazelcastInstance;

public class HazelcastRepositories extends CrmRepositoriesAdapter implements CrmRepositories {

	public HazelcastRepositories(XATransactionAwareHazelcastInstance hzInstance) {
		super(
				new HazelcastConfigurationRepository(hzInstance), 
				new HazelcastOptionRepository(hzInstance),
				new HazelcastOrganizationRespository(hzInstance),
				new HazelcastLocationRepository(hzInstance),
				new HazelcastPersonRepository(hzInstance),
				new HazelcastUserRepository(hzInstance));
	}

	@Override
	public void reset() {
	}

	@Override
	public void dump(OutputStream os) {		
	}	
}
