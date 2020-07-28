package ca.magex.crm.mongodb.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.bson.Document;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.result.InsertOneResult;

import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.observer.CrmUpdateNotifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.LocaleIdentifier;
import ca.magex.crm.api.system.id.StatusIdentifier;
import ca.magex.crm.mongodb.config.MongoTestConfig;
import ca.magex.crm.mongodb.util.FilterTransform;
import ca.magex.crm.mongodb.util.TextUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MongoTestConfig.class })
@EnableTransactionManagement
public class MongoOptionRepositoryTests {

	@Autowired MongoDatabase mongoCrm;	
	@MockBean CrmUpdateNotifier notifier;
	
	@Test
	public void testOptions() {
		MongoOptionRepository repo = new MongoOptionRepository(mongoCrm, notifier);
		Assert.assertNotNull(repo.findOption(new StatusIdentifier("ACTIVE")));
		Assert.assertNotNull(repo.findOption(new StatusIdentifier("INACTIVE")));
		Assert.assertNotNull(repo.findOption(new StatusIdentifier("PENDING")));
		
//		Assert.assertEquals(6, repo.countOptions(new OptionsFilter()));
		
//		mongoCrm.getCollection("options").find().projection(Projections.include("_id")).forEach((d) -> System.out.println(d.toJson()));

		Paging paging = new Paging(2, 2, Sort.by(List.of(Order.asc("options.status"), Order.asc("options.optionId"))));
		
		AggregateIterable<Document> doc = mongoCrm.getCollection("options").aggregate(List.of(
				Aggregates.match(Filters.eq("type", Type.LOCALE.getCode()))
				,Aggregates.unwind("$options")
				,Aggregates.match(Filters.eq("options.status", Status.ACTIVE.getCode()))
				,Aggregates.project(Projections.include("options.optionId"))
				,Aggregates.sort(FilterTransform.toSort(paging))
				,Aggregates.skip((int)paging.getOffset())
				,Aggregates.limit(paging.getPageSize())
				,Aggregates.count("count")
				));
		doc.forEach((d) -> {
			System.out.println(d.toJson());
		});
		
		
		
	}
	
//	@Test
	public void insertOptionsRepository() {
		MongoCollection<Document> collection = mongoCrm.getCollection("options");
		
		{
			List<BasicDBObject> statusList = new ArrayList<>();
			for (Status status : Status.values()) {
				Option option = new Option(new StatusIdentifier(status.getName().getCode()), null, Type.STATUS, Status.ACTIVE, false, status.getName());
				statusList.add(new BasicDBObject()					
						.append("optionId",  option.getOptionId().getFullIdentifier())
						.append("parentId", option.getParentId() == null ? null : option.getParentId().getFullIdentifier())
						.append("status", option.getStatus().getCode())
						.append("mutable", option.getMutable())
						.append("name", new BasicDBObject()
								.append("code", option.getName().getCode())
								.append("english", option.getName().getEnglishName())
								.append("english_searchable", TextUtils.toSearchable(option.getName().getEnglishName()))
								.append("french", option.getName().getFrenchName())
								.append("french_searchable", TextUtils.toSearchable(option.getName().getFrenchName()))
						));
			}
			InsertOneResult result = collection.insertOne(new Document()
					.append("type", Type.STATUS.getCode())
					.append("options", statusList));
			System.out.println(result);
		}
		
		{
			List<BasicDBObject> localeList = new ArrayList<>();
			for (Map.Entry<Locale, Localized> entry : Lang.NAMES.entrySet()) {			
				Option option = new Option(new LocaleIdentifier(entry.getValue().getCode()), null, Type.LOCALE, Status.ACTIVE, false, entry.getValue());
				localeList.add(new BasicDBObject()					
						.append("optionId",  option.getOptionId().getFullIdentifier())
						.append("parentId", option.getParentId() == null ? null : option.getParentId().getFullIdentifier())
						.append("status", option.getStatus().getCode())
						.append("mutable", option.getMutable())
						.append("name", new BasicDBObject()
								.append("code", option.getName().getCode())
								.append("english", option.getName().getEnglishName())
								.append("english_searchable", TextUtils.toSearchable(option.getName().getEnglishName()))
								.append("french", option.getName().getFrenchName())
								.append("french_searchable", TextUtils.toSearchable(option.getName().getFrenchName()))
						));
			}
			InsertOneResult result = collection.insertOne(new Document()
					.append("type", Type.LOCALE.getCode())
					.append("options", localeList));
			System.out.println(result);
		}
	}
		
//	@Test
//	public void findOptionsDocument() {
//		MongoCollection<Document> collection = mongoCrm.getCollection("options");		
//		
////		Document locales = collection.find(new BasicDBObject()
////				.append("type", Type.LOCALE.getCode())
////				.append("options.optionId", "/options/locales/DE")).first();
//				
//		Document doc = collection.find(
//				Filters.and(
//						Filters.eq("type", Type.LOCALE.getCode())
//				)
//		).projection(
//				Projections.fields(						
//						Projections.elemMatch("options", Filters.eq("name.french", "Francais"))
//				)
//		).first();
//		if (doc == null) {
//			System.out.println("No Matching Doc");
//		}
//		else {
//			JsonObject json = new JsonObject(doc.toJson());
//			if (json.getArray("options").size() == 0) {
//				System.out.println("No Matching Option");
//			}
//			else {
//				JsonObject option = json.getArray("options").getObject(0);
//				OptionIdentifier optionId = IdentifierFactory.forOptionId(option.getString("optionId"));
//				System.out.println(new Option(
//						optionId, 
//						option.contains("parentId") ? 
//								IdentifierFactory.forOptionId(option.getString("parentId")) : null,
//						optionId.getType(),
//						Status.of(option.getString("status")),
//						option.getBoolean("mutable"), 
//						new Localized(
//								option.getObject("name").getString("code"),
//								option.getObject("name").getString("english"),
//								option.getObject("name").getString("french"))));
//			}
//		}
//		
//	}
	
}
