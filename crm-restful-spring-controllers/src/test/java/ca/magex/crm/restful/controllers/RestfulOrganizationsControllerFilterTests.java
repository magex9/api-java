package ca.magex.crm.restful.controllers;

import static ca.magex.crm.test.CrmAsserts.ORG_AUTH_GROUPS;
import static ca.magex.crm.test.CrmAsserts.ORG_BIZ_GROUPS;
import static ca.magex.crm.test.CrmAsserts.SYS_AUTH_GROUPS;
import static ca.magex.crm.test.CrmAsserts.SYS_BIZ_GROUPS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.AuthenticationGroupIdentifier;
import ca.magex.crm.api.system.id.BusinessGroupIdentifier;
import ca.magex.json.model.JsonObject;

public class RestfulOrganizationsControllerFilterTests extends AbstractControllerTests {
	
	private Identifier org0;
	
	private Identifier org1;
	
	private Identifier org2;
	
	private Identifier org3;
	
	@Before
	public void setup() {
		initialize();
		org0 = getSystemOrganizationIdentifier();
		org1 = crm.createOrganization("A new org 1", SYS_AUTH_GROUPS, SYS_BIZ_GROUPS).getOrganizationId();
		org2 = crm.createOrganization("A néw org 2", ORG_AUTH_GROUPS, ORG_BIZ_GROUPS).getOrganizationId();
		org3 = crm.disableOrganization(crm.createOrganization("An inactive org 3", ORG_AUTH_GROUPS, ORG_BIZ_GROUPS).getOrganizationId()).getOrganizationId();
	}
	
	@Test
	public void testOrganizationFilterDefaultRoot() throws Exception {
		JsonObject json = get("/organizations", null, HttpStatus.OK);
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("@context", "page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals("http://api.magex.ca/crm/schema/system/Page", json.getString("@context"));
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(4, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(4, json.getArray("content").size());
		assertEquals(List.of("@context", "organizationId", "status", "displayName", "lastModified"), json.getArray("content").getObject(0).keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/organization/OrganizationSummary", json.getArray("content").getObject(0).getString("@context"));
		assertEquals(Crm.REST_BASE + org1.toString(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), json.getArray("content").getObject(0).getObject("status").keys());
		assertEquals("A new org 1", json.getArray("content").getObject(0).getString("displayName"));
		assertEquals(List.of("@context", "organizationId", "status", "displayName", "lastModified"), json.getArray("content").getObject(1).keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/organization/OrganizationSummary", json.getArray("content").getObject(1).getString("@context"));
		assertEquals(Crm.REST_BASE + org2.toString(), json.getArray("content").getObject(1).getString("organizationId"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), json.getArray("content").getObject(1).getObject("status").keys());
		assertEquals("A néw org 2", json.getArray("content").getObject(1).getString("displayName"));
		assertEquals(List.of("@context", "organizationId", "status", "displayName", "lastModified"), json.getArray("content").getObject(2).keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/organization/OrganizationSummary", json.getArray("content").getObject(2).getString("@context"));
		assertEquals(Crm.REST_BASE + org3.toString(), json.getArray("content").getObject(2).getString("organizationId"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), json.getArray("content").getObject(2).getObject("status").keys());
		assertEquals("An inactive org 3", json.getArray("content").getObject(2).getString("displayName"));
		assertEquals(List.of("@context", "organizationId", "status", "displayName", "lastModified"), json.getArray("content").getObject(3).keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/organization/OrganizationSummary", json.getArray("content").getObject(3).getString("@context"));
		assertEquals(Crm.REST_BASE + org0.toString(), json.getArray("content").getObject(3).getString("organizationId"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), json.getArray("content").getObject(3).getObject("status").keys());
		assertEquals("System", json.getArray("content").getObject(3).getString("displayName"));	}
	
	@Test
	public void testOrganizationFilterDefaultEnglish() throws Exception {
		JsonObject json = get("/organizations", Lang.ENGLISH, HttpStatus.OK);
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(4, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(4, json.getArray("content").size());
		assertEquals(List.of("organizationId", "status", "displayName", "lastModified"), json.getArray("content").getObject(0).keys());
		assertEquals(org1.getCode(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(0).getString("status"));
		assertEquals("A new org 1", json.getArray("content").getObject(0).getString("displayName"));
		assertEquals(List.of("organizationId", "status", "displayName", "lastModified"), json.getArray("content").getObject(1).keys());
		assertEquals(org2.getCode(), json.getArray("content").getObject(1).getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(1).getString("status"));
		assertEquals("A néw org 2", json.getArray("content").getObject(1).getString("displayName"));
		assertEquals(List.of("organizationId", "status", "displayName", "lastModified"), json.getArray("content").getObject(2).keys());
		assertEquals(org3.getCode(), json.getArray("content").getObject(2).getString("organizationId"));
		assertEquals("Inactive", json.getArray("content").getObject(2).getString("status"));
		assertEquals("An inactive org 3", json.getArray("content").getObject(2).getString("displayName"));
		assertEquals(List.of("organizationId", "status", "displayName", "lastModified"), json.getArray("content").getObject(3).keys());
		assertEquals(org0.getCode(), json.getArray("content").getObject(3).getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(3).getString("status"));
		assertEquals("System", json.getArray("content").getObject(3).getString("displayName"));
	}
	
	@Test
	public void testOrganizationFilterDefaultFrench() throws Exception {
		JsonObject json = get("/organizations", Lang.FRENCH, HttpStatus.OK);
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(4, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(4, json.getArray("content").size());
		assertEquals(List.of("organizationId", "status", "displayName", "lastModified"), json.getArray("content").getObject(0).keys());
		assertEquals(org1.getCode(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals("Actif", json.getArray("content").getObject(0).getString("status"));
		assertEquals("A new org 1", json.getArray("content").getObject(0).getString("displayName"));
		assertEquals(List.of("organizationId", "status", "displayName", "lastModified"), json.getArray("content").getObject(1).keys());
		assertEquals(org2.getCode(), json.getArray("content").getObject(1).getString("organizationId"));
		assertEquals("Actif", json.getArray("content").getObject(1).getString("status"));
		assertEquals("A néw org 2", json.getArray("content").getObject(1).getString("displayName"));
		assertEquals(List.of("organizationId", "status", "displayName", "lastModified"), json.getArray("content").getObject(2).keys());
		assertEquals(org3.getCode(), json.getArray("content").getObject(2).getString("organizationId"));
		assertEquals("Inactif", json.getArray("content").getObject(2).getString("status"));
		assertEquals("An inactive org 3", json.getArray("content").getObject(2).getString("displayName"));
		assertEquals(List.of("organizationId", "status", "displayName", "lastModified"), json.getArray("content").getObject(3).keys());
		assertEquals(org0.getCode(), json.getArray("content").getObject(3).getString("organizationId"));
		assertEquals("Actif", json.getArray("content").getObject(3).getString("status"));
		assertEquals("System", json.getArray("content").getObject(3).getString("displayName"));
	}
	
	@Test
	public void testAssertCaseInsenstivieAndAccents() throws Exception {
		OrganizationsFilter filter = crm.defaultOrganizationsFilter();
		assertTrue(filter.containsIgnoreCaseAndAccent("new", "new"));
		assertFalse(filter.containsIgnoreCaseAndAccent("new", "old"));
		assertTrue(filter.containsIgnoreCaseAndAccent("new", "NEW"));
		assertTrue(filter.containsIgnoreCaseAndAccent("new", "néw"));
		assertTrue(filter.containsIgnoreCaseAndAccent("new", "NÉW"));
		assertTrue(filter.containsIgnoreCaseAndAccent("a new string", "nêw"));
		assertFalse(filter.containsIgnoreCaseAndAccent("new", "NÓW"));
		
		String accents	= "çÇáéíóúýÁÉÍÓÚÝàèìòùÀÈÌÒÙãõñäëïöüÿÄËÏÖÜÃÕÑâêîôûÂÊÎÔÛ";
		String normalized = "cCaeiouyAEIOUYaeiouAEIOUaonaeiouyAEIOUAONaeiouAEIOU";
		assertTrue(filter.containsIgnoreCaseAndAccent(accents, normalized));
		assertTrue(filter.containsIgnoreCaseAndAccent(normalized, accents));
		assertEquals(filter.normalize(accents), filter.normalize(normalized));
	}
	
	@Test
	public void testFilterByDisplayNameCaseAccentInsensitive() throws Exception {
		JsonObject json = get("/organizations", Lang.ENGLISH, HttpStatus.OK, new JsonObject().with("displayName", "NEW"));
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(2, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(2, json.getArray("content").size());
		assertEquals(List.of("organizationId", "status", "displayName", "lastModified"), json.getArray("content").getObject(0).keys());
		assertEquals(org1.getCode(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(0).getString("status"));
		assertEquals("A new org 1", json.getArray("content").getObject(0).getString("displayName"));
		assertEquals(List.of("organizationId", "status", "displayName", "lastModified"), json.getArray("content").getObject(1).keys());
		assertEquals(org2.getCode(), json.getArray("content").getObject(1).getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(1).getString("status"));
		assertEquals("A néw org 2", json.getArray("content").getObject(1).getString("displayName"));
	}
	
	@Test
	public void testFilterByActifDesc() throws Exception {
		JsonObject json = get("/organizations", Lang.FRENCH, HttpStatus.OK, new JsonObject()
			.with("status", "Actif")
			.with("order", "displayName")
			.with("direction", "desc"));
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(3, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(3, json.getArray("content").size());
		assertEquals(List.of("organizationId", "status", "displayName", "lastModified"), json.getArray("content").getObject(0).keys());
		assertEquals(org0.getCode(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals("Actif", json.getArray("content").getObject(0).getString("status"));
		assertEquals("System", json.getArray("content").getObject(0).getString("displayName"));
		assertEquals(List.of("organizationId", "status", "displayName", "lastModified"), json.getArray("content").getObject(1).keys());
		assertEquals(org2.getCode(), json.getArray("content").getObject(1).getString("organizationId"));
		assertEquals("Actif", json.getArray("content").getObject(1).getString("status"));
		assertEquals("A néw org 2", json.getArray("content").getObject(1).getString("displayName"));
		assertEquals(List.of("organizationId", "status", "displayName", "lastModified"), json.getArray("content").getObject(2).keys());
		assertEquals(org1.getCode(), json.getArray("content").getObject(2).getString("organizationId"));
		assertEquals("Actif", json.getArray("content").getObject(2).getString("status"));
		assertEquals("A new org 1", json.getArray("content").getObject(2).getString("displayName"));
	}

	@Test
	public void testFilterByInactif() throws Exception {
		JsonObject json = get("/organizations", Lang.FRENCH, HttpStatus.OK, new JsonObject().with("status", "Inactif"));
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(1, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(1, json.getArray("content").size());
		assertEquals(List.of("organizationId", "status", "displayName", "lastModified"), json.getArray("content").getObject(0).keys());
		assertEquals(org3.getCode(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals("Inactif", json.getArray("content").getObject(0).getString("status"));
		assertEquals("An inactive org 3", json.getArray("content").getObject(0).getString("displayName"));
	}
	
	@Test
	public void testFilterByAuthenticationGroup() throws Exception {
		Option option = crm.findOptionByCode(Type.AUTHENTICATION_GROUP, AuthenticationGroupIdentifier.SYS.getCode());
		JsonObject json = get("/organizations", Lang.ENGLISH, HttpStatus.OK, new JsonObject().with("authenticationGroupId", option.getName(Lang.ENGLISH)));
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(2, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(2, json.getArray("content").size());
		assertEquals(List.of("organizationId", "status", "displayName", "lastModified"), json.getArray("content").getObject(0).keys());
		assertEquals(org1.getCode(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(0).getString("status"));
		assertEquals("A new org 1", json.getArray("content").getObject(0).getString("displayName"));
		assertEquals(List.of("organizationId", "status", "displayName", "lastModified"), json.getArray("content").getObject(1).keys());
		assertEquals(getSystemOrganizationIdentifier().getCode(), json.getArray("content").getObject(1).getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(1).getString("status"));
		assertEquals("System", json.getArray("content").getObject(1).getString("displayName"));
	}
	
	@Test
	public void testFilterByBusinessGroup() throws Exception {
		Option option = crm.findOptionByCode(Type.BUSINESS_GROUP, BusinessGroupIdentifier.EXTERNAL.getCode());
		JsonObject json = get("/organizations", Lang.FRENCH, HttpStatus.OK, new JsonObject().with("businessGroupId", option.getName(Lang.FRENCH)));
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(2, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(2, json.getArray("content").size());
		assertEquals(List.of("organizationId", "status", "displayName", "lastModified"), json.getArray("content").getObject(0).keys());
		assertEquals(org2.getCode(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals("Actif", json.getArray("content").getObject(0).getString("status"));
		assertEquals("A néw org 2", json.getArray("content").getObject(0).getString("displayName"));
		assertEquals(List.of("organizationId", "status", "displayName", "lastModified"), json.getArray("content").getObject(1).keys());
		assertEquals(org3.getCode(), json.getArray("content").getObject(1).getString("organizationId"));
		assertEquals("Inactif", json.getArray("content").getObject(1).getString("status"));
		assertEquals("An inactive org 3", json.getArray("content").getObject(1).getString("displayName"));
	}
	
}
