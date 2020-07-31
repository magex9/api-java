package ca.magex.crm.mongodb.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Facet;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.result.UpdateResult;

import ca.magex.crm.api.crm.UserDetails;
import ca.magex.crm.api.crm.UserSummary;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.observer.CrmUpdateNotifier;
import ca.magex.crm.api.repositories.CrmUserRepository;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;
import ca.magex.crm.mongodb.util.BsonUtils;
import ca.magex.crm.mongodb.util.JsonUtils;
import ca.magex.crm.mongodb.util.TextUtils;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonObject;

/**
 * Implementation of the Crm User Repository backed by a MongoDB
 * 
 * @author Jonny
 */
public class MongoUserRepository extends AbstractMongoRepository implements CrmUserRepository {

	
	/**
	 * Creates our new MongoDB Backed User Repository
	 * @param mongoCrm
	 * @param notifier
	 */
	public MongoUserRepository(MongoDatabase mongoCrm, CrmUpdateNotifier notifier, String env) {
		super(mongoCrm, notifier, env);
	}
	
	@Override
	public UserDetails saveUserDetails(UserDetails user) {
		MongoCollection<Document> collection = getOrganizations();
		/* add all the fields that can be updated */
		final UpdateResult setResult = collection.updateOne(
				new BasicDBObject()
						.append("env", getEnv())
						.append("organizationId", user.getOrganizationId().getFullIdentifier())
						.append("users.userId", user.getUserId().getFullIdentifier()),
				new BasicDBObject()
						.append("$set", new BasicDBObject()
								.append("users.$.status", user.getStatus().getCode())
								.append("users.$.username", user.getUsername())
								.append("users.$.username_searchable", TextUtils.toSearchable(user.getUsername()))								
								.append("users.$.authenticationRoleIds", user
										.getAuthenticationRoleIds()
										.stream()
										.map((id) -> id.getFullIdentifier())
										.collect(Collectors.toList()))));
		if (setResult.getMatchedCount() == 0) {
			/* if we had no matching location id, then we need to do a push to the locations */
			final UpdateResult pushResult = collection.updateOne(
					new BasicDBObject()
							.append("env", getEnv())
							.append("organizationId", user.getOrganizationId().getFullIdentifier())
							.append("users.userId", new BasicDBObject()
									.append("$ne", user.getPersonId().getFullIdentifier())),
					new BasicDBObject()
							.append("$push", new BasicDBObject()
									.append("users", BsonUtils.toBson(user))));
			if (pushResult.getModifiedCount() == 0) {
				throw new ApiException("Unable to update or insert user: " + user);
			}
			debug(() -> "saveUserDetails(" + user + ") performed an insert with result " + pushResult);
		} else {
			debug(() -> "saveUserDetails(" + user + ") performed an update with result " + setResult);
		}
		return user;
	}

	@Override
	public UserDetails findUserDetails(UserIdentifier userId) {
		MongoCollection<Document> collection = getOrganizations();
		Document doc = collection
				.find(Filters.and(
						Filters.eq("users.userId", userId.getFullIdentifier()),
						Filters.eq("env", getEnv())))
				.projection(Projections.fields(
						Projections.elemMatch("users", Filters.eq("userId", userId.getFullIdentifier())),
						Projections.include("organizationId", "users.userId", "users.personId", "users.status", "users.username", "users.authenticationRoleIds")))
				.first();
		if (doc == null) {
			return null;
		}
		JsonObject json = new JsonObject(doc.toJson());
		return JsonUtils.toUserDetails(
				json.getArray("users").getObject(0),
				new OrganizationIdentifier(json.getString("organizationId")));
	}

	@Override
	public UserSummary findUserSummary(UserIdentifier userId) {
		MongoCollection<Document> collection = getOrganizations();
		Document doc = collection
				.find(Filters.and(
						Filters.eq("users.userId", userId.getFullIdentifier()),
						Filters.eq("env", getEnv())))
				.projection(Projections.fields(
						Projections.elemMatch("users", Filters.eq("userId", userId.getFullIdentifier())),
						Projections.include("organizationId", "users.userId", "users.status", "users.username")))
				.first();
		if (doc == null) {
			return null;
		}
		JsonObject json = new JsonObject(doc.toJson());
		return JsonUtils.toUserSummary(
				json.getArray("users").getObject(0),
				new OrganizationIdentifier(json.getString("organizationId")));
	}

	@Override
	public FilteredPage<UserDetails> findUserDetails(UsersFilter filter, Paging paging) {
		MongoCollection<Document> collection = getOrganizations();
		ArrayList<Bson> pipeline = new ArrayList<>();
		pipeline.add(Aggregates.match(Filters.eq("env", getEnv())));
		/* match an organization if required */
		if (filter.getOrganizationId() != null) {
			pipeline.add(Aggregates.match(Filters.eq("organizationId", filter.getOrganizationId().getFullIdentifier())));
		}
		pipeline.add(Aggregates.unwind("$users"));
		/* match on fields if required */
		Bson fieldMatcher = BsonUtils.toBson(filter);
		if (fieldMatcher != null) {
			pipeline.add(Aggregates.match(fieldMatcher));
		}
		pipeline.add(Aggregates.facet(
				new Facet("totalCount",
						Aggregates.count()),
				new Facet("results", List.of(
						Aggregates.project(Projections.include("organizationId", "users.userId", "users.personId", "users.status", "users.username", "users.authenticationRoleIds")),						
						Aggregates.sort(BsonUtils.toBson(paging, "users")),
						Aggregates.skip((int) paging.getOffset()),
						Aggregates.limit(paging.getPageSize())))));
		
		/* single document because we have facets */
		Document doc = collection.aggregate(pipeline).first();
		JsonObject json = new JsonObject(doc.toJson());
		Long totalCount = json.getArray("totalCount").getObject(0, new JsonObject()).getLong("count", 0L);
		JsonArray results = json.getArray("results");
		List<UserDetails> content = results
				.stream()
				.map(o -> (JsonObject) o)
				.map(o -> JsonUtils.toUserDetails(o.getObject("users"), new OrganizationIdentifier(o.getString("organizationId"))))
				.collect(Collectors.toList());
		return new FilteredPage<>(filter, paging, content, totalCount);
	}

	@Override
	public FilteredPage<UserSummary> findUserSummaries(UsersFilter filter, Paging paging) {
		MongoCollection<Document> collection = getOrganizations();
		ArrayList<Bson> pipeline = new ArrayList<>();
		pipeline.add(Aggregates.match(Filters.eq("env", getEnv())));
		/* match an organization if required */
		if (filter.getOrganizationId() != null) {
			pipeline.add(Aggregates.match(Filters.eq("organizationId", filter.getOrganizationId().getFullIdentifier())));
		}
		pipeline.add(Aggregates.unwind("$users"));
		/* match on fields if required */
		Bson fieldMatcher = BsonUtils.toBson(filter);
		if (fieldMatcher != null) {
			pipeline.add(Aggregates.match(fieldMatcher));
		}
		pipeline.add(Aggregates.facet(
				new Facet("totalCount",
						Aggregates.count()),
				new Facet("results", List.of(
						Aggregates.project(Projections.include("organizationId", "users.userId", "users.status", "users.username")),
						Aggregates.sort(BsonUtils.toBson(paging, "users")),
						Aggregates.skip((int) paging.getOffset()),
						Aggregates.limit(paging.getPageSize())))));

		/* single document because we have facets */
		Document doc = collection.aggregate(pipeline).first();
		JsonObject json = new JsonObject(doc.toJson());
		Long totalCount = json.getArray("totalCount").getObject(0, new JsonObject()).getLong("count", 0L);
		JsonArray results = json.getArray("results");
		List<UserSummary> content = results
				.stream()
				.map(o -> (JsonObject) o)
				.map(o -> JsonUtils.toUserSummary(o.getObject("users"), new OrganizationIdentifier(o.getString("organizationId"))))
				.collect(Collectors.toList());
		return new FilteredPage<>(filter, paging, content, totalCount);
	}

	@Override
	public long countUsers(UsersFilter filter) {
		MongoCollection<Document> collection = getOrganizations();
		ArrayList<Bson> pipeline = new ArrayList<>();
		pipeline.add(Aggregates.match(Filters.eq("env", getEnv())));
		/* match on document type if required */
		if (filter.getOrganizationId() != null) {
			pipeline.add(Aggregates.match(Filters.eq("organizationId", filter.getOrganizationId().getFullIdentifier())));
		}
		/* unwind the options so we can search for a count of specific locations */
		pipeline.add(Aggregates.unwind("$users"));
		/* create our matcher based on the filter */
		Bson fieldMatcher = BsonUtils.toBson(filter);
		if (fieldMatcher != null) {
			pipeline.add(Aggregates.match(fieldMatcher));
		}
		pipeline.add(Aggregates.count("count"));
		Document doc = collection.aggregate(pipeline).first();
		if (doc == null) {
			return 0L;
		}
		return new JsonObject(doc.toJson()).getLong("count");
	}

}
