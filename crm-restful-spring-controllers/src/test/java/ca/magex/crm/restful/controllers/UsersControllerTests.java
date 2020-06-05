package ca.magex.crm.restful.controllers;

import static ca.magex.crm.test.CrmAsserts.BUSINESS_POSITION;
import static ca.magex.crm.test.CrmAsserts.CHLOE;
import static ca.magex.crm.test.CrmAsserts.MAILING_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.WORK_COMMUNICATIONS;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Status;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonAsserts;
import ca.magex.json.model.JsonObject;

public class UsersControllerTests extends AbstractControllerTests {

	private Identifier systemOrgId;
	
	private Identifier systemPersonId;
	
	private Identifier systemUserId;
	
	private Identifier testOrgId;
	
	private Identifier testPersonId;
	
	@Before
	public void setup() {
		initialize();
		systemOrgId = crm.findOrganizationSummaries(crm.defaultOrganizationsFilter().withGroup("SYS")).getSingleItem().getOrganizationId();
		systemPersonId = crm.findPersonDetails(crm.defaultPersonsFilter()).getSingleItem().getPersonId();
		systemUserId = crm.findUsers(crm.defaultUsersFilter().withPersonId(systemPersonId)).getSingleItem().getUserId();
		testOrgId = crm.createOrganization("Test Org", List.of("ORG")).getOrganizationId();
		testPersonId = crm.createPerson(testOrgId, CHLOE, MAILING_ADDRESS, WORK_COMMUNICATIONS, BUSINESS_POSITION).getPersonId();
	}
	
	@Test
	public void testCreateUser() throws Exception {
		// Get the initial list of groups to make sure they are blank
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/users")
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
		assertEquals("User", json.getArray("content").getObject(0).getString("@type"));
		assertEquals(systemUserId.toString(), json.getArray("content").getObject(0).getString("userId"));
		assertEquals("system", json.getArray("content").getObject(0).getString("username"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(0).getObject("person").keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(0).getObject("person").getString("@type"));
		assertEquals(systemPersonId.toString(), json.getArray("content").getObject(0).getObject("person").getString("personId"));
		assertEquals(systemOrgId.toString(), json.getArray("content").getObject(0).getObject("person").getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(0).getObject("person").getString("status"));
		assertEquals("Bacon, Chris P", json.getArray("content").getObject(0).getObject("person").getString("displayName"));
		assertEquals("Active", json.getArray("content").getObject(0).getString("status"));
		assertEquals(4, json.getArray("content").getObject(0).getArray("roles").size());
		assertEquals("SYS_ADMIN", json.getArray("content").getObject(0).getArray("roles").getString(0));
		assertEquals("SYS_ACTUATOR", json.getArray("content").getObject(0).getArray("roles").getString(1));
		assertEquals("SYS_ACCESS", json.getArray("content").getObject(0).getArray("roles").getString(2));
		assertEquals("CRM_ADMIN", json.getArray("content").getObject(0).getArray("roles").getString(3));
		
		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.post("/api/users")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("personId", testPersonId.toString())
				.with("username", "bob")
				.with("roles", List.of("ORG_ADMIN", "CRM_ADMIN"))
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("@type", "userId", "username", "person", "status", "roles"), json.keys());
		assertEquals("User", json.getString("@type"));
		assertEquals("bob", json.getString("username"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getObject("person").keys());
		assertEquals("PersonSummary", json.getObject("person").getString("@type"));
		assertEquals(testPersonId.toString(), json.getObject("person").getString("personId"));
		assertEquals(testOrgId.toString(), json.getObject("person").getString("organizationId"));
		assertEquals("Active", json.getObject("person").getString("status"));
		assertEquals("LaRue, Chloé", json.getObject("person").getString("displayName"));
		assertEquals("Active", json.getString("status"));
		assertEquals(2, json.getArray("roles").size());
		assertEquals("ORG_ADMIN", json.getArray("roles").getString(0));
		assertEquals("CRM_ADMIN", json.getArray("roles").getString(1));
		
		Identifier userId = new Identifier(json.getString("userId"));
		
		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/users/" + userId)
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("@type", "userId", "username", "person", "status", "roles"), json.keys());
		assertEquals("User", json.getString("@type"));
		assertEquals(userId.toString(), json.getString("userId"));
		assertEquals("bob", json.getString("username"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getObject("person").keys());
		assertEquals("PersonSummary", json.getObject("person").getString("@type"));
		assertEquals(testPersonId.toString(), json.getObject("person").getString("personId"));
		assertEquals(testOrgId.toString(), json.getObject("person").getString("organizationId"));
		assertEquals("Active", json.getObject("person").getString("status"));
		assertEquals("LaRue, Chloé", json.getObject("person").getString("displayName"));
		assertEquals("Active", json.getString("status"));
		assertEquals(2, json.getArray("roles").size());
		assertEquals("ORG_ADMIN", json.getArray("roles").getString(0));
		assertEquals("CRM_ADMIN", json.getArray("roles").getString(1));
		
		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/users/" + userId)
			.header("Locale", Lang.FRENCH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("@type", "userId", "username", "person", "status", "roles"), json.keys());
		assertEquals("User", json.getString("@type"));
		assertEquals(userId.toString(), json.getString("userId"));
		assertEquals("bob", json.getString("username"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getObject("person").keys());
		assertEquals("PersonSummary", json.getObject("person").getString("@type"));
		assertEquals(testPersonId.toString(), json.getObject("person").getString("personId"));
		assertEquals(testOrgId.toString(), json.getObject("person").getString("organizationId"));
		assertEquals("Actif", json.getObject("person").getString("status"));
		assertEquals("LaRue, Chloé", json.getObject("person").getString("displayName"));
		assertEquals("Actif", json.getString("status"));
		assertEquals(2, json.getArray("roles").size());
		assertEquals("ORG_ADMIN", json.getArray("roles").getString(0));
		assertEquals("CRM_ADMIN", json.getArray("roles").getString(1));
		
		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/users/" + userId))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("@type", "userId", "username", "person", "status", "roles"), json.keys());
		assertEquals("User", json.getString("@type"));
		assertEquals(userId.toString(), json.getString("userId"));
		assertEquals("bob", json.getString("username"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getObject("person").keys());
		assertEquals("PersonSummary", json.getObject("person").getString("@type"));
		assertEquals(testPersonId.toString(), json.getObject("person").getString("personId"));
		assertEquals(testOrgId.toString(), json.getObject("person").getString("organizationId"));
		assertEquals("active", json.getObject("person").getString("status"));
		assertEquals("LaRue, Chloé", json.getObject("person").getString("displayName"));
		assertEquals("active", json.getString("status"));
		assertEquals(2, json.getArray("roles").size());
		assertEquals("ORG_ADMIN", json.getArray("roles").getString(0));
		assertEquals("CRM_ADMIN", json.getArray("roles").getString(1));

		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/users")
			.header("Locale", Lang.ENGLISH))
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
		assertEquals(List.of("@type", "userId", "username", "person", "status", "roles"), json.getArray("content").getObject(0).keys());
		assertEquals("User", json.getArray("content").getObject(0).getString("@type"));
		assertEquals(userId.toString(), json.getArray("content").getObject(0).getString("userId"));
		assertEquals("bob", json.getArray("content").getObject(0).getString("username"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(0).getObject("person").keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(0).getObject("person").getString("@type"));
		assertEquals(testPersonId.toString(), json.getArray("content").getObject(0).getObject("person").getString("personId"));
		assertEquals(testOrgId.toString(), json.getArray("content").getObject(0).getObject("person").getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(0).getObject("person").getString("status"));
		assertEquals("LaRue, Chloé", json.getArray("content").getObject(0).getObject("person").getString("displayName"));
		assertEquals("Active", json.getArray("content").getObject(0).getString("status"));
		assertEquals(2, json.getArray("content").getObject(0).getArray("roles").size());
		assertEquals("ORG_ADMIN", json.getArray("content").getObject(0).getArray("roles").getString(0));
		assertEquals("CRM_ADMIN", json.getArray("content").getObject(0).getArray("roles").getString(1));
		assertEquals(List.of("@type", "userId", "username", "person", "status", "roles"), json.getArray("content").getObject(1).keys());
		assertEquals("User", json.getArray("content").getObject(1).getString("@type"));
		assertEquals(systemUserId.toString(), json.getArray("content").getObject(1).getString("userId"));
		assertEquals("system", json.getArray("content").getObject(1).getString("username"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(1).getObject("person").keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(1).getObject("person").getString("@type"));
		assertEquals(systemPersonId.toString(), json.getArray("content").getObject(1).getObject("person").getString("personId"));
		assertEquals(systemOrgId.toString(), json.getArray("content").getObject(1).getObject("person").getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(1).getObject("person").getString("status"));
		assertEquals("Bacon, Chris P", json.getArray("content").getObject(1).getObject("person").getString("displayName"));
		assertEquals("Active", json.getArray("content").getObject(1).getString("status"));
		assertEquals(4, json.getArray("content").getObject(1).getArray("roles").size());
		assertEquals("SYS_ADMIN", json.getArray("content").getObject(1).getArray("roles").getString(0));
		assertEquals("SYS_ACTUATOR", json.getArray("content").getObject(1).getArray("roles").getString(1));
		assertEquals("SYS_ACCESS", json.getArray("content").getObject(1).getArray("roles").getString(2));
		assertEquals("CRM_ADMIN", json.getArray("content").getObject(1).getArray("roles").getString(3));
	}
	
	@Test
	public void testGetUser() throws Exception {
		Identifier userId = crm.createUser(testPersonId, "chloe", List.of("ORG_ADMIN", "CRM_ADMIN")).getUserId();
		
		JsonObject data = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/api/users/" + userId)
				.header("Locale", Lang.ROOT))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(data, "data");
		assertEquals(List.of("@type", "userId", "username", "person", "status", "roles"), data.keys());
		assertEquals("User", data.getString("@type"));
		assertEquals(userId.toString(), data.getString("userId"));
		assertEquals("chloe", data.getString("username"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), data.getObject("person").keys());
		assertEquals("PersonSummary", data.getObject("person").getString("@type"));
		assertEquals(testPersonId.toString(), data.getObject("person").getString("personId"));
		assertEquals(testOrgId.toString(), data.getObject("person").getString("organizationId"));
		assertEquals("active", data.getObject("person").getString("status"));
		assertEquals("LaRue, Chloé", data.getObject("person").getString("displayName"));
		assertEquals("active", data.getString("status"));
		assertEquals(2, data.getArray("roles").size());
		assertEquals("ORG_ADMIN", data.getArray("roles").getString(0));
		assertEquals("CRM_ADMIN", data.getArray("roles").getString(1));
		
		assertEquals(data, new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/api/user/chloe")
				.header("Locale", Lang.ROOT))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString()));
		
		JsonObject english = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/api/users/" + userId)
				.header("Locale", Lang.ENGLISH))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("@type", "userId", "username", "person", "status", "roles"), english.keys());
		assertEquals("User", english.getString("@type"));
		assertEquals(userId.toString(), english.getString("userId"));
		assertEquals("chloe", english.getString("username"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), english.getObject("person").keys());
		assertEquals("PersonSummary", english.getObject("person").getString("@type"));
		assertEquals(testPersonId.toString(), english.getObject("person").getString("personId"));
		assertEquals(testOrgId.toString(), english.getObject("person").getString("organizationId"));
		assertEquals("Active", english.getObject("person").getString("status"));
		assertEquals("LaRue, Chloé", english.getObject("person").getString("displayName"));
		assertEquals("Active", english.getString("status"));
		assertEquals(2, english.getArray("roles").size());
		assertEquals("ORG_ADMIN", english.getArray("roles").getString(0));
		assertEquals("CRM_ADMIN", english.getArray("roles").getString(1));
		
		assertEquals(english, new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/api/user/chloe")
				.header("Locale", Lang.ENGLISH))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString()));
		
		JsonObject french = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/api/users/" + userId)
				.header("Locale", Lang.FRENCH))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("@type", "userId", "username", "person", "status", "roles"), french.keys());
		assertEquals("User", french.getString("@type"));
		assertEquals(userId.toString(), french.getString("userId"));
		assertEquals("chloe", french.getString("username"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), french.getObject("person").keys());
		assertEquals("PersonSummary", french.getObject("person").getString("@type"));
		assertEquals(testPersonId.toString(), french.getObject("person").getString("personId"));
		assertEquals(testOrgId.toString(), french.getObject("person").getString("organizationId"));
		assertEquals("Actif", french.getObject("person").getString("status"));
		assertEquals("LaRue, Chloé", french.getObject("person").getString("displayName"));
		assertEquals("Actif", french.getString("status"));
		assertEquals(2, french.getArray("roles").size());
		assertEquals("ORG_ADMIN", french.getArray("roles").getString(0));
		assertEquals("CRM_ADMIN", french.getArray("roles").getString(1));
		
		assertEquals(french, new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/api/user/chloe")
				.header("Locale", Lang.FRENCH))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString()));
	}
	
	@Test
	public void testGetUserPerson() throws Exception {
		Identifier userId = crm.createUser(testPersonId, "chloe", List.of("ORG_ADMIN", "CRM_ADMIN")).getUserId();
		
		JsonObject data = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/api/users/" + userId + "/person")
				.header("Locale", Lang.ROOT))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(data, "data");
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "position"), data.keys());
		assertEquals("PersonDetails", data.getString("@type"));
		assertEquals(testPersonId.toString(), data.getString("personId"));
		assertEquals(testOrgId.toString(), data.getString("organizationId"));
		assertEquals("active", data.getString("status"));
		assertEquals("LaRue, Chloé", data.getString("displayName"));
		assertEquals(List.of("@type", "firstName", "lastName"), data.getObject("legalName").keys());
		assertEquals("PersonName", data.getObject("legalName").getString("@type"));
		assertEquals("Chloé", data.getObject("legalName").getString("firstName"));
		assertEquals("LaRue", data.getObject("legalName").getString("lastName"));
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), data.getObject("address").keys());
		assertEquals("MailingAddress", data.getObject("address").getString("@type"));
		assertEquals("123 Main St", data.getObject("address").getString("street"));
		assertEquals("Ottawa", data.getObject("address").getString("city"));
		assertEquals("QC", data.getObject("address").getString("province"));
		assertEquals("CA", data.getObject("address").getString("country"));
		assertEquals("K1K1K1", data.getObject("address").getString("postalCode"));
		assertEquals(List.of("@type", "jobTitle", "language", "email", "homePhone", "faxNumber"), data.getObject("communication").keys());
		assertEquals("Communication", data.getObject("communication").getString("@type"));
		assertEquals("Developer", data.getObject("communication").getString("jobTitle"));
		assertEquals("EN", data.getObject("communication").getString("language"));
		assertEquals("user@work.ca", data.getObject("communication").getString("email"));
		assertEquals(List.of("@type", "number", "extension"), data.getObject("communication").getObject("homePhone").keys());
		assertEquals("Telephone", data.getObject("communication").getObject("homePhone").getString("@type"));
		assertEquals("5551234567", data.getObject("communication").getObject("homePhone").getString("number"));
		assertEquals("42", data.getObject("communication").getObject("homePhone").getString("extension"));
		assertEquals("8881234567", data.getObject("communication").getString("faxNumber"));
		assertEquals(List.of("@type", "sector", "unit", "classification"), data.getObject("position").keys());
		assertEquals("BusinessPosition", data.getObject("position").getString("@type"));
		assertEquals("1", data.getObject("position").getString("sector"));
		assertEquals("1", data.getObject("position").getString("unit"));
		assertEquals("1", data.getObject("position").getString("classification"));
		
		assertEquals(data, new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/api/user/chloe/person")
				.header("Locale", Lang.ROOT))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString()));
		
		JsonObject english = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/api/users/" + userId + "/person")
				.header("Locale", Lang.ENGLISH))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "position"), english.keys());
		assertEquals("PersonDetails", english.getString("@type"));
		assertEquals(testPersonId.toString(), english.getString("personId"));
		assertEquals(testOrgId.toString(), english.getString("organizationId"));
		assertEquals("Active", english.getString("status"));
		assertEquals("LaRue, Chloé", english.getString("displayName"));
		assertEquals(List.of("@type", "firstName", "lastName"), english.getObject("legalName").keys());
		assertEquals("PersonName", english.getObject("legalName").getString("@type"));
		assertEquals("Chloé", english.getObject("legalName").getString("firstName"));
		assertEquals("LaRue", english.getObject("legalName").getString("lastName"));
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), english.getObject("address").keys());
		assertEquals("MailingAddress", english.getObject("address").getString("@type"));
		assertEquals("123 Main St", english.getObject("address").getString("street"));
		assertEquals("Ottawa", english.getObject("address").getString("city"));
		assertEquals("Quebec", english.getObject("address").getString("province"));
		assertEquals("Canada", english.getObject("address").getString("country"));
		assertEquals("K1K1K1", english.getObject("address").getString("postalCode"));
		assertEquals(List.of("@type", "jobTitle", "language", "email", "homePhone", "faxNumber"), english.getObject("communication").keys());
		assertEquals("Communication", english.getObject("communication").getString("@type"));
		assertEquals("Developer", english.getObject("communication").getString("jobTitle"));
		assertEquals("English", english.getObject("communication").getString("language"));
		assertEquals("user@work.ca", english.getObject("communication").getString("email"));
		assertEquals(List.of("@type", "number", "extension"), english.getObject("communication").getObject("homePhone").keys());
		assertEquals("Telephone", english.getObject("communication").getObject("homePhone").getString("@type"));
		assertEquals("5551234567", english.getObject("communication").getObject("homePhone").getString("number"));
		assertEquals("42", english.getObject("communication").getObject("homePhone").getString("extension"));
		assertEquals("8881234567", english.getObject("communication").getString("faxNumber"));
		assertEquals(List.of("@type", "sector", "unit", "classification"), english.getObject("position").keys());
		assertEquals("BusinessPosition", english.getObject("position").getString("@type"));
		assertEquals("External", english.getObject("position").getString("sector"));
		assertEquals("Solutions", english.getObject("position").getString("unit"));
		assertEquals("Developer", english.getObject("position").getString("classification"));
		
		assertEquals(english, new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/api/user/chloe/person")
				.header("Locale", Lang.ENGLISH))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString()));
		
		JsonObject french = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/api/users/" + userId + "/person")
				.header("Locale", Lang.FRENCH))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "position"), french.keys());
		assertEquals("PersonDetails", french.getString("@type"));
		assertEquals(testPersonId.toString(), french.getString("personId"));
		assertEquals(testOrgId.toString(), french.getString("organizationId"));
		assertEquals("Actif", french.getString("status"));
		assertEquals("LaRue, Chloé", french.getString("displayName"));
		assertEquals(List.of("@type", "firstName", "lastName"), french.getObject("legalName").keys());
		assertEquals("PersonName", french.getObject("legalName").getString("@type"));
		assertEquals("Chloé", french.getObject("legalName").getString("firstName"));
		assertEquals("LaRue", french.getObject("legalName").getString("lastName"));
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), french.getObject("address").keys());
		assertEquals("MailingAddress", french.getObject("address").getString("@type"));
		assertEquals("123 Main St", french.getObject("address").getString("street"));
		assertEquals("Ottawa", french.getObject("address").getString("city"));
		assertEquals("Québec", french.getObject("address").getString("province"));
		assertEquals("Canada", french.getObject("address").getString("country"));
		assertEquals("K1K1K1", french.getObject("address").getString("postalCode"));
		assertEquals(List.of("@type", "jobTitle", "language", "email", "homePhone", "faxNumber"), french.getObject("communication").keys());
		assertEquals("Communication", french.getObject("communication").getString("@type"));
		assertEquals("Developer", french.getObject("communication").getString("jobTitle"));
		assertEquals("Anglais", french.getObject("communication").getString("language"));
		assertEquals("user@work.ca", french.getObject("communication").getString("email"));
		assertEquals(List.of("@type", "number", "extension"), french.getObject("communication").getObject("homePhone").keys());
		assertEquals("Telephone", french.getObject("communication").getObject("homePhone").getString("@type"));
		assertEquals("5551234567", french.getObject("communication").getObject("homePhone").getString("number"));
		assertEquals("42", french.getObject("communication").getObject("homePhone").getString("extension"));
		assertEquals("8881234567", french.getObject("communication").getString("faxNumber"));
		assertEquals(List.of("@type", "sector", "unit", "classification"), french.getObject("position").keys());
		assertEquals("BusinessPosition", french.getObject("position").getString("@type"));
		assertEquals("External", french.getObject("position").getString("sector"));
		assertEquals("Solutions", french.getObject("position").getString("unit"));
		assertEquals("Développeur", french.getObject("position").getString("classification"));
		
		assertEquals(french, new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/api/user/chloe/person")
				.header("Locale", Lang.FRENCH))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString()));
	}
	
	@Test
	public void testUpdatingRoles() throws Exception {
		Identifier userId = crm.createUser(testPersonId, "chloe", List.of("ORG_ADMIN", "CRM_ADMIN")).getUserId();
		
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.patch("/api/users/" + userId)
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("roles", List.of("ORG_USER", "SYS_ADMIN"))
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("@type", "userId", "username", "person", "status", "roles"), json.keys());
		assertEquals("User", json.getString("@type"));
		assertEquals(userId.toString(), json.getString("userId"));
		assertEquals("chloe", json.getString("username"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getObject("person").keys());
		assertEquals("PersonSummary", json.getObject("person").getString("@type"));
		assertEquals(testPersonId.toString(), json.getObject("person").getString("personId"));
		assertEquals(testOrgId.toString(), json.getObject("person").getString("organizationId"));
		assertEquals("Active", json.getObject("person").getString("status"));
		assertEquals("LaRue, Chloé", json.getObject("person").getString("displayName"));
		assertEquals("Active", json.getString("status"));
		assertEquals(2, json.getArray("roles").size());
		assertEquals("ORG_USER", json.getArray("roles").getString(0));
		assertEquals("SYS_ADMIN", json.getArray("roles").getString(1));
	}

	@Test
	public void testEnableDisablePerson() throws Exception {
		Identifier userId = crm.createUser(testPersonId, "chloe", List.of("ORG_ADMIN", "CRM_ADMIN")).getUserId();

		assertEquals(Status.ACTIVE, crm.findUser(userId).getStatus());

		JsonArray error1 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/api/users/" + userId + "/disable")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(userId.toString(), error1.getObject(0).getString("identifier"));
		assertEquals("error", error1.getObject(0).getString("type"));
		assertEquals("confirm", error1.getObject(0).getString("path"));
		assertEquals("You must send in the confirmation message", error1.getObject(0).getString("reason"));
		assertEquals(Status.ACTIVE, crm.findUser(userId).getStatus());

		JsonArray error2 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/api/users/" + userId + "/disable")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("confirm", false)
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(userId.toString(), error2.getObject(0).getString("identifier"));
		assertEquals("error", error2.getObject(0).getString("type"));
		assertEquals("confirm", error2.getObject(0).getString("path"));
		assertEquals("You must send in the confirmation message", error2.getObject(0).getString("reason"));
		assertEquals(Status.ACTIVE, crm.findUser(userId).getStatus());

		JsonArray error3 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/api/users/" + userId + "/disable")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("confirm", "Test")
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(userId.toString(), error3.getObject(0).getString("identifier"));
		assertEquals("error", error3.getObject(0).getString("type"));
		assertEquals("confirm", error3.getObject(0).getString("path"));
		assertEquals("Confirmation message must be a boolean", error3.getObject(0).getString("reason"));
		assertEquals(Status.ACTIVE, crm.findUser(userId).getStatus());

		JsonObject disable = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.put("/api/users/" + userId + "/disable")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("confirm", true)
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(disable, "disable");
		assertEquals(List.of("@type", "userId", "username", "person", "status", "roles"), disable.keys());
		assertEquals("User", disable.getString("@type"));
		assertEquals(userId.toString(), disable.getString("userId"));
		assertEquals("chloe", disable.getString("username"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), disable.getObject("person").keys());
		assertEquals("PersonSummary", disable.getObject("person").getString("@type"));
		assertEquals(testPersonId.toString(), disable.getObject("person").getString("personId"));
		assertEquals(testOrgId.toString(), disable.getObject("person").getString("organizationId"));
		assertEquals("Active", disable.getObject("person").getString("status"));
		assertEquals("LaRue, Chloé", disable.getObject("person").getString("displayName"));
		assertEquals("Inactive", disable.getString("status"));
		assertEquals(2, disable.getArray("roles").size());
		assertEquals("ORG_ADMIN", disable.getArray("roles").getString(0));
		assertEquals("CRM_ADMIN", disable.getArray("roles").getString(1));
		
		JsonArray error4 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/api/users/" + userId + "/enable")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(userId.toString(), error4.getObject(0).getString("identifier"));
		assertEquals("error", error4.getObject(0).getString("type"));
		assertEquals("confirm", error4.getObject(0).getString("path"));
		assertEquals("You must send in the confirmation message", error4.getObject(0).getString("reason"));
		assertEquals(Status.INACTIVE, crm.findUser(userId).getStatus());
		
		JsonArray error5 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/api/users/" + userId + "/enable")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("confirm", false)
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(userId.toString(), error5.getObject(0).getString("identifier"));
		assertEquals("error", error5.getObject(0).getString("type"));
		assertEquals("confirm", error5.getObject(0).getString("path"));
		assertEquals("You must send in the confirmation message", error5.getObject(0).getString("reason"));
		assertEquals(Status.INACTIVE, crm.findUser(userId).getStatus());
		
		JsonArray error6 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/api/users/" + userId + "/enable")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("confirm", "test")
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(userId.toString(), error6.getObject(0).getString("identifier"));
		assertEquals("error", error6.getObject(0).getString("type"));
		assertEquals("confirm", error6.getObject(0).getString("path"));
		assertEquals("Confirmation message must be a boolean", error6.getObject(0).getString("reason"));
		assertEquals(Status.INACTIVE, crm.findUser(userId).getStatus());
	
		JsonObject enable = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.put("/api/users/" + userId + "/enable")
			.header("Locale", Lang.FRENCH)
			.content(new JsonObject()
				.with("confirm", true)
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(enable, "enable");
		assertEquals(List.of("@type", "userId", "username", "person", "status", "roles"), enable.keys());
		assertEquals("User", enable.getString("@type"));
		assertEquals(userId.toString(), enable.getString("userId"));
		assertEquals("chloe", enable.getString("username"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), enable.getObject("person").keys());
		assertEquals("PersonSummary", enable.getObject("person").getString("@type"));
		assertEquals(testPersonId.toString(), enable.getObject("person").getString("personId"));
		assertEquals(testOrgId.toString(), enable.getObject("person").getString("organizationId"));
		assertEquals("Actif", enable.getObject("person").getString("status"));
		assertEquals("LaRue, Chloé", enable.getObject("person").getString("displayName"));
		assertEquals("Actif", enable.getString("status"));
		assertEquals(2, enable.getArray("roles").size());
		assertEquals("ORG_ADMIN", enable.getArray("roles").getString(0));
		assertEquals("CRM_ADMIN", enable.getArray("roles").getString(1));
		
		assertEquals(Status.ACTIVE, crm.findUser(userId).getStatus());
		
		mockMvc.perform(MockMvcRequestBuilders
			.put("/api/user/chloe/disable")
			.header("Locale", Lang.FRENCH)
			.content(new JsonObject()
				.with("confirm", true)
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString();
		
		assertEquals(Status.INACTIVE, crm.findUser(userId).getStatus());

		mockMvc.perform(MockMvcRequestBuilders
				.put("/api/user/chloe/enable")
				.header("Locale", Lang.FRENCH)
				.content(new JsonObject()
					.with("confirm", true)
					.toString()))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString();
		
		assertEquals(Status.ACTIVE, crm.findUser(userId).getStatus());

	}
	
//	@Test
//	public void testPersonWithLongName() throws Exception {
//		JsonArray json = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
//			.post("/api/users")
//			.header("Locale", Lang.ENGLISH)
//			.content(new JsonObject()
//				.with("displayName", LoremIpsumGenerator.buildWords(20))
//				.with("groups", List.of("ORG"))
//				.toString()))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().isBadRequest())
//			.andReturn().getResponse().getContentAsString());
//		assertSingleJsonMessage(json, null, "error", "displayName", "Display name must be 60 characters or less");
//	}
//
//	@Test
//	public void testPersonWithNoName() throws Exception {
//		JsonArray json = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
//			.post("/api/users")
//			.header("Locale", Lang.ENGLISH)
//			.content(new JsonObject()
//				.with("groups", List.of("ORG"))
//				.toString()))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().isBadRequest())
//			.andReturn().getResponse().getContentAsString());
//		assertSingleJsonMessage(json, null, "error", "displayName", "Field is mandatory");
//	}
	
}
