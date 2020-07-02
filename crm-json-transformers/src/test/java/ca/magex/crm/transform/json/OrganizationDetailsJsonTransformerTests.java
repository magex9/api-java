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
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;

public class OrganizationDetailsJsonTransformerTests {
	
	private Crm crm;
	
	private Transformer<OrganizationDetails, JsonElement> transformer;
	
	private OrganizationDetails organization;
	
	@Before
	public void setup() {
		crm = TestCrm.build();
		crm.initializeSystem(SYSTEM_ORG, SYSTEM_PERSON, SYSTEM_EMAIL, "admin", "admin");
		transformer = new OrganizationDetailsJsonTransformer(crm);
		organization = new OrganizationDetails(new OrganizationIdentifier("Mc9rbFKPqf"), Status.ACTIVE, "Org Name", 
			new LocationIdentifier("Z9XCi4sCTk"), new PersonIdentifier("5Z7cuX3K2T"), List.of(
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
		System.out.println(linked);
		assertEquals(List.of("@type", "organizationId", "status", "displayName", "mainLocationId", "mainContactId", "groups"), linked.keys());
		assertEquals("OrganizationDetails", linked.getString("@type"));
		assertEquals(List.of("@type", "@id"), linked.getObject("organizationId").keys());
		assertEquals("Identifier", linked.getObject("organizationId").getString("@type"));
		assertEquals("org", linked.getObject("organizationId").getString("@id"));
		assertEquals(List.of("@type", "@lookup", "@value", "@en", "@fr"), linked.getObject("status").keys());
		assertEquals("Status", linked.getObject("status").getString("@type"));
		assertEquals("active", linked.getObject("status").getString("@value"));
		assertEquals("Active", linked.getObject("status").getString("@en"));
		assertEquals("Actif", linked.getObject("status").getString("@fr"));
		assertEquals("Org Name", linked.getString("displayName"));
		assertEquals(List.of("@type", "@id"), linked.getObject("mainLocationId").keys());
		assertEquals("Identifier", linked.getObject("mainLocationId").getString("@type"));
		assertEquals("mainLoc", linked.getObject("mainLocationId").getString("@id"));
		assertEquals(List.of("@type", "@id"), linked.getObject("mainContactId").keys());
		assertEquals("Identifier", linked.getObject("mainContactId").getString("@type"));
		assertEquals("mainContact", linked.getObject("mainContactId").getString("@id"));
		assertEquals(new JsonArray().with("G1", "G2"), linked.getArray("groups"));
		assertEquals(organization, transformer.parse(linked, null));
	}
	
	@Test
	public void testRootJson() throws Exception {
		JsonObject root = (JsonObject)transformer.format(organization, Lang.ROOT);
		System.out.println(root);
		assertEquals(List.of("@type", "organizationId", "status", "displayName", "mainLocationId", "mainContactId", "groups"), root.keys());
		assertEquals("OrganizationDetails", root.getString("@type"));
		assertEquals("org", root.getString("organizationId"));
		assertEquals("active", root.getString("status"));
		assertEquals("Org Name", root.getString("displayName"));
		assertEquals("mainLoc", root.getString("mainLocationId"));
		assertEquals("mainContact", root.getString("mainContactId"));
		assertEquals(new JsonArray().with("G1", "G2"), root.getArray("groups"));
	}
	
	@Test
	public void testEnglishJson() throws Exception {
		JsonObject english = (JsonObject)transformer.format(organization, Lang.ENGLISH);
		System.out.println(english);
		assertEquals(List.of("@type", "organizationId", "status", "displayName", "mainLocationId", "mainContactId", "groups"), english.keys());
		assertEquals("OrganizationDetails", english.getString("@type"));
		assertEquals("org", english.getString("organizationId"));
		assertEquals("Active", english.getString("status"));
		assertEquals("Org Name", english.getString("displayName"));
		assertEquals("mainLoc", english.getString("mainLocationId"));
		assertEquals("mainContact", english.getString("mainContactId"));
		assertEquals(new JsonArray().with("G1", "G2"), english.getArray("groups"));
	}
	
	@Test
	public void testFrenchJson() throws Exception {
		JsonObject french = (JsonObject)transformer.format(organization, Lang.FRENCH);
		System.out.println(french);
		assertEquals(List.of("@type", "organizationId", "status", "displayName", "mainLocationId", "mainContactId", "groups"), french.keys());
		assertEquals("OrganizationDetails", french.getString("@type"));
		assertEquals("org", french.getString("organizationId"));
		assertEquals("Actif", french.getString("status"));
		assertEquals("Org Name", french.getString("displayName"));
		assertEquals("mainLoc", french.getString("mainLocationId"));
		assertEquals("mainContact", french.getString("mainContactId"));
		assertEquals(new JsonArray().with("G1", "G2"), french.getArray("groups"));
	}
	
}
