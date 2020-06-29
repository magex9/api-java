package ca.magex.crm.transform.json;

import static ca.magex.crm.test.CrmAsserts.MAILING_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_EMAIL;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_ORG;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_PERSON;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.transform.Transformer;
import ca.magex.crm.transform.TestCrm;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;

public class LocationDetailsJsonTransformerTests {
	
	private Crm crm;
	
	private Transformer<LocationDetails, JsonElement> transformer;
	
	private LocationDetails location;
	
	@Before
	public void setup() {
		crm = TestCrm.build();
		crm.initializeSystem(SYSTEM_ORG, SYSTEM_PERSON, SYSTEM_EMAIL, "admin", "admin");
		transformer = new LocationDetailsJsonTransformer(crm);
		location = new LocationDetails(new LocationIdentifier("loc"), new OrganizationIdentifier("org"), Status.ACTIVE, "REF", "Location Name", MAILING_ADDRESS);
	}
	
	@Test
	public void testTransformerType() throws Exception {
		assertEquals(LocationDetails.class, transformer.getSourceType());
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
		System.out.println(linked);
		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName", "address"), linked.keys());
		assertEquals("LocationDetails", linked.getString("@type"));
		assertEquals(List.of("@type", "@id"), linked.getObject("locationId").keys());
		assertEquals("Identifier", linked.getObject("locationId").getString("@type"));
		assertEquals("loc", linked.getObject("locationId").getString("@id"));
		assertEquals(List.of("@type", "@id"), linked.getObject("organizationId").keys());
		assertEquals("Identifier", linked.getObject("organizationId").getString("@type"));
		assertEquals("org", linked.getObject("organizationId").getString("@id"));
		assertEquals(List.of("@type", "@lookup", "@value", "@en", "@fr"), linked.getObject("status").keys());
		assertEquals("Option", linked.getObject("status").getString("@type"));
		assertEquals("STATUS", linked.getObject("status").getString("@lookup"));
		assertEquals("active", linked.getObject("status").getString("@value"));
		assertEquals("Active", linked.getObject("status").getString("@en"));
		assertEquals("Actif", linked.getObject("status").getString("@fr"));
		assertEquals("REF", linked.getString("reference"));
		assertEquals("Location Name", linked.getString("displayName"));
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), linked.getObject("address").keys());
		assertEquals("MailingAddress", linked.getObject("address").getString("@type"));
		assertEquals("123 Main St", linked.getObject("address").getString("street"));
		assertEquals("Ottawa", linked.getObject("address").getString("city"));
		assertEquals(List.of("@type", "@lookup", "@value", "@en", "@fr"), linked.getObject("address").getObject("province").keys());
		assertEquals("PROVINCE", linked.getObject("address").getObject("province").getString("@type"));
		assertEquals("QC", linked.getObject("address").getObject("province").getString("@value"));
		assertEquals("Quebec", linked.getObject("address").getObject("province").getString("@en"));
		assertEquals("Québec", linked.getObject("address").getObject("province").getString("@fr"));
		assertEquals(List.of("@type", "@lookup", "@value", "@en", "@fr"), linked.getObject("address").getObject("country").keys());
		assertEquals("Country", linked.getObject("address").getObject("country").getString("@type"));
		assertEquals("CA", linked.getObject("address").getObject("country").getString("@value"));
		assertEquals("Canada", linked.getObject("address").getObject("country").getString("@en"));
		assertEquals("Canada", linked.getObject("address").getObject("country").getString("@fr"));
		assertEquals("K1K1K1", linked.getObject("address").getString("postalCode"));
		assertEquals(location, transformer.parse(linked, null));
	}
	
	@Test
	public void testRootJson() throws Exception {
		JsonObject root = (JsonObject)transformer.format(location, Lang.ROOT);
		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName", "address"), root.keys());
		assertEquals("LocationDetails", root.getString("@type"));
		assertEquals("loc", root.getString("locationId"));
		assertEquals("org", root.getString("organizationId"));
		assertEquals("active", root.getString("status"));
		assertEquals("REF", root.getString("reference"));
		assertEquals("Location Name", root.getString("displayName"));
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), root.getObject("address").keys());
		assertEquals("MailingAddress", root.getObject("address").getString("@type"));
		assertEquals("123 Main St", root.getObject("address").getString("street"));
		assertEquals("Ottawa", root.getObject("address").getString("city"));
		assertEquals("QC", root.getObject("address").getString("province"));
		assertEquals("CA", root.getObject("address").getString("country"));
		assertEquals("K1K1K1", root.getObject("address").getString("postalCode"));
	}
	
	@Test
	public void testEnglishJson() throws Exception {
		JsonObject english = (JsonObject)transformer.format(location, Lang.ENGLISH);
		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName", "address"), english.keys());
		assertEquals("LocationDetails", english.getString("@type"));
		assertEquals("loc", english.getString("locationId"));
		assertEquals("org", english.getString("organizationId"));
		assertEquals("Active", english.getString("status"));
		assertEquals("REF", english.getString("reference"));
		assertEquals("Location Name", english.getString("displayName"));
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), english.getObject("address").keys());
		assertEquals("MailingAddress", english.getObject("address").getString("@type"));
		assertEquals("123 Main St", english.getObject("address").getString("street"));
		assertEquals("Ottawa", english.getObject("address").getString("city"));
		assertEquals("Quebec", english.getObject("address").getString("province"));
		assertEquals("Canada", english.getObject("address").getString("country"));
		assertEquals("K1K1K1", english.getObject("address").getString("postalCode"));
	}
	
	@Test
	public void testFrenchJson() throws Exception {
		JsonObject french = (JsonObject)transformer.format(location, Lang.FRENCH);
		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName", "address"), french.keys());
		assertEquals("LocationDetails", french.getString("@type"));
		assertEquals("loc", french.getString("locationId"));
		assertEquals("org", french.getString("organizationId"));
		assertEquals("Actif", french.getString("status"));
		assertEquals("REF", french.getString("reference"));
		assertEquals("Location Name", french.getString("displayName"));
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), french.getObject("address").keys());
		assertEquals("MailingAddress", french.getObject("address").getString("@type"));
		assertEquals("123 Main St", french.getObject("address").getString("street"));
		assertEquals("Ottawa", french.getObject("address").getString("city"));
		assertEquals("Québec", french.getObject("address").getString("province"));
		assertEquals("Canada", french.getObject("address").getString("country"));
		assertEquals("K1K1K1", french.getObject("address").getString("postalCode"));
	}
	
}
