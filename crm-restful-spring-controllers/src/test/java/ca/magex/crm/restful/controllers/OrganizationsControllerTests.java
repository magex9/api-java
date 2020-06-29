package ca.magex.crm.restful.controllers;

import static ca.magex.crm.test.CrmAsserts.BUSINESS_POSITION;
import static ca.magex.crm.test.CrmAsserts.MAILING_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.ORG_NAME;
import static ca.magex.crm.test.CrmAsserts.PERSON_NAME;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_ORG;
import static ca.magex.crm.test.CrmAsserts.WORK_COMMUNICATIONS;
import static ca.magex.crm.test.CrmAsserts.assertSingleJsonMessage;
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
import ca.magex.json.util.LoremIpsumGenerator;

public class OrganizationsControllerTests extends AbstractControllerTests {
	
	@Before
	public void setup() {
		initialize();
	}
	
	@Test
	public void testCreateOrganization() throws Exception {
		// Get the initial list of groups to make sure they are blank
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/organizations")
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
		assertEquals("System", json.getArray("content").getObject(0).getString("displayName"));
		
		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.post("/rest/organizations")
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
			.get("/rest/organizations/" + organizationId)
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
			.get("/rest/organizations/" + organizationId)
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
			.get("/rest/organizations/" + organizationId))
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
			.get("/rest/organizations")
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
		assertEquals(SYSTEM_ORG, json.getArray("content").getObject(1).getString("displayName"));
	}
	
	@Test
	public void testGetOrganizationSummary() throws Exception {
		Identifier organizationId = crm.createOrganization(ORG_NAME.getEnglishName(), List.of("ORG")).getOrganizationId();
		Identifier locationId = crm.createLocation(organizationId, "MAIN", "Main Location", MAILING_ADDRESS).getLocationId();
		Identifier personId = crm.createPerson(organizationId, PERSON_NAME, MAILING_ADDRESS, WORK_COMMUNICATIONS, BUSINESS_POSITION).getPersonId();
		crm.updateOrganizationMainLocation(organizationId, locationId);
		crm.updateOrganizationMainContact(organizationId, personId);
		
		JsonObject data = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/rest/organizations/" + organizationId + "/summary")
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
				.get("/rest/organizations/" + organizationId + "/summary")
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
				.get("/rest/organizations/" + organizationId + "/summary")
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
		Identifier organizationId = crm.createOrganization(ORG_NAME.getEnglishName(), List.of("ORG")).getOrganizationId();
		Identifier locationId = crm.createLocation(organizationId, "MAIN", "Main Location", MAILING_ADDRESS.withProvince("NL")).getLocationId();
		Identifier personId = crm.createPerson(organizationId, PERSON_NAME, MAILING_ADDRESS, WORK_COMMUNICATIONS, BUSINESS_POSITION).getPersonId();
		crm.updateOrganizationMainLocation(organizationId, locationId);
		crm.updateOrganizationMainContact(organizationId, personId);
		
		JsonObject data = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/rest/organizations/" + organizationId + "/mainLocation")
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
				.get("/rest/organizations/" + organizationId + "/mainLocation")
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
				.get("/rest/organizations/" + organizationId + "/mainLocation")
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
		Identifier organizationId = crm.createOrganization(ORG_NAME.getEnglishName(), List.of("ORG")).getOrganizationId();
		Identifier personId = crm.createPerson(organizationId, PERSON_NAME, MAILING_ADDRESS, WORK_COMMUNICATIONS, BUSINESS_POSITION).getPersonId();
		crm.updateOrganizationMainContact(organizationId, personId);
		
		JsonObject data = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/rest/organizations/" + organizationId + "/mainContact")
				.header("Locale", Lang.ROOT))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(data, "data");
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "position"), data.keys());
		assertEquals("PersonDetails", data.getString("@type"));
		assertEquals(personId.toString(), data.getString("personId"));
		assertEquals(organizationId.toString(), data.getString("organizationId"));
		assertEquals("active", data.getString("status"));
		assertEquals("Bacon, Chris P", data.getString("displayName"));
		assertEquals(List.of("@type", "salutation", "firstName", "middleName", "lastName"), data.getObject("legalName").keys());
		assertEquals("PersonName", data.getObject("legalName").getString("@type"));
		assertEquals("3", data.getObject("legalName").getString("salutation"));
		assertEquals("Chris", data.getObject("legalName").getString("firstName"));
		assertEquals("P", data.getObject("legalName").getString("middleName"));
		assertEquals("Bacon", data.getObject("legalName").getString("lastName"));
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
		assertEquals("execs", data.getObject("position").getString("sector").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("ceo", data.getObject("position").getString("unit").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("director", data.getObject("position").getString("classification").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));

		JsonObject english = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/rest/organizations/" + organizationId + "/mainContact")
				.header("Locale", Lang.ENGLISH))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "position"), english.keys());
		assertEquals("PersonDetails", english.getString("@type"));
		assertEquals(personId.toString(), english.getString("personId"));
		assertEquals(organizationId.toString(), english.getString("organizationId"));
		assertEquals("Active", english.getString("status"));
		assertEquals("Bacon, Chris P", english.getString("displayName"));
		assertEquals(List.of("@type", "salutation", "firstName", "middleName", "lastName"), english.getObject("legalName").keys());
		assertEquals("PersonName", english.getObject("legalName").getString("@type"));
		assertEquals("Mr.", english.getObject("legalName").getString("salutation"));
		assertEquals("Chris", english.getObject("legalName").getString("firstName"));
		assertEquals("P", english.getObject("legalName").getString("middleName"));
		assertEquals("Bacon", english.getObject("legalName").getString("lastName"));
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
		assertEquals("Executives", english.getObject("position").getString("sector").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("Chief Executive Officer", english.getObject("position").getString("unit").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("Director", english.getObject("position").getString("classification").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		
		JsonObject french = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/rest/organizations/" + organizationId + "/mainContact")
				.header("Locale", Lang.FRENCH))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "position"), french.keys());
		assertEquals("PersonDetails", french.getString("@type"));
		assertEquals(personId.toString(), french.getString("personId"));
		assertEquals(organizationId.toString(), french.getString("organizationId"));
		assertEquals("Actif", french.getString("status"));
		assertEquals("Bacon, Chris P", french.getString("displayName"));
		assertEquals(List.of("@type", "salutation", "firstName", "middleName", "lastName"), french.getObject("legalName").keys());
		assertEquals("PersonName", french.getObject("legalName").getString("@type"));
		assertEquals("M.", french.getObject("legalName").getString("salutation"));
		assertEquals("Chris", french.getObject("legalName").getString("firstName"));
		assertEquals("P", french.getObject("legalName").getString("middleName"));
		assertEquals("Bacon", french.getObject("legalName").getString("lastName"));
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
		assertEquals("Cadres", french.getObject("position").getString("sector").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("Directeur Général", french.getObject("position").getString("unit").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("Réalisateur", french.getObject("position").getString("classification").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
	}
	
	@Test
	public void testUpdatingFullOrganization() throws Exception {
		Identifier organizationId = crm.createOrganization(ORG_NAME.getEnglishName(), List.of("ORG")).getOrganizationId();
		Identifier locationId = crm.createLocation(organizationId, "MAIN", "Main Location", MAILING_ADDRESS).getLocationId();
		Identifier personId = crm.createPerson(organizationId, PERSON_NAME, MAILING_ADDRESS, WORK_COMMUNICATIONS, BUSINESS_POSITION).getPersonId();
		
		OrganizationDetails org = crm.findOrganizationDetails(organizationId);
		assertEquals(organizationId, org.getOrganizationId());
		assertEquals(ORG_NAME.getEnglishName(), org.getDisplayName());
		assertNull(org.getMainLocationId());
		assertNull(org.getMainContactId());
		
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.patch("/rest/organizations/" + organizationId)
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
		
		org = crm.findOrganizationDetails(organizationId);
		assertEquals(organizationId, org.getOrganizationId());
		assertEquals("Updated name", org.getDisplayName());
		assertEquals(locationId, org.getMainLocationId());
		assertEquals(personId, org.getMainContactId());
	}
	
	@Test
	public void testUpdatingDisplayName() throws Exception {
		Identifier organizationId = crm.createOrganization(ORG_NAME.getEnglishName(), List.of("ORG")).getOrganizationId();
		
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.patch("/rest/organizations/" + organizationId)
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
		Identifier organizationId = crm.createOrganization(ORG_NAME.getEnglishName(), List.of("ORG")).getOrganizationId();
		Identifier locationId = crm.createLocation(organizationId, "MAIN", "Main Location", MAILING_ADDRESS).getLocationId();
		
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.patch("/rest/organizations/" + organizationId)
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
		Identifier organizationId = crm.createOrganization(ORG_NAME.getEnglishName(), List.of("ORG")).getOrganizationId();
		Identifier locationId = crm.createLocation(organizationId, "MAIN", "Main Location", MAILING_ADDRESS).getLocationId();
		crm.updateOrganizationMainLocation(organizationId, locationId);

		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.patch("/rest/organizations/" + organizationId)
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("mainLocationId", null)
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("@type", "organizationId", "status", "displayName", "mainLocationId", "groups"), json.keys());
		assertEquals("OrganizationDetails", json.getString("@type").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(organizationId.toString(), json.getString("organizationId"));
		assertEquals("Active", json.getString("status").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("Organization", json.getString("displayName").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(locationId.toString(), json.getString("mainLocationId"));
		assertEquals(1, json.getArray("groups").size());
		assertEquals("ORG", json.getArray("groups").getString(0).replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));

	}
	
	@Test
	public void testUpdatingMainContact() throws Exception {
		Identifier organizationId = crm.createOrganization(ORG_NAME.getEnglishName(), List.of("ORG")).getOrganizationId();
		Identifier personId = crm.createPerson(organizationId, PERSON_NAME, MAILING_ADDRESS, WORK_COMMUNICATIONS, BUSINESS_POSITION).getPersonId();
		
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.patch("/rest/organizations/" + organizationId)
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
		Identifier organizationId = crm.createOrganization(ORG_NAME.getEnglishName(), List.of("ORG")).getOrganizationId();
		Identifier personId = crm.createPerson(organizationId, PERSON_NAME, MAILING_ADDRESS, WORK_COMMUNICATIONS, BUSINESS_POSITION).getPersonId();
		crm.updateOrganizationMainContact(organizationId, personId);

		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.patch("/rest/organizations/" + organizationId)
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("mainContactId", null)
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("@type", "organizationId", "status", "displayName", "mainContactId", "groups"), json.keys());
		assertEquals("OrganizationDetails", json.getString("@type").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(organizationId.toString(), json.getString("organizationId"));
		assertEquals("Active", json.getString("status").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("Organization", json.getString("displayName").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(personId.toString(), json.getString("mainContactId"));
		assertEquals(1, json.getArray("groups").size());
		assertEquals("ORG", json.getArray("groups").getString(0).replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
	}
	
	@Test
	public void testEnableDisableOrganization() throws Exception {
		Identifier organizationId = crm.createOrganization(ORG_NAME.getEnglishName(), List.of("ORG")).getOrganizationId();
		assertEquals(Status.ACTIVE, crm.findOrganizationSummary(organizationId).getStatus());

		JsonArray error1 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/rest/organizations/" + organizationId + "/disable")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(organizationId.toString(), error1.getObject(0).getString("identifier"));
		assertEquals("error", error1.getObject(0).getString("type"));
		assertEquals("confirm", error1.getObject(0).getString("path"));
		assertEquals("You must send in the confirmation message", error1.getObject(0).getString("reason"));
		assertEquals(Status.ACTIVE, crm.findOrganizationSummary(organizationId).getStatus());

		JsonArray error2 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/rest/organizations/" + organizationId + "/disable")
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
		assertEquals(Status.ACTIVE, crm.findOrganizationSummary(organizationId).getStatus());

		JsonArray error3 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/rest/organizations/" + organizationId + "/disable")
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
		assertEquals(Status.ACTIVE, crm.findOrganizationSummary(organizationId).getStatus());

		JsonObject disable = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.put("/rest/organizations/" + organizationId + "/disable")
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
		assertEquals(Status.INACTIVE, crm.findOrganizationSummary(organizationId).getStatus());
		
		JsonArray error4 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/rest/organizations/" + organizationId + "/enable")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(organizationId.toString(), error4.getObject(0).getString("identifier"));
		assertEquals("error", error4.getObject(0).getString("type"));
		assertEquals("confirm", error4.getObject(0).getString("path"));
		assertEquals("You must send in the confirmation message", error4.getObject(0).getString("reason"));
		assertEquals(Status.INACTIVE, crm.findOrganizationSummary(organizationId).getStatus());
		
		JsonArray error5 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/rest/organizations/" + organizationId + "/enable")
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
		assertEquals(Status.INACTIVE, crm.findOrganizationSummary(organizationId).getStatus());
		
		JsonArray error6 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/rest/organizations/" + organizationId + "/enable")
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
		assertEquals(Status.INACTIVE, crm.findOrganizationSummary(organizationId).getStatus());
	
		JsonObject enable = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.put("/rest/organizations/" + organizationId + "/enable")
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
		assertEquals(Status.ACTIVE, crm.findOrganizationSummary(organizationId).getStatus());
	}
	
	@Test
	public void testOrganizationWithLongName() throws Exception {
		JsonArray json = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.post("/rest/organizations")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("displayName", LoremIpsumGenerator.buildWords(20))
				.with("groups", List.of("ORG"))
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			.andReturn().getResponse().getContentAsString());
		assertSingleJsonMessage(json, null, "error", "displayName", "Display name must be 60 characters or less");
	}

	@Test
	public void testOrganizationWithNoName() throws Exception {
		JsonArray json = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.post("/rest/organizations")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("groups", List.of("ORG"))
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			.andReturn().getResponse().getContentAsString());
		assertSingleJsonMessage(json, null, "error", "displayName", "Field is mandatory");
	}
	
}
