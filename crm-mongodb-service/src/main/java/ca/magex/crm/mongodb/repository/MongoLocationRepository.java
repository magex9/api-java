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

import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.event.CrmEventObserver;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.repositories.CrmLocationRepository;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.mongodb.util.BsonUtils;
import ca.magex.crm.mongodb.util.JsonUtils;
import ca.magex.crm.mongodb.util.TextUtils;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonObject;

/**
 * Implementation of the Crm Location Repository backed by a MongoDB
 * 
 * @author Jonny
 */
public class MongoLocationRepository extends AbstractMongoRepository implements CrmLocationRepository {

	/**
	 * Creates our new MongoDB Backed Location Repository
	 * @param mongoCrm
	 * @param notifier
	 * @param env
	 */
	public MongoLocationRepository(MongoDatabase mongoCrm, CrmEventObserver observer, String env) {
		super(mongoCrm, observer, env);
	}

	@Override
	public LocationDetails saveLocationDetails(LocationDetails original) {
		LocationDetails location = original.withLastModified(System.currentTimeMillis());
		MongoCollection<Document> collection = getOrganizations();
		/* add all the fields that can be updated */
		final UpdateResult setResult = collection.updateOne(
				new BasicDBObject()
						.append("env", getEnv())
						.append("organizationId", location.getOrganizationId().getFullIdentifier())
						.append("locations.locationId", location.getLocationId().getFullIdentifier()),
				new BasicDBObject()
						.append("$set", new BasicDBObject()
								.append("locations.$.status", location.getStatus().getCode())
								.append("locations.$.reference", location.getReference())
								.append("locations.$.displayName", location.getDisplayName())
								.append("locations.$.displayName_searchable", TextUtils.toSearchable(location.getDisplayName()))
								.append("locations.$.address", BsonUtils.toBson(location.getAddress()))
								.append("locations.$.lastModified", location.getLastModified())));
		if (setResult.getMatchedCount() == 0) {
			/* if we had no matching location id, then we need to do a push to the locations */
			final UpdateResult pushResult = collection.updateOne(
					new BasicDBObject()
							.append("env", getEnv())
							.append("organizationId", location.getOrganizationId().getFullIdentifier())
							.append("locations.locationId", new BasicDBObject()
									.append("$ne", location.getLocationId().getFullIdentifier())),
					new BasicDBObject()
							.append("$push", new BasicDBObject()
									.append("locations", BsonUtils.toBson(location))));
			if (pushResult.getModifiedCount() == 0) {
				throw new ApiException("Unable to update or insert location: " + location);
			}
			debug(() -> "saveLocationDetails(" + location + ") performed an insert with result " + pushResult);
		}
		else {
			debug(() -> "saveLocationDetails(" + location + ") performed an update with result " + setResult);
		}
		return location;
	}

	@Override
	public LocationDetails findLocationDetails(LocationIdentifier locationId) {
		MongoCollection<Document> collection = getOrganizations();
		Document doc = collection
				.find(Filters.and(
						Filters.eq("locations.locationId", locationId.getFullIdentifier()),
						Filters.eq("env", getEnv())))
				.projection(Projections.fields(
						Projections.elemMatch("locations", Filters.eq("locationId", locationId.getFullIdentifier())),
						Projections.include("organizationId", "locations.locationId", "locations.status", "locations.reference", "locations.displayName", "locations.address", "locations.lastModified")))
				.first();
		if (doc == null) {
			return null;
		}
		JsonObject json = new JsonObject(doc.toJson());
		return JsonUtils.toLocationDetails(
				json.getArray("locations").getObject(0), 
				new OrganizationIdentifier(json.getString("organizationId")));
	}

	@Override
	public LocationSummary findLocationSummary(LocationIdentifier locationId) {
		MongoCollection<Document> collection = getOrganizations();
		Document doc = collection
				.find(Filters.and(
						Filters.eq("locations.locationId", locationId.getFullIdentifier()),
						Filters.eq("env", getEnv())))
				.projection(Projections.fields(						
						Projections.elemMatch("locations", Filters.eq("locationId", locationId.getFullIdentifier())),
						Projections.include("organizationId", "locations.locationId", "locations.status", "locations.reference", "locations.displayName", "locations.lastModified")))				
				.first();
		if (doc == null) {
			return null;
		}
		JsonObject json = new JsonObject(doc.toJson());
		return JsonUtils.toLocationSummary(
				json.getArray("locations").getObject(0), 
				new OrganizationIdentifier(json.getString("organizationId")));
	}

	@Override
	public FilteredPage<LocationDetails> findLocationDetails(LocationsFilter filter, Paging paging) {
		MongoCollection<Document> collection = getOrganizations();
		ArrayList<Bson> pipeline = new ArrayList<>();
		pipeline.add(Aggregates.match(Filters.eq("env", getEnv())));
		/* match an organization if required */
		if (filter.getOrganizationId() != null) {
			pipeline.add(Aggregates.match(Filters.eq("organizationId", filter.getOrganizationId().getFullIdentifier())));
		}
		pipeline.add(Aggregates.unwind("$locations"));
		/* match on fields if required */
		Bson fieldMatcher = BsonUtils.toBson(filter);
		if (fieldMatcher != null) {
			pipeline.add(Aggregates.match(fieldMatcher));
		}
		pipeline.add(Aggregates.facet(
				new Facet("totalCount", 
						Aggregates.count()), 
				new Facet("results", List.of(
						Aggregates.project(Projections.include("organizationId", "locations.locationId", "locations.status", "locations.reference", "locations.displayName", "locations.address", "locations.lastModified")),
						Aggregates.sort(BsonUtils.toBson(paging, "locations")),
						Aggregates.skip((int) paging.getOffset()),
						Aggregates.limit(paging.getPageSize())))));
		
		/* single document because we have facets */
		Document doc = collection.aggregate(pipeline).first();
		JsonObject json = new JsonObject(doc.toJson());
		Long totalCount = json.getArray("totalCount").getObject(0, new JsonObject()).getLong("count", 0L);		
		JsonArray results = json.getArray("results");
		List<LocationDetails> content = results
				.stream()
				.map(o -> (JsonObject)o)
				.map(o -> JsonUtils.toLocationDetails(o.getObject("locations"), new OrganizationIdentifier(o.getString("organizationId"))))
				.collect(Collectors.toList());
		
		return new FilteredPage<>(filter, paging, content, totalCount);
	}

	@Override
	public FilteredPage<LocationSummary> findLocationSummary(LocationsFilter filter, Paging paging) {
		MongoCollection<Document> collection = getOrganizations();
		ArrayList<Bson> pipeline = new ArrayList<>();
		pipeline.add(Aggregates.match(Filters.eq("env", getEnv())));
		/* match an organization if required */
		if (filter.getOrganizationId() != null) {
			pipeline.add(Aggregates.match(Filters.eq("organizationId", filter.getOrganizationId().getFullIdentifier())));
		}
		pipeline.add(Aggregates.unwind("$locations"));
		/* match on fields if required */
		Bson fieldMatcher = BsonUtils.toBson(filter);
		if (fieldMatcher != null) {
			pipeline.add(Aggregates.match(fieldMatcher));
		}
		pipeline.add(Aggregates.facet(
				new Facet("totalCount", 
						Aggregates.count()), 
				new Facet("results", List.of(
						Aggregates.project(Projections.include("organizationId", "locations.locationId", "locations.status", "locations.reference", "locations.displayName", "locations.lastModified")),
						Aggregates.sort(BsonUtils.toBson(paging, "locations")),
						Aggregates.skip((int) paging.getOffset()),
						Aggregates.limit(paging.getPageSize())))));
		
		/* single document because we have facets */
		Document doc = collection.aggregate(pipeline).first();
		JsonObject json = new JsonObject(doc.toJson());
		Long totalCount = json.getArray("totalCount").getObject(0, new JsonObject()).getLong("count", 0L);		
		JsonArray results = json.getArray("results");
		List<LocationSummary> content = results
				.stream()
				.map(o -> (JsonObject)o)
				.map(o -> JsonUtils.toLocationSummary(o.getObject("locations"), new OrganizationIdentifier(o.getString("organizationId"))))
				.collect(Collectors.toList());
		
		return new FilteredPage<>(filter, paging, content, totalCount);
	}

	@Override
	public long countLocations(LocationsFilter filter) {
		MongoCollection<Document> collection = getOrganizations();
		ArrayList<Bson> pipeline = new ArrayList<>();
		pipeline.add(Aggregates.match(Filters.eq("env", getEnv())));
		/* match on document type if required */
		if (filter.getOrganizationId() != null) {
			pipeline.add(Aggregates.match(Filters.eq("organizationId", filter.getOrganizationId().getFullIdentifier())));
		}
		/* unwind the options so we can search for a count of specific locations */
		pipeline.add(Aggregates.unwind("$locations"));
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