package ca.magex.crm.mongodb.repository;

import java.util.function.Supplier;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import ca.magex.crm.api.event.CrmEventObserver;

/**
 * Base class used for all of the Mono Repositories
 * 
 * @author Jonny
 */
public abstract class AbstractMongoRepository {

	private final Logger logger = LoggerFactory.getLogger(getClass().getName());

	private MongoDatabase mongoCrm;
	private CrmEventObserver observer;
	private String env;

	protected AbstractMongoRepository(MongoDatabase mongoCrm, CrmEventObserver observer, String env) {
		this.mongoCrm = mongoCrm;
		this.observer = observer;
		this.env = env;
	}

	/**
	 * returns the organizations collection
	 * @return
	 */
	protected MongoCollection<Document> getOrganizations() {
		return mongoCrm.getCollection("organizations");
	}

	/**
	 * returns the options collection
	 * @return
	 */
	protected MongoCollection<Document> getOptions() {
		return mongoCrm.getCollection("options");
	}

	/**
	 * returns the configurations collection
	 * @return
	 */
	protected MongoCollection<Document> getConfigurations() {
		return mongoCrm.getCollection("configurations");
	}

	/**
	 * returns our notifier used when items within the repository change
	 * @return
	 */
	protected CrmEventObserver getUpdateObserver() {
		return observer;
	}

	protected String getEnv() {
		return env;
	}

	/**
	 * logger helper
	 * @param messageSupplier
	 */
	protected void debug(Supplier<String> messageSupplier) {
		if (logger.isDebugEnabled()) {
			logger.debug(messageSupplier.get());
		}
	}	
	
	/**
	 * logger helper
	 * @param messageSupplier
	 */
	protected void info(Supplier<String> messageSupplier) {
		if (logger.isInfoEnabled()) {
			logger.info(messageSupplier.get());
		}
	}
}