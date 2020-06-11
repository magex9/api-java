package ca.magex.crm.transform.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ca.magex.crm.amnesia.services.AmnesiaCrm;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.transform.Transformer;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;

public class OrganizationDetailsJsonTransformerTests {
	
	private Crm crm;
	
	private Transformer<OrganizationDetails, JsonElement> transformer;
	
	private OrganizationDetails organization;
	
	@Before
	public void setup() {
		crm = new AmnesiaCrm();
		transformer = new OrganizationDetailsJsonTransformer(crm);
		organization = new OrganizationDetails(new Identifier("org"), Status.ACTIVE, "Org Name", new Identifier("mainLoc"), new Identifier("mainContact"), List.of("G1", "G2"));
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
		assertEquals(List.of("@type", "organizationId", "status", "displayName", "mainLocationId", "mainContactId", "groups"), linked.keys());
		assertEquals("OrganizationDetails", linked.getString("@type"));
		assertEquals(List.of("@type", "@id"), linked.getObject("organizationId").keys());
		assertEquals("Identifier", linked.getObject("organizationId").getString("@type"));
		assertEquals("org", linked.getObject("organizationId").getString("@id"));
		assertEquals(List.of("@type", "@value", "@en", "@fr"), linked.getObject("status").keys());
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
