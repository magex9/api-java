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

import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.observer.CrmUpdateNotifier;
import ca.magex.crm.api.repositories.CrmPersonRepository;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.mongodb.util.BsonUtils;
import ca.magex.crm.mongodb.util.JsonUtils;
import ca.magex.crm.mongodb.util.TextUtils;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonObject;

/**
 * Implementation of the Crm Person Repository backed by a MongoDB
 * 
 * @author Jonny
 */
public class MongoPersonRepository extends AbstractMongoRepository implements CrmPersonRepository {

	/**
	 * Creates our new MongoDB Backed Person Repository
	 * @param mongoCrm
	 * @param notifier
	 * @param env
	 */
	public MongoPersonRepository(MongoDatabase mongoCrm, CrmUpdateNotifier notifier, String env) {
		super(mongoCrm, notifier, env);
	}

	@Override
	public PersonDetails savePersonDetails(PersonDetails person) {
		MongoCollection<Document> collection = getOrganizations();
		/* add all the fields that can be updated */
		final UpdateResult setResult = collection.updateOne(
				new BasicDBObject()
						.append("env", getEnv())
						.append("organizationId", person.getOrganizationId().getFullIdentifier())
						.append("persons.personId", person.getPersonId().getFullIdentifier()),
				new BasicDBObject()
						.append("$set", new BasicDBObject()
								.append("persons.$.status", person.getStatus().getCode())
								.append("persons.$.displayName", person.getLegalName().getDisplayName())
								.append("persons.$.displayName_searchable", TextUtils.toSearchable(person.getLegalName().getDisplayName()))
								.append("persons.$.legalName", BsonUtils.toBson(person.getLegalName()))
								.append("persons.$.address", BsonUtils.toBson(person.getAddress()))
								.append("persons.$.communication", BsonUtils.toBson(person.getCommunication()))
								.append("persons.$.businessRoleIds", person
										.getBusinessRoleIds()
										.stream()
										.map((id) -> id.getFullIdentifier())
										.collect(Collectors.toList()))));
		if (setResult.getMatchedCount() == 0) {
			/* if we had no matching location id, then we need to do a push to the locations */
			final UpdateResult pushResult = collection.updateOne(
					new BasicDBObject()
							.append("env", getEnv())
							.append("organizationId", person.getOrganizationId().getFullIdentifier())
							.append("persons.personId", new BasicDBObject()
									.append("$ne", person.getPersonId().getFullIdentifier())),
					new BasicDBObject()
							.append("$push", new BasicDBObject()
									.append("persons", BsonUtils.toBson(person))));
			if (pushResult.getModifiedCount() == 0) {
				throw new ApiException("Unable to update or insert person: " + person);
			}
			debug(() -> "savePersonDetails(" + person + ") performed an insert with result " + pushResult);
		} else {
			debug(() -> "savePersonDetails(" + person + ") performed an update with result " + setResult);
		}
		return person;
	}

	@Override
	public PersonDetails findPersonDetails(PersonIdentifier personId) {
		MongoCollection<Document> collection = getOrganizations();
		Document doc = collection
				.find(Filters.and(
						Filters.eq("persons.personId", personId.getFullIdentifier()),
						Filters.eq("env", getEnv())))
				.projection(Projections.fields(
						Projections.elemMatch("persons", Filters.eq("personId", personId.getFullIdentifier())),
						Projections.include("organizationId", "persons.personId", "persons.status", "persons.displayName", "persons.legalName", "persons.address", "persons.communication", "persons.businessRoleIds")))
				.first();
		if (doc == null) {
			return null;
		}
		JsonObject json = new JsonObject(doc.toJson());
		return JsonUtils.toPersonDetails(
				json.getArray("persons").getObject(0),
				new OrganizationIdentifier(json.getString("organizationId")));
	}

	@Override
	public PersonSummary findPersonSummary(PersonIdentifier personId) {
		MongoCollection<Document> collection = getOrganizations();
		Document doc = collection
				.find(Filters.and(
						Filters.eq("persons.personId", personId.getFullIdentifier()),
						Filters.eq("env", getEnv())))
				.projection(Projections.fields(
						Projections.elemMatch("persons", Filters.eq("personId", personId.getFullIdentifier())),
						Projections.include("organizationId", "persons.personId", "persons.status", "persons.displayName")))
				.first();
		if (doc == null) {
			return null;
		}
		JsonObject json = new JsonObject(doc.toJson());
		return JsonUtils.toPersonSummary(
				json.getArray("persons").getObject(0),
				new OrganizationIdentifier(json.getString("organizationId")));
	}

	@Override
	public FilteredPage<PersonDetails> findPersonDetails(PersonsFilter filter, Paging paging) {
		MongoCollection<Document> collection = getOrganizations();
		ArrayList<Bson> pipeline = new ArrayList<>();
		pipeline.add(Aggregates.match(Filters.eq("env", getEnv())));
		/* match an organization if required */
		if (filter.getOrganizationId() != null) {
			pipeline.add(Aggregates.match(Filters.eq("organizationId", filter.getOrganizationId().getFullIdentifier())));
		}
		pipeline.add(Aggregates.unwind("$persons"));
		/* match on fields if required */
		Bson fieldMatcher = BsonUtils.toBson(filter);
		if (fieldMatcher != null) {
			pipeline.add(Aggregates.match(fieldMatcher));
		}
		pipeline.add(Aggregates.facet(
				new Facet("totalCount",
						Aggregates.count()),
				new Facet("results", List.of(
						Aggregates.project(Projections.include("organizationId", "persons.personId", "persons.status", "persons.displayName", "persons.legalName", "persons.address", "persons.communication", "persons.businessRoleIds")),
						Aggregates.sort(BsonUtils.toBson(paging, "persons")),
						Aggregates.skip((int) paging.getOffset()),
						Aggregates.limit(paging.getPageSize())))));

		/* single document because we have facets */
		Document doc = collection.aggregate(pipeline).first();
		JsonObject json = new JsonObject(doc.toJson());
		Long totalCount = json.getArray("totalCount").getObject(0, new JsonObject()).getLong("count", 0L);
		JsonArray results = json.getArray("results");
		List<PersonDetails> content = results
				.stream()
				.map(o -> (JsonObject) o)
				.map(o -> JsonUtils.toPersonDetails(o.getObject("persons"), new OrganizationIdentifier(o.getString("organizationId"))))
				.collect(Collectors.toList());

		return new FilteredPage<>(filter, paging, content, totalCount);
	}

	@Override
	public FilteredPage<PersonSummary> findPersonSummaries(PersonsFilter filter, Paging paging) {
		MongoCollection<Document> collection = getOrganizations();
		ArrayList<Bson> pipeline = new ArrayList<>();
		pipeline.add(Aggregates.match(Filters.eq("env", getEnv())));
		/* match an organization if required */
		if (filter.getOrganizationId() != null) {
			pipeline.add(Aggregates.match(Filters.eq("organizationId", filter.getOrganizationId().getFullIdentifier())));
		}
		pipeline.add(Aggregates.unwind("$persons"));
		/* match on fields if required */
		Bson fieldMatcher = BsonUtils.toBson(filter);
		if (fieldMatcher != null) {
			pipeline.add(Aggregates.match(fieldMatcher));
		}
		pipeline.add(Aggregates.facet(
				new Facet("totalCount",
						Aggregates.count()),
				new Facet("results", List.of(
						Aggregates.project(Projections.include("organizationId", "persons.personId", "persons.status", "persons.displayName")),
						Aggregates.sort(BsonUtils.toBson(paging, "persons")),
						Aggregates.skip((int) paging.getOffset()),
						Aggregates.limit(paging.getPageSize())))));

		/* single document because we have facets */
		Document doc = collection.aggregate(pipeline).first();
		JsonObject json = new JsonObject(doc.toJson());
		Long totalCount = json.getArray("totalCount").getObject(0, new JsonObject()).getLong("count", 0L);
		JsonArray results = json.getArray("results");
		List<PersonSummary> content = results
				.stream()
				.map(o -> (JsonObject) o)
				.map(o -> JsonUtils.toPersonSummary(o.getObject("persons"), new OrganizationIdentifier(o.getString("organizationId"))))
				.collect(Collectors.toList());

		return new FilteredPage<>(filter, paging, content, totalCount);
	}

	@Override
	public long countPersons(PersonsFilter filter) {
		MongoCollection<Document> collection = getOrganizations();
		ArrayList<Bson> pipeline = new ArrayList<>();
		pipeline.add(Aggregates.match(Filters.eq("env", getEnv())));
		/* match on document type if required */
		if (filter.getOrganizationId() != null) {
			pipeline.add(Aggregates.match(Filters.eq("organizationId", filter.getOrganizationId().getFullIdentifier())));
		}
		/* unwind the options so we can search for a count of specific locations */
		pipeline.add(Aggregates.unwind("$persons"));
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
