package ca.magex.crm.restful.controllers;

import static ca.magex.crm.test.CrmAsserts.BUSINESS_POSITION;
import static ca.magex.crm.test.CrmAsserts.COMMUNICATIONS;
import static ca.magex.crm.test.CrmAsserts.MAILING_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.ORG_NAME;
import static ca.magex.crm.test.CrmAsserts.PERSON_NAME;
import static ca.magex.crm.test.CrmAsserts.SYS_ADMIN;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Status;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonObject;

public class OrganizationsControllerTests extends AbstractControllerTests {
	
	@Before
	public void setup() {
		initialize();
	}
	
	@Test
	public void testCreateOrganization() throws Exception {
		// Get the initial list of groups to make sure they are blank
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/organizations")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(1, json.getInt("page"));
		assertEquals(1, json.getInt("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(JsonArray.class, json.get("content").getClass());
		assertEquals(1, json.getArray("content").size());
		assertEquals("System Administrator", json.getArray("content").getObject(0).getString("displayName"));
		
		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.post("/api/organizations")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("displayName", ORG_NAME.getEnglishName())
				.with("groups", List.of("ORG"))
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(List.of("@type", "organizationId", "status", "displayName", "groups"), json.keys());
		assertEquals("OrganizationDetails", json.getString("@type"));
		assertTrue(json.getString("organizationId").matches("[A-Za-z0-9]+"));
		assertEquals("Active", json.getString("status"));
		assertEquals(ORG_NAME.getEnglishName(), json.getString("displayName"));
		assertEquals(new JsonArray().with("ORG"), json.getArray("groups"));
		Identifier organizationId = new Identifier(json.getString("organizationId"));
		
		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/organizations/" + organizationId)
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(List.of("@type", "organizationId", "status", "displayName", "groups"), json.keys());
		assertEquals("OrganizationDetails", json.getString("@type"));
		assertEquals(organizationId.toString(), json.getString("organizationId"));
		assertEquals("Active", json.getString("status"));
		assertEquals(ORG_NAME.getEnglishName(), json.getString("displayName"));
		assertEquals(new JsonArray().with("ORG"), json.getArray("groups"));

		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/organizations/" + organizationId)
			.header("Locale", Lang.FRENCH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(List.of("@type", "organizationId", "status", "displayName", "groups"), json.keys());
		assertEquals("OrganizationDetails", json.getString("@type"));
		assertEquals(organizationId.toString(), json.getString("organizationId"));
		assertEquals("Actif", json.getString("status"));
		assertEquals(ORG_NAME.getEnglishName(), json.getString("displayName"));
		assertEquals(new JsonArray().with("ORG"), json.getArray("groups"));

		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/organizations/" + organizationId))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(List.of("@type", "organizationId", "status", "displayName", "groups"), json.keys());
		assertEquals("OrganizationDetails", json.getString("@type"));
		assertEquals(organizationId.toString(), json.getString("organizationId"));
		assertEquals("active", json.getString("status"));
		assertEquals(ORG_NAME.getEnglishName(), json.getString("displayName"));
		assertEquals(new JsonArray().with("ORG"), json.getArray("groups"));

		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/organizations")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		
		assertEquals(1, json.getInt("page"));
		assertEquals(2, json.getInt("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(JsonArray.class, json.get("content").getClass());
		assertEquals(2, json.getArray("content").size());
		
		json.getArray("content").values().forEach(el -> {
			assertEquals(JsonObject.class, el.getClass());
			assertEquals(List.of("@type", "organizationId", "status", "displayName"), ((JsonObject)el).keys());
		});
		
		assertEquals(organizationId.toString(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(0).getString("status"));
		assertEquals(ORG_NAME.getEnglishName(), json.getArray("content").getObject(0).getString("displayName"));

		assertEquals("Active", json.getArray("content").getObject(1).getString("status"));
		assertEquals(SYS_ADMIN.getEnglishName(), json.getArray("content").getObject(1).getString("displayName"));
	}
	
	@Test
	public void testGetOrganizationSummary() throws Exception {
		Identifier organizationId = organizations.createOrganization(ORG_NAME.getEnglishName(), List.of("ORG")).getOrganizationId();
		Identifier locationId = locations.createLocation(organizationId, "Main Location", "MAIN", MAILING_ADDRESS).getLocationId();
		Identifier personId = persons.createPerson(organizationId, PERSON_NAME, MAILING_ADDRESS, COMMUNICATIONS, BUSINESS_POSITION).getPersonId();
		organizations.updateOrganizationMainLocation(organizationId, locationId);
		organizations.updateOrganizationMainContact(organizationId, personId);
		
		JsonObject data = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/api/organizations/" + organizationId + "/summary")
				.header("Locale", Lang.ROOT))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString());
		assertEquals(List.of("@type", "organizationId", "status", "displayName"), data.keys());
		assertEquals("OrganizationSummary", data.getString("@type"));
		assertTrue(data.getString("organizationId").matches("[A-Za-z0-9]+"));
		assertEquals("active", data.getString("status"));
		assertEquals(ORG_NAME.getEnglishName(), data.getString("displayName"));
		
		JsonObject english = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/api/organizations/" + organizationId + "/summary")
				.header("Locale", Lang.ENGLISH))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString());
		assertEquals(List.of("@type", "organizationId", "status", "displayName"), english.keys());
		assertEquals("OrganizationSummary", english.getString("@type"));
		assertTrue(english.getString("organizationId").matches("[A-Za-z0-9]+"));
		assertEquals("Active", english.getString("status"));
		assertEquals(ORG_NAME.getEnglishName(), english.getString("displayName"));
		
		JsonObject french = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/api/organizations/" + organizationId + "/summary")
				.header("Locale", Lang.FRENCH))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString());
		assertEquals(List.of("@type", "organizationId", "status", "displayName"), french.keys());
		assertEquals("OrganizationSummary", french.getString("@type"));
		assertTrue(french.getString("organizationId").matches("[A-Za-z0-9]+"));
		assertEquals("Actif", french.getString("status"));
		assertEquals(ORG_NAME.getEnglishName(), french.getString("displayName"));
	}
	
	@Test
	public void testGetMainLocation() throws Exception {
		Identifier organizationId = organizations.createOrganization(ORG_NAME.getEnglishName(), List.of("ORG")).getOrganizationId();
		Identifier locationId = locations.createLocation(organizationId, "Main Location", "MAIN", MAILING_ADDRESS.withProvince("NL")).getLocationId();
		Identifier personId = persons.createPerson(organizationId, PERSON_NAME, MAILING_ADDRESS, COMMUNICATIONS, BUSINESS_POSITION).getPersonId();
		organizations.updateOrganizationMainLocation(organizationId, locationId);
		organizations.updateOrganizationMainContact(organizationId, personId);
		
		JsonObject data = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/api/organizations/" + organizationId + "/mainLocation")
				.header("Locale", Lang.ROOT))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString());
		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName", "address"), data.keys());
		assertEquals("LocationDetails", data.getString("@type"));
		assertEquals(locationId.toString(), data.getString("locationId"));
		assertEquals(organizationId.toString(), data.getString("organizationId"));
		assertEquals("active", data.getString("status"));
		assertEquals("Main Location", data.getString("displayName"));
		assertEquals("MAIN", data.getString("reference"));
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), data.getObject("address").keys());
		assertEquals("MailingAddress", data.getObject("address").getString("@type"));
		assertEquals(MAILING_ADDRESS.getStreet(), data.getObject("address").getString("street"));
		assertEquals(MAILING_ADDRESS.getCity(), data.getObject("address").getString("city"));
		assertEquals("NL", data.getObject("address").getString("province"));
		assertEquals("CA", data.getObject("address").getString("country"));
		assertEquals(MAILING_ADDRESS.getPostalCode(), data.getObject("address").getString("postalCode"));

		JsonObject english = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/api/organizations/" + organizationId + "/mainLocation")
				.header("Locale", Lang.ENGLISH))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString());
		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName", "address"), english.keys());
		assertEquals("LocationDetails", english.getString("@type"));
		assertEquals(locationId.toString(), english.getString("locationId"));
		assertEquals(organizationId.toString(), english.getString("organizationId"));
		assertEquals("Active", english.getString("status"));
		assertEquals("Main Location", english.getString("displayName"));
		assertEquals("MAIN", english.getString("reference"));
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), english.getObject("address").keys());
		assertEquals("MailingAddress", data.getObject("address").getString("@type"));
		assertEquals(MAILING_ADDRESS.getStreet(), english.getObject("address").getString("street"));
		assertEquals(MAILING_ADDRESS.getCity(), english.getObject("address").getString("city"));
		assertEquals("Newfoundland and Labrador", english.getObject("address").getString("province"));
		assertEquals("Canada", english.getObject("address").getString("country"));
		assertEquals(MAILING_ADDRESS.getPostalCode(), english.getObject("address").getString("postalCode"));
		
		JsonObject french = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/api/organizations/" + organizationId + "/mainLocation")
				.header("Locale", Lang.FRENCH))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString());
		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName", "address"), french.keys());
		assertEquals("LocationDetails", french.getString("@type"));
		assertEquals(locationId.toString(), french.getString("locationId"));
		assertEquals(organizationId.toString(), french.getString("organizationId"));
		assertEquals("Actif", french.getString("status"));
		assertEquals("Main Location", french.getString("displayName"));
		assertEquals("MAIN", french.getString("reference"));
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), french.getObject("address").keys());
		assertEquals("MailingAddress", french.getObject("address").getString("@type"));
		assertEquals(MAILING_ADDRESS.getStreet(), french.getObject("address").getString("street"));
		assertEquals(MAILING_ADDRESS.getCity(), french.getObject("address").getString("city"));
		assertEquals("Terre-Neuve et Labrador", french.getObject("address").getString("province"));
		assertEquals("Canada", french.getObject("address").getString("country"));
		assertEquals(MAILING_ADDRESS.getPostalCode(), french.getObject("address").getString("postalCode"));
	}
	
	@Test
	public void testGetMainContact() throws Exception {
		
	}
	
	@Test
	public void testUpdatingFullOrganization() throws Exception {
		Identifier organizationId = organizations.createOrganization(ORG_NAME.getEnglishName(), List.of("ORG")).getOrganizationId();
		Identifier locationId = locations.createLocation(organizationId, "Main Location", "MAIN", MAILING_ADDRESS).getLocationId();
		Identifier personId = persons.createPerson(organizationId, PERSON_NAME, MAILING_ADDRESS, COMMUNICATIONS, BUSINESS_POSITION).getPersonId();
		
		OrganizationDetails org = organizations.findOrganizationDetails(organizationId);
		assertEquals(organizationId, org.getOrganizationId());
		assertEquals(ORG_NAME.getEnglishName(), org.getDisplayName());
		assertNull(org.getMainLocationId());
		assertNull(org.getMainContactId());
		
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.patch("/api/organizations/" + organizationId)
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("displayName", "Updated name")
				.with("mainLocationId", locationId.toString())
				.with("mainContactId", personId.toString())
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		
		assertEquals(List.of("@type", "organizationId", "status", "displayName", "mainLocationId", "mainContactId", "groups"), json.keys());
		assertEquals("OrganizationDetails", json.getString("@type"));
		assertTrue(json.getString("organizationId").matches("[A-Za-z0-9]+"));
		assertEquals("Active", json.getString("status"));
		assertEquals("Updated name", json.getString("displayName"));
		assertEquals(locationId.toString(), json.getString("mainLocationId"));
		assertEquals(personId.toString(), json.getString("mainContactId"));
		assertEquals(new JsonArray().with("ORG"), json.getArray("groups"));
		
		org = organizations.findOrganizationDetails(organizationId);
		assertEquals(organizationId, org.getOrganizationId());
		assertEquals("Updated name", org.getDisplayName());
		assertEquals(locationId, org.getMainLocationId());
		assertEquals(personId, org.getMainContactId());
	}
	
	@Test
	public void testUpdatingDisplayName() throws Exception {
		Identifier organizationId = organizations.createOrganization(ORG_NAME.getEnglishName(), List.of("ORG")).getOrganizationId();
		
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.patch("/api/organizations/" + organizationId)
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("displayName", "Updated name")
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		
		assertEquals(List.of("@type", "organizationId", "status", "displayName", "groups"), json.keys());
		assertEquals("OrganizationDetails", json.getString("@type"));
		assertTrue(json.getString("organizationId").matches("[A-Za-z0-9]+"));
		assertEquals("Active", json.getString("status"));
		assertEquals("Updated name", json.getString("displayName"));
		assertEquals(new JsonArray().with("ORG"), json.getArray("groups"));
	}
	
	@Test
	public void testUpdatingMainLocation() throws Exception {
		Identifier organizationId = organizations.createOrganization(ORG_NAME.getEnglishName(), List.of("ORG")).getOrganizationId();
		Identifier locationId = locations.createLocation(organizationId, "Main Location", "MAIN", MAILING_ADDRESS).getLocationId();
		
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.patch("/api/organizations/" + organizationId)
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("mainLocationId", locationId.toString())
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		
		assertEquals(List.of("@type", "organizationId", "status", "displayName", "mainLocationId", "groups"), json.keys());
		assertEquals("OrganizationDetails", json.getString("@type"));
		assertTrue(json.getString("organizationId").matches("[A-Za-z0-9]+"));
		assertEquals("Active", json.getString("status"));
		assertEquals(ORG_NAME.getEnglishName(), json.getString("displayName"));
		assertEquals(locationId.toString(), json.getString("mainLocationId"));
		assertEquals(new JsonArray().with("ORG"), json.getArray("groups"));
	}
	
	@Test
	public void testUpdatingMainLocationAsNull() throws Exception {
		Identifier organizationId = organizations.createOrganization(ORG_NAME.getEnglishName(), List.of("ORG")).getOrganizationId();
		Identifier locationId = locations.createLocation(organizationId, "Main Location", "MAIN", MAILING_ADDRESS).getLocationId();
		organizations.updateOrganizationMainLocation(organizationId, locationId);

		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.patch("/api/organizations/" + organizationId)
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("mainLocationId", null)
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		
		assertEquals(List.of("@type", "organizationId", "status", "displayName", "groups"), json.keys());
		assertEquals("OrganizationDetails", json.getString("@type"));
		assertTrue(json.getString("organizationId").matches("[A-Za-z0-9]+"));
		assertEquals("Active", json.getString("status"));
		assertEquals(ORG_NAME.getEnglishName(), json.getString("displayName"));
		assertFalse(json.contains("mainLocationId"));
		assertEquals(new JsonArray().with("ORG"), json.getArray("groups"));
	}
	
	@Test
	public void testUpdatingMainContact() throws Exception {
		Identifier organizationId = organizations.createOrganization(ORG_NAME.getEnglishName(), List.of("ORG")).getOrganizationId();
		Identifier personId = persons.createPerson(organizationId, PERSON_NAME, MAILING_ADDRESS, COMMUNICATIONS, BUSINESS_POSITION).getPersonId();
		
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.patch("/api/organizations/" + organizationId)
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("mainContactId", personId.toString())
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		
		assertEquals(List.of("@type", "organizationId", "status", "displayName", "mainContactId", "groups"), json.keys());
		assertEquals("OrganizationDetails", json.getString("@type"));
		assertTrue(json.getString("organizationId").matches("[A-Za-z0-9]+"));
		assertEquals("Active", json.getString("status"));
		assertEquals(ORG_NAME.getEnglishName(), json.getString("displayName"));
		assertEquals(personId.toString(), json.getString("mainContactId"));
		assertEquals(new JsonArray().with("ORG"), json.getArray("groups"));
	}
	
	@Test
	public void testUpdatingMainContactAsNull() throws Exception {
		Identifier organizationId = organizations.createOrganization(ORG_NAME.getEnglishName(), List.of("ORG")).getOrganizationId();
		Identifier personId = persons.createPerson(organizationId, PERSON_NAME, MAILING_ADDRESS, COMMUNICATIONS, BUSINESS_POSITION).getPersonId();
		organizations.updateOrganizationMainContact(organizationId, personId);

		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.patch("/api/organizations/" + organizationId)
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("mainContactId", null)
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		
		assertEquals(List.of("@type", "organizationId", "status", "displayName", "groups"), json.keys());
		assertEquals("OrganizationDetails", json.getString("@type"));
		assertTrue(json.getString("organizationId").matches("[A-Za-z0-9]+"));
		assertEquals("Active", json.getString("status"));
		assertEquals(ORG_NAME.getEnglishName(), json.getString("displayName"));
		assertFalse(json.contains("mainContactId"));
		assertEquals(new JsonArray().with("ORG"), json.getArray("groups"));
	}
	
	@Test
	public void testEnableDisableOrganization() throws Exception {
		Identifier organizationId = organizations.createOrganization(ORG_NAME.getEnglishName(), List.of("ORG")).getOrganizationId();
		assertEquals(Status.ACTIVE, organizations.findOrganizationSummary(organizationId).getStatus());

		JsonArray error1 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/api/organizations/" + organizationId + "/disable")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(organizationId.toString(), error1.getObject(0).getString("identifier"));
		assertEquals("error", error1.getObject(0).getString("type"));
		assertEquals("confirm", error1.getObject(0).getString("path"));
		assertEquals("You must send in the confirmation message", error1.getObject(0).getString("reason"));
		assertEquals(Status.ACTIVE, organizations.findOrganizationSummary(organizationId).getStatus());

		JsonArray error2 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/api/organizations/" + organizationId + "/disable")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("confirm", false)
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(organizationId.toString(), error2.getObject(0).getString("identifier"));
		assertEquals("error", error2.getObject(0).getString("type"));
		assertEquals("confirm", error2.getObject(0).getString("path"));
		assertEquals("You must send in the confirmation message", error2.getObject(0).getString("reason"));
		assertEquals(Status.ACTIVE, organizations.findOrganizationSummary(organizationId).getStatus());

		JsonArray error3 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/api/organizations/" + organizationId + "/disable")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("confirm", "Test")
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(organizationId.toString(), error3.getObject(0).getString("identifier"));
		assertEquals("error", error3.getObject(0).getString("type"));
		assertEquals("confirm", error3.getObject(0).getString("path"));
		assertEquals("Confirmation message must be a boolean", error3.getObject(0).getString("reason"));
		assertEquals(Status.ACTIVE, organizations.findOrganizationSummary(organizationId).getStatus());

		JsonObject disable = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.put("/api/organizations/" + organizationId + "/disable")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("confirm", true)
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		System.out.println(disable);
		assertEquals(organizationId.toString(), disable.getString("organizationId"));
		assertEquals("Inactive", disable.getString("status"));
		assertEquals(ORG_NAME.getEnglishName(), disable.getString("displayName"));
		assertEquals(Status.INACTIVE, organizations.findOrganizationSummary(organizationId).getStatus());
		
		JsonArray error4 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/api/organizations/" + organizationId + "/enable")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(organizationId.toString(), error4.getObject(0).getString("identifier"));
		assertEquals("error", error4.getObject(0).getString("type"));
		assertEquals("confirm", error4.getObject(0).getString("path"));
		assertEquals("You must send in the confirmation message", error4.getObject(0).getString("reason"));
		assertEquals(Status.INACTIVE, organizations.findOrganizationSummary(organizationId).getStatus());
		
		JsonArray error5 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/api/organizations/" + organizationId + "/enable")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("confirm", false)
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(organizationId.toString(), error5.getObject(0).getString("identifier"));
		assertEquals("error", error5.getObject(0).getString("type"));
		assertEquals("confirm", error5.getObject(0).getString("path"));
		assertEquals("You must send in the confirmation message", error5.getObject(0).getString("reason"));
		assertEquals(Status.INACTIVE, organizations.findOrganizationSummary(organizationId).getStatus());
		
		JsonArray error6 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/api/organizations/" + organizationId + "/enable")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("confirm", "test")
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(organizationId.toString(), error6.getObject(0).getString("identifier"));
		assertEquals("error", error6.getObject(0).getString("type"));
		assertEquals("confirm", error6.getObject(0).getString("path"));
		assertEquals("Confirmation message must be a boolean", error6.getObject(0).getString("reason"));
		assertEquals(Status.INACTIVE, organizations.findOrganizationSummary(organizationId).getStatus());
	
		JsonObject enable = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.put("/api/organizations/" + organizationId + "/enable")
			.header("Locale", Lang.FRENCH)
			.content(new JsonObject()
				.with("confirm", true)
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(organizationId.toString(), enable.getString("organizationId"));
		assertEquals("Actif", enable.getString("status"));
		assertEquals(ORG_NAME.getEnglishName(), disable.getString("displayName"));
		assertEquals(Status.ACTIVE, organizations.findOrganizationSummary(organizationId).getStatus());
	}
	
}
