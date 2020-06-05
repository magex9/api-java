package ca.magex.crm.restful.controllers;

import static ca.magex.crm.test.CrmAsserts.ADAM;
import static ca.magex.crm.test.CrmAsserts.BOB;
import static ca.magex.crm.test.CrmAsserts.BUSINESS_POSITION;
import static ca.magex.crm.test.CrmAsserts.CA_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.CHLOE;
import static ca.magex.crm.test.CrmAsserts.DAN;
import static ca.magex.crm.test.CrmAsserts.DE_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.ELAINE;
import static ca.magex.crm.test.CrmAsserts.EN_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.FRANCOIS;
import static ca.magex.crm.test.CrmAsserts.FR_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.HOME_COMMUNICATIONS;
import static ca.magex.crm.test.CrmAsserts.MX_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.US_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.WORK_COMMUNICATIONS;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.json.model.JsonAsserts;
import ca.magex.json.model.JsonObject;

public class PersonsFilterControllerTests extends AbstractControllerTests {
	
	private Identifier org1;
	
	private Identifier org2;
	
	private Identifier adamId;
	
	private Identifier bobId;
	
	private Identifier chloeId;
	
	private Identifier danId;
	
	private Identifier elaineId;
	
	private Identifier francoisId;
	
	private Identifier systemOrgId;
	
	private Identifier systemPersonId;
	
	@Before
	public void setup() {
		initialize();

		systemOrgId = crm.findOrganizationSummaries(crm.defaultOrganizationsFilter().withGroup("SYS")).getSingleItem().getOrganizationId();
		systemPersonId = crm.findPersonDetails(crm.defaultPersonsFilter()).getSingleItem().getPersonId();

		org1 = crm.createOrganization("Org 1", List.of("ORG")).getOrganizationId();
		org2 = crm.createOrganization("Org 2", List.of("ORG")).getOrganizationId();
		
		adamId = crm.createPerson(org1, ADAM, CA_ADDRESS, HOME_COMMUNICATIONS, BUSINESS_POSITION).getPersonId();
		bobId = crm.disablePerson(crm.createPerson(org1, BOB, US_ADDRESS, HOME_COMMUNICATIONS, BUSINESS_POSITION).getPersonId()).getPersonId();
		chloeId = crm.createPerson(org1, CHLOE, MX_ADDRESS, WORK_COMMUNICATIONS, BUSINESS_POSITION).getPersonId();
		danId = crm.createPerson(org2, DAN, EN_ADDRESS, HOME_COMMUNICATIONS, BUSINESS_POSITION).getPersonId();
		elaineId = crm.disablePerson(crm.createPerson(org2, ELAINE, DE_ADDRESS, WORK_COMMUNICATIONS, BUSINESS_POSITION).getPersonId()).getPersonId();
		francoisId = crm.createPerson(org2, FRANCOIS, FR_ADDRESS, WORK_COMMUNICATIONS, BUSINESS_POSITION).getPersonId();
	}
	
	@Test
	public void testOrganizationFilterDefaultRoot() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/persons"))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(7, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(7, json.getArray("content").size());
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(0).keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(0).getString("@type"));
		assertEquals(adamId.toString(), json.getArray("content").getObject(0).getString("personId"));
		assertEquals(org1.toString(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals("active", json.getArray("content").getObject(0).getString("status"));
		assertEquals("Anderson, Adam A", json.getArray("content").getObject(0).getString("displayName"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(1).keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(1).getString("@type"));
		assertEquals(systemPersonId.toString(), json.getArray("content").getObject(1).getString("personId"));
		assertEquals(systemOrgId.toString(), json.getArray("content").getObject(1).getString("organizationId"));
		assertEquals("active", json.getArray("content").getObject(1).getString("status"));
		assertEquals("Bacon, Chris P", json.getArray("content").getObject(1).getString("displayName"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(2).keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(2).getString("@type"));
		assertEquals(chloeId.toString(), json.getArray("content").getObject(2).getString("personId"));
		assertEquals(org1.toString(), json.getArray("content").getObject(2).getString("organizationId"));
		assertEquals("active", json.getArray("content").getObject(2).getString("status"));
		assertEquals("LaRue, Chloé", json.getArray("content").getObject(2).getString("displayName"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(3).keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(3).getString("@type"));
		assertEquals(francoisId.toString(), json.getArray("content").getObject(3).getString("personId"));
		assertEquals(org2.toString(), json.getArray("content").getObject(3).getString("organizationId"));
		assertEquals("active", json.getArray("content").getObject(3).getString("status"));
		assertEquals("Mátyás, François", json.getArray("content").getObject(3).getString("displayName"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(4).keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(4).getString("@type"));
		assertEquals(elaineId.toString(), json.getArray("content").getObject(4).getString("personId"));
		assertEquals(org2.toString(), json.getArray("content").getObject(4).getString("organizationId"));
		assertEquals("inactive", json.getArray("content").getObject(4).getString("status"));
		assertEquals("McKay, Elaine M", json.getArray("content").getObject(4).getString("displayName"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(5).keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(5).getString("@type"));
		assertEquals(danId.toString(), json.getArray("content").getObject(5).getString("personId"));
		assertEquals(org2.toString(), json.getArray("content").getObject(5).getString("organizationId"));
		assertEquals("active", json.getArray("content").getObject(5).getString("status"));
		assertEquals("O'Sullivan, Daniel D", json.getArray("content").getObject(5).getString("displayName"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(6).keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(6).getString("@type"));
		assertEquals(bobId.toString(), json.getArray("content").getObject(6).getString("personId"));
		assertEquals(org1.toString(), json.getArray("content").getObject(6).getString("organizationId"));
		assertEquals("inactive", json.getArray("content").getObject(6).getString("status"));
		assertEquals("Robert, Bob K", json.getArray("content").getObject(6).getString("displayName"));
	}
	
	@Test
	public void testOrganizationFilterDefaultEnglish() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/persons")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(7, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(7, json.getArray("content").size());
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(0).keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(0).getString("@type"));
		assertEquals(adamId.toString(), json.getArray("content").getObject(0).getString("personId"));
		assertEquals(org1.toString(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(0).getString("status"));
		assertEquals("Anderson, Adam A", json.getArray("content").getObject(0).getString("displayName"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(1).keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(1).getString("@type"));
		assertEquals(systemPersonId.toString(), json.getArray("content").getObject(1).getString("personId"));
		assertEquals(systemOrgId.toString(), json.getArray("content").getObject(1).getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(1).getString("status"));
		assertEquals("Bacon, Chris P", json.getArray("content").getObject(1).getString("displayName"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(2).keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(2).getString("@type"));
		assertEquals(chloeId.toString(), json.getArray("content").getObject(2).getString("personId"));
		assertEquals(org1.toString(), json.getArray("content").getObject(2).getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(2).getString("status"));
		assertEquals("LaRue, Chloé", json.getArray("content").getObject(2).getString("displayName"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(3).keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(3).getString("@type"));
		assertEquals(francoisId.toString(), json.getArray("content").getObject(3).getString("personId"));
		assertEquals(org2.toString(), json.getArray("content").getObject(3).getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(3).getString("status"));
		assertEquals("Mátyás, François", json.getArray("content").getObject(3).getString("displayName"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(4).keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(4).getString("@type"));
		assertEquals(elaineId.toString(), json.getArray("content").getObject(4).getString("personId"));
		assertEquals(org2.toString(), json.getArray("content").getObject(4).getString("organizationId"));
		assertEquals("Inactive", json.getArray("content").getObject(4).getString("status"));
		assertEquals("McKay, Elaine M", json.getArray("content").getObject(4).getString("displayName"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(5).keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(5).getString("@type"));
		assertEquals(danId.toString(), json.getArray("content").getObject(5).getString("personId"));
		assertEquals(org2.toString(), json.getArray("content").getObject(5).getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(5).getString("status"));
		assertEquals("O'Sullivan, Daniel D", json.getArray("content").getObject(5).getString("displayName"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(6).keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(6).getString("@type"));
		assertEquals(bobId.toString(), json.getArray("content").getObject(6).getString("personId"));
		assertEquals(org1.toString(), json.getArray("content").getObject(6).getString("organizationId"));
		assertEquals("Inactive", json.getArray("content").getObject(6).getString("status"));
		assertEquals("Robert, Bob K", json.getArray("content").getObject(6).getString("displayName"));
	}
	
	@Test
	public void testOrganizationFilterDefaultFrench() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/persons")
			.header("Locale", Lang.FRENCH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(7, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(7, json.getArray("content").size());
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(0).keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(0).getString("@type"));
		assertEquals(adamId.toString(), json.getArray("content").getObject(0).getString("personId"));
		assertEquals(org1.toString(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals("Actif", json.getArray("content").getObject(0).getString("status"));
		assertEquals("Anderson, Adam A", json.getArray("content").getObject(0).getString("displayName"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(1).keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(1).getString("@type"));
		assertEquals(systemPersonId.toString(), json.getArray("content").getObject(1).getString("personId"));
		assertEquals(systemOrgId.toString(), json.getArray("content").getObject(1).getString("organizationId"));
		assertEquals("Actif", json.getArray("content").getObject(1).getString("status"));
		assertEquals("Bacon, Chris P", json.getArray("content").getObject(1).getString("displayName"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(2).keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(2).getString("@type"));
		assertEquals(chloeId.toString(), json.getArray("content").getObject(2).getString("personId"));
		assertEquals(org1.toString(), json.getArray("content").getObject(2).getString("organizationId"));
		assertEquals("Actif", json.getArray("content").getObject(2).getString("status"));
		assertEquals("LaRue, Chloé", json.getArray("content").getObject(2).getString("displayName"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(3).keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(3).getString("@type"));
		assertEquals(francoisId.toString(), json.getArray("content").getObject(3).getString("personId"));
		assertEquals(org2.toString(), json.getArray("content").getObject(3).getString("organizationId"));
		assertEquals("Actif", json.getArray("content").getObject(3).getString("status"));
		assertEquals("Mátyás, François", json.getArray("content").getObject(3).getString("displayName"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(4).keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(4).getString("@type"));
		assertEquals(elaineId.toString(), json.getArray("content").getObject(4).getString("personId"));
		assertEquals(org2.toString(), json.getArray("content").getObject(4).getString("organizationId"));
		assertEquals("Inactif", json.getArray("content").getObject(4).getString("status"));
		assertEquals("McKay, Elaine M", json.getArray("content").getObject(4).getString("displayName"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(5).keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(5).getString("@type"));
		assertEquals(danId.toString(), json.getArray("content").getObject(5).getString("personId"));
		assertEquals(org2.toString(), json.getArray("content").getObject(5).getString("organizationId"));
		assertEquals("Actif", json.getArray("content").getObject(5).getString("status"));
		assertEquals("O'Sullivan, Daniel D", json.getArray("content").getObject(5).getString("displayName"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(6).keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(6).getString("@type"));
		assertEquals(bobId.toString(), json.getArray("content").getObject(6).getString("personId"));
		assertEquals(org1.toString(), json.getArray("content").getObject(6).getString("organizationId"));
		assertEquals("Inactif", json.getArray("content").getObject(6).getString("status"));
		assertEquals("Robert, Bob K", json.getArray("content").getObject(6).getString("displayName"));
	}

	@Test
	public void testFilterByDisplayNameCaseAccentInsensitive() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/persons")
			.queryParam("displayName", "C")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(3, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(3, json.getArray("content").size());
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(0).keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(0).getString("@type"));
		assertEquals(systemPersonId.toString(), json.getArray("content").getObject(0).getString("personId"));
		assertEquals(systemOrgId.toString(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(0).getString("status"));
		assertEquals("Bacon, Chris P", json.getArray("content").getObject(0).getString("displayName"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(1).keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(1).getString("@type"));
		assertEquals(chloeId.toString(), json.getArray("content").getObject(1).getString("personId"));
		assertEquals(org1.toString(), json.getArray("content").getObject(1).getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(1).getString("status"));
		assertEquals("LaRue, Chloé", json.getArray("content").getObject(1).getString("displayName"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(2).keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(2).getString("@type"));
		assertEquals(elaineId.toString(), json.getArray("content").getObject(2).getString("personId"));
		assertEquals(org2.toString(), json.getArray("content").getObject(2).getString("organizationId"));
		assertEquals("Inactive", json.getArray("content").getObject(2).getString("status"));
		assertEquals("McKay, Elaine M", json.getArray("content").getObject(2).getString("displayName"));
	}
	
	@Test
	public void testFilterByInactifDesc() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/persons")
			.queryParam("status", "Inactif")
			.queryParam("order", "displayName")
			.queryParam("direction", "desc")
			.header("Locale", Lang.FRENCH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(2, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(2, json.getArray("content").size());
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(0).keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(0).getString("@type"));
		assertEquals(bobId.toString(), json.getArray("content").getObject(0).getString("personId"));
		assertEquals(org1.toString(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals("Inactif", json.getArray("content").getObject(0).getString("status"));
		assertEquals("Robert, Bob K", json.getArray("content").getObject(0).getString("displayName"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(1).keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(1).getString("@type"));
		assertEquals(elaineId.toString(), json.getArray("content").getObject(1).getString("personId"));
		assertEquals(org2.toString(), json.getArray("content").getObject(1).getString("organizationId"));
		assertEquals("Inactif", json.getArray("content").getObject(1).getString("status"));
		assertEquals("McKay, Elaine M", json.getArray("content").getObject(1).getString("displayName"));
	}
	
	@Test
	public void testFilterByOrganization() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/persons")
			.queryParam("organization", org2.toString())
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(3, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(3, json.getArray("content").size());
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(0).keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(0).getString("@type"));
		assertEquals(francoisId.toString(), json.getArray("content").getObject(0).getString("personId"));
		assertEquals(org2.toString(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(0).getString("status"));
		assertEquals("Mátyás, François", json.getArray("content").getObject(0).getString("displayName"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(1).keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(1).getString("@type"));
		assertEquals(elaineId.toString(), json.getArray("content").getObject(1).getString("personId"));
		assertEquals(org2.toString(), json.getArray("content").getObject(1).getString("organizationId"));
		assertEquals("Inactive", json.getArray("content").getObject(1).getString("status"));
		assertEquals("McKay, Elaine M", json.getArray("content").getObject(1).getString("displayName"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(2).keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(2).getString("@type"));
		assertEquals(danId.toString(), json.getArray("content").getObject(2).getString("personId"));
		assertEquals(org2.toString(), json.getArray("content").getObject(2).getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(2).getString("status"));
		assertEquals("O'Sullivan, Daniel D", json.getArray("content").getObject(2).getString("displayName"));
	}

}
