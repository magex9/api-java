package ca.magex.crm.mongodb.repository;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.result.UpdateResult;

import ca.magex.crm.api.authentication.CrmPasswordDetails;
import ca.magex.crm.api.event.CrmEventObserver;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.repositories.CrmPasswordRepository;
import ca.magex.crm.api.store.CrmPasswordStore;
import ca.magex.crm.mongodb.util.BsonUtils;
import ca.magex.crm.mongodb.util.JsonUtils;
import ca.magex.crm.mongodb.util.TextUtils;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonObject;

/**
 * Mongo Password repository implementation 
 * 
 * @author Jonny
 */
public class MongoPasswordRepository extends AbstractMongoRepository implements CrmPasswordRepository {

	/**
	 * Creates our new MongoDB Backed Organization Repository
	 * @param mongoCrm
	 * @param observer
	 * @param env
	 */
	public MongoPasswordRepository(MongoDatabase mongoCrm, CrmEventObserver observer, String env) {
		super(mongoCrm, observer, env);
	}

	@Override
	public String generateTemporaryPassword() {
		return CrmPasswordStore.generatePassword();
	}

	@Override
	public CrmPasswordDetails savePasswordDetails(CrmPasswordDetails passwordDetails) {
		MongoCollection<Document> collection = getOrganizations();

		final UpdateResult pushResult = collection.updateOne(
				new BasicDBObject()
						.append("env", getEnv())
						.append("users.username_searchable", TextUtils.toSearchable(passwordDetails.getUsername())),
				new BasicDBObject()
						.append("$push", new BasicDBObject()
								.append("users.$.passwords", BsonUtils.toBson(passwordDetails))));
		if (pushResult.getModifiedCount() != 1) {
			throw new ApiException("Unable to save password details: " + passwordDetails);
		}
		debug(() -> "savePasswordDetails(" + passwordDetails + ") performed an update with result " + pushResult);
		return passwordDetails;
	}

	@Override
	public CrmPasswordDetails findPasswordDetails(String username) {
		MongoCollection<Document> collection = getOrganizations();
		Document doc = collection
				.find(Filters.and(
						Filters.eq("env", getEnv()),
						Filters.eq("users.username_searchable", TextUtils.toSearchable(username))))
				.projection(Projections.fields(
						Projections.elemMatch("users", Filters.eq("username_searchable", TextUtils.toSearchable(username))),
						Projections.include("users.username", "users.passwords")))
				.first();
		if (doc == null) {
			throw new ItemNotFoundException("Username '" + username + "'");
		}
		JsonObject json = new JsonObject(doc.toJson());
		JsonArray users = json.getArray("users");
		if (users.size() == 0) {
			throw new ItemNotFoundException("Username '" + username + "'");
		}
		JsonObject user = users.getObject(0);
		return JsonUtils.toPasswordDetails(user);
	}
}