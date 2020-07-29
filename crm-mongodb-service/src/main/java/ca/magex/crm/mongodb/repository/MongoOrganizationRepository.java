package ca.magex.crm.mongodb.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
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
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.AuthenticationGroupIdentifier;
import ca.magex.crm.api.system.id.BusinessGroupIdentifier;
import ca.magex.crm.api.system.id.IdentifierFactory;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
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
	 */
	public MongoOrganizationRepository(MongoDatabase mongoCrm, CrmUpdateNotifier notifier) {
		super(mongoCrm, notifier);
	}

	@Override
	public OrganizationDetails saveOrganizationDetails(OrganizationDetails orgDetails) {
		MongoCollection<Document> collection = getMongoCrm().getCollection("organizations");
		Document doc = collection
				.find(Filters.eq("organizationId", orgDetails.getOrganizationId().getFullIdentifier()))
				.first();						
		if (doc == null) {
			/* if we have no document for this organization, then create one */
			final InsertOneResult insertResult = collection.insertOne(new Document()
					.append("organizationId", orgDetails.getOrganizationId().getFullIdentifier())					
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
					.append("locations", List.of())
					.append("persons", List.of())
					.append("users", List.of()));
			debug(() -> "saveOrganizationDetials(" + orgDetails + ") created a new document with result " + insertResult);			
		} else {
			/* add all the fields that can be updated */
			final UpdateResult setResult = collection.updateOne(
					new BasicDBObject()
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
										.collect(Collectors.toList()))));
			if (setResult.getMatchedCount() == 0) {
				throw new ApiException("Unable to update or insert organization: " + orgDetails);
			}
			debug(() -> "saveOrganizationDetials(" + orgDetails + ") performed an update with result " + setResult);
		}
		return orgDetails;
	}

	@Override
	public OrganizationSummary findOrganizationSummary(OrganizationIdentifier organizationId) {
		Document doc = getMongoCrm()
				.getCollection("organizations")
				.find(Filters.eq("organizationId", organizationId.getFullIdentifier()))
				.projection(Projections.fields(
						Projections.include("status", "displayName")))
				.first();
		if (doc == null) {
			return null;
		}
		return new OrganizationSummary(
				organizationId,
				Status.of(doc.getString("status")),
				doc.getString("displayName"));
	}

	@Override
	public OrganizationDetails findOrganizationDetails(OrganizationIdentifier organizationId) {
		MongoCollection<Document> collection = getMongoCrm().getCollection("organizations");
		Document doc = collection
				.find(Filters.eq("organizationId", organizationId.getFullIdentifier()))
				.projection(Projections.fields(
						Projections.include("status", "displayName", "mainLocationId", "mainContactId", "authenticationGroupIds", "businessGroupIds")))
				.first();
		if (doc == null) {
			return null;
		}
		return new OrganizationDetails(
				organizationId,
				Status.of(doc.getString("status")),
				doc.getString("displayName"),
				IdentifierFactory.forId(doc.getString("mainLocationId")),
				IdentifierFactory.forId(doc.getString("mainContactId")),
				doc.getList("authenticationGroupIds", String.class).stream().map(AuthenticationGroupIdentifier::new).collect(Collectors.toList()),
				doc.getList("businessGroupIds", String.class).stream().map(BusinessGroupIdentifier::new).collect(Collectors.toList()));
	}

	@Override
	public long countOrganizations(OrganizationsFilter filter) {
		MongoCollection<Document> collection = getMongoCrm().getCollection("organizations");
		ArrayList<Bson> pipeline = new ArrayList<>();
		/* create our matcher based on the filter */
		Bson fieldMatcher = toMatcher(filter);
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
		MongoCollection<Document> collection = getMongoCrm().getCollection("organizations");
		ArrayList<Bson> pipeline = new ArrayList<>();		
		/* match on fields if required */
		Bson fieldMatcher = toMatcher(filter);
		if (fieldMatcher != null) {
			pipeline.add(Aggregates.match(fieldMatcher));
		}
		pipeline.add(Aggregates.facet(
				new Facet("totalCount", 
						Aggregates.count()), 
				new Facet("results", List.of(
						Aggregates.sort(sorting(paging)),
						Aggregates.skip((int) paging.getOffset()),
						Aggregates.limit(paging.getPageSize()),
						Aggregates.project(Projections.fields(Projections.include("organizationId", "status", "displayName")))))));

		/* single document because we have facets */
		Document doc = collection.aggregate(pipeline).first();
		JsonObject json = new JsonObject(doc.toJson());
		Long totalCount = json.getArray("totalCount").getObject(0, new JsonObject()).getLong("count", 0L);		
		JsonArray results = json.getArray("results");
		List<OrganizationSummary> content = results
				.stream()
				.map(o -> toSummary((JsonObject)o))
				.collect(Collectors.toList());
		
		return new FilteredPage<>(filter, paging, content, totalCount);
	}

	@Override
	public FilteredPage<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter, Paging paging) {
		MongoCollection<Document> collection = getMongoCrm().getCollection("organizations");
		ArrayList<Bson> pipeline = new ArrayList<>();		
		/* match on fields if required */
		Bson fieldMatcher = toMatcher(filter);
		if (fieldMatcher != null) {
			pipeline.add(Aggregates.match(fieldMatcher));
		}
		pipeline.add(Aggregates.facet(
				new Facet("totalCount", 
						Aggregates.count()), 
				new Facet("results", List.of(
						Aggregates.sort(sorting(paging)),
						Aggregates.skip((int) paging.getOffset()),
						Aggregates.limit(paging.getPageSize()),
						Aggregates.project(Projections.fields(Projections.include("organizationId", "status", "displayName", "mainLocationId", "mainContactId", "authenticationGroupIds", "businessGroupIds")))))));

		/* single document because we have facets */
		Document doc = collection.aggregate(pipeline).first();
		JsonObject json = new JsonObject(doc.toJson());
		Long totalCount = json.getArray("totalCount").getObject(0, new JsonObject()).getLong("count", 0L);		
		JsonArray results = json.getArray("results");
		List<OrganizationDetails> content = results
				.stream()
				.map(o -> toDetails((JsonObject)o))
				.collect(Collectors.toList());		
		return new FilteredPage<>(filter, paging, content, totalCount);
	}
	
	/**
	 * converts to a summary
	 * @param json
	 * @return
	 */
	private OrganizationSummary toSummary(JsonObject json) {
		return new OrganizationSummary(
				new OrganizationIdentifier(json.getString("organizationId")),
				Status.of(json.getString("status")),
				json.getString("displayName"));
	}
	
	/**
	 * converts to a details
	 * @param json
	 * @return
	 */
	private OrganizationDetails toDetails(JsonObject json) {
		return new OrganizationDetails(
				new OrganizationIdentifier(json.getString("organizationId")),
				Status.of(json.getString("status")),
				json.getString("displayName"),
				IdentifierFactory.forId(json.getString("mainLocationId")),
				IdentifierFactory.forId(json.getString("mainContactId")),
				json.getArray("authenticationGroupIds", String.class).stream().map(AuthenticationGroupIdentifier::new).collect(Collectors.toList()),
				json.getArray("businessGroupIds", String.class).stream().map(BusinessGroupIdentifier::new).collect(Collectors.toList()));
	}

	/**
	 * Constructs a Bson based on the given organizations filter or null if no filtering provided
	 * 
	 * @param filter
	 * @return
	 */
	private Bson toMatcher(OrganizationsFilter filter) {
		List<Bson> filters = new ArrayList<>();
		if (filter.getStatus() != null) {
			filters.add(Filters.eq("status", filter.getStatusCode()));
		}
		if (StringUtils.isNotBlank(filter.getDisplayName())) {
			filters.add(Filters.eq("displayName_searchable", TextUtils.toSearchable(filter.getDisplayName())));
		}
		if (filter.getAuthenticationGroupId() != null) {
			filters.add(Filters.eq("authenticationGroupIds", filter.getAuthenticationGroupId().getFullIdentifier()));
		}
		if (filter.getBusinessGroupId() != null) {
			filters.add(Filters.eq("businessGroupIds", filter.getBusinessGroupId().getFullIdentifier()));
		}

		return conjunction(filters);
	}
}
