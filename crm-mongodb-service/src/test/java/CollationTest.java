import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.bson.conversions.Bson;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Collation;
import com.mongodb.client.model.CollationCaseFirst;
import com.mongodb.client.model.CollationStrength;
import com.mongodb.client.model.Sorts;

import ca.magex.crm.api.observer.CrmUpdateNotifier;
import ca.magex.crm.api.utils.CollationComparator;
import ca.magex.crm.mongodb.config.MongoTestConfig;
import ca.magex.json.model.JsonObject;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MongoTestConfig.class })
@EnableTransactionManagement
@Ignore
public class CollationTest {

	@Autowired MongoDatabase mongoCrm;
	@MockBean CrmUpdateNotifier notifier;
	
	@Test
	public void testCollation() {
//		List<Bson> list = new ArrayList<>();
//		for (Localized value : CrmAsserts.LOCALIZED_SORTING_OPTIONS) {
//			list.add(new BasicDBObject()
//					.append("code", value.getCode())
//					.append("english", value.getEnglishName())
//					.append("french", value.getFrenchName()));
//		}	
//		mongoCrm.getCollection("collation").insertOne(new Document().append("sortingOptions", list));
		
		ArrayList<Bson> pipeline = new ArrayList<>();
		pipeline.add(Aggregates.unwind("$sortingOptions"));
		pipeline.add(Aggregates.sort(Sorts.ascending("sortingOptions.french")));
		pipeline.add(Aggregates.replaceRoot("$sortingOptions")); // flatten the root with the sorting options element
		
		Collation collation = Collation
				.builder()
				.backwards(false)
				.collationStrength(CollationStrength.SECONDARY)
				.locale(Locale.CANADA_FRENCH.toString())
				.caseLevel(true)
				.collationCaseFirst(CollationCaseFirst.LOWER)				
				.build();
		
		final List<String> values = new ArrayList<>();
		
		mongoCrm.getCollection("collation").aggregate(pipeline).collation(collation).forEach((doc) -> {
			Assert.assertNotNull(doc);
			String jsonText = doc.toJson();
			JsonObject json = new JsonObject(jsonText);
			System.out.println(json.getString("french") + "(" + json.getString("code") + ")");
			values.add(json.getString("french"));
		});
		
		System.out.println("********************");		
		Collections.shuffle(values);
		values.forEach((v) -> {
			System.out.println(v);
		});		
		
		System.out.println(Arrays.asList(Collator.getAvailableLocales()));
		
		System.out.println("********************");		
		Collections.sort(values, new CollationComparator());
		values.forEach((v) -> {
			System.out.println(v);
		});
	}
}