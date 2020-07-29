package ca.magex.crm.mongodb.repository;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.result.UpdateResult;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.observer.CrmUpdateNotifier;
import ca.magex.crm.api.repositories.CrmLocationRepository;
import ca.magex.crm.api.system.Choice;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.CountryIdentifier;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.ProvinceIdentifier;
import ca.magex.crm.mongodb.util.TextUtils;
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
	 */
	public MongoLocationRepository(MongoDatabase mongoCrm, CrmUpdateNotifier notifier) {
		super(mongoCrm, notifier);
	}

	@Override
	public LocationDetails saveLocationDetails(LocationDetails location) {
		MongoCollection<Document> collection = getMongoCrm().getCollection("organizations");

		/* add all the fields that can be updated */
		final UpdateResult setResult = collection.updateOne(
				new BasicDBObject()
						.append("organizationId", location.getOrganizationId().getFullIdentifier())
						.append("locations.locationId", location.getLocationId().getFullIdentifier()),
				new BasicDBObject()
						.append("$set", new BasicDBObject()
								.append("locations.$.status", location.getStatus().getCode())
								.append("locations.$.reference", location.getReference())
								.append("locations.$.displayName", location.getDisplayName())
								.append("locations.$.displayName_searchable", TextUtils.toSearchable(location.getDisplayName()))
								.append("locations.$.address", toBson(location.getAddress()))));
		if (setResult.getMatchedCount() == 0) {
			/* if we had no matching location id, then we need to do a push to the locations */
			final UpdateResult pushResult = collection.updateOne(
					new BasicDBObject()
							.append("organizationId", location.getOrganizationId().getFullIdentifier())
							.append("locations.locationId", new BasicDBObject()
									.append("$ne", location.getLocationId().getFullIdentifier())),
					new BasicDBObject()
							.append("$push", new BasicDBObject()
									.append("locations", toBson(location))));
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
		MongoCollection<Document> collection = getMongoCrm().getCollection("organizations");
		Document doc = collection
				.find(Filters.eq("locations.locationId", locationId.getFullIdentifier()))
				.projection(Projections.include("organizationId", "locations.locationId", "locations.status", "locations.reference", "locations.displayName", "locations.address"))
				.first();
		if (doc == null) {
			return null;
		}
		JsonObject json = new JsonObject(doc.toJson());
		return toDetails(
				json.getArray("locations").getObject(0), 
				new OrganizationIdentifier(json.getString("organizationId")));
	}

	@Override
	public LocationSummary findLocationSummary(LocationIdentifier locationId) {
		MongoCollection<Document> collection = getMongoCrm().getCollection("organizations");
		Document doc = collection
				.find(Filters.eq("locations.locationId", locationId.getFullIdentifier()))
				.projection(Projections.include("organizationId", "locations.locationId", "locations.status", "locations.reference", "locations.displayName"))				
				.first();
		if (doc == null) {
			return null;
		}
		JsonObject json = new JsonObject(doc.toJson());
		return toSummary(
				json.getArray("locations").getObject(0), 
				new OrganizationIdentifier(json.getString("organizationId")));
	}

	@Override
	public FilteredPage<LocationDetails> findLocationDetails(LocationsFilter filter, Paging paging) {
		// TODO implement this
		return null;
	}

	@Override
	public FilteredPage<LocationSummary> findLocationSummary(LocationsFilter filter, Paging paging) {
		// TODO implement this
		return null;
	}

	@Override
	public long countLocations(LocationsFilter filter) {
		MongoCollection<Document> collection = getMongoCrm().getCollection("organizations");
		ArrayList<Bson> pipeline = new ArrayList<>();
		/* match on document type if required */
		if (filter.getOrganizationId() != null) {
			pipeline.add(Aggregates.match(Filters.eq("organizationId", filter.getOrganizationId().getFullIdentifier())));
		}
		/* unwind the options so we can search for a count of specific options */
		pipeline.add(Aggregates.unwind("$locations"));
		/* create our matcher based on the filter */
		Bson fieldMatcher = toMatcher(filter);
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

	private BasicDBObject toBson(LocationDetails location) {
		return new BasicDBObject()
				.append("organizationId", location.getOrganizationId().getFullIdentifier())
				.append("locationId", location.getLocationId().getFullIdentifier())
				.append("status", location.getStatus().getCode())
				.append("reference", location.getReference())
				.append("displayName", location.getDisplayName())
				.append("displayName_searchable", TextUtils.toSearchable(location.getDisplayName()))
				.append("address", toBson(location.getAddress()));
	}
	
	private BasicDBObject toBson(MailingAddress address) {
		if (address == null) {
			return null;	
		}
		return new BasicDBObject()
				.append("street", address.getStreet())
				.append("city", address.getCity())
				.append("province", toBson(address.getProvince()))					
				.append("country", toBson(address.getCountry()))				
				.append("postalCode", address.getPostalCode());
	}
	
	private BasicDBObject toBson(Choice<?> choice) {
		if (choice == null || choice.isEmpty()) {
			return new BasicDBObject()
					.append("identifier", null)
					.append("other", null);
		}
		else if (choice.isIdentifer()) {
			return new BasicDBObject()
					.append("identifier", choice.getIdentifier().getFullIdentifier())
					.append("other", null);
		}
		else {
			return new BasicDBObject()
					.append("identifier", null)
					.append("other", choice.getOther());
		}
	}

	/**
	 * converts to a summary
	 * @param json
	 * @return
	 */
	private LocationSummary toSummary(JsonObject json, OrganizationIdentifier organizationId) {
		return new LocationSummary(
				new LocationIdentifier(json.getString("locationId")),
				new OrganizationIdentifier(organizationId),
				Status.of(json.getString("status")),
				json.getString("reference"),
				json.getString("displayName"));
	}
	
	/**
	 * converts to a summary
	 * @param json
	 * @return
	 */
	private LocationDetails toDetails(JsonObject json, OrganizationIdentifier organizationId) {
		return new LocationDetails(
				new LocationIdentifier(json.getString("locationId")),
				new OrganizationIdentifier(organizationId),
				Status.of(json.getString("status")),
				json.getString("reference"),
				json.getString("displayName"),
				toMailingAddress(json.getObject("address")));
	}
	
	private MailingAddress toMailingAddress(JsonObject json) {
		if (json == null) {
			return null;
		}
		return new MailingAddress(
				json.getString("street"),
				json.getString("city"),
				toProvince(json.getObject("province")),
				toCountry(json.getObject("country")),
				json.getString("postalCode"));
	}
	
	private Choice<ProvinceIdentifier> toProvince(JsonObject json) {
		if (json.contains("identifier")) {
			return new Choice<>(new ProvinceIdentifier(json.getString("identifier")));
		}
		else if (json.contains("other")) {
			return new Choice<>(json.getString("other"));
		}
		else {
			return new Choice<>();
		}
	}
	
	private Choice<CountryIdentifier> toCountry(JsonObject json) {
		if (json.contains("identifier")) {
			return new Choice<>(new CountryIdentifier(json.getString("identifier")));
		}
		else if (json.contains("other")) {
			return new Choice<>(json.getString("other"));
		}
		else {
			return new Choice<>();
		}
	}
		
	/**
	 * constructs a Bson based on the given Options Filter or null if no filtering provided
	 * @param filter
	 * @return
	 */
	private Bson toMatcher(LocationsFilter filter) {
		List<Bson> filters = new ArrayList<>();
		if (filter.getStatus() != null) {
			filters.add(Filters.eq("locations.status", filter.getStatusCode()));
		}		
		if (StringUtils.isNotBlank(filter.getReference())) {
			filters.add(Filters.eq("locations.reference", filter.getReference()));
		}
		if (StringUtils.isNotBlank(filter.getDisplayName())) {
			filters.add(Filters.eq("locations.displayName_searchable", TextUtils.toSearchable(filter.getDisplayName())));
		}
		return conjunction(filters);		
	}
	
}