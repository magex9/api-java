package ca.magex.crm.mongodb.repository;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;

import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.observer.CrmUpdateNotifier;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.CountryIdentifier;
import ca.magex.crm.api.system.id.SalutationIdentifier;
import ca.magex.crm.api.system.id.StatusIdentifier;
import ca.magex.crm.mongodb.config.MongoTestConfig;
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
		
		for (Status status : Status.values()) {
			Option option = new Option(new StatusIdentifier(status.getName().getCode()), null, Type.STATUS, Status.ACTIVE, false, status.getName());
			Assert.assertEquals(option, repo.saveOption(option));
		}
		
		Assert.assertEquals(3, repo.countOptions(new OptionsFilter().withType(Type.STATUS)));
		
		FilteredPage<Option> optionsPage = repo.findOptions(new OptionsFilter().withType(Type.STATUS), new Paging(1, 5, Sort.by("optionId")));
		Assert.assertEquals(3, optionsPage.getNumberOfElements());
		Assert.assertEquals(repo.findOption(new StatusIdentifier("ACTIVE")), optionsPage.getContent().get(0));
		Assert.assertEquals(repo.findOption(new StatusIdentifier("INACTIVE")), optionsPage.getContent().get(1));
		Assert.assertEquals(repo.findOption(new StatusIdentifier("PENDING")), optionsPage.getContent().get(2));
		
		optionsPage = repo.findOptions(new OptionsFilter().withType(Type.STATUS), new Paging(1, 2, Sort.by("optionId")));
		Assert.assertEquals(2, optionsPage.getNumberOfElements());
		Assert.assertEquals(repo.findOption(new StatusIdentifier("ACTIVE")), optionsPage.getContent().get(0));
		Assert.assertEquals(repo.findOption(new StatusIdentifier("INACTIVE")), optionsPage.getContent().get(1));
		
		optionsPage = repo.findOptions(new OptionsFilter().withType(Type.STATUS), new Paging(2, 2, Sort.by("optionId")));
		Assert.assertEquals(1, optionsPage.getNumberOfElements());
		Assert.assertEquals(repo.findOption(new StatusIdentifier("PENDING")), optionsPage.getContent().get(0));
		
//		Assert.assertNotNull(repo.findOption(new StatusIdentifier("ACTIVE")));
//		Assert.assertNotNull(repo.findOption(new StatusIdentifier("INACTIVE")));
//		Assert.assertNotNull(repo.findOption(new StatusIdentifier("PENDING")));
		
//		Assert.assertEquals(6, repo.countOptions(new OptionsFilter()));
		
//		mongoCrm.getCollection("options").find().projection(Projections.include("_id")).forEach((d) -> System.out.println(d.toJson()));

//		Paging paging = new Paging(2, 2, Sort.by(List.of(Order.asc("options.status"), Order.asc("options.optionId"))));
//		
//		AggregateIterable<Document> doc = mongoCrm.getCollection("options").aggregate(List.of(
//				Aggregates.match(Filters.eq("type", Type.LOCALE.getCode()))
//				,Aggregates.unwind("$options")
//				,Aggregates.match(Filters.eq("options.status", Status.ACTIVE.getCode()))
//				,Aggregates.project(Projections.include("options.optionId"))
//				,Aggregates.sort(FilterTransform.toSort(paging, "options"))
//				,Aggregates.skip((int)paging.getOffset())
//				,Aggregates.limit(paging.getPageSize())
//				,Aggregates.count("count")
//				));
//		doc.forEach((d) -> {
//			System.out.println(d.toJson());
//		});
	}
	
//	@Test
	public void testUpdateOption() {
//		Option canada = new Option(new CountryIdentifier("CA"), null, Type.COUNTRY, Status.INACTIVE, true, new Localized("CA", "Canada", "Canada"));
		
		Bson query = new BasicDBObject()
				.append("type", Type.COUNTRY.getCode())
				.append("options.optionId", new CountryIdentifier("CA").getFullIdentifier());
		
		BasicDBObject data = new BasicDBObject()
				.append("options.$.status", Status.INACTIVE.getCode())
				.append("options.$.name.english", "CANADA")
				.append("options.$.name.french", "CANADA");
			
		
		BasicDBObject command = new BasicDBObject()
				.append("$set", data);
		
		UpdateResult result = mongoCrm.getCollection("options").updateOne(query, command);
		if (result.getModifiedCount() == 0) {
			System.out.println("No updates applied");
		}
		else {
			System.out.println(result);
		}
	}
	
//	@Test
	public void testCreateOption() {
		
		Bson query = new BasicDBObject()
				.append("type", Type.SALUTATION.getCode())
				.append("options.optionId", new BasicDBObject()
						.append("$ne", new SalutationIdentifier("MR").getFullIdentifier())); // don't push if it already exists
		
		BasicDBObject data = new BasicDBObject()
				.append("options", new BasicDBObject()
						.append("optionId", new SalutationIdentifier("MR").getFullIdentifier())
						.append("parentId", null)
						.append("status", Status.ACTIVE.getCode())
						.append("mutable", Boolean.TRUE)
						.append("name", new BasicDBObject()
								.append("code", "MR.")
								.append("english", "Mr.")
								.append("english_searchable", "mr")
								.append("french", "M.")
								.append("english_searchable", "m")));
				
		BasicDBObject command = new BasicDBObject()
				.append("$push", data);
		
		UpdateResult result = mongoCrm.getCollection("options").updateOne(query, command, new UpdateOptions().upsert(true));
		if (result.getModifiedCount() == 0) {
			System.out.println("No updates applied: " + result);
		}
		else {
			System.out.println(result);
		}
	}
	
//	@Test
	public void insertOptionsRepository() {
		MongoCollection<Document> collection = mongoCrm.getCollection("options");
		
//		{
//			List<BasicDBObject> statusList = new ArrayList<>();
//			for (Status status : Status.values()) {
//				Option option = new Option(new StatusIdentifier(status.getName().getCode()), null, Type.STATUS, Status.ACTIVE, false, status.getName());
//				statusList.add(new BasicDBObject()					
//						.append("optionId",  option.getOptionId().getFullIdentifier())
//						.append("parentId", option.getParentId() == null ? null : option.getParentId().getFullIdentifier())
//						.append("status", option.getStatus().getCode())
//						.append("mutable", option.getMutable())
//						.append("name", new BasicDBObject()
//								.append("code", option.getName().getCode())
//								.append("english", option.getName().getEnglishName())
//								.append("english_searchable", TextUtils.toSearchable(option.getName().getEnglishName()))
//								.append("french", option.getName().getFrenchName())
//								.append("french_searchable", TextUtils.toSearchable(option.getName().getFrenchName()))
//						));
//			}
//			InsertOneResult result = collection.insertOne(new Document()
//					.append("type", Type.STATUS.getCode())
//					.append("options", statusList));
//			System.out.println(result);
//		}
//		
//		{
//			List<BasicDBObject> localeList = new ArrayList<>();
//			for (Map.Entry<Locale, Localized> entry : Lang.NAMES.entrySet()) {			
//				Option option = new Option(new LocaleIdentifier(entry.getValue().getCode()), null, Type.LOCALE, Status.ACTIVE, false, entry.getValue());
//				localeList.add(new BasicDBObject()					
//						.append("optionId",  option.getOptionId().getFullIdentifier())
//						.append("parentId", option.getParentId() == null ? null : option.getParentId().getFullIdentifier())
//						.append("status", option.getStatus().getCode())
//						.append("mutable", option.getMutable())
//						.append("name", new BasicDBObject()
//								.append("code", option.getName().getCode())
//								.append("english", option.getName().getEnglishName())
//								.append("english_searchable", TextUtils.toSearchable(option.getName().getEnglishName()))
//								.append("french", option.getName().getFrenchName())
//								.append("french_searchable", TextUtils.toSearchable(option.getName().getFrenchName()))
//						));
//			}
//			InsertOneResult result = collection.insertOne(new Document()
//					.append("type", Type.LOCALE.getCode())
//					.append("options", localeList));
//			System.out.println(result);
//		}
		
		{			
			List<BasicDBObject> countryList = new ArrayList<>();
			for (Localized country : List.of(new Localized("CA", "Canada", "Canada"), new Localized("US", "United States", "États Unis"), new Localized("DE", "Germany", "Allemagne"), new Localized("FR", "France", "France"))) {			
				Option option = new Option(new CountryIdentifier(country.getCode()), null, Type.COUNTRY, Status.ACTIVE, true, country);
				countryList.add(new BasicDBObject()					
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
					.append("type", Type.COUNTRY.getCode())
					.append("options", countryList));
			System.out.println(result);
		}
		
//		{
//			List<BasicDBObject> provinceList = new ArrayList<>();
//			for (Localized province : List.of(new Localized("CA/ON", "Ontario", "Ontario"), new Localized("CA/QC", "Quebec", "Quèbec"), new Localized("US/NY", "New York", "New York"), new Localized("US/CA", "California", "Californie"))) {			
//				Option option = new Option(new ProvinceIdentifier(province.getCode()), new CountryIdentifier(province.getCode().subSequence(0, 2)), Type.PROVINCE, Status.ACTIVE, true, province);
//				provinceList.add(new BasicDBObject()					
//						.append("optionId",  option.getOptionId().getFullIdentifier())
//						.append("parentId", option.getParentId() == null ? null : option.getParentId().getFullIdentifier())
//						.append("status", option.getStatus().getCode())
//						.append("mutable", option.getMutable())
//						.append("name", new BasicDBObject()
//								.append("code", option.getName().getCode())
//								.append("english", option.getName().getEnglishName())
//								.append("english_searchable", TextUtils.toSearchable(option.getName().getEnglishName()))
//								.append("french", option.getName().getFrenchName())
//								.append("french_searchable", TextUtils.toSearchable(option.getName().getFrenchName()))
//						));
//			}
//			InsertOneResult result = collection.insertOne(new Document()
//					.append("type", Type.PROVINCE.getCode())
//					.append("options", provinceList));
//			System.out.println(result);
//		}
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
