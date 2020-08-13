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
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.observer.CrmUpdateNotifier;
import ca.magex.crm.api.repositories.CrmOrganizationRepository;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.id.AuthenticationGroupIdentifier;
import ca.magex.crm.api.system.id.BusinessGroupIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.mongodb.util.BsonUtils;
import ca.magex.crm.mongodb.util.JsonUtils;
import ca.magex.crm.mongodb.util.TextUtils;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonObject;

/**
 * Implementation of the Crm Organization Repository backed by a MongoDB
 * 
 * @author Jonny
 */
public class MongoOrganizationRepository extends AbstractMongoRepository implements CrmOrganizationRepository {

	/**
	 * Creates our new MongoDB Backed Organization Repository
	 * @param mongoCrm
	 * @param notifier
	 * @param env
	 */
	public MongoOrganizationRepository(MongoDatabase mongoCrm, CrmUpdateNotifier notifier, String env) {
		super(mongoCrm, notifier, env);
	}

	@Override
	public OrganizationDetails saveOrganizationDetails(OrganizationDetails original) {		
		OrganizationDetails orgDetails = original.withLastModified(System.currentTimeMillis());
		MongoCollection<Document> collection = getOrganizations();
		Document doc = collection
				.find(Filters.and(
						Filters.eq("organizationId", orgDetails.getOrganizationId().getFullIdentifier()),
						Filters.eq("env", getEnv())))
				.first();						
		if (doc == null) {
			/* if we have no document for this organization, then create one */
			final InsertOneResult insertResult = collection.insertOne(new Document()
					.append("env", getEnv())
					.append("organizationId", orgDetails.getOrganizationId().getFullIdentifier())
					.append("status", orgDetails.getStatus().getCode())
					.append("displayName", orgDetails.getDisplayName())
					.append("displayName_searchable", TextUtils.toSearchable(orgDetails.getDisplayName()))
					.append("mainLocationId", orgDetails.getMainLocationId() == null ? null : orgDetails.getMainLocationId().getFullIdentifier())
					.append("mainContactId", orgDetails.getMainContactId() == null ? null : orgDetails.getMainContactId().getFullIdentifier())
					.append("authenticationGroupIds", orgDetails
							.getAuthenticationGroupIds()
							.stream()
							.map((id) -> id.getFullIdentifier())
							.collect(Collectors.toList()))
					.append("businessGroupIds", orgDetails
							.getBusinessGroupIds()
							.stream()
							.map((id) -> id.getFullIdentifier())
							.collect(Collectors.toList()))
					.append("locations", List.of())
					.append("persons", List.of())
					.append("users", List.of())
					.append("lastModified", orgDetails.getLastModified()));
			debug(() -> "saveOrganizationDetials(" + orgDetails + ") created a new document with result " + insertResult);			
		} else {
			/* add all the fields that can be updated */
			final UpdateResult setResult = collection.updateOne(
					new BasicDBObject()
						.append("env", getEnv())
						.append("organizationId", orgDetails.getOrganizationId().getFullIdentifier()),
					new BasicDBObject()
						.append("$set", new BasicDBObject()
								.append("status", orgDetails.getStatus().getCode())
								.append("displayName", orgDetails.getDisplayName())
								.append("displayName_searchable", TextUtils.toSearchable(orgDetails.getDisplayName()))
								.append("mainLocationId", orgDetails.getMainLocationId() == null ? null : orgDetails.getMainLocationId().getFullIdentifier())
								.append("mainContactId", orgDetails.getMainContactId() == null ? null : orgDetails.getMainContactId().getFullIdentifier())
								.append("authenticationGroupIds", orgDetails
										.getAuthenticationGroupIds()
										.stream()
										.map((id) -> new AuthenticationGroupIdentifier(id).getFullIdentifier())
										.collect(Collectors.toList()))
								.append("businessGroupIds", orgDetails
										.getBusinessGroupIds()
										.stream()
										.map((id) -> new BusinessGroupIdentifier(id).getFullIdentifier())
										.collect(Collectors.toList()))
								.append("lastModified", orgDetails.getLastModified())));
			if (setResult.getMatchedCount() == 0) {
				throw new ApiException("Unable to update or insert organization: " + orgDetails);
			}
			debug(() -> "saveOrganizationDetials(" + orgDetails + ") performed an update with result " + setResult);
		}
		return orgDetails;
	}

	@Override
	public OrganizationSummary findOrganizationSummary(OrganizationIdentifier organizationId) {
		MongoCollection<Document> collection = getOrganizations();
		Document doc = collection
				.find(Filters.and(
						Filters.eq("organizationId", organizationId.getFullIdentifier()),
						Filters.eq("env", getEnv())))
				.projection(Projections.fields(
						Projections.include("organizationId", "status", "displayName", "lastModified")))
				.first();
		if (doc == null) {
			return null;
		}
		JsonObject json = new JsonObject(doc.toJson());
		return JsonUtils.toOrganizationSummary(json);		
	}

	@Override
	public OrganizationDetails findOrganizationDetails(OrganizationIdentifier organizationId) {
		MongoCollection<Document> collection = getOrganizations();
		Document doc = collection
				.find(Filters.and(
						Filters.eq("organizationId", organizationId.getFullIdentifier()),
						Filters.eq("env", getEnv())))
				.projection(Projections.fields(
						Projections.include("organizationId", "status", "displayName", "mainLocationId", "mainContactId", "authenticationGroupIds", "businessGroupIds", "lastModified")))
				.first();
		if (doc == null) {
			return null;
		}
		JsonObject json = new JsonObject(doc.toJson());
		return JsonUtils.toOrganizationDetails(json);		
	}

	@Override
	public long countOrganizations(OrganizationsFilter filter) {
		MongoCollection<Document> collection = getOrganizations();
		ArrayList<Bson> pipeline = new ArrayList<>();
		pipeline.add(Aggregates.match(Filters.eq("env", getEnv())));
		/* create our matcher based on the filter */
		Bson fieldMatcher = BsonUtils.toBson(filter);
		if (fieldMatcher != null) {
			pipeline.add(Aggregates.match(fieldMatcher));
		}
		pipeline.add(Aggregates.project(Projections.include("organizationId")));
		pipeline.add(Aggregates.count("count"));
		Document doc = collection.aggregate(pipeline).first();
		if (doc == null) {
			return 0L;
		}
		return new JsonObject(doc.toJson()).getLong("count");
	}

	@Override
	public FilteredPage<OrganizationSummary> findOrganizationSummary(OrganizationsFilter filter, Paging paging) {
		MongoCollection<Document> collection = getOrganizations();
		ArrayList<Bson> pipeline = new ArrayList<>();
		pipeline.add(Aggregates.match(Filters.eq("env", getEnv())));
		/* match on fields if required */
		Bson fieldMatcher = BsonUtils.toBson(filter);
		if (fieldMatcher != null) {
			pipeline.add(Aggregates.match(fieldMatcher));
		}
		pipeline.add(Aggregates.facet(
				new Facet("totalCount", 
						Aggregates.count()), 
				new Facet("results", List.of(
						Aggregates.sort(BsonUtils.toBson(paging)),
						Aggregates.skip((int) paging.getOffset()),
						Aggregates.limit(paging.getPageSize()),
						Aggregates.project(Projections.fields(Projections.include("organizationId", "status", "displayName", "lastModified")))))));

		/* single document because we have facets */
		Document doc = collection.aggregate(pipeline).first();
		JsonObject json = new JsonObject(doc.toJson());
		Long totalCount = json.getArray("totalCount").getObject(0, new JsonObject()).getLong("count", 0L);		
		JsonArray results = json.getArray("results");
		List<OrganizationSummary> content = results
				.stream()
				.map(o -> JsonUtils.toOrganizationSummary((JsonObject)o))
				.collect(Collectors.toList());
		
		return new FilteredPage<>(filter, paging, content, totalCount);
	}

	@Override
	public FilteredPage<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter, Paging paging) {
		MongoCollection<Document> collection = getOrganizations();
		ArrayList<Bson> pipeline = new ArrayList<>();		
		pipeline.add(Aggregates.match(Filters.eq("env", getEnv())));
		/* match on fields if required */
		Bson fieldMatcher = BsonUtils.toBson(filter);
		if (fieldMatcher != null) {
			pipeline.add(Aggregates.match(fieldMatcher));
		}
		pipeline.add(Aggregates.facet(
				new Facet("totalCount", 
						Aggregates.count()), 
				new Facet("results", List.of(
						Aggregates.sort(BsonUtils.toBson(paging)),
						Aggregates.skip((int) paging.getOffset()),
						Aggregates.limit(paging.getPageSize()),
						Aggregates.project(Projections.fields(Projections.include("organizationId", "status", "displayName", "mainLocationId", "mainContactId", "authenticationGroupIds", "businessGroupIds", "lastModified")))))));

		/* single document because we have facets */
		Document doc = collection.aggregate(pipeline).first();
		JsonObject json = new JsonObject(doc.toJson());
		Long totalCount = json.getArray("totalCount").getObject(0, new JsonObject()).getLong("count", 0L);		
		JsonArray results = json.getArray("results");
		List<OrganizationDetails> content = results
				.stream()
				.map(o -> JsonUtils.toOrganizationDetails((JsonObject)o))
				.collect(Collectors.toList());		
		return new FilteredPage<>(filter, paging, content, totalCount);
	}

	
}
