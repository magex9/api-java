package ca.magex.crm.transform.json;

import static ca.magex.crm.test.CrmAsserts.PERSON_NAME;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_EMAIL;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_ORG;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_PERSON;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.transform.Transformer;
import ca.magex.crm.transform.TestCrm;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;

public class PersonNameJsonTransformerTests {
	
	private Crm crm;
	
	private Transformer<PersonName, JsonElement> transformer;
	
	private Option mr;
	
	private PersonName personName;
	
	@Before
	public void setup() {
		crm = TestCrm.build();
		crm.initializeSystem(SYSTEM_ORG, SYSTEM_PERSON, SYSTEM_EMAIL, "admin", "admin");
		transformer = new PersonNameJsonTransformer(crm);
		mr = crm.findOptions(crm.defaultOptionsFilter()
			.withType(Type.SALUTATION)
			.withName(Lang.ENGLISH, "Mr.")
		).getSingleItem();
		personName = PERSON_NAME.withSalutation(mr.getCode());
	}
	
	@Test
	public void testTransformerType() throws Exception {
		assertEquals(PersonName.class, transformer.getSourceType());
	}

	@Test
	public void testFormatNull() throws Exception {
		assertNull(transformer.format(null, null));
		assertNull(transformer.format(null, Lang.ROOT));
		assertNull(transformer.format(null, Lang.ENGLISH));
		assertNull(transformer.format(null, Lang.FRENCH));
	}
	
	@Test
	public void testLinkedJson() throws Exception {
		JsonObject linked = (JsonObject)transformer.format(personName, null);
		System.out.println(linked);
		assertEquals(List.of("@type", "salutation", "firstName", "middleName", "lastName"), linked.keys());
		assertEquals("PersonName", linked.getString("@type"));
		assertEquals(List.of("@type", "@lookup", "@value", "@en", "@fr"), linked.getObject("salutation").keys());
		assertEquals("Option", linked.getObject("salutation").getString("@type"));
		assertEquals("SALUTATION", linked.getObject("salutation").getString("@lookup"));
		assertEquals("MR", linked.getObject("salutation").getString("@value"));
		assertEquals("Mr.", linked.getObject("salutation").getString("@en"));
		assertEquals("M.", linked.getObject("salutation").getString("@fr"));
		assertEquals("Chris", linked.getString("firstName"));
		assertEquals("P", linked.getString("middleName"));
		assertEquals("Bacon", linked.getString("lastName"));
		assertEquals(personName, transformer.parse(linked, null));
	}
	
	@Test
	public void testRootJson() throws Exception {
		JsonObject root = (JsonObject)transformer.format(personName, Lang.ROOT);
		System.out.println(root);
		assertEquals(List.of("@type", "salutation", "firstName", "middleName", "lastName"), root.keys());
		assertEquals("PersonName", root.getString("@type"));
		assertEquals("MR", root.getString("salutation"));
		assertEquals("Chris", root.getString("firstName"));
		assertEquals("P", root.getString("middleName"));
		assertEquals("Bacon", root.getString("lastName"));
		assertEquals(personName, transformer.parse(root, Lang.ROOT));
	}
	
	@Test
	public void testEnglishJson() throws Exception {
		JsonObject english = (JsonObject)transformer.format(personName, Lang.ENGLISH);
		System.out.println(english);
		assertEquals(List.of("@type", "salutation", "firstName", "middleName", "lastName"), english.keys());
		assertEquals("PersonName", english.getString("@type"));
		assertEquals("Mr.", english.getString("salutation"));
		assertEquals("Chris", english.getString("firstName"));
		assertEquals("P", english.getString("middleName"));
		assertEquals("Bacon", english.getString("lastName"));
		assertEquals(personName, transformer.parse(english, Lang.ENGLISH));
	}
	
	@Test
	public void testFrenchJson() throws Exception {
		JsonObject french = (JsonObject)transformer.format(personName, Lang.FRENCH);
		System.out.println(french);
		assertEquals(List.of("@type", "salutation", "firstName", "middleName", "lastName"), french.keys());
		assertEquals("PersonName", french.getString("@type"));
		assertEquals("M.", french.getString("salutation"));
		assertEquals("Chris", french.getString("firstName"));
		assertEquals("P", french.getString("middleName"));
		assertEquals("Bacon", french.getString("lastName"));
		assertEquals(personName, transformer.parse(french, Lang.FRENCH));
	}
	
}
