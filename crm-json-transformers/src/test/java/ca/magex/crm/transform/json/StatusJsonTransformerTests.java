package ca.magex.crm.transform.json;

import static ca.magex.crm.test.CrmAsserts.SYSTEM_EMAIL;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_ORG;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_PERSON;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.transform.Transformer;
import ca.magex.crm.transform.TestCrm;
import ca.magex.json.model.JsonAsserts;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonText;

public class StatusJsonTransformerTests {
	
	private Crm crm;
	
	private Transformer<Status, JsonElement> transformer;
	
	@Before
	public void setup() {
		crm = TestCrm.build();
		crm.initializeSystem(SYSTEM_ORG, SYSTEM_PERSON, SYSTEM_EMAIL, "admin", "admin");
		transformer = new StatusJsonTransformer(crm);
	}
	
	@Test
	public void testTransformerType() throws Exception {
		assertEquals(Status.class, transformer.getSourceType());
	}

	@Test
	public void testFormatNull() throws Exception {
		assertNull(transformer.format(null, null));
		assertNull(transformer.format(null, Lang.ROOT));
		assertNull(transformer.format(null, Lang.ENGLISH));
		assertNull(transformer.format(null, Lang.FRENCH));
	}
	
	@Test
	public void testFormatJson() throws Exception {
		JsonObject root = (JsonObject)transformer.format(Status.ACTIVE, null);
		//JsonAsserts.print(root, "root");
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), root.keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Statuses", root.getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/statuses/active", root.getString("@id"));
		assertEquals("ACTIVE", root.getString("@value"));
		assertEquals("Active", root.getString("@en"));
		assertEquals("Actif", root.getString("@fr"));

		assertEquals(new JsonText("ACTIVE"), transformer.format(Status.ACTIVE, Lang.ROOT));
		assertEquals(new JsonText("Active"), transformer.format(Status.ACTIVE, Lang.ENGLISH));
		assertEquals(new JsonText("Actif"), transformer.format(Status.ACTIVE, Lang.FRENCH));

		assertEquals(new JsonText("INACTIVE"), transformer.format(Status.INACTIVE, Lang.ROOT));
		assertEquals(new JsonText("Inactive"), transformer.format(Status.INACTIVE, Lang.ENGLISH));
		assertEquals(new JsonText("Inactif"), transformer.format(Status.INACTIVE, Lang.FRENCH));

		assertEquals(new JsonText("PENDING"), transformer.format(Status.PENDING, Lang.ROOT));
		assertEquals(new JsonText("Pending"), transformer.format(Status.PENDING, Lang.ENGLISH));
		assertEquals(new JsonText("En attente"), transformer.format(Status.PENDING, Lang.FRENCH));
	}
	
	@Test
	public void testParsingJson() throws Exception {
		assertEquals(Status.ACTIVE, transformer.parse(transformer.format(Status.ACTIVE, null), null));
		assertEquals(Status.ACTIVE, transformer.parse(transformer.format(Status.ACTIVE, Lang.ROOT), Lang.ROOT));
		assertEquals(Status.ACTIVE, transformer.parse(transformer.format(Status.ACTIVE, Lang.ENGLISH), Lang.ENGLISH));
		assertEquals(Status.ACTIVE, transformer.parse(transformer.format(Status.ACTIVE, Lang.FRENCH), Lang.FRENCH));
		
		assertEquals(Status.INACTIVE, transformer.parse(transformer.format(Status.INACTIVE, null), null));
		assertEquals(Status.INACTIVE, transformer.parse(transformer.format(Status.INACTIVE, Lang.ROOT), Lang.ROOT));
		assertEquals(Status.INACTIVE, transformer.parse(transformer.format(Status.INACTIVE, Lang.ENGLISH), Lang.ENGLISH));
		assertEquals(Status.INACTIVE, transformer.parse(transformer.format(Status.INACTIVE, Lang.FRENCH), Lang.FRENCH));
		
		assertNull(transformer.parse(transformer.format(null, null), null));
		assertNull(transformer.parse(transformer.format(null, Lang.ROOT), Lang.ROOT));
		assertNull(transformer.parse(transformer.format(null, Lang.ENGLISH), Lang.ENGLISH));
		assertNull(transformer.parse(transformer.format(null, Lang.FRENCH), Lang.FRENCH));
	}
		
}
