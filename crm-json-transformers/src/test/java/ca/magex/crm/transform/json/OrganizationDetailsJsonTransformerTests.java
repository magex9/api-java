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
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.transform.Transformer;
import ca.magex.crm.transform.TestCrm;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;

public class OrganizationDetailsJsonTransformerTests {
	
	private Crm crm;
	
	private Transformer<OrganizationDetails, JsonElement> transformer;
	
	private OrganizationIdentifier organizationId;
	
	private LocationIdentifier mainLocationId;
	
	private PersonIdentifier mainContactId;
	
	private OrganizationDetails organization;
	
	@Before
	public void setup() {
		crm = TestCrm.build();
		crm.initializeSystem(SYSTEM_ORG, SYSTEM_PERSON, SYSTEM_EMAIL, "admin", "admin");
		transformer = new OrganizationDetailsJsonTransformer(crm);
		organizationId = new OrganizationIdentifier("Mc9rbFKPqf");
		mainLocationId = new LocationIdentifier("Z9XCi4sCTk");
		mainContactId = new PersonIdentifier("5Z7cuX3K2T");
		organization = new OrganizationDetails(organizationId, Status.ACTIVE, "Org Name", 
			mainLocationId, mainContactId, List.of(
			crm.findOptionByCode(Type.AUTHENTICATION_GROUP, "CRM").getOptionId(),
			crm.findOptionByCode(Type.AUTHENTICATION_GROUP, "ORG").getOptionId()
		));
	}
	
	@Test
	public void testTransformerType() throws Exception {
		assertEquals(OrganizationDetails.class, transformer.getSourceType());
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
		JsonObject linked = (JsonObject)transformer.format(organization, null);
		//JsonAsserts.print(linked, "linked");
		assertEquals(List.of("@context", "organizationId", "status", "displayName", "mainLocationId", "mainContactId", "groupIds"), linked.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/organization/OrganizationDetails", linked.getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/organizations/" + organizationId.getId(), linked.getString("organizationId"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("status").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Statuses", linked.getObject("status").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/statuses/active", linked.getObject("status").getString("@id"));
		assertEquals("ACTIVE", linked.getObject("status").getString("@value"));
		assertEquals("Active", linked.getObject("status").getString("@en"));
		assertEquals("Actif", linked.getObject("status").getString("@fr"));
		assertEquals("Org Name", linked.getString("displayName"));
		assertEquals("http://api.magex.ca/crm/rest/locations/" + mainLocationId.getId(), linked.getString("mainLocationId"));
		assertEquals("http://api.magex.ca/crm/rest/persons/" + mainContactId.getId(), linked.getString("mainContactId"));
		assertEquals(2, linked.getArray("groupIds").size());
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getArray("groupIds").getObject(0).keys());
		assertEquals("http://api.magex.ca/crm/schema/options/AuthenticationGroups", linked.getArray("groupIds").getObject(0).getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/authentication-groups/crm", linked.getArray("groupIds").getObject(0).getString("@id"));
		assertEquals("CRM", linked.getArray("groupIds").getObject(0).getString("@value"));
		assertEquals("Customer Relationship Management", linked.getArray("groupIds").getObject(0).getString("@en"));
		assertEquals("Gestion de la relation client", linked.getArray("groupIds").getObject(0).getString("@fr"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getArray("groupIds").getObject(1).keys());
		assertEquals("http://api.magex.ca/crm/schema/options/AuthenticationGroups", linked.getArray("groupIds").getObject(1).getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/authentication-groups/org", linked.getArray("groupIds").getObject(1).getString("@id"));
		assertEquals("ORG", linked.getArray("groupIds").getObject(1).getString("@value"));
		assertEquals("Organization", linked.getArray("groupIds").getObject(1).getString("@en"));
		assertEquals("Organisation", linked.getArray("groupIds").getObject(1).getString("@fr"));
		assertEquals(organization, transformer.parse(linked, null));
	}
	
	@Test
	public void testRootJson() throws Exception {
		JsonObject root = (JsonObject)transformer.format(organization, Lang.ROOT);
		//JsonAsserts.print(root, "root");
		assertEquals(List.of("organizationId", "status", "displayName", "mainLocationId", "mainContactId", "groupIds"), root.keys());
		assertEquals(organizationId.getId(), root.getString("organizationId"));
		assertEquals("ACTIVE", root.getString("status"));
		assertEquals("Org Name", root.getString("displayName"));
		assertEquals(mainLocationId.getId(), root.getString("mainLocationId"));
		assertEquals(mainContactId.getId(), root.getString("mainContactId"));
		assertEquals(2, root.getArray("groupIds").size());
		assertEquals("CRM", root.getArray("groupIds").getString(0));
		assertEquals("ORG", root.getArray("groupIds").getString(1));
		assertEquals(organization, transformer.parse(root, Lang.ROOT));
	}
	
	@Test
	public void testEnglishJson() throws Exception {
		JsonObject english = (JsonObject)transformer.format(organization, Lang.ENGLISH);
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("organizationId", "status", "displayName", "mainLocationId", "mainContactId", "groupIds"), english.keys());
		assertEquals(organizationId.getId(), english.getString("organizationId"));
		assertEquals("Active", english.getString("status"));
		assertEquals("Org Name", english.getString("displayName"));
		assertEquals(mainLocationId.getId(), english.getString("mainLocationId"));
		assertEquals(mainContactId.getId(), english.getString("mainContactId"));
		assertEquals(2, english.getArray("groupIds").size());
		assertEquals("Customer Relationship Management", english.getArray("groupIds").getString(0));
		assertEquals("Organization", english.getArray("groupIds").getString(1));
		assertEquals(organization, transformer.parse(english, Lang.ENGLISH));
	}
	
	@Test
	public void testFrenchJson() throws Exception {
		JsonObject french = (JsonObject)transformer.format(organization, Lang.FRENCH);
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("organizationId", "status", "displayName", "mainLocationId", "mainContactId", "groupIds"), french.keys());
		assertEquals(organizationId.getId(), french.getString("organizationId"));
		assertEquals("Actif", french.getString("status"));
		assertEquals("Org Name", french.getString("displayName"));
		assertEquals(mainLocationId.getId(), french.getString("mainLocationId"));
		assertEquals(mainContactId.getId(), french.getString("mainContactId"));
		assertEquals(2, french.getArray("groupIds").size());
		assertEquals("Gestion de la relation client", french.getArray("groupIds").getString(0));
		assertEquals("Organisation", french.getArray("groupIds").getString(1));
		assertEquals(organization, transformer.parse(french, Lang.FRENCH));
	}
	
}
