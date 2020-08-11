package ca.magex.crm.mongodb.repository;

import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;

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
	
	private MongoDatabase mongoCrm;
	private String env;
	
	/**
	 * Constructs our Repositories with the given mongo database
	 * @param mongoCrm
	 * @param notifier
	 */
	public MongoRepositories(MongoDatabase mongoCrm, CrmUpdateNotifier notifier, String env) {
		super(
				new CrmConfigurationRepositorySlf4jDecorator(
						new MongoConfigurationRepository(mongoCrm, notifier, env), 
						logger), 
				new CrmOptionRepositorySlf4jDecorator(
						new MongoOptionRepository(mongoCrm, notifier, env), 
						logger),
				new CrmOrganizationRepositorySlf4jDecorator(
						new MongoOrganizationRepository(mongoCrm, notifier, env), 
						logger),
				new CrmLocationRepositorySlf4jDecorator(
						new MongoLocationRepository(mongoCrm, notifier, env), 
						logger),
				new CrmPersonRepositorySlf4jDecorator(
						new MongoPersonRepository(mongoCrm, notifier, env), 
						logger),
				new CrmUserRepositorySlf4jDecorator(
						new MongoUserRepository(mongoCrm, notifier, env),
						logger));
		this.mongoCrm = mongoCrm;
		this.env = env;
	}

	@Override
	public void reset() {
		logger.info("Deleting all documents for environment: " + env);
		DeleteResult deleteResult = mongoCrm.getCollection("configurations").deleteMany(new BasicDBObject().append("env", env));
		logger.info("Delete Result for configurations: " + deleteResult);
		deleteResult = mongoCrm.getCollection("organizations").deleteMany(new BasicDBObject().append("env", env));
		logger.info("Delete Result for organizations: " + deleteResult);
		deleteResult = mongoCrm.getCollection("options").deleteMany(new BasicDBObject().append("env", env));
		logger.info("Delete Result for options: " + deleteResult);
	}

	@Override
	public void dump(OutputStream os) {
	}	
}
