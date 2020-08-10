package ca.magex.crm.restful.controllers;

import static ca.magex.crm.test.CrmAsserts.MAILING_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.MX_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.NL_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.ORG_NAME;
import static ca.magex.crm.test.CrmAsserts.US_ADDRESS;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.transform.json.MailingAddressJsonTransformer;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonObject;

public class LocationsControllerTests extends AbstractControllerTests {

	private OrganizationIdentifier organizationId;
	
	@Before
	public void setup() {
		initialize();
		organizationId = createTestOrganization("Test Org");
	}
	
	@Test
	public void testCreateLocation() throws Exception {
		// Get the initial list of groups to make sure they are blank
		JsonObject orig = get("/locations", Lang.ENGLISH, HttpStatus.OK);
		//JsonAsserts.print(orig, "orig");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), orig.keys());
		assertEquals(1, orig.getNumber("page"));
		assertEquals(10, orig.getNumber("limit"));
		assertEquals(1, orig.getNumber("total"));
		assertEquals(false, orig.getBoolean("hasNext"));
		assertEquals(false, orig.getBoolean("hasPrevious"));
		assertEquals(1, orig.getArray("content").size());
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName"), orig.getArray("content").getObject(0).keys());
		assertEquals(getSystemLocationIdentifier().getCode(), orig.getArray("content").getObject(0).getString("locationId"));
		assertEquals(getSystemOrganizationIdentifier().getCode(), orig.getArray("content").getObject(0).getString("organizationId"));
		assertEquals("Active", orig.getArray("content").getObject(0).getString("status"));
		assertEquals("SYSTEM", orig.getArray("content").getObject(0).getString("reference"));
		assertEquals("System Administrator", orig.getArray("content").getObject(0).getString("displayName"));

		JsonObject created = post("/locations", Lang.ENGLISH, HttpStatus.OK, new JsonObject()
				.with("organizationId", organizationId.toString())
				.with("displayName", ORG_NAME.getEnglishName())
				.with("reference", "LOC")
				.with("address", new MailingAddressJsonTransformer(crm).format(MAILING_ADDRESS, Lang.ENGLISH)));
		//JsonAsserts.print(created, "created");
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName", "address"), created.keys());
		assertEquals(organizationId.getCode(), created.getString("organizationId"));
		assertEquals("Active", created.getString("status"));
		assertEquals("LOC", created.getString("reference"));
		assertEquals("Organization", created.getString("displayName"));
		assertEquals(List.of("street", "city", "province", "country", "postalCode"), created.getObject("address").keys());
		assertEquals("123 Main St", created.getObject("address").getString("street"));
		assertEquals("Ottawa", created.getObject("address").getString("city"));
		assertEquals("Quebec", created.getObject("address").getString("province"));
		assertEquals("Canada", created.getObject("address").getString("country"));
		assertEquals("K1K1K1", created.getObject("address").getString("postalCode"));
		LocationIdentifier locationId =  new LocationIdentifier(created.getString("locationId"));
		
		JsonObject fetch = get(locationId + "/details", Lang.ROOT, HttpStatus.OK);
		//JsonAsserts.print(fetch, "fetch");
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName", "address"), fetch.keys());
		assertEquals(locationId.getCode(), fetch.getString("locationId"));
		assertEquals(organizationId.getCode(), fetch.getString("organizationId"));
		assertEquals("ACTIVE", fetch.getString("status"));
		assertEquals("LOC", fetch.getString("reference"));
		assertEquals("Organization", fetch.getString("displayName"));
		assertEquals(List.of("street", "city", "province", "country", "postalCode"), fetch.getObject("address").keys());
		assertEquals("123 Main St", fetch.getObject("address").getString("street"));
		assertEquals("Ottawa", fetch.getObject("address").getString("city"));
		assertEquals("CA/QC", fetch.getObject("address").getString("province"));
		assertEquals("CA", fetch.getObject("address").getString("country"));
		assertEquals("K1K1K1", fetch.getObject("address").getString("postalCode"));
		
		JsonObject english = get(locationId + "/details", Lang.ENGLISH, HttpStatus.OK);
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName", "address"), english.keys());
		assertEquals(locationId.getCode(), english.getString("locationId"));
		assertEquals(organizationId.getCode(), english.getString("organizationId"));
		assertEquals("Active", english.getString("status"));
		assertEquals("LOC", english.getString("reference"));
		assertEquals("Organization", english.getString("displayName"));
		assertEquals(List.of("street", "city", "province", "country", "postalCode"), english.getObject("address").keys());
		assertEquals("123 Main St", english.getObject("address").getString("street"));
		assertEquals("Ottawa", english.getObject("address").getString("city"));
		assertEquals("Quebec", english.getObject("address").getString("province"));
		assertEquals("Canada", english.getObject("address").getString("country"));
		assertEquals("K1K1K1", english.getObject("address").getString("postalCode"));

		JsonObject french = get(locationId + "/details", Lang.FRENCH, HttpStatus.OK);
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName", "address"), french.keys());
		assertEquals(locationId.getCode(), french.getString("locationId"));
		assertEquals(organizationId.getCode(), french.getString("organizationId"));
		assertEquals("Actif", french.getString("status"));
		assertEquals("LOC", french.getString("reference"));
		assertEquals("Organization", french.getString("displayName"));
		assertEquals(List.of("street", "city", "province", "country", "postalCode"), french.getObject("address").keys());
		assertEquals("123 Main St", french.getObject("address").getString("street"));
		assertEquals("Ottawa", french.getObject("address").getString("city"));
		assertEquals("Québec", french.getObject("address").getString("province"));
		assertEquals("Canada", french.getObject("address").getString("country"));
		assertEquals("K1K1K1", french.getObject("address").getString("postalCode"));
		
		JsonObject jsonld = get(locationId + "/details", null, HttpStatus.OK);
		//JsonAsserts.print(jsonld, "jsonld");
		assertEquals(List.of("@context", "locationId", "organizationId", "status", "reference", "displayName", "address"), jsonld.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/organization/LocationDetails", jsonld.getString("@context"));
		assertEquals(Crm.REST_BASE + locationId.toString(), jsonld.getString("locationId"));
		assertEquals(Crm.REST_BASE + organizationId.toString(), jsonld.getString("organizationId"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), jsonld.getObject("status").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Statuses", jsonld.getObject("status").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/statuses/active", jsonld.getObject("status").getString("@id"));
		assertEquals("ACTIVE", jsonld.getObject("status").getString("@value"));
		assertEquals("Active", jsonld.getObject("status").getString("@en"));
		assertEquals("Actif", jsonld.getObject("status").getString("@fr"));
		assertEquals("LOC", jsonld.getString("reference"));
		assertEquals("Organization", jsonld.getString("displayName"));
		assertEquals(List.of("@context", "street", "city", "province", "country", "postalCode"), jsonld.getObject("address").keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/MailingAddress", jsonld.getObject("address").getString("@context"));
		assertEquals("123 Main St", jsonld.getObject("address").getString("street"));
		assertEquals("Ottawa", jsonld.getObject("address").getString("city"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), jsonld.getObject("address").getObject("province").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Provinces", jsonld.getObject("address").getObject("province").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/provinces/ca/qc", jsonld.getObject("address").getObject("province").getString("@id"));
		assertEquals("CA/QC", jsonld.getObject("address").getObject("province").getString("@value"));
		assertEquals("Quebec", jsonld.getObject("address").getObject("province").getString("@en"));
		assertEquals("Québec", jsonld.getObject("address").getObject("province").getString("@fr"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), jsonld.getObject("address").getObject("country").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Countries", jsonld.getObject("address").getObject("country").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/countries/ca", jsonld.getObject("address").getObject("country").getString("@id"));
		assertEquals("CA", jsonld.getObject("address").getObject("country").getString("@value"));
		assertEquals("Canada", jsonld.getObject("address").getObject("country").getString("@en"));
		assertEquals("Canada", jsonld.getObject("address").getObject("country").getString("@fr"));
		assertEquals("K1K1K1", jsonld.getObject("address").getString("postalCode"));
		
		JsonObject paging = get("/locations" + "/details", Lang.ENGLISH, HttpStatus.OK);
		//JsonAsserts.print(paging, "paging");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), paging.keys());
		assertEquals(1, paging.getNumber("page"));
		assertEquals(10, paging.getNumber("limit"));
		assertEquals(2, paging.getNumber("total"));
		assertEquals(false, paging.getBoolean("hasNext"));
		assertEquals(false, paging.getBoolean("hasPrevious"));
		assertEquals(2, paging.getArray("content").size());
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName"), paging.getArray("content").getObject(0).keys());
		assertEquals(locationId.getCode(), paging.getArray("content").getObject(0).getString("locationId"));
		assertEquals(organizationId.getCode(), paging.getArray("content").getObject(0).getString("organizationId"));
		assertEquals("Active", paging.getArray("content").getObject(0).getString("status"));
		assertEquals("LOC", paging.getArray("content").getObject(0).getString("reference"));
		assertEquals("Organization", paging.getArray("content").getObject(0).getString("displayName"));
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName"), paging.getArray("content").getObject(1).keys());
		assertEquals(getSystemLocationIdentifier().getCode(), paging.getArray("content").getObject(1).getString("locationId"));
		assertEquals(getSystemOrganizationIdentifier().getCode(), paging.getArray("content").getObject(1).getString("organizationId"));
		assertEquals("Active", paging.getArray("content").getObject(1).getString("status"));
		assertEquals("SYSTEM", paging.getArray("content").getObject(1).getString("reference"));
		assertEquals("System Administrator", paging.getArray("content").getObject(1).getString("displayName"));
	}
	
	@Test
	public void testGetLocationDetails() throws Exception {
		LocationIdentifier locationId = crm.createLocation(organizationId, "NUEVOLEON", "Nuevo Leon", MX_ADDRESS).getLocationId();
		
		JsonObject root = get(locationId + "/details", Lang.ROOT, HttpStatus.OK);
		//JsonAsserts.print(root, "root");
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName", "address"), root.keys());
		assertEquals(locationId.getCode(), root.getString("locationId"));
		assertEquals(organizationId.getCode(), root.getString("organizationId"));
		assertEquals("ACTIVE", root.getString("status"));
		assertEquals("NUEVOLEON", root.getString("reference"));
		assertEquals("Nuevo Leon", root.getString("displayName"));
		assertEquals(List.of("street", "city", "province", "country", "postalCode"), root.getObject("address").keys());
		assertEquals("120 Col. Hipodromo Condesa", root.getObject("address").getString("street"));
		assertEquals("Monterrey", root.getObject("address").getString("city"));
		assertEquals("MX/NL", root.getObject("address").getString("province"));
		assertEquals("MX", root.getObject("address").getString("country"));
		assertEquals("06100", root.getObject("address").getString("postalCode"));
		
		JsonObject english = get(locationId + "/details", Lang.ENGLISH, HttpStatus.OK);
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName", "address"), english.keys());
		assertEquals(locationId.getCode(), english.getString("locationId"));
		assertEquals(organizationId.getCode(), english.getString("organizationId"));
		assertEquals("Active", english.getString("status"));
		assertEquals("NUEVOLEON", english.getString("reference"));
		assertEquals("Nuevo Leon", english.getString("displayName"));
		assertEquals(List.of("street", "city", "province", "country", "postalCode"), english.getObject("address").keys());
		assertEquals("120 Col. Hipodromo Condesa", english.getObject("address").getString("street"));
		assertEquals("Monterrey", english.getObject("address").getString("city"));
		assertEquals("Nuevo Leon", english.getObject("address").getString("province"));
		assertEquals("Mexico", english.getObject("address").getString("country"));
		assertEquals("06100", english.getObject("address").getString("postalCode"));
		
		JsonObject french = get(locationId + "/details", Lang.FRENCH, HttpStatus.OK);
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName", "address"), french.keys());
		assertEquals(locationId.getCode(), french.getString("locationId"));
		assertEquals(organizationId.getCode(), french.getString("organizationId"));
		assertEquals("Actif", french.getString("status"));
		assertEquals("NUEVOLEON", french.getString("reference"));
		assertEquals("Nuevo Leon", french.getString("displayName"));
		assertEquals(List.of("street", "city", "province", "country", "postalCode"), french.getObject("address").keys());
		assertEquals("120 Col. Hipodromo Condesa", french.getObject("address").getString("street"));
		assertEquals("Monterrey", french.getObject("address").getString("city"));
		assertEquals("Nuevo Leon", french.getObject("address").getString("province"));
		assertEquals("Mexique", french.getObject("address").getString("country"));
		assertEquals("06100", french.getObject("address").getString("postalCode"));
	}
	
	@Test
	public void testGetLocationSummary() throws Exception {
		LocationIdentifier locationId = crm.createLocation(organizationId, "MAIN", "Main Location", MAILING_ADDRESS).getLocationId();

		JsonObject linked = get(locationId, null, HttpStatus.OK);
		//JsonAsserts.print(linked, "linked");
		assertEquals(List.of("@context", "locationId", "organizationId", "status", "reference", "displayName"), linked.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/organization/LocationSummary", linked.getString("@context"));
		assertEquals(Crm.REST_BASE + locationId.toString(), linked.getString("locationId"));
		assertEquals(Crm.REST_BASE + organizationId.toString(), linked.getString("organizationId"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("status").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Statuses", linked.getObject("status").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/statuses/active", linked.getObject("status").getString("@id"));
		assertEquals("ACTIVE", linked.getObject("status").getString("@value"));
		assertEquals("Active", linked.getObject("status").getString("@en"));
		assertEquals("Actif", linked.getObject("status").getString("@fr"));
		assertEquals("MAIN", linked.getString("reference"));
		assertEquals("Main Location", linked.getString("displayName"));

		JsonObject root = get(locationId, Lang.ROOT, HttpStatus.OK);
		//JsonAsserts.print(root, "root");
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName"), root.keys());
		assertEquals(locationId.getCode(), root.getString("locationId"));
		assertEquals(organizationId.getCode(), root.getString("organizationId"));
		assertEquals("ACTIVE", root.getString("status"));
		assertEquals("MAIN", root.getString("reference"));
		assertEquals("Main Location", root.getString("displayName"));

		JsonObject english = get(locationId, Lang.ENGLISH, HttpStatus.OK);
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName"), english.keys());
		assertEquals(locationId.getCode(), english.getString("locationId"));
		assertEquals(organizationId.getCode(), english.getString("organizationId"));
		assertEquals("Active", english.getString("status"));
		assertEquals("MAIN", english.getString("reference"));
		assertEquals("Main Location", english.getString("displayName"));

		JsonObject french = get(locationId, Lang.FRENCH, HttpStatus.OK);
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName"), french.keys());
		assertEquals(locationId.getCode(), french.getString("locationId"));
		assertEquals(organizationId.getCode(), french.getString("organizationId"));
		assertEquals("Actif", french.getString("status"));
		assertEquals("MAIN", french.getString("reference"));
		assertEquals("Main Location", french.getString("displayName"));
	}
	
	@Test
	public void testGetLocationAddress() throws Exception {
		LocationIdentifier locationId = crm.createLocation(organizationId, "NEWFOUNDLAND", "Labrador City", NL_ADDRESS).getLocationId();

		JsonObject linked = get(locationId + "/address", null, HttpStatus.OK);
		//JsonAsserts.print(linked, "linked");
		assertEquals(List.of("@context", "street", "city", "province", "country", "postalCode"), linked.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/MailingAddress", linked.getString("@context"));
		assertEquals("90 Avalon Drive", linked.getString("street"));
		assertEquals("Labrador City", linked.getString("city"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("province").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Provinces", linked.getObject("province").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/provinces/ca/nl", linked.getObject("province").getString("@id"));
		assertEquals("CA/NL", linked.getObject("province").getString("@value"));
		assertEquals("Newfoundland and Labrador", linked.getObject("province").getString("@en"));
		assertEquals("Terre-Neuve et Labrador", linked.getObject("province").getString("@fr"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("country").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Countries", linked.getObject("country").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/countries/ca", linked.getObject("country").getString("@id"));
		assertEquals("CA", linked.getObject("country").getString("@value"));
		assertEquals("Canada", linked.getObject("country").getString("@en"));
		assertEquals("Canada", linked.getObject("country").getString("@fr"));
		assertEquals("A2V 2Y2", linked.getString("postalCode"));

		JsonObject root = get(locationId + "/address", Lang.ROOT, HttpStatus.OK);
		//JsonAsserts.print(root, "root");
		assertEquals(List.of("street", "city", "province", "country", "postalCode"), root.keys());
		assertEquals("90 Avalon Drive", root.getString("street"));
		assertEquals("Labrador City", root.getString("city"));
		assertEquals("CA/NL", root.getString("province"));
		assertEquals("CA", root.getString("country"));
		assertEquals("A2V 2Y2", root.getString("postalCode"));

		JsonObject english = get(locationId + "/address", Lang.ENGLISH, HttpStatus.OK);
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("street", "city", "province", "country", "postalCode"), english.keys());
		assertEquals("90 Avalon Drive", english.getString("street"));
		assertEquals("Labrador City", english.getString("city"));
		assertEquals("Newfoundland and Labrador", english.getString("province"));
		assertEquals("Canada", english.getString("country"));
		assertEquals("A2V 2Y2", english.getString("postalCode"));

		JsonObject french = get(locationId + "/address", Lang.FRENCH, HttpStatus.OK);
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("street", "city", "province", "country", "postalCode"), french.keys());
		assertEquals("90 Avalon Drive", french.getString("street"));
		assertEquals("Labrador City", french.getString("city"));
		assertEquals("Terre-Neuve et Labrador", french.getString("province"));
		assertEquals("Canada", french.getString("country"));
		assertEquals("A2V 2Y2", french.getString("postalCode"));
	}
	
	@Test
	public void testUpdatingDisplayName() throws Exception {
		LocationIdentifier locationId = crm.createLocation(organizationId, "MAIN", "Main Location", MAILING_ADDRESS).getLocationId();
		
		JsonObject json = patch(locationId, Lang.ENGLISH, HttpStatus.OK, new JsonObject().with("displayName", "Updated name"));
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName", "address"), json.keys());
		assertEquals(locationId.getCode(), json.getString("locationId"));
		assertEquals(organizationId.getCode(), json.getString("organizationId"));
		assertEquals("Active", json.getString("status"));
		assertEquals("MAIN", json.getString("reference"));
		assertEquals("Updated name", json.getString("displayName"));
		assertEquals(List.of("street", "city", "province", "country", "postalCode"), json.getObject("address").keys());
		assertEquals("123 Main St", json.getObject("address").getString("street"));
		assertEquals("Ottawa", json.getObject("address").getString("city"));
		assertEquals("Quebec", json.getObject("address").getString("province"));
		assertEquals("Canada", json.getObject("address").getString("country"));
		assertEquals("K1K1K1", json.getObject("address").getString("postalCode"));
	}
	
	@Test
	public void testUpdatingAddress() throws Exception {
		LocationIdentifier locationId = crm.createLocation(organizationId, "MAIN", "Main Location", MAILING_ADDRESS).getLocationId();
		
		JsonObject json = patch(locationId, Lang.ENGLISH, HttpStatus.OK, new JsonObject()
			.with("address", new MailingAddressJsonTransformer(crm).format(US_ADDRESS, Lang.ENGLISH)));

		//JsonAsserts.print(json, "json");
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName", "address"), json.keys());
		assertEquals(locationId.getCode(), json.getString("locationId"));
		assertEquals(organizationId.getCode(), json.getString("organizationId"));
		assertEquals("Active", json.getString("status"));
		assertEquals("MAIN", json.getString("reference"));
		assertEquals("Main Location", json.getString("displayName"));
		assertEquals(List.of("street", "city", "province", "country", "postalCode"), json.getObject("address").keys());
		assertEquals("465 Huntington Ave", json.getObject("address").getString("street"));
		assertEquals("Boston", json.getObject("address").getString("city"));
		assertEquals("Massachusetts", json.getObject("address").getString("province"));
		assertEquals("United States", json.getObject("address").getString("country"));
		assertEquals("02115", json.getObject("address").getString("postalCode"));
	}
	
	@Test
	public void testUpdatingAddressEndpoint() throws Exception {
		LocationIdentifier locationId = crm.createLocation(organizationId, "MAIN", "Main Location", MAILING_ADDRESS).getLocationId();
		
		JsonObject json = put(locationId + "/address", Lang.ENGLISH, HttpStatus.OK, new JsonObject()
			.with("address", new MailingAddressJsonTransformer(crm).format(US_ADDRESS, Lang.ENGLISH)));

		//JsonAsserts.print(json, "json");
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName", "address"), json.keys());
		assertEquals(locationId.getCode(), json.getString("locationId"));
		assertEquals(organizationId.getCode(), json.getString("organizationId"));
		assertEquals("Active", json.getString("status"));
		assertEquals("MAIN", json.getString("reference"));
		assertEquals("Main Location", json.getString("displayName"));
		assertEquals(List.of("street", "city", "province", "country", "postalCode"), json.getObject("address").keys());
		assertEquals("465 Huntington Ave", json.getObject("address").getString("street"));
		assertEquals("Boston", json.getObject("address").getString("city"));
		assertEquals("Massachusetts", json.getObject("address").getString("province"));
		assertEquals("United States", json.getObject("address").getString("country"));
		assertEquals("02115", json.getObject("address").getString("postalCode"));
	}

	@Test
	public void testEnableDisableLocation() throws Exception {
		LocationIdentifier locationId = crm.createLocation(organizationId, "MAIN", "Main Location", MAILING_ADDRESS).getLocationId();
		assertEquals(Status.ACTIVE, crm.findLocationSummary(locationId).getStatus());

		JsonArray error1 = put(locationId + "/disable", Lang.ENGLISH, HttpStatus.BAD_REQUEST, null);
		assertEquals(locationId.toString(), error1.getObject(0).getString("identifier"));
		assertEquals("Error", error1.getObject(0).getString("type"));
		assertEquals("confirm", error1.getObject(0).getString("path"));
		assertEquals("Field is required", error1.getObject(0).getString("reason"));
		assertEquals(Status.ACTIVE, crm.findLocationSummary(locationId).getStatus());

		JsonArray error2 = put(locationId + "/disable", Lang.ENGLISH, HttpStatus.BAD_REQUEST, new JsonObject().with("confirm", false));
		assertEquals(locationId.toString(), error2.getObject(0).getString("identifier"));
		assertEquals("Error", error2.getObject(0).getString("type"));
		assertEquals("confirm", error2.getObject(0).getString("path"));
		assertEquals("Field is required", error2.getObject(0).getString("reason"));
		assertEquals(Status.ACTIVE, crm.findLocationSummary(locationId).getStatus());

		JsonArray error3 = put(locationId + "/disable", Lang.ENGLISH, HttpStatus.BAD_REQUEST, new JsonObject().with("confirm", "Test"));
		assertEquals(locationId.toString(), error3.getObject(0).getString("identifier"));
		assertEquals("Error", error3.getObject(0).getString("type"));
		assertEquals("confirm", error3.getObject(0).getString("path"));
		assertEquals("Format is invalid", error3.getObject(0).getString("reason"));
		assertEquals(Status.ACTIVE, crm.findLocationSummary(locationId).getStatus());

		JsonObject disable = put(locationId + "/disable", Lang.ENGLISH, HttpStatus.OK, new JsonObject().with("confirm", true));
		//JsonAsserts.print(disable, "disable");
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName"), disable.keys());
		assertEquals(locationId.getCode(), disable.getString("locationId"));
		assertEquals(organizationId.getCode(), disable.getString("organizationId"));
		assertEquals("Inactive", disable.getString("status"));
		assertEquals("MAIN", disable.getString("reference"));
		assertEquals("Main Location", disable.getString("displayName"));
		
		JsonArray error4 = put(locationId + "/enable", Lang.ENGLISH, HttpStatus.BAD_REQUEST, null);
		assertEquals(locationId.toString(), error4.getObject(0).getString("identifier"));
		assertEquals("Error", error4.getObject(0).getString("type"));
		assertEquals("confirm", error4.getObject(0).getString("path"));
		assertEquals("Field is required", error4.getObject(0).getString("reason"));
		assertEquals(Status.INACTIVE, crm.findLocationSummary(locationId).getStatus());
		
		JsonArray error5 = put(locationId + "/enable", Lang.ENGLISH, HttpStatus.BAD_REQUEST, new JsonObject().with("confirm", false));
		assertEquals(locationId.toString(), error5.getObject(0).getString("identifier"));
		assertEquals("Error", error5.getObject(0).getString("type"));
		assertEquals("confirm", error5.getObject(0).getString("path"));
		assertEquals("Field is required", error5.getObject(0).getString("reason"));
		assertEquals(Status.INACTIVE, crm.findLocationSummary(locationId).getStatus());
		
		JsonArray error6 = put(locationId + "/enable", Lang.ENGLISH, HttpStatus.BAD_REQUEST, new JsonObject().with("confirm", "Test"));
		assertEquals(locationId.toString(), error6.getObject(0).getString("identifier"));
		assertEquals("Error", error6.getObject(0).getString("type"));
		assertEquals("confirm", error6.getObject(0).getString("path"));
		assertEquals("Format is invalid", error6.getObject(0).getString("reason"));
		assertEquals(Status.INACTIVE, crm.findLocationSummary(locationId).getStatus());
	
		JsonObject enable = put(locationId + "/enable", Lang.FRENCH, HttpStatus.OK, new JsonObject().with("confirm", true));
		//JsonAsserts.print(enable, "enable");
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName"), enable.keys());
		assertEquals(locationId.getCode(), enable.getString("locationId"));
		assertEquals(organizationId.getCode(), enable.getString("organizationId"));
		assertEquals("Actif", enable.getString("status"));
		assertEquals("MAIN", enable.getString("reference"));
		assertEquals("Main Location", enable.getString("displayName"));
	}
	
}
