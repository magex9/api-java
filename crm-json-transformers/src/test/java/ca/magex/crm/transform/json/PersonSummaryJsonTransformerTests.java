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
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.transform.Transformer;
import ca.magex.crm.transform.TestCrm;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;

public class PersonSummaryJsonTransformerTests {
	
	private Crm crm;
	
	private Transformer<PersonSummary, JsonElement> transformer;
	
	private PersonSummary person;
	
	@Before
	public void setup() {
		crm = TestCrm.build();
		crm.initializeSystem(SYSTEM_ORG, SYSTEM_PERSON, SYSTEM_EMAIL, "admin", "admin");
		transformer = new PersonSummaryJsonTransformer(crm);
		person = new PersonSummary(new Identifier("prsn"), new Identifier("org"), Status.ACTIVE, PERSON_NAME.getDisplayName());
	}
	
	@Test
	public void testTransformerType() throws Exception {
		assertEquals(PersonSummary.class, transformer.getSourceType());
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
		JsonObject linked = (JsonObject)transformer.format(person, null);
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), linked.keys());
		assertEquals("PersonSummary", linked.getString("@type"));
		assertEquals(List.of("@type", "@id"), linked.getObject("personId").keys());
		assertEquals("Identifier", linked.getObject("personId").getString("@type"));
		assertEquals("prsn", linked.getObject("personId").getString("@id"));
		assertEquals(List.of("@type", "@id"), linked.getObject("organizationId").keys());
		assertEquals("Identifier", linked.getObject("organizationId").getString("@type"));
		assertEquals("org", linked.getObject("organizationId").getString("@id"));
		assertEquals(List.of("@type", "@value", "@en", "@fr"), linked.getObject("status").keys());
		assertEquals("Status", linked.getObject("status").getString("@type"));
		assertEquals("active", linked.getObject("status").getString("@value"));
		assertEquals("Active", linked.getObject("status").getString("@en"));
		assertEquals("Actif", linked.getObject("status").getString("@fr"));
		assertEquals("Bacon, Chris P", linked.getString("displayName"));
		assertEquals(person, transformer.parse(linked, null));
	}
	
	@Test
	public void testRootJson() throws Exception {
		JsonObject root = (JsonObject)transformer.format(person, Lang.ROOT);
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), root.keys());
		assertEquals("PersonSummary", root.getString("@type"));
		assertEquals("prsn", root.getString("personId"));
		assertEquals("org", root.getString("organizationId"));
		assertEquals("active", root.getString("status"));
		assertEquals("Bacon, Chris P", root.getString("displayName"));
	}
	
	@Test
	public void testEnglishJson() throws Exception {
		JsonObject english = (JsonObject)transformer.format(person, Lang.ENGLISH);
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), english.keys());
		assertEquals("PersonSummary", english.getString("@type"));
		assertEquals("prsn", english.getString("personId"));
		assertEquals("org", english.getString("organizationId"));
		assertEquals("Active", english.getString("status"));
		assertEquals("Bacon, Chris P", english.getString("displayName"));
	}
	
	@Test
	public void testFrenchJson() throws Exception {
		JsonObject french = (JsonObject)transformer.format(person, Lang.FRENCH);
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), french.keys());
		assertEquals("PersonSummary", french.getString("@type"));
		assertEquals("prsn", french.getString("personId"));
		assertEquals("org", french.getString("organizationId"));
		assertEquals("Actif", french.getString("status"));
		assertEquals("Bacon, Chris P", french.getString("displayName"));
	}
	
}
