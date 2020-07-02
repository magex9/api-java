package ca.magex.crm.transform.json;

import static ca.magex.crm.test.CrmAsserts.BASE_URL;
import static ca.magex.crm.test.CrmAsserts.CA_ADDRESS;
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
	
	private LocationIdentifier locationId;
	
	private OrganizationIdentifier organizationId;
	
	private LocationDetails location;
	
	@Before
	public void setup() {
		crm = TestCrm.build();
		crm.initializeSystem(SYSTEM_ORG, SYSTEM_PERSON, SYSTEM_EMAIL, "admin", "admin");
		transformer = new LocationDetailsJsonTransformer(crm);
		locationId = new LocationIdentifier("bN2ifcbtzA");
		organizationId = new OrganizationIdentifier("EEtP6HwXMo");
		location = new LocationDetails(locationId, organizationId, Status.ACTIVE, "REF", "Location Name", CA_ADDRESS);
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
		//JsonAsserts.print(linked, "linked");
		assertEquals(List.of("@context", "locationId", "organizationId", "status", "reference", "displayName", "address"), linked.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/organization/LocationDetails", linked.getString("@context"));
		assertEquals(BASE_URL + locationId.toString(), linked.getString("locationId"));
		assertEquals(BASE_URL + organizationId.toString(), linked.getString("organizationId"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("status").keys());
		assertEquals("http://api.magex.ca/crm/schema/lookup/Statuses", linked.getObject("status").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/lookups/statuses/active", linked.getObject("status").getString("@id"));
		assertEquals("ACTIVE", linked.getObject("status").getString("@value"));
		assertEquals("Active", linked.getObject("status").getString("@en"));
		assertEquals("Actif", linked.getObject("status").getString("@fr"));
		assertEquals("REF", linked.getString("reference"));
		assertEquals("Location Name", linked.getString("displayName"));
		assertEquals(List.of("@context", "street", "city", "province", "country", "postalCode"), linked.getObject("address").keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/MailingAddress", linked.getObject("address").getString("@context"));
		assertEquals("123 Main St", linked.getObject("address").getString("street"));
		assertEquals("Ottawa", linked.getObject("address").getString("city"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("address").getObject("province").keys());
		assertEquals("http://api.magex.ca/crm/schema/lookup/Provinces", linked.getObject("address").getObject("province").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/lookups/provinces/ca/qc", linked.getObject("address").getObject("province").getString("@id"));
		assertEquals("CA/QC", linked.getObject("address").getObject("province").getString("@value"));
		assertEquals("Quebec", linked.getObject("address").getObject("province").getString("@en"));
		assertEquals("Québec", linked.getObject("address").getObject("province").getString("@fr"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("address").getObject("country").keys());
		assertEquals("http://api.magex.ca/crm/schema/lookup/Countries", linked.getObject("address").getObject("country").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/lookups/countries/ca", linked.getObject("address").getObject("country").getString("@id"));
		assertEquals("CA", linked.getObject("address").getObject("country").getString("@value"));
		assertEquals("Canada", linked.getObject("address").getObject("country").getString("@en"));
		assertEquals("Canada", linked.getObject("address").getObject("country").getString("@fr"));
		assertEquals("K1K1K1", linked.getObject("address").getString("postalCode"));
		assertEquals(location, transformer.parse(linked, null));
	}
	
	@Test
	public void testRootJson() throws Exception {
		JsonObject root = (JsonObject)transformer.format(location, Lang.ROOT);
		//JsonAsserts.print(root, "root");
		assertEquals(List.of("@context", "locationId", "organizationId", "status", "reference", "displayName", "address"), root.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/organization/LocationDetails", root.getString("@context"));
		assertEquals(BASE_URL + locationId.toString(), root.getString("locationId"));
		assertEquals(BASE_URL + organizationId.toString(), root.getString("organizationId"));
		assertEquals("http://api.magex.ca/crm/rest/lookups/statuses/active", root.getString("status"));
		assertEquals("REF", root.getString("reference"));
		assertEquals("Location Name", root.getString("displayName"));
		assertEquals(List.of("@context", "street", "city", "province", "country", "postalCode"), root.getObject("address").keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/MailingAddress", root.getObject("address").getString("@context"));
		assertEquals("123 Main St", root.getObject("address").getString("street"));
		assertEquals("Ottawa", root.getObject("address").getString("city"));
		assertEquals("http://api.magex.ca/crm/rest/lookups/provinces/ca/qc", root.getObject("address").getString("province"));
		assertEquals("http://api.magex.ca/crm/rest/lookups/countries/ca", root.getObject("address").getString("country"));
		assertEquals("K1K1K1", root.getObject("address").getString("postalCode"));
	}
	
	@Test
	public void testEnglishJson() throws Exception {
		JsonObject english = (JsonObject)transformer.format(location, Lang.ENGLISH);
		System.out.println(english);
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("@context", "locationId", "organizationId", "status", "reference", "displayName", "address"), english.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/organization/LocationDetails", english.getString("@context"));
		assertEquals(BASE_URL + locationId.toString(), english.getString("locationId"));
		assertEquals(BASE_URL + organizationId.toString(), english.getString("organizationId"));
		assertEquals("Active", english.getString("status"));
		assertEquals("REF", english.getString("reference"));
		assertEquals("Location Name", english.getString("displayName"));
		assertEquals(List.of("@context", "street", "city", "province", "country", "postalCode"), english.getObject("address").keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/MailingAddress", english.getObject("address").getString("@context"));
		assertEquals("123 Main St", english.getObject("address").getString("street"));
		assertEquals("Ottawa", english.getObject("address").getString("city"));
		assertEquals("Quebec", english.getObject("address").getString("province"));
		assertEquals("Canada", english.getObject("address").getString("country"));
		assertEquals("K1K1K1", english.getObject("address").getString("postalCode"));
	}
	
	@Test
	public void testFrenchJson() throws Exception {
		JsonObject french = (JsonObject)transformer.format(location, Lang.FRENCH);
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("@context", "locationId", "organizationId", "status", "reference", "displayName", "address"), french.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/organization/LocationDetails", french.getString("@context"));
		assertEquals(BASE_URL + locationId.toString(), french.getString("locationId"));
		assertEquals(BASE_URL + organizationId.toString(), french.getString("organizationId"));
		assertEquals("Actif", french.getString("status"));
		assertEquals("REF", french.getString("reference"));
		assertEquals("Location Name", french.getString("displayName"));
		assertEquals(List.of("@context", "street", "city", "province", "country", "postalCode"), french.getObject("address").keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/MailingAddress", french.getObject("address").getString("@context"));
		assertEquals("123 Main St", french.getObject("address").getString("street"));
		assertEquals("Ottawa", french.getObject("address").getString("city"));
		assertEquals("Québec", french.getObject("address").getString("province"));
		assertEquals("Canada", french.getObject("address").getString("country"));
		assertEquals("K1K1K1", french.getObject("address").getString("postalCode"));
	}
	
}
