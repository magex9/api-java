package ca.magex.crm.mongodb.repository;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;

import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.observer.CrmUpdateNotifier;
import ca.magex.crm.api.repositories.CrmConfigurationRepository;

/**
 * CRM Repository Backed by a MongoDB cluster
 * 
 * @author Jonny
 */
public class MongoConfigurationRepository extends AbstractMongoRepository implements CrmConfigurationRepository {

	/**
	 * Creates our new MongoDB Backed Option Repository
	 * @param mongoCrm
	 * @param notifier
	 * @param env
	 */
	public MongoConfigurationRepository(MongoDatabase mongoCrm, CrmUpdateNotifier notifier, String env) {
		super(mongoCrm, notifier, env);
	}

	@Override
	public boolean isInitialized() {
		MongoCollection<Document> configurations = getConfigurations();
		Document doc = configurations.find(Filters.eq("env", getEnv())).first();
		if (doc == null) {
			return false;
		}
		return doc.containsKey("initialized") && doc.getLong("initialized") > 0L;
	}

	@Override
	public boolean prepareInitialize() {
		MongoCollection<Document> configurations = getConfigurations();
		Document doc = configurations.find(Filters.eq("env", getEnv())).first();
		if (doc == null) {
			final InsertOneResult result = configurations.insertOne(new Document()
					.append("env", getEnv())
					.append("initialized", 0L));
			debug(() -> "prepareInitiailze() inserted new document with result: " + result);
			return true;
		}
		return false;
	}

	@Override
	public void setInitialized() {
		MongoCollection<Document> configurations = getConfigurations();
		Document doc = configurations.find(Filters.eq("env", getEnv())).first();
		if (doc == null) {
			throw new ApiException("prepareInitialize() was not called");
		}
		UpdateResult setResult = configurations.updateOne(
				new BasicDBObject()
					.append("env", getEnv()),
				new BasicDBObject()
						.append("$set", new BasicDBObject()
								.append("initialized", System.currentTimeMillis())));
		if (setResult.getMatchedCount() != 1) {
			throw new ApiException("Unable to set initialized: " + setResult);
		}
	}
}