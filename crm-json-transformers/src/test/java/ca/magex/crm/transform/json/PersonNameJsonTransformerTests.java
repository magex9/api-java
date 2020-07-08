package ca.magex.crm.transform.json;

import static ca.magex.crm.test.CrmAsserts.MR;
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
import ca.magex.crm.api.transform.Transformer;
import ca.magex.crm.transform.TestCrm;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;

public class PersonNameJsonTransformerTests {
	
	private Crm crm;
	
	private Transformer<PersonName, JsonElement> transformer;
	
	private PersonName personName;
	
	@Before
	public void setup() {
		crm = TestCrm.build();
		crm.initializeSystem(SYSTEM_ORG, SYSTEM_PERSON, SYSTEM_EMAIL, "admin", "admin");
		transformer = new PersonNameJsonTransformer(crm);
		personName = PERSON_NAME.withSalutation(MR);
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
		//JsonAsserts.print(linked, "linked");
		assertEquals(List.of("@context", "salutation", "firstName", "middleName", "lastName"), linked.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/PersonName", linked.getString("@context").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("salutation").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Salutations", linked.getObject("salutation").getString("@context").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("http://api.magex.ca/crm/rest/options/salutations/mr", linked.getObject("salutation").getString("@id").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("MR", linked.getObject("salutation").getString("@value").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("Mr.", linked.getObject("salutation").getString("@en").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("M.", linked.getObject("salutation").getString("@fr").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("Chris", linked.getString("firstName").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("P", linked.getString("middleName").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("Bacon", linked.getString("lastName").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(personName, transformer.parse(linked, null));
	}
	
	@Test
	public void testRootJson() throws Exception {
		JsonObject root = (JsonObject)transformer.format(personName, Lang.ROOT);
		//JsonAsserts.print(root, "root");
		assertEquals(List.of("salutation", "firstName", "middleName", "lastName"), root.keys());
		assertEquals("MR", root.getString("salutation"));
		assertEquals("Chris", root.getString("firstName"));
		assertEquals("P", root.getString("middleName"));
		assertEquals("Bacon", root.getString("lastName"));
		assertEquals(personName, transformer.parse(root, Lang.ROOT));
	}
	
	@Test
	public void testEnglishJson() throws Exception {
		JsonObject english = (JsonObject)transformer.format(personName, Lang.ENGLISH);
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("salutation", "firstName", "middleName", "lastName"), english.keys());
		assertEquals("Mr.", english.getString("salutation"));
		assertEquals("Chris", english.getString("firstName"));
		assertEquals("P", english.getString("middleName"));
		assertEquals("Bacon", english.getString("lastName"));
		assertEquals(personName, transformer.parse(english, Lang.ENGLISH));
	}
	
	@Test
	public void testFrenchJson() throws Exception {
		JsonObject french = (JsonObject)transformer.format(personName, Lang.FRENCH);
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("salutation", "firstName", "middleName", "lastName"), french.keys());
		assertEquals("M.", french.getString("salutation"));
		assertEquals("Chris", french.getString("firstName"));
		assertEquals("P", french.getString("middleName"));
		assertEquals("Bacon", french.getString("lastName"));
		assertEquals(personName, transformer.parse(french, Lang.FRENCH));
	}
	
}
