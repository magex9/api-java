package ca.magex.crm.restful.controllers;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.test.CrmAsserts;
import ca.magex.json.model.JsonObject;

public class OrganizationFiltersControllerTests extends AbstractControllerTests {
	
	private Identifier org0;
	
	private Identifier org1;
	
	private Identifier org2;
	
	private Identifier org3;
	
	@Before
	public void setup() {
		initialize();
		org0 = crm.findOrganizationSummaries(crm.defaultOrganizationsFilter()).getSingleItem().getOrganizationId();
		org1 = crm.createOrganization("A new org 1", List.of("ORG")).getOrganizationId();
		org2 = crm.createOrganization("A néw org 2", List.of("ORG")).getOrganizationId();
		org3 = crm.disableOrganization(crm.createOrganization("An inactive org 3", List.of("ORG")).getOrganizationId()).getOrganizationId();
	}
	
	@Test
	public void testOrganizationFilterDefaultRoot() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/organizations"))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		//CrmAsserts.printLinkedDataAsserts(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(4, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(4, json.getArray("content").size());
		assertEquals(List.of("@type", "organizationId", "status", "displayName"), json.getArray("content").getObject(0).keys());
		assertEquals("OrganizationSummary", json.getArray("content").getObject(0).getString("@type"));
		assertEquals(org1.toString(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals("active", json.getArray("content").getObject(0).getString("status"));
		assertEquals("A new org 1", json.getArray("content").getObject(0).getString("displayName"));
		assertEquals(List.of("@type", "organizationId", "status", "displayName"), json.getArray("content").getObject(1).keys());
		assertEquals("OrganizationSummary", json.getArray("content").getObject(1).getString("@type"));
		assertEquals(org2.toString(), json.getArray("content").getObject(1).getString("organizationId"));
		assertEquals("active", json.getArray("content").getObject(1).getString("status"));
		assertEquals("A néw org 2", json.getArray("content").getObject(1).getString("displayName"));
		assertEquals(List.of("@type", "organizationId", "status", "displayName"), json.getArray("content").getObject(2).keys());
		assertEquals("OrganizationSummary", json.getArray("content").getObject(2).getString("@type"));
		assertEquals(org3.toString(), json.getArray("content").getObject(2).getString("organizationId"));
		assertEquals("inactive", json.getArray("content").getObject(2).getString("status"));
		assertEquals("An inactive org 3", json.getArray("content").getObject(2).getString("displayName"));
		assertEquals(List.of("@type", "organizationId", "status", "displayName"), json.getArray("content").getObject(3).keys());
		assertEquals("OrganizationSummary", json.getArray("content").getObject(3).getString("@type"));
		assertEquals(org0.toString(), json.getArray("content").getObject(3).getString("organizationId"));
		assertEquals("active", json.getArray("content").getObject(3).getString("status"));
		assertEquals("System Administrator", json.getArray("content").getObject(3).getString("displayName"));
	}
	
	@Test
	public void testOrganizationFilterDefaultEnglish() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/organizations")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		//CrmAsserts.printLinkedDataAsserts(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(4, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(4, json.getArray("content").size());
		assertEquals(List.of("@type", "organizationId", "status", "displayName"), json.getArray("content").getObject(0).keys());
		assertEquals("OrganizationSummary", json.getArray("content").getObject(0).getString("@type"));
		assertEquals(org1.toString(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(0).getString("status"));
		assertEquals("A new org 1", json.getArray("content").getObject(0).getString("displayName"));
		assertEquals(List.of("@type", "organizationId", "status", "displayName"), json.getArray("content").getObject(1).keys());
		assertEquals("OrganizationSummary", json.getArray("content").getObject(1).getString("@type"));
		assertEquals(org2.toString(), json.getArray("content").getObject(1).getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(1).getString("status"));
		assertEquals("A néw org 2", json.getArray("content").getObject(1).getString("displayName"));
		assertEquals(List.of("@type", "organizationId", "status", "displayName"), json.getArray("content").getObject(2).keys());
		assertEquals("OrganizationSummary", json.getArray("content").getObject(2).getString("@type"));
		assertEquals(org3.toString(), json.getArray("content").getObject(2).getString("organizationId"));
		assertEquals("Inactive", json.getArray("content").getObject(2).getString("status"));
		assertEquals("An inactive org 3", json.getArray("content").getObject(2).getString("displayName"));
		assertEquals(List.of("@type", "organizationId", "status", "displayName"), json.getArray("content").getObject(3).keys());
		assertEquals("OrganizationSummary", json.getArray("content").getObject(3).getString("@type"));
		assertEquals(org0.toString(), json.getArray("content").getObject(3).getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(3).getString("status"));
		assertEquals("System Administrator", json.getArray("content").getObject(3).getString("displayName"));
	}
	
	@Test
	public void testOrganizationFilterDefaultFrench() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/organizations")
			.header("Locale", Lang.FRENCH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		CrmAsserts.printLinkedDataAsserts(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(4, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(4, json.getArray("content").size());
		assertEquals(List.of("@type", "organizationId", "status", "displayName"), json.getArray("content").getObject(0).keys());
		assertEquals("OrganizationSummary", json.getArray("content").getObject(0).getString("@type"));
		assertEquals(org1.toString(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals("Actif", json.getArray("content").getObject(0).getString("status"));
		assertEquals("A new org 1", json.getArray("content").getObject(0).getString("displayName"));
		assertEquals(List.of("@type", "organizationId", "status", "displayName"), json.getArray("content").getObject(1).keys());
		assertEquals("OrganizationSummary", json.getArray("content").getObject(1).getString("@type"));
		assertEquals(org2.toString(), json.getArray("content").getObject(1).getString("organizationId"));
		assertEquals("Actif", json.getArray("content").getObject(1).getString("status"));
		assertEquals("A néw org 2", json.getArray("content").getObject(1).getString("displayName"));
		assertEquals(List.of("@type", "organizationId", "status", "displayName"), json.getArray("content").getObject(2).keys());
		assertEquals("OrganizationSummary", json.getArray("content").getObject(2).getString("@type"));
		assertEquals(org3.toString(), json.getArray("content").getObject(2).getString("organizationId"));
		assertEquals("Inactif", json.getArray("content").getObject(2).getString("status"));
		assertEquals("An inactive org 3", json.getArray("content").getObject(2).getString("displayName"));
		assertEquals(List.of("@type", "organizationId", "status", "displayName"), json.getArray("content").getObject(3).keys());
		assertEquals("OrganizationSummary", json.getArray("content").getObject(3).getString("@type"));
		assertEquals(org0.toString(), json.getArray("content").getObject(3).getString("organizationId"));
		assertEquals("Actif", json.getArray("content").getObject(3).getString("status"));
		assertEquals("System Administrator", json.getArray("content").getObject(3).getString("displayName"));
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
	}
	
	@Test
	public void testFilterByDisplayNameCaseAccentInsensitive() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/organizations")
			.queryParam("displayName", "NEW")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		//CrmAsserts.printLinkedDataAsserts(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(2, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(2, json.getArray("content").size());
		assertEquals(List.of("@type", "organizationId", "status", "displayName"), json.getArray("content").getObject(0).keys());
		assertEquals("OrganizationSummary", json.getArray("content").getObject(0).getString("@type"));
		assertEquals(org1.toString(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(0).getString("status"));
		assertEquals("A new org 1", json.getArray("content").getObject(0).getString("displayName"));
		assertEquals(List.of("@type", "organizationId", "status", "displayName"), json.getArray("content").getObject(1).keys());
		assertEquals("OrganizationSummary", json.getArray("content").getObject(1).getString("@type"));
		assertEquals(org2.toString(), json.getArray("content").getObject(1).getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(1).getString("status"));
		assertEquals("A néw org 2", json.getArray("content").getObject(1).getString("displayName"));
	}
	
	@Test
	public void testFilterByActifAsc() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/organizations")
			.queryParam("status", "Actif")
			.queryParam("order", "frenchName")
			.queryParam("direction", "desc")
			.header("Locale", Lang.FRENCH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		//CrmAsserts.printLinkedDataAsserts(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(3, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(3, json.getArray("content").size());
		assertEquals(List.of("@type", "organizationId", "status", "displayName"), json.getArray("content").getObject(0).keys());
		assertEquals("OrganizationSummary", json.getArray("content").getObject(0).getString("@type"));
		assertEquals(org1.toString(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals("Actif", json.getArray("content").getObject(0).getString("status"));
		assertEquals("A new org 1", json.getArray("content").getObject(0).getString("displayName"));
		assertEquals(List.of("@type", "organizationId", "status", "displayName"), json.getArray("content").getObject(1).keys());
		assertEquals("OrganizationSummary", json.getArray("content").getObject(1).getString("@type"));
		assertEquals(org2.toString(), json.getArray("content").getObject(1).getString("organizationId"));
		assertEquals("Actif", json.getArray("content").getObject(1).getString("status"));
		assertEquals("A néw org 2", json.getArray("content").getObject(1).getString("displayName"));
		assertEquals(List.of("@type", "organizationId", "status", "displayName"), json.getArray("content").getObject(2).keys());
		assertEquals("OrganizationSummary", json.getArray("content").getObject(2).getString("@type"));
		assertEquals(org0.toString(), json.getArray("content").getObject(2).getString("organizationId"));
		assertEquals("Actif", json.getArray("content").getObject(2).getString("status"));
		assertEquals("System Administrator", json.getArray("content").getObject(2).getString("displayName"));
	}

	@Test
	public void testFilterByInactif() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/organizations")
			.queryParam("status", "Inactif")
			.header("Locale", Lang.FRENCH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		//CrmAsserts.printLinkedDataAsserts(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(1, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(1, json.getArray("content").size());
		assertEquals(List.of("@type", "organizationId", "status", "displayName"), json.getArray("content").getObject(0).keys());
		assertEquals("OrganizationSummary", json.getArray("content").getObject(0).getString("@type"));
		assertEquals(org3.toString(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals("Inactif", json.getArray("content").getObject(0).getString("status"));
		assertEquals("An inactive org 3", json.getArray("content").getObject(0).getString("displayName"));
	}
}
