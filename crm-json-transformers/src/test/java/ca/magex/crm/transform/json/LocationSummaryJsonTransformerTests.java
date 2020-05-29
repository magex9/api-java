package ca.magex.crm.transform.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ca.magex.crm.amnesia.services.AmnesiaServices;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Status;
import ca.magex.json.model.JsonObject;
import ca.magex.json.util.Transformer;

public class LocationSummaryJsonTransformerTests {
	
	private CrmServices crm;
	
	private Transformer<LocationSummary> transformer;
	
	private LocationSummary location;
	
	@Before
	public void setup() {
		crm = new AmnesiaServices();
		transformer = new LocationSummaryJsonTransformer(crm);
		location = new LocationSummary(new Identifier("loc"), new Identifier("org"), Status.ACTIVE, "REF", "Location Name");
	}
	
	@Test
	public void testTransformerType() throws Exception {
		assertEquals(LocationSummary.class, transformer.getType());
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
		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName"), linked.keys());
		assertEquals("LocationSummary", linked.getString("@type"));
		assertEquals(List.of("@type", "@id"), linked.getObject("locationId").keys());
		assertEquals("Identifier", linked.getObject("locationId").getString("@type"));
		assertEquals("loc", linked.getObject("locationId").getString("@id"));
		assertEquals(List.of("@type", "@id"), linked.getObject("organizationId").keys());
		assertEquals("Identifier", linked.getObject("organizationId").getString("@type"));
		assertEquals("org", linked.getObject("organizationId").getString("@id"));
		assertEquals(List.of("@type", "@value", "@en", "@fr"), linked.getObject("status").keys());
		assertEquals("Status", linked.getObject("status").getString("@type"));
		assertEquals("active", linked.getObject("status").getString("@value"));
		assertEquals("Active", linked.getObject("status").getString("@en"));
		assertEquals("Actif", linked.getObject("status").getString("@fr"));
		assertEquals("REF", linked.getString("reference"));
		assertEquals("Location Name", linked.getString("displayName"));
		assertEquals(location, transformer.parse(linked, null));
	}
	
	@Test
	public void testRootJson() throws Exception {
		JsonObject root = (JsonObject)transformer.format(location, Lang.ROOT);
		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName"), root.keys());
		assertEquals("LocationSummary", root.getString("@type"));
		assertEquals("loc", root.getString("locationId"));
		assertEquals("org", root.getString("organizationId"));
		assertEquals("active", root.getString("status"));
		assertEquals("REF", root.getString("reference"));
		assertEquals("Location Name", root.getString("displayName"));
	}
	
	@Test
	public void testEnglishJson() throws Exception {
		JsonObject english = (JsonObject)transformer.format(location, Lang.ENGLISH);
		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName"), english.keys());
		assertEquals("LocationSummary", english.getString("@type"));
		assertEquals("loc", english.getString("locationId"));
		assertEquals("org", english.getString("organizationId"));
		assertEquals("Active", english.getString("status"));
		assertEquals("REF", english.getString("reference"));
		assertEquals("Location Name", english.getString("displayName"));
	}
	
	@Test
	public void testFrenchJson() throws Exception {
		JsonObject french = (JsonObject)transformer.format(location, Lang.FRENCH);
		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName"), french.keys());
		assertEquals("LocationSummary", french.getString("@type"));
		assertEquals("loc", french.getString("locationId"));
		assertEquals("org", french.getString("organizationId"));
		assertEquals("Actif", french.getString("status"));
		assertEquals("REF", french.getString("reference"));
		assertEquals("Location Name", french.getString("displayName"));
	}
	
}
