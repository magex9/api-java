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

import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.observer.CrmUpdateNotifier;
import ca.magex.crm.api.repositories.CrmOptionRepository;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.IdentifierFactory;
import ca.magex.crm.api.system.id.OptionIdentifier;
import ca.magex.crm.mongodb.util.TextUtils;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonObject;

/**
 * Implementation of the Crm Option Repository backed by a MongoDB
 * 
 * @author Jonny
 */
public class MongoOptionRepository extends AbstractMongoRepository implements CrmOptionRepository {

	/**
	 * Creates our new MongoDB Backed Option Repository
	 * @param mongoCrm
	 * @param notifier
	 */
	public MongoOptionRepository(MongoDatabase mongoCrm, CrmUpdateNotifier notifier) {
		super(mongoCrm, notifier);
	}

	@Override
	public Option saveOption(Option option) {
		MongoCollection<Document> collection = getMongoCrm().getCollection("options");
		Document doc = collection
				.find(Filters.eq("type", option.getType().getCode()))
				.projection(Projections.fields(Projections.include("type")))
				.first();		
		if (doc == null) {
			/* if we have no document for this type, then create one */
			final InsertOneResult insertResult = collection.insertOne(new Document()
					.append("type", option.getType().getCode())
					.append("options", List.of(toBson(option))));
			debug(() -> "saveOption(" + option + ") created a new document with result " + insertResult);
		} else {
			/* add all the fields that can be updated */
			final UpdateResult setResult = collection.updateOne(
					new BasicDBObject()
							.append("type", option.getType().getCode())
							.append("options.optionId", option.getOptionId().getFullIdentifier()),
					new BasicDBObject()
							.append("$set", new BasicDBObject()
									.append("options.$.status", option.getStatus().getCode())
									.append("options.$.name.english", option.getName().getEnglishName())
									.append("options.$.name.english_searchable", TextUtils.toSearchable(option.getName().getEnglishName()))
									.append("options.$.name.french", option.getName().getFrenchName())
									.append("options.$.name.french_searchable", TextUtils.toSearchable(option.getName().getEnglishName()))));
			
			if (setResult.getMatchedCount() == 0) {
				/* if we had no matching option id, then we need to do a push to the existing array */
				final UpdateResult pushResult = collection.updateOne(
						new BasicDBObject()
								.append("type", option.getType().getCode())
								.append("options.optionId", new BasicDBObject()
										.append("$ne", option.getOptionId().getFullIdentifier())),
						new BasicDBObject()
								.append("$push", new BasicDBObject()
										.append("options", toBson(option))));
				if (pushResult.getModifiedCount() == 0) {
					throw new ApiException("Unable to update or insert option: " + option);
				}
				debug(() -> "saveOption(" + option + ") performed an insert with result " + pushResult);
			} else {
				debug(() -> "saveOption(" + option + ") performed an update with result " + setResult);
			}
		}
		getNotifier().optionUpdated(System.currentTimeMillis(), option.getOptionId());
		return option;
	}

	@Override
	public FilteredPage<Option> findOptions(OptionsFilter filter, Paging paging) {
		MongoCollection<Document> collection = getMongoCrm().getCollection("options");
		ArrayList<Bson> pipeline = new ArrayList<>();
		/* match on document type if required */
		if (filter.getTypeCode() != null) {
			pipeline.add(Aggregates.match(Filters.eq("type", filter.getType().getCode())));
		}
		pipeline.add(Aggregates.unwind("$options"));
		/* match on fields if required */
		Bson fieldMatcher = toMatcher(filter);
		if (fieldMatcher != null) {
			pipeline.add(Aggregates.match(fieldMatcher));
		}
		pipeline.add(Aggregates.facet(
				new Facet("totalCount", 
						Aggregates.count()), 
				new Facet("results", List.of(
						Aggregates.sort(sorting(paging, "options")),
						Aggregates.skip((int) paging.getOffset()),
						Aggregates.limit(paging.getPageSize())))));
		
		/* single document because we have facets */
		Document doc = collection.aggregate(pipeline).first();
		JsonObject json = new JsonObject(doc.toJson());
		Long totalCount = json.getArray("totalCount").getObject(0, new JsonObject()).getLong("count", 0L);		
		JsonArray results = json.getArray("results");
		List<Option> content = results
				.stream()
				.map(o -> (JsonObject)o)
				.map(o -> toOption(o.getObject("options"), Type.of(o.getString("type"))))
				.collect(Collectors.toList());
		
		return new FilteredPage<>(filter, paging, content, totalCount);
	}

	@Override
	public long countOptions(OptionsFilter filter) {
		MongoCollection<Document> collection = getMongoCrm().getCollection("options");
		ArrayList<Bson> pipeline = new ArrayList<>();
		/* match on document type if required */
		if (filter.getTypeCode() != null) {
			pipeline.add(Aggregates.match(Filters.eq("type", filter.getType().getCode())));
		}
		/* unwind the options so we can search for a count of specific options */
		pipeline.add(Aggregates.unwind("$options"));
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

	@Override
	public Option findOption(OptionIdentifier optionId) {
		MongoCollection<Document> collection = getMongoCrm().getCollection("options");
		Document doc = collection
				.find(Filters.eq("type", optionId.getType().getCode()))
				.projection(Projections.fields(
						Projections.elemMatch("options", Filters.eq("optionId", optionId.getFullIdentifier()))))
				.first();
		if (doc == null) {
			return null;
		}
		JsonObject json = new JsonObject(doc.toJson());
		JsonObject option = json.getArray("options").getObject(0);
		return toOption(option, optionId.getType());
	}

	/**
	 * Converts the option to a Bson for persistence
	 * @param option
	 * @return
	 */
	private BasicDBObject toBson(Option option) {
		return new BasicDBObject()
				.append("optionId", option.getOptionId().getFullIdentifier())
				.append("parentId", option.getParentId() == null ? null : option.getParentId().getFullIdentifier())
				.append("status", option.getStatus().getCode())
				.append("mutable", option.getMutable())
				.append("name", new BasicDBObject()
						.append("code", option.getName().getCode())
						.append("english", option.getName().getEnglishName())
						.append("english_searchable", TextUtils.toSearchable(option.getName().getEnglishName()))
						.append("french", option.getName().getFrenchName())
						.append("french_searchable", TextUtils.toSearchable(option.getName().getFrenchName())));
	}

	/**
	 * Converts the json response back to an Option for persistence
	 * @param option
	 * @param type
	 * @return
	 */
	private Option toOption(JsonObject option, Type type) {
		return new Option(
				type.generateId(option.getString("optionId")),
				option.contains("parentId") ? IdentifierFactory.forOptionId(option.getString("parentId")) : null,
				type,
				Status.of(option.getString("status")),
				option.getBoolean("mutable"),
				new Localized(
						option.getObject("name").getString("code"),
						option.getObject("name").getString("english"),
						option.getObject("name").getString("french")));
	}
	

	/**
	 * constructs a Bson based on the given Options Filter or null if no filtering provided
	 * @param filter
	 * @return
	 */
	private Bson toMatcher(OptionsFilter filter) {
		List<Bson> filters = new ArrayList<>();
		if (filter.getStatus() != null) {
			filters.add(Filters.eq("options.status", filter.getStatusCode()));
		}
		if (filter.getParentId() != null) {
			filters.add(Filters.eq("options.parentId", filter.getParentId().getFullIdentifier()));
		}
		if (StringUtils.isNotBlank(filter.getCode())) {
			filters.add(Filters.or(
					Filters.eq("options.name.code", filter.getCode()), 						// matches full code
					Filters.regex("options.name.code", "/" + filter.getCode() + "$"))); 	// ends with /code
		}
		if (StringUtils.isNotBlank(filter.getEnglishName())) {
			filters.add(Filters.eq("options.name.english_searchable", TextUtils.toSearchable(filter.getEnglishName())));
		}		
		if (StringUtils.isNotBlank(filter.getFrenchName())) {
			filters.add(Filters.eq("options.name.french_searchable", TextUtils.toSearchable(filter.getFrenchName())));
		}
		return conjunction(filters);		
	}
}