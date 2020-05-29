package ca.magex.crm.transform.json;

import static ca.magex.crm.test.CrmAsserts.PERSON_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ca.magex.crm.amnesia.services.AmnesiaServices;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.transform.Transformer;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;

public class PersonNameJsonTransformerTests {
	
	private CrmServices crm;
	
	private Transformer<PersonName, JsonElement> transformer;
	
	private Salutation salutation;
	
	private PersonName personName;
	
	@Before
	public void setup() {
		crm = new AmnesiaServices();
		transformer = new PersonNameJsonTransformer(crm,
			new SalutationJsonTransformer(crm)
		);
		salutation = crm.findSalutationByLocalizedName(Lang.ENGLISH, "Mr.");
		personName = PERSON_NAME.withSalutation(salutation.getCode());
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
		assertEquals(List.of("@type", "salutation", "firstName", "middleName", "lastName"), linked.keys());
		assertEquals("PersonName", linked.getString("@type"));
		assertEquals(List.of("@type", "@value", "@en", "@fr"), linked.getObject("salutation").keys());
		assertEquals("Salutation", linked.getObject("salutation").getString("@type"));
		assertEquals("3", linked.getObject("salutation").getString("@value"));
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
		assertEquals(List.of("@type", "salutation", "firstName", "middleName", "lastName"), root.keys());
		assertEquals("PersonName", root.getString("@type"));
		assertEquals("3", root.getString("salutation"));
		assertEquals("Chris", root.getString("firstName"));
		assertEquals("P", root.getString("middleName"));
		assertEquals("Bacon", root.getString("lastName"));
		assertEquals(personName, transformer.parse(root, Lang.ROOT));
	}
	
	@Test
	public void testEnglishJson() throws Exception {
		JsonObject english = (JsonObject)transformer.format(personName, Lang.ENGLISH);
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
		assertEquals(List.of("@type", "salutation", "firstName", "middleName", "lastName"), french.keys());
		assertEquals("PersonName", french.getString("@type"));
		assertEquals("M.", french.getString("salutation"));
		assertEquals("Chris", french.getString("firstName"));
		assertEquals("P", french.getString("middleName"));
		assertEquals("Bacon", french.getString("lastName"));
		assertEquals(personName, transformer.parse(french, Lang.FRENCH));
	}
	
}
