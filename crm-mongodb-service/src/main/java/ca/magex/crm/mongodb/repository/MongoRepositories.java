package ca.magex.crm.mongodb.repository;

import java.io.OutputStream;

import com.mongodb.client.MongoDatabase;

import ca.magex.crm.api.adapters.CrmRepositoriesAdapter;
import ca.magex.crm.api.observer.CrmUpdateNotifier;
import ca.magex.crm.api.repositories.CrmRepositories;

/**
 * Implementation of the CrmRepositories that uses the Mongo Repositories
 * 
 * @author Jonny
 */
public class MongoRepositories extends CrmRepositoriesAdapter implements CrmRepositories {
	
	/**
	 * Constructs our Repositories with the given mongo database
	 * @param mongoCrm
	 * @param notifier
	 */
	public MongoRepositories(MongoDatabase mongoCrm, CrmUpdateNotifier notifier) {
		super(
				new MongoConfigurationRepository(mongoCrm, notifier), 
				new MongoOptionRepository(mongoCrm, notifier),
				new MongoOrganizationRepository(mongoCrm, notifier),
				new MongoLocationRepository(mongoCrm, notifier),
				new MongoPersonRepository(mongoCrm, notifier),
				new MongoUserRepository(mongoCrm, notifier));
	}

	@Override
	public void reset() {
	}

	@Override
	public void dump(OutputStream os) {		
	}	
}
