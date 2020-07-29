package ca.magex.crm.mongodb.repository;

import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoDatabase;

import ca.magex.crm.api.adapters.CrmRepositoriesAdapter;
import ca.magex.crm.api.decorators.CrmConfigurationRepositorySlf4jDecorator;
import ca.magex.crm.api.decorators.CrmLocationRepositorySlf4jDecorator;
import ca.magex.crm.api.decorators.CrmOptionRepositorySlf4jDecorator;
import ca.magex.crm.api.decorators.CrmOrganizationRepositorySlf4jDecorator;
import ca.magex.crm.api.decorators.CrmPersonRepositorySlf4jDecorator;
import ca.magex.crm.api.decorators.CrmUserRepositorySlf4jDecorator;
import ca.magex.crm.api.observer.CrmUpdateNotifier;
import ca.magex.crm.api.repositories.CrmRepositories;

/**
 * Implementation of the CrmRepositories that uses the Mongo Repositories
 * 
 * @author Jonny
 */
public class MongoRepositories extends CrmRepositoriesAdapter implements CrmRepositories {
	
	private static final Logger logger = LoggerFactory.getLogger(MongoRepositories.class);
	
	/**
	 * Constructs our Repositories with the given mongo database
	 * @param mongoCrm
	 * @param notifier
	 */
	public MongoRepositories(MongoDatabase mongoCrm, CrmUpdateNotifier notifier) {
		super(
				new CrmConfigurationRepositorySlf4jDecorator(
						new MongoConfigurationRepository(mongoCrm, notifier), 
						logger), 
				new CrmOptionRepositorySlf4jDecorator(
						new MongoOptionRepository(mongoCrm, notifier), 
						logger),
				new CrmOrganizationRepositorySlf4jDecorator(
						new MongoOrganizationRepository(mongoCrm, notifier), 
						logger),
				new CrmLocationRepositorySlf4jDecorator(
						new MongoLocationRepository(mongoCrm, notifier), 
						logger),
				new CrmPersonRepositorySlf4jDecorator(
						new MongoPersonRepository(mongoCrm, notifier), 
						logger),
				new CrmUserRepositorySlf4jDecorator(
						new MongoUserRepository(mongoCrm, notifier),
						logger));
	}

	@Override
	public void reset() {
	}

	@Override
	public void dump(OutputStream os) {		
	}	
}
