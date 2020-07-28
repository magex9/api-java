package ca.magex.crm.mongodb.repository;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;

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
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonObject;

/**
 * Implementation of the Crm Option Repository backed by a MongoDB
 * 
 * @author Jonny
 */
public class MongoOptionRepository implements CrmOptionRepository {

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
		notifier.optionUpdated(System.nanoTime(), option.getOptionId());
		return null;
	}

	@Override
	public FilteredPage<Option> findOptions(OptionsFilter filter, Paging paging) {
		MongoCollection<Document> collection = mongoCrm.getCollection("options");
		ArrayList<Bson> pipeline = new ArrayList<>();
		/* match on document type if required */
		if (filter.getTypeCode() != null) {
			pipeline.add(Aggregates.match(Filters.eq("type", filter.getType())));
		}
		pipeline.add(Aggregates.unwind("$options"));
		pipeline.add(Aggregates.match(FilterTransform.toMatcher(filter)));
		pipeline.add(Aggregates.project(Projections.include("options.optionId")));
		
		List<Option> options = new ArrayList<>();
		collection.aggregate(pipeline).forEach((doc) -> {
			JsonObject json = new JsonObject(doc.toJson());
			for (int i=0; i< json.getArray("options").size(); i++) {
				JsonObject option = json.getArray("options").getObject(i);
				options.add(new Option(
						IdentifierFactory.forOptionId(doc.getString("optionId")), 
						option.contains("parentId") ? 
								IdentifierFactory.forOptionId(option.getString("parentId")) : null,
						Type.of(option.getString("type")),
						Status.of(option.getString("status")),
						option.getBoolean("mutable"), 
						new Localized(
								option.getObject("name").getString("code"),
								option.getObject("name").getString("english"),
								option.getObject("name").getString("french"))));
			}
		});
		return new FilteredPage<>(filter, paging, options, options.size());
	}

	@Override
	public long countOptions(OptionsFilter filter) {
		MongoCollection<Document> collection = mongoCrm.getCollection("options");
		ArrayList<Bson> pipeline = new ArrayList<>();
		/* match on document type if required */
		if (filter.getTypeCode() != null) {
			pipeline.add(Aggregates.match(Filters.eq("type", filter.getType())));
		}
		pipeline.add(Aggregates.unwind("$options"));
		pipeline.add(Aggregates.match(FilterTransform.toMatcher(filter)));
		pipeline.add(Aggregates.project(Projections.include("options.optionId")));
		pipeline.add(Aggregates.count("count"));		
		return new JsonObject(collection.aggregate(pipeline).first().toJson()).getLong("count");
	}

	@Override
	public Option findOption(OptionIdentifier optionId) {
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
		return new Option(
				optionId, 
				option.contains("parentId") ? IdentifierFactory.forOptionId(option.getString("parentId")) : null,
				optionId.getType(),
				Status.of(option.getString("status")),
				option.getBoolean("mutable"), 
				new Localized(
						option.getObject("name").getString("code"),
						option.getObject("name").getString("english"),
						option.getObject("name").getString("french")));
	}
}
