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
import ca.magex.json.model.JsonObject;

public class UsersFilterControllerTests extends AbstractControllerTests {
	
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
	
	private Identifier systemUserId;
	
	@Before
	public void setup() {
		initialize();

		systemOrgId = crm.findOrganizationSummaries(crm.defaultOrganizationsFilter().withGroup("SYS")).getSingleItem().getOrganizationId();
		systemPersonId = crm.findPersonDetails(crm.defaultPersonsFilter()).getSingleItem().getPersonId();
		systemUserId = crm.findUserByUsername("system").getUserId();

		org1 = crm.createOrganization("Org 1", List.of("ORG")).getOrganizationId();
		org2 = crm.createOrganization("Org 2", List.of("ORG")).getOrganizationId();
		
		adamId = crm.createUser(crm.createPerson(org1, ADAM, CA_ADDRESS, HOME_COMMUNICATIONS, BUSINESS_POSITION).getPersonId(), "adam", List.of("ORG_ADMIN", "CRM_ADMIN")).getUserId();
		bobId = crm.disableUser(crm.createUser(crm.createPerson(org1, BOB, US_ADDRESS, HOME_COMMUNICATIONS, BUSINESS_POSITION).getPersonId(), "bob", List.of("ORG_USER")).getUserId()).getUserId();
		chloeId = crm.createUser(crm.createPerson(org1, CHLOE, MX_ADDRESS, WORK_COMMUNICATIONS, BUSINESS_POSITION).getPersonId(), "chloe", List.of("CRM_USER")).getUserId();
		danId = crm.createUser(crm.createPerson(org2, DAN, EN_ADDRESS, HOME_COMMUNICATIONS, BUSINESS_POSITION).getPersonId(), "dan", List.of("CRM_USER")).getUserId();
		elaineId = crm.createUser(crm.createPerson(org2, ELAINE, DE_ADDRESS, WORK_COMMUNICATIONS, BUSINESS_POSITION).getPersonId(), "elaine", List.of("SYS_ADMIN")).getUserId();
		crm.disablePerson(crm.findUser(elaineId).getPerson().getPersonId());
		francoisId = crm.createUser(crm.createPerson(org2, FRANCOIS, FR_ADDRESS, WORK_COMMUNICATIONS, BUSINESS_POSITION).getPersonId(), "francois", List.of("ORG_ADMIN", "CRM_USER")).getUserId();
	}
	
	@Test
	public void testOrganizationFilterDefaultRoot() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/users"))
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
		assertEquals(List.of("@type", "userId", "username", "person", "status", "roles"), json.getArray("content").getObject(0).keys());
		assertEquals("User", json.getArray("content").getObject(0).getString("@type"));
		assertEquals(adamId.toString(), json.getArray("content").getObject(0).getString("userId"));
		assertEquals("adam", json.getArray("content").getObject(0).getString("username"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(0).getObject("person").keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(0).getObject("person").getString("@type"));
		assertEquals(crm.findUser(adamId).getPerson().getPersonId().toString(), json.getArray("content").getObject(0).getObject("person").getString("personId"));
		assertEquals(crm.findUser(adamId).getPerson().getOrganizationId().toString(), json.getArray("content").getObject(0).getObject("person").getString("organizationId"));
		assertEquals("active", json.getArray("content").getObject(0).getObject("person").getString("status"));
		assertEquals("Anderson, Adam A", json.getArray("content").getObject(0).getObject("person").getString("displayName"));
		assertEquals("active", json.getArray("content").getObject(0).getString("status"));
		assertEquals(2, json.getArray("content").getObject(0).getArray("roles").size());
		assertEquals("ORG_ADMIN", json.getArray("content").getObject(0).getArray("roles").getString(0));
		assertEquals("CRM_ADMIN", json.getArray("content").getObject(0).getArray("roles").getString(1));
		assertEquals(List.of("@type", "userId", "username", "person", "status", "roles"), json.getArray("content").getObject(1).keys());
		assertEquals("User", json.getArray("content").getObject(1).getString("@type"));
		assertEquals(bobId.toString(), json.getArray("content").getObject(1).getString("userId"));
		assertEquals("bob", json.getArray("content").getObject(1).getString("username"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(1).getObject("person").keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(1).getObject("person").getString("@type"));
		assertEquals(crm.findUser(bobId).getPerson().getPersonId().toString(), json.getArray("content").getObject(1).getObject("person").getString("personId"));
		assertEquals(crm.findUser(bobId).getPerson().getOrganizationId().toString(), json.getArray("content").getObject(1).getObject("person").getString("organizationId"));
		assertEquals("active", json.getArray("content").getObject(1).getObject("person").getString("status"));
		assertEquals("Robert, Bob K", json.getArray("content").getObject(1).getObject("person").getString("displayName"));
		assertEquals("inactive", json.getArray("content").getObject(1).getString("status"));
		assertEquals(1, json.getArray("content").getObject(1).getArray("roles").size());
		assertEquals("ORG_USER", json.getArray("content").getObject(1).getArray("roles").getString(0));
	}
	
	@Test
	public void testOrganizationFilterDefaultEnglish() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/users")
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
		assertEquals(List.of("@type", "userId", "username", "person", "status", "roles"), json.getArray("content").getObject(0).keys());
		assertEquals("User", json.getArray("content").getObject(0).getString("@type").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(adamId.toString(), json.getArray("content").getObject(0).getString("userId"));
		assertEquals("adam", json.getArray("content").getObject(0).getString("username").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(0).getObject("person").keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(0).getObject("person").getString("@type").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(crm.findUser(adamId).getPerson().getPersonId().toString(), json.getArray("content").getObject(0).getObject("person").getString("personId"));
		assertEquals(org1.toString(), json.getArray("content").getObject(0).getObject("person").getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(0).getObject("person").getString("status").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("Anderson, Adam A", json.getArray("content").getObject(0).getObject("person").getString("displayName").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("Active", json.getArray("content").getObject(0).getString("status").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(2, json.getArray("content").getObject(0).getArray("roles").size());
		assertEquals("ORG_ADMIN", json.getArray("content").getObject(0).getArray("roles").getString(0).replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("CRM_ADMIN", json.getArray("content").getObject(0).getArray("roles").getString(1).replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(List.of("@type", "userId", "username", "person", "status", "roles"), json.getArray("content").getObject(1).keys());
		assertEquals("User", json.getArray("content").getObject(1).getString("@type").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(bobId.toString(), json.getArray("content").getObject(1).getString("userId"));
		assertEquals("bob", json.getArray("content").getObject(1).getString("username").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(1).getObject("person").keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(1).getObject("person").getString("@type").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(crm.findUser(bobId).getPerson().getPersonId().toString(), json.getArray("content").getObject(1).getObject("person").getString("personId"));
		assertEquals(org1.toString(), json.getArray("content").getObject(1).getObject("person").getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(1).getObject("person").getString("status").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("Robert, Bob K", json.getArray("content").getObject(1).getObject("person").getString("displayName").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("Inactive", json.getArray("content").getObject(1).getString("status").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(1, json.getArray("content").getObject(1).getArray("roles").size());
		assertEquals("ORG_USER", json.getArray("content").getObject(1).getArray("roles").getString(0).replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
	}
	
	@Test
	public void testOrganizationFilterDefaultFrench() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/users")
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
		assertEquals(List.of("@type", "userId", "username", "person", "status", "roles"), json.getArray("content").getObject(0).keys());
		assertEquals("User", json.getArray("content").getObject(0).getString("@type").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(adamId.toString(), json.getArray("content").getObject(0).getString("userId"));
		assertEquals("adam", json.getArray("content").getObject(0).getString("username").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(0).getObject("person").keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(0).getObject("person").getString("@type").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(crm.findUser(adamId).getPerson().getPersonId().toString(), json.getArray("content").getObject(0).getObject("person").getString("personId"));
		assertEquals(org1.toString(), json.getArray("content").getObject(0).getObject("person").getString("organizationId"));
		assertEquals("Actif", json.getArray("content").getObject(0).getObject("person").getString("status").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("Anderson, Adam A", json.getArray("content").getObject(0).getObject("person").getString("displayName").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("Actif", json.getArray("content").getObject(0).getString("status").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(2, json.getArray("content").getObject(0).getArray("roles").size());
		assertEquals("ORG_ADMIN", json.getArray("content").getObject(0).getArray("roles").getString(0).replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("CRM_ADMIN", json.getArray("content").getObject(0).getArray("roles").getString(1).replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(List.of("@type", "userId", "username", "person", "status", "roles"), json.getArray("content").getObject(1).keys());
		assertEquals("User", json.getArray("content").getObject(1).getString("@type").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(bobId.toString(), json.getArray("content").getObject(1).getString("userId"));
		assertEquals("bob", json.getArray("content").getObject(1).getString("username").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(1).getObject("person").keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(1).getObject("person").getString("@type").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(crm.findUser(bobId).getPerson().getPersonId().toString(), json.getArray("content").getObject(1).getObject("person").getString("personId"));
		assertEquals(org1.toString(), json.getArray("content").getObject(1).getObject("person").getString("organizationId"));
		assertEquals("Actif", json.getArray("content").getObject(1).getObject("person").getString("status").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("Robert, Bob K", json.getArray("content").getObject(1).getObject("person").getString("displayName").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("Inactif", json.getArray("content").getObject(1).getString("status").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(1, json.getArray("content").getObject(1).getArray("roles").size());
		assertEquals("ORG_USER", json.getArray("content").getObject(1).getArray("roles").getString(0).replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
	}
	
	@Test
	public void testFilterByUsername() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/users")
			.queryParam("username", "e")
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
		assertEquals(List.of("@type", "userId", "username", "person", "status", "roles"), json.getArray("content").getObject(0).keys());
		assertEquals("User", json.getArray("content").getObject(0).getString("@type").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(chloeId.toString(), json.getArray("content").getObject(0).getString("userId"));
		assertEquals("chloe", json.getArray("content").getObject(0).getString("username").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(0).getObject("person").keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(0).getObject("person").getString("@type").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(crm.findUser(chloeId).getPerson().getPersonId().toString(), json.getArray("content").getObject(0).getObject("person").getString("personId"));
		assertEquals(org1.toString(), json.getArray("content").getObject(0).getObject("person").getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(0).getObject("person").getString("status").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("LaRue, Chloé", json.getArray("content").getObject(0).getObject("person").getString("displayName").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("Active", json.getArray("content").getObject(0).getString("status").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(1, json.getArray("content").getObject(0).getArray("roles").size());
		assertEquals("CRM_USER", json.getArray("content").getObject(0).getArray("roles").getString(0).replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(List.of("@type", "userId", "username", "person", "status", "roles"), json.getArray("content").getObject(1).keys());
		assertEquals("User", json.getArray("content").getObject(1).getString("@type").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(elaineId.toString(), json.getArray("content").getObject(1).getString("userId"));
		assertEquals("elaine", json.getArray("content").getObject(1).getString("username").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(1).getObject("person").keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(1).getObject("person").getString("@type").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(crm.findUser(elaineId).getPerson().getPersonId().toString(), json.getArray("content").getObject(1).getObject("person").getString("personId"));
		assertEquals(org2.toString(), json.getArray("content").getObject(1).getObject("person").getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(1).getObject("person").getString("status").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("McKay, Elaine M", json.getArray("content").getObject(1).getObject("person").getString("displayName").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("Active", json.getArray("content").getObject(1).getString("status").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(1, json.getArray("content").getObject(1).getArray("roles").size());
		assertEquals("SYS_ADMIN", json.getArray("content").getObject(1).getArray("roles").getString(0).replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(List.of("@type", "userId", "username", "person", "status", "roles"), json.getArray("content").getObject(2).keys());
		assertEquals("User", json.getArray("content").getObject(2).getString("@type").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(systemUserId.toString(), json.getArray("content").getObject(2).getString("userId"));
		assertEquals("system", json.getArray("content").getObject(2).getString("username").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(2).getObject("person").keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(2).getObject("person").getString("@type").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(systemPersonId.toString(), json.getArray("content").getObject(2).getObject("person").getString("personId"));
		assertEquals(systemOrgId.toString(), json.getArray("content").getObject(2).getObject("person").getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(2).getObject("person").getString("status").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("Bacon, Chris P", json.getArray("content").getObject(2).getObject("person").getString("displayName").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("Active", json.getArray("content").getObject(2).getString("status").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(4, json.getArray("content").getObject(2).getArray("roles").size());
		assertEquals("SYS_ADMIN", json.getArray("content").getObject(2).getArray("roles").getString(0).replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("SYS_ACTUATOR", json.getArray("content").getObject(2).getArray("roles").getString(1).replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("SYS_ACCESS", json.getArray("content").getObject(2).getArray("roles").getString(2).replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("CRM_ADMIN", json.getArray("content").getObject(2).getArray("roles").getString(3).replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
	}
	
	@Test
	public void testFilterByRole() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/users")
			.queryParam("role", "CRM_USER")
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
		assertEquals(List.of("@type", "userId", "username", "person", "status", "roles"), json.getArray("content").getObject(0).keys());
		assertEquals("User", json.getArray("content").getObject(0).getString("@type").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(chloeId.toString(), json.getArray("content").getObject(0).getString("userId"));
		assertEquals("chloe", json.getArray("content").getObject(0).getString("username").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(0).getObject("person").keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(0).getObject("person").getString("@type").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(crm.findUser(chloeId).getPerson().getPersonId().toString(), json.getArray("content").getObject(0).getObject("person").getString("personId"));
		assertEquals(org1.toString(), json.getArray("content").getObject(0).getObject("person").getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(0).getObject("person").getString("status").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("LaRue, Chloé", json.getArray("content").getObject(0).getObject("person").getString("displayName").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("Active", json.getArray("content").getObject(0).getString("status").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(1, json.getArray("content").getObject(0).getArray("roles").size());
		assertEquals("CRM_USER", json.getArray("content").getObject(0).getArray("roles").getString(0).replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(List.of("@type", "userId", "username", "person", "status", "roles"), json.getArray("content").getObject(1).keys());
		assertEquals("User", json.getArray("content").getObject(1).getString("@type").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(danId.toString(), json.getArray("content").getObject(1).getString("userId"));
		assertEquals("dan", json.getArray("content").getObject(1).getString("username").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(1).getObject("person").keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(1).getObject("person").getString("@type").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(crm.findUser(danId).getPerson().getPersonId().toString(), json.getArray("content").getObject(1).getObject("person").getString("personId"));
		assertEquals(org2.toString(), json.getArray("content").getObject(1).getObject("person").getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(1).getObject("person").getString("status").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("O'Sullivan, Daniel D", json.getArray("content").getObject(1).getObject("person").getString("displayName").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("Active", json.getArray("content").getObject(1).getString("status").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(1, json.getArray("content").getObject(1).getArray("roles").size());
		assertEquals("CRM_USER", json.getArray("content").getObject(1).getArray("roles").getString(0).replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(List.of("@type", "userId", "username", "person", "status", "roles"), json.getArray("content").getObject(2).keys());
		assertEquals("User", json.getArray("content").getObject(2).getString("@type").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(francoisId.toString(), json.getArray("content").getObject(2).getString("userId"));
		assertEquals("francois", json.getArray("content").getObject(2).getString("username").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(2).getObject("person").keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(2).getObject("person").getString("@type").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(crm.findUser(francoisId).getPerson().getPersonId().toString(), json.getArray("content").getObject(2).getObject("person").getString("personId"));
		assertEquals(org2.toString(), json.getArray("content").getObject(2).getObject("person").getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(2).getObject("person").getString("status").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("Mátyás, François", json.getArray("content").getObject(2).getObject("person").getString("displayName").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("Active", json.getArray("content").getObject(2).getString("status").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(2, json.getArray("content").getObject(2).getArray("roles").size());
		assertEquals("ORG_ADMIN", json.getArray("content").getObject(2).getArray("roles").getString(0).replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("CRM_USER", json.getArray("content").getObject(2).getArray("roles").getString(1).replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
	}
	
	@Test
	public void testFilterByPersonId() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/users")
			.queryParam("person", crm.findUser(francoisId).getPerson().getPersonId().toString())
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(1, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(1, json.getArray("content").size());
		assertEquals(List.of("@type", "userId", "username", "person", "status", "roles"), json.getArray("content").getObject(0).keys());
		assertEquals("User", json.getArray("content").getObject(0).getString("@type").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(francoisId.toString(), json.getArray("content").getObject(0).getString("userId"));
		assertEquals("francois", json.getArray("content").getObject(0).getString("username").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(0).getObject("person").keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(0).getObject("person").getString("@type").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(crm.findUser(francoisId).getPerson().getPersonId().toString(), json.getArray("content").getObject(0).getObject("person").getString("personId"));
		assertEquals(org2.toString(), json.getArray("content").getObject(0).getObject("person").getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(0).getObject("person").getString("status").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("Mátyás, François", json.getArray("content").getObject(0).getObject("person").getString("displayName").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("Active", json.getArray("content").getObject(0).getString("status").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(2, json.getArray("content").getObject(0).getArray("roles").size());
		assertEquals("ORG_ADMIN", json.getArray("content").getObject(0).getArray("roles").getString(0).replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("CRM_USER", json.getArray("content").getObject(0).getArray("roles").getString(1).replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
	}
	
	@Test
	public void testFilterByOrgId() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/users")
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
		assertEquals(List.of("@type", "userId", "username", "person", "status", "roles"), json.getArray("content").getObject(0).keys());
		assertEquals("User", json.getArray("content").getObject(0).getString("@type").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(danId.toString(), json.getArray("content").getObject(0).getString("userId"));
		assertEquals("dan", json.getArray("content").getObject(0).getString("username").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(0).getObject("person").keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(0).getObject("person").getString("@type").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(crm.findUser(danId).getPerson().getPersonId().toString(), json.getArray("content").getObject(0).getObject("person").getString("personId"));
		assertEquals(org2.toString(), json.getArray("content").getObject(0).getObject("person").getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(0).getObject("person").getString("status").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("O'Sullivan, Daniel D", json.getArray("content").getObject(0).getObject("person").getString("displayName").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("Active", json.getArray("content").getObject(0).getString("status").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(1, json.getArray("content").getObject(0).getArray("roles").size());
		assertEquals("CRM_USER", json.getArray("content").getObject(0).getArray("roles").getString(0).replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(List.of("@type", "userId", "username", "person", "status", "roles"), json.getArray("content").getObject(1).keys());
		assertEquals("User", json.getArray("content").getObject(1).getString("@type").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(elaineId.toString(), json.getArray("content").getObject(1).getString("userId"));
		assertEquals("elaine", json.getArray("content").getObject(1).getString("username").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(1).getObject("person").keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(1).getObject("person").getString("@type").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(crm.findUser(elaineId).getPerson().getPersonId().toString(), json.getArray("content").getObject(1).getObject("person").getString("personId"));
		assertEquals(org2.toString(), json.getArray("content").getObject(1).getObject("person").getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(1).getObject("person").getString("status").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("McKay, Elaine M", json.getArray("content").getObject(1).getObject("person").getString("displayName").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("Active", json.getArray("content").getObject(1).getString("status").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(1, json.getArray("content").getObject(1).getArray("roles").size());
		assertEquals("SYS_ADMIN", json.getArray("content").getObject(1).getArray("roles").getString(0).replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(List.of("@type", "userId", "username", "person", "status", "roles"), json.getArray("content").getObject(2).keys());
		assertEquals("User", json.getArray("content").getObject(2).getString("@type").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(francoisId.toString(), json.getArray("content").getObject(2).getString("userId"));
		assertEquals("francois", json.getArray("content").getObject(2).getString("username").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(2).getObject("person").keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(2).getObject("person").getString("@type").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(crm.findUser(francoisId).getPerson().getPersonId().toString(), json.getArray("content").getObject(2).getObject("person").getString("personId"));
		assertEquals(org2.toString(), json.getArray("content").getObject(2).getObject("person").getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(2).getObject("person").getString("status").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("Mátyás, François", json.getArray("content").getObject(2).getObject("person").getString("displayName").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("Active", json.getArray("content").getObject(2).getString("status").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(2, json.getArray("content").getObject(2).getArray("roles").size());
		assertEquals("ORG_ADMIN", json.getArray("content").getObject(2).getArray("roles").getString(0).replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("CRM_USER", json.getArray("content").getObject(2).getArray("roles").getString(1).replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
	}
	
	@Test
	public void testFilterByInactifDesc() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/users")
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
		assertEquals(1, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(1, json.getArray("content").size());
		assertEquals(List.of("@type", "userId", "username", "person", "status", "roles"), json.getArray("content").getObject(0).keys());
		assertEquals("User", json.getArray("content").getObject(0).getString("@type").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(bobId.toString(), json.getArray("content").getObject(0).getString("userId"));
		assertEquals("bob", json.getArray("content").getObject(0).getString("username").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(0).getObject("person").keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(0).getObject("person").getString("@type").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(crm.findUser(bobId).getPerson().getPersonId().toString(), json.getArray("content").getObject(0).getObject("person").getString("personId"));
		assertEquals(org1.toString(), json.getArray("content").getObject(0).getObject("person").getString("organizationId"));
		assertEquals("Actif", json.getArray("content").getObject(0).getObject("person").getString("status").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("Robert, Bob K", json.getArray("content").getObject(0).getObject("person").getString("displayName").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("Inactif", json.getArray("content").getObject(0).getString("status").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(1, json.getArray("content").getObject(0).getArray("roles").size());
		assertEquals("ORG_USER", json.getArray("content").getObject(0).getArray("roles").getString(0).replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));

	}
	
}
