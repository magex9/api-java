package ca.magex.crm.transform.json;

import static ca.magex.crm.test.CrmAsserts.PERSON_DISPLAY_NAME;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_EMAIL;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_ORG;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_PERSON;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.services.CrmConfigurationService;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.transform.Transformer;
import ca.magex.crm.test.config.BasicTestConfig;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { BasicTestConfig.class })
public class PersonSummaryJsonTransformerTests {
	
	@Autowired private Crm crm;
	
	@Autowired private CrmConfigurationService config;
	
	private Transformer<PersonSummary, JsonElement> transformer;
	
	private PersonIdentifier personId;
	
	private OrganizationIdentifier organizationId;
	
	private PersonSummary person;
	
	@Before
	public void setup() {
		config.initializeSystem(SYSTEM_ORG, SYSTEM_PERSON, SYSTEM_EMAIL, "admin", "admin");
		transformer = new PersonSummaryJsonTransformer(crm);
		personId = new PersonIdentifier("TkNj8jzNGC");
		organizationId = new OrganizationIdentifier("DSbVnvGGyf");
		person = new PersonSummary(personId, organizationId, Status.ACTIVE, PERSON_DISPLAY_NAME);
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
		//JsonAsserts.print(linked, "linked");
		assertEquals(List.of("@context", "personId", "organizationId", "status", "displayName"), linked.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/organization/PersonSummary", linked.getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/persons/" + personId.getCode(), linked.getString("personId"));
		assertEquals("http://api.magex.ca/crm/rest/organizations/" + organizationId.getCode(), linked.getString("organizationId"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("status").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Statuses", linked.getObject("status").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/statuses/active", linked.getObject("status").getString("@id"));
		assertEquals("ACTIVE", linked.getObject("status").getString("@value"));
		assertEquals("Active", linked.getObject("status").getString("@en"));
		assertEquals("Actif", linked.getObject("status").getString("@fr"));
		assertEquals("Bacon, Chris P", linked.getString("displayName"));
		assertEquals(person, transformer.parse(linked, null));
	}
	
	@Test
	public void testRootJson() throws Exception {
		JsonObject root = (JsonObject)transformer.format(person, Lang.ROOT);
		//JsonAsserts.print(root, "root");
		assertEquals(List.of("personId", "organizationId", "status", "displayName"), root.keys());
		assertEquals(personId.getCode(), root.getString("personId"));
		assertEquals(organizationId.getCode(), root.getString("organizationId"));
		assertEquals("ACTIVE", root.getString("status"));
		assertEquals("Bacon, Chris P", root.getString("displayName"));
		assertEquals(person, transformer.parse(root, Lang.ROOT));
	}
	
	@Test
	public void testEnglishJson() throws Exception {
		JsonObject english = (JsonObject)transformer.format(person, Lang.ENGLISH);
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("personId", "organizationId", "status", "displayName"), english.keys());
		assertEquals(personId.getCode(), english.getString("personId"));
		assertEquals(organizationId.getCode(), english.getString("organizationId"));
		assertEquals("Active", english.getString("status"));
		assertEquals("Bacon, Chris P", english.getString("displayName"));
		assertEquals(person, transformer.parse(english, Lang.ENGLISH));
	}
	
	@Test
	public void testFrenchJson() throws Exception {
		JsonObject french = (JsonObject)transformer.format(person, Lang.FRENCH);
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("personId", "organizationId", "status", "displayName"), french.keys());
		assertEquals(personId.getCode(), french.getString("personId"));
		assertEquals(organizationId.getCode(), french.getString("organizationId"));
		assertEquals("Actif", french.getString("status"));
		assertEquals("Bacon, Chris P", french.getString("displayName"));
		assertEquals(person, transformer.parse(french, Lang.FRENCH));
	}
	
}
