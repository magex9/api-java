package ca.magex.crm.mongodb.repository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
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
import ca.magex.crm.mongodb.util.FilterTransform;
import ca.magex.crm.mongodb.util.TextUtils;
import ca.magex.json.model.JsonObject;

/**
 * Implementation of the Crm Option Repository backed by a MongoDB
 * 
 * @author Jonny
 */
public class MongoOptionRepository implements CrmOptionRepository {

	private static final Logger logger = LoggerFactory.getLogger(MongoOptionRepository.class.getPackageName());
	private MongoDatabase mongoCrm;
	private CrmUpdateNotifier notifier;

	/**
	 * Creates our new MongoDB Backed Option Repository
	 * @param mongoCrm
	 * @param notifier
	 */
	public MongoOptionRepository(MongoDatabase mongoCrm, CrmUpdateNotifier notifier) {
		this.mongoCrm = mongoCrm;
		this.notifier = notifier;
	}

	@Override
	public Option saveOption(Option option) {
		long t1 = System.nanoTime();
		try {
			MongoCollection<Document> collection = mongoCrm.getCollection("options");
			Document doc = collection
					.find(Filters.eq("type", option.getType().getCode()))
					.projection(Projections.fields(Projections.include("type")))
					.first();
			/* if we have no document for this type, then create one */
			if (doc == null) {
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

				/* if we had no matching option id, then we need to do a push to the existing array */
				if (setResult.getMatchedCount() == 0) {
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
			notifier.optionUpdated(System.currentTimeMillis(), option.getOptionId());
			return option;
		} finally {
			debug(() -> "saveOption(" + option + ") took " + Duration.ofNanos(System.nanoTime() - t1));
		}
	}

	@Override
	public FilteredPage<Option> findOptions(OptionsFilter filter, Paging paging) {
		long t1 = System.nanoTime();
		try {
			MongoCollection<Document> collection = mongoCrm.getCollection("options");
			ArrayList<Bson> pipeline = new ArrayList<>();
			/* match on document type if required */
			if (filter.getTypeCode() != null) {
				pipeline.add(Aggregates.match(Filters.eq("type", filter.getType().getCode())));
			}
			pipeline.add(Aggregates.unwind("$options"));
			Bson fieldMatcher = FilterTransform.toMatcher(filter);
			if (fieldMatcher != null) {
				pipeline.add(Aggregates.match(fieldMatcher));
			}
			pipeline.add(Aggregates.sort(FilterTransform.toSort(paging, "options")));
			pipeline.add(Aggregates.skip((int) paging.getOffset()));
			pipeline.add(Aggregates.limit(paging.getPageSize()));
	
			List<Option> options = new ArrayList<>();
			collection.aggregate(pipeline).forEach((doc) -> {
				JsonObject json = new JsonObject(doc.toJson());
				JsonObject option = json.getObject("options");
				options.add(toOption(option, Type.of(json.getString("type"))));
			});
			return new FilteredPage<>(filter, paging, options, options.size());
		}
		finally {
			debug(() -> "findOptions(" + filter + "," + paging + ") took " + Duration.ofNanos(System.nanoTime() - t1));
		}
	}

	@Override
	public long countOptions(OptionsFilter filter) {
		long t1 = System.nanoTime();
		try {
			MongoCollection<Document> collection = mongoCrm.getCollection("options");
			ArrayList<Bson> pipeline = new ArrayList<>();
			/* match on document type if required */
			if (filter.getTypeCode() != null) {
				pipeline.add(Aggregates.match(Filters.eq("type", filter.getType().getCode())));
			}
			pipeline.add(Aggregates.unwind("$options"));
			Bson fieldMatcher = FilterTransform.toMatcher(filter);
			if (fieldMatcher != null) {
				pipeline.add(Aggregates.match(fieldMatcher));
			}
			pipeline.add(Aggregates.project(Projections.include("options.optionId")));
			pipeline.add(Aggregates.count("count"));
			Document doc = collection.aggregate(pipeline).first();
			if (doc == null) {
				return 0L;
			}
			return new JsonObject(doc.toJson()).getLong("count");
		}
		finally {
			debug(() -> "countOptions(" + filter + ") took " + Duration.ofNanos(System.nanoTime() - t1));
		}
	}

	@Override
	public Option findOption(OptionIdentifier optionId) {
		long t1 = System.nanoTime();
		try {
			MongoCollection<Document> collection = mongoCrm.getCollection("options");
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
		finally {
			debug(() -> "findOption(" + optionId + ") took " + Duration.ofNanos(System.nanoTime() - t1));
		}
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
	 * logger helper
	 * @param messageSupplier
	 */
	private void debug(Supplier<String> messageSupplier) {
		if (logger.isDebugEnabled()) {
			logger.debug(messageSupplier.get());
		}
	}
}
