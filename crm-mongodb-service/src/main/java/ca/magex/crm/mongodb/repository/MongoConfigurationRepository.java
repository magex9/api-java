package ca.magex.crm.mongodb.repository;

import com.mongodb.client.MongoDatabase;

import ca.magex.crm.api.observer.CrmUpdateNotifier;
import ca.magex.crm.api.repositories.CrmConfigurationRepository;

/**
 * CRM Repository Backed by a MongoDB cluster
 * 
 * @author Jonny
 */
public class MongoConfigurationRepository implements CrmConfigurationRepository {

	private MongoDatabase mongoCrm;
	private CrmUpdateNotifier notifier;
	
	/**
	 * Creates our new MongoDB Backed Option Repository
	 * @param mongoCrm
	 * @param notifier
	 */
	public MongoConfigurationRepository(MongoDatabase mongoCrm, CrmUpdateNotifier notifier) {
		this.mongoCrm = mongoCrm;
		this.notifier = notifier;
	}
	
	@Override
	public boolean isInitialized() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean prepareInitialize() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setInitialized() {
		// TODO Auto-generated method stub
		
	}

}
