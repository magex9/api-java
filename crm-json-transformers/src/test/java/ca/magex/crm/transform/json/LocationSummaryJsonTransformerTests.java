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
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.transform.Transformer;
import ca.magex.crm.transform.TestCrm;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;

public class LocationSummaryJsonTransformerTests {
	
	private Crm crm;
	
	private Transformer<LocationSummary, JsonElement> transformer;
	
	private LocationIdentifier locationId;
	
	private OrganizationIdentifier organizationId;
	
	private LocationSummary location;
	
	@Before
	public void setup() {
		crm = TestCrm.build();
		crm.initializeSystem(SYSTEM_ORG, SYSTEM_PERSON, SYSTEM_EMAIL, "admin", "admin");
		transformer = new LocationSummaryJsonTransformer(crm);
		locationId = new LocationIdentifier("YnkAfZQnsk");
		organizationId = new OrganizationIdentifier("s6rf61eooZ");
		location = new LocationSummary(locationId, organizationId, Status.ACTIVE, "SUM_REF", "Summary Name");
	}
	
	@Test
	public void testTransformerType() throws Exception {
		assertEquals(LocationSummary.class, transformer.getSourceType());
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
		JsonObject linked = (JsonObject)transformer.format(location, null);
		//JsonAsserts.print(linked, "linked");
		assertEquals(List.of("@context", "locationId", "organizationId", "status", "reference", "displayName"), linked.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/organization/LocationSummary", linked.getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/locations/" + locationId.getCode(), linked.getString("locationId"));
		assertEquals("http://api.magex.ca/crm/rest/organizations/" + organizationId.getCode(), linked.getString("organizationId"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("status").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Statuses", linked.getObject("status").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/statuses/active", linked.getObject("status").getString("@id"));
		assertEquals("ACTIVE", linked.getObject("status").getString("@value"));
		assertEquals("Active", linked.getObject("status").getString("@en"));
		assertEquals("Actif", linked.getObject("status").getString("@fr"));
		assertEquals("SUM_REF", linked.getString("reference"));
		assertEquals("Summary Name", linked.getString("displayName"));
		assertEquals(location, transformer.parse(linked, null));
	}
	
	@Test
	public void testRootJson() throws Exception {
		JsonObject root = (JsonObject)transformer.format(location, Lang.ROOT);
		//JsonAsserts.print(root, "root");
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName"), root.keys());
		assertEquals(locationId.getCode(), root.getString("locationId"));
		assertEquals(organizationId.getCode(), root.getString("organizationId"));
		assertEquals("ACTIVE", root.getString("status"));
		assertEquals("SUM_REF", root.getString("reference"));
		assertEquals("Summary Name", root.getString("displayName"));
		assertEquals(location, transformer.parse(root, Lang.ROOT));
	}
	
	@Test
	public void testEnglishJson() throws Exception {
		JsonObject english = (JsonObject)transformer.format(location, Lang.ENGLISH);
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName"), english.keys());
		assertEquals(locationId.getCode(), english.getString("locationId"));
		assertEquals(organizationId.getCode(), english.getString("organizationId"));
		assertEquals("Active", english.getString("status"));
		assertEquals("SUM_REF", english.getString("reference"));
		assertEquals("Summary Name", english.getString("displayName"));
		assertEquals(location, transformer.parse(english, Lang.ENGLISH));
	}
	
	@Test
	public void testFrenchJson() throws Exception {
		JsonObject french = (JsonObject)transformer.format(location, Lang.FRENCH);
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName"), french.keys());
		assertEquals(locationId.getCode(), french.getString("locationId"));
		assertEquals(organizationId.getCode(), french.getString("organizationId"));
		assertEquals("Actif", french.getString("status"));
		assertEquals("SUM_REF", french.getString("reference"));
		assertEquals("Summary Name", french.getString("displayName"));
		assertEquals(location, transformer.parse(french, Lang.FRENCH));
	}
	
}
