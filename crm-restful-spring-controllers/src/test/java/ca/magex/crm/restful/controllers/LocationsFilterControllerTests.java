package ca.magex.crm.restful.controllers;

import static ca.magex.crm.test.CrmAsserts.CA_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.DE_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.EN_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.FR_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.MAILING_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.MX_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.NL_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.ORG_AUTH_GROUPS;
import static ca.magex.crm.test.CrmAsserts.ORG_BIZ_GROUPS;
import static ca.magex.crm.test.CrmAsserts.US_ADDRESS;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.json.model.JsonObject;

public class LocationsFilterControllerTests extends AbstractControllerTests {
	
	private OrganizationIdentifier org1;
	
	private OrganizationIdentifier org2;
	
	private LocationIdentifier locId;
	
	private LocationIdentifier caId;
	
	private LocationIdentifier nlId;
	
	private LocationIdentifier usId;
	
	private LocationIdentifier mxId;
	
	private LocationIdentifier enId;
	
	private LocationIdentifier frId;
	
	private LocationIdentifier deId;
	
	@Before
	public void setup() {
		initialize();

		org1 = crm.createOrganization("Org 1", ORG_AUTH_GROUPS, ORG_BIZ_GROUPS).getOrganizationId();
		org2 = crm.createOrganization("Org 2", ORG_AUTH_GROUPS, ORG_BIZ_GROUPS).getOrganizationId();
		
		locId = crm.createLocation(org1, "MAIN", "Main Location", MAILING_ADDRESS).getLocationId();
		caId = crm.createLocation(org1, "CANADIAN", "Canadian Location", CA_ADDRESS).getLocationId();
		nlId = crm.createLocation(org1, "NEWFOUNDLAND", "Newfoundland Location", NL_ADDRESS).getLocationId();
		usId = crm.disableLocation(crm.createLocation(org1, "AMERICAN", "American Location", US_ADDRESS).getLocationId()).getLocationId();
		mxId = crm.createLocation(org1, "MEXICAN", "Mexican Location", MX_ADDRESS).getLocationId();
		enId = crm.createLocation(org2, "BRITISH", "British Location", EN_ADDRESS).getLocationId();
		frId = crm.disableLocation(crm.createLocation(org2, "FRANCE", "France Location", FR_ADDRESS).getLocationId()).getLocationId();
		deId = crm.createLocation(org2, "GERMAN", "German Location", DE_ADDRESS).getLocationId();
	}
	
	@Test
	public void testLocationsFilterDefaultRoot() throws Exception {
		JsonObject json = get("/locations", null, HttpStatus.OK);
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("@context", "page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals("http://api.magex.ca/crm/schema/system/Page", json.getString("@context"));
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(9, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(9, json.getArray("content").size());
		assertEquals(List.of("@context", "locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(0).keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/organization/LocationSummary", json.getArray("content").getObject(0).getString("@context"));
		assertEquals(Crm.REST_BASE + usId.toString(), json.getArray("content").getObject(0).getString("locationId"));
		assertEquals(Crm.REST_BASE + org1.toString(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), json.getArray("content").getObject(0).getObject("status").keys());
		assertEquals("AMERICAN", json.getArray("content").getObject(0).getString("reference"));
		assertEquals("American Location", json.getArray("content").getObject(0).getString("displayName"));
		assertEquals(List.of("@context", "locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(1).keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/organization/LocationSummary", json.getArray("content").getObject(1).getString("@context"));
		assertEquals(Crm.REST_BASE + enId.toString(), json.getArray("content").getObject(1).getString("locationId"));
		assertEquals(Crm.REST_BASE + org2.toString(), json.getArray("content").getObject(1).getString("organizationId"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), json.getArray("content").getObject(1).getObject("status").keys());
		assertEquals("BRITISH", json.getArray("content").getObject(1).getString("reference"));
		assertEquals("British Location", json.getArray("content").getObject(1).getString("displayName"));
		assertEquals(List.of("@context", "locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(2).keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/organization/LocationSummary", json.getArray("content").getObject(2).getString("@context"));
		assertEquals(Crm.REST_BASE + caId.toString(), json.getArray("content").getObject(2).getString("locationId"));
		assertEquals(Crm.REST_BASE + org1.toString(), json.getArray("content").getObject(2).getString("organizationId"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), json.getArray("content").getObject(2).getObject("status").keys());
		assertEquals("CANADIAN", json.getArray("content").getObject(2).getString("reference"));
		assertEquals("Canadian Location", json.getArray("content").getObject(2).getString("displayName"));
	}
	
	@Test
	public void testLocationsFilterDefaultEnglish() throws Exception {
		JsonObject json = get("/locations", Lang.ENGLISH, HttpStatus.OK);
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(9, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(9, json.getArray("content").size());
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(0).keys());
		assertEquals(usId.getCode(), json.getArray("content").getObject(0).getString("locationId"));
		assertEquals(org1.getCode(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals("Inactive", json.getArray("content").getObject(0).getString("status"));
		assertEquals("AMERICAN", json.getArray("content").getObject(0).getString("reference"));
		assertEquals("American Location", json.getArray("content").getObject(0).getString("displayName"));
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(1).keys());
		assertEquals(enId.getCode(), json.getArray("content").getObject(1).getString("locationId"));
		assertEquals(org2.getCode(), json.getArray("content").getObject(1).getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(1).getString("status"));
		assertEquals("BRITISH", json.getArray("content").getObject(1).getString("reference"));
		assertEquals("British Location", json.getArray("content").getObject(1).getString("displayName"));
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(2).keys());
		assertEquals(caId.getCode(), json.getArray("content").getObject(2).getString("locationId"));
		assertEquals(org1.getCode(), json.getArray("content").getObject(2).getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(2).getString("status"));
		assertEquals("CANADIAN", json.getArray("content").getObject(2).getString("reference"));
		assertEquals("Canadian Location", json.getArray("content").getObject(2).getString("displayName"));
	}
	
	@Test
	public void testLocationsFilterDefaultFrench() throws Exception {
		JsonObject json = get("/locations", Lang.FRENCH, HttpStatus.OK);
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(9, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(9, json.getArray("content").size());
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(0).keys());
		assertEquals(usId.getCode(), json.getArray("content").getObject(0).getString("locationId"));
		assertEquals(org1.getCode(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals("Inactif", json.getArray("content").getObject(0).getString("status"));
		assertEquals("AMERICAN", json.getArray("content").getObject(0).getString("reference"));
		assertEquals("American Location", json.getArray("content").getObject(0).getString("displayName"));
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(1).keys());
		assertEquals(enId.getCode(), json.getArray("content").getObject(1).getString("locationId"));
		assertEquals(org2.getCode(), json.getArray("content").getObject(1).getString("organizationId"));
		assertEquals("Actif", json.getArray("content").getObject(1).getString("status"));
		assertEquals("BRITISH", json.getArray("content").getObject(1).getString("reference"));
		assertEquals("British Location", json.getArray("content").getObject(1).getString("displayName"));
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(2).keys());
		assertEquals(caId.getCode(), json.getArray("content").getObject(2).getString("locationId"));
		assertEquals(org1.getCode(), json.getArray("content").getObject(2).getString("organizationId"));
		assertEquals("Actif", json.getArray("content").getObject(2).getString("status"));
		assertEquals("CANADIAN", json.getArray("content").getObject(2).getString("reference"));
		assertEquals("Canadian Location", json.getArray("content").getObject(2).getString("displayName"));
	}

	@Test
	public void testFilterByDisplayNameCaseAccentInsensitive() throws Exception {
		JsonObject json = get("/locations", Lang.ENGLISH, HttpStatus.OK, new JsonObject().with("displayName", "CAN"));
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(3, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(3, json.getArray("content").size());
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(0).keys());
		assertEquals(usId.getCode(), json.getArray("content").getObject(0).getString("locationId"));
		assertEquals(org1.getCode(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals("Inactive", json.getArray("content").getObject(0).getString("status"));
		assertEquals("AMERICAN", json.getArray("content").getObject(0).getString("reference"));
		assertEquals("American Location", json.getArray("content").getObject(0).getString("displayName"));
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(1).keys());
		assertEquals(caId.getCode(), json.getArray("content").getObject(1).getString("locationId"));
		assertEquals(org1.getCode(), json.getArray("content").getObject(1).getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(1).getString("status"));
		assertEquals("CANADIAN", json.getArray("content").getObject(1).getString("reference"));
		assertEquals("Canadian Location", json.getArray("content").getObject(1).getString("displayName"));
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(2).keys());
		assertEquals(mxId.getCode(), json.getArray("content").getObject(2).getString("locationId"));
		assertEquals(org1.getCode(), json.getArray("content").getObject(2).getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(2).getString("status"));
		assertEquals("MEXICAN", json.getArray("content").getObject(2).getString("reference"));
		assertEquals("Mexican Location", json.getArray("content").getObject(2).getString("displayName"));
	}
	
	@Test
	public void testFilterByInactifDesc() throws Exception {
		JsonObject json = get("/locations", Lang.FRENCH, HttpStatus.OK, new JsonObject()
			.with("status", "Inactif")
			.with("order", "displayName")
			.with("direction", "desc"));
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(2, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(2, json.getArray("content").size());
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(0).keys());
		assertEquals(frId.getCode(), json.getArray("content").getObject(0).getString("locationId"));
		assertEquals(org2.getCode(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals("Inactif", json.getArray("content").getObject(0).getString("status"));
		assertEquals("FRANCE", json.getArray("content").getObject(0).getString("reference"));
		assertEquals("France Location", json.getArray("content").getObject(0).getString("displayName"));
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(1).keys());
		assertEquals(usId.getCode(), json.getArray("content").getObject(1).getString("locationId"));
		assertEquals(org1.getCode(), json.getArray("content").getObject(1).getString("organizationId"));
		assertEquals("Inactif", json.getArray("content").getObject(1).getString("status"));
		assertEquals("AMERICAN", json.getArray("content").getObject(1).getString("reference"));
		assertEquals("American Location", json.getArray("content").getObject(1).getString("displayName"));
	}
	
	@Test
	public void testFilterByActiveDesc() throws Exception {
		JsonObject json = get("/locations", Lang.ROOT, HttpStatus.OK, new JsonObject()
			.with("organization", org1.toString())
			.with("status", "ACTIVE")
			.with("order", "displayName")
			.with("direction", "desc"));
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(4, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(4, json.getArray("content").size());
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(0).keys());
		assertEquals(nlId.getCode(), json.getArray("content").getObject(0).getString("locationId"));
		assertEquals(org1.getCode(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals("ACTIVE", json.getArray("content").getObject(0).getString("status"));
		assertEquals("NEWFOUNDLAND", json.getArray("content").getObject(0).getString("reference"));
		assertEquals("Newfoundland Location", json.getArray("content").getObject(0).getString("displayName"));
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(1).keys());
		assertEquals(mxId.getCode(), json.getArray("content").getObject(1).getString("locationId"));
		assertEquals(org1.getCode(), json.getArray("content").getObject(1).getString("organizationId"));
		assertEquals("ACTIVE", json.getArray("content").getObject(1).getString("status"));
		assertEquals("MEXICAN", json.getArray("content").getObject(1).getString("reference"));
		assertEquals("Mexican Location", json.getArray("content").getObject(1).getString("displayName"));
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(2).keys());
		assertEquals(locId.getCode(), json.getArray("content").getObject(2).getString("locationId"));
		assertEquals(org1.getCode(), json.getArray("content").getObject(2).getString("organizationId"));
		assertEquals("ACTIVE", json.getArray("content").getObject(2).getString("status"));
		assertEquals("MAIN", json.getArray("content").getObject(2).getString("reference"));
		assertEquals("Main Location", json.getArray("content").getObject(2).getString("displayName"));
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(3).keys());
		assertEquals(caId.getCode(), json.getArray("content").getObject(3).getString("locationId"));
		assertEquals(org1.getCode(), json.getArray("content").getObject(3).getString("organizationId"));
		assertEquals("ACTIVE", json.getArray("content").getObject(3).getString("status"));
		assertEquals("CANADIAN", json.getArray("content").getObject(3).getString("reference"));
		assertEquals("Canadian Location", json.getArray("content").getObject(3).getString("displayName"));
	}
	
	@Test
	public void testFilterByLocations() throws Exception {
		JsonObject json = get("/locations", Lang.ROOT, HttpStatus.OK, new JsonObject().with("organization", org2.toString()));
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(3, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(3, json.getArray("content").size());
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(0).keys());
		assertEquals(enId.getCode(), json.getArray("content").getObject(0).getString("locationId"));
		assertEquals(org2.getCode(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals("ACTIVE", json.getArray("content").getObject(0).getString("status"));
		assertEquals("BRITISH", json.getArray("content").getObject(0).getString("reference"));
		assertEquals("British Location", json.getArray("content").getObject(0).getString("displayName"));
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(1).keys());
		assertEquals(frId.getCode(), json.getArray("content").getObject(1).getString("locationId"));
		assertEquals(org2.getCode(), json.getArray("content").getObject(1).getString("organizationId"));
		assertEquals("INACTIVE", json.getArray("content").getObject(1).getString("status"));
		assertEquals("FRANCE", json.getArray("content").getObject(1).getString("reference"));
		assertEquals("France Location", json.getArray("content").getObject(1).getString("displayName"));
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(2).keys());
		assertEquals(deId.getCode(), json.getArray("content").getObject(2).getString("locationId"));
		assertEquals(org2.getCode(), json.getArray("content").getObject(2).getString("organizationId"));
		assertEquals("ACTIVE", json.getArray("content").getObject(2).getString("status"));
		assertEquals("GERMAN", json.getArray("content").getObject(2).getString("reference"));
		assertEquals("German Location", json.getArray("content").getObject(2).getString("displayName"));
	}

}
