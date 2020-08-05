package ca.magex.crm.mongodb.repository;

import java.util.function.Supplier;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import ca.magex.crm.api.observer.CrmUpdateNotifier;

/**
 * Base class used for all of the Mono Repositories
 * 
 * @author Jonny
 */
public abstract class AbstractMongoRepository {

	private static final Logger logger = LoggerFactory.getLogger(AbstractMongoRepository.class.getPackageName());

	private MongoDatabase mongoCrm;
	private CrmUpdateNotifier notifier;
	private String env;

	protected AbstractMongoRepository(MongoDatabase mongoCrm, CrmUpdateNotifier notifier, String env) {
		this.mongoCrm = mongoCrm;
		this.notifier = notifier;
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
	protected CrmUpdateNotifier getNotifier() {
		return notifier;
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
}