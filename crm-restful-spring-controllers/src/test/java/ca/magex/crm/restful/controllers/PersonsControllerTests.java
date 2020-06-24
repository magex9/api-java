package ca.magex.crm.restful.controllers;

import static ca.magex.crm.test.CrmAsserts.ADAM;
import static ca.magex.crm.test.CrmAsserts.BOB;
import static ca.magex.crm.test.CrmAsserts.BUSINESS_POSITION;
import static ca.magex.crm.test.CrmAsserts.CHLOE;
import static ca.magex.crm.test.CrmAsserts.DAN;
import static ca.magex.crm.test.CrmAsserts.DEVELOPER_POSITION;
import static ca.magex.crm.test.CrmAsserts.HOME_COMMUNICATIONS;
import static ca.magex.crm.test.CrmAsserts.MAILING_ADDRESS;
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
import ca.magex.crm.api.system.Status;
import ca.magex.crm.transform.json.BusinessPositionJsonTransformer;
import ca.magex.crm.transform.json.CommunicationJsonTransformer;
import ca.magex.crm.transform.json.MailingAddressJsonTransformer;
import ca.magex.crm.transform.json.PersonNameJsonTransformer;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonObject;

public class PersonsControllerTests extends AbstractControllerTests {

	private Identifier systemOrgId;
	
	private Identifier systemPersonId;
	
	private Identifier organizationId;
	
	@Before
	public void setup() {
		initialize();
		systemOrgId = crm.findOrganizationSummaries(crm.defaultOrganizationsFilter().withGroup("SYS")).getSingleItem().getOrganizationId();
		systemPersonId = crm.findPersonDetails(crm.defaultPersonsFilter()).getSingleItem().getPersonId();
		organizationId = crm.createOrganization("Test Org", List.of("ORG")).getOrganizationId();
	}
	
	@Test
	public void testCreatePerson() throws Exception {
		// Get the initial list of groups to make sure they are blank
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/persons")
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
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(0).keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(0).getString("@type"));
		assertEquals(systemPersonId.toString(), json.getArray("content").getObject(0).getString("personId"));
		assertEquals(systemOrgId.toString(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(0).getString("status"));
		assertEquals("Admin, System", json.getArray("content").getObject(0).getString("displayName"));
		
		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.post("/rest/persons")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("organizationId", organizationId.toString())
				.with("name", new PersonNameJsonTransformer(crm).format(ADAM, Lang.ENGLISH))
				.with("address", new MailingAddressJsonTransformer(crm).format(MAILING_ADDRESS, Lang.ENGLISH))
				.with("communication", new CommunicationJsonTransformer(crm).format(WORK_COMMUNICATIONS, Lang.ENGLISH))
				.with("position", new BusinessPositionJsonTransformer(crm).format(BUSINESS_POSITION, Lang.ENGLISH))
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "position"), json.keys());
		assertEquals("PersonDetails", json.getString("@type"));
		assertEquals(organizationId.toString(), json.getString("organizationId"));
		assertEquals("Active", json.getString("status"));
		assertEquals("Anderson, Adam A", json.getString("displayName"));
		assertEquals(List.of("@type", "salutation", "firstName", "middleName", "lastName"), json.getObject("legalName").keys());
		assertEquals("PersonName", json.getObject("legalName").getString("@type"));
		assertEquals("Mr.", json.getObject("legalName").getString("salutation"));
		assertEquals("Adam", json.getObject("legalName").getString("firstName"));
		assertEquals("A", json.getObject("legalName").getString("middleName"));
		assertEquals("Anderson", json.getObject("legalName").getString("lastName"));
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), json.getObject("address").keys());
		assertEquals("MailingAddress", json.getObject("address").getString("@type"));
		assertEquals("123 Main St", json.getObject("address").getString("street"));
		assertEquals("Ottawa", json.getObject("address").getString("city"));
		assertEquals("Quebec", json.getObject("address").getString("province"));
		assertEquals("Canada", json.getObject("address").getString("country"));
		assertEquals("K1K1K1", json.getObject("address").getString("postalCode"));
		assertEquals(List.of("@type", "jobTitle", "language", "email", "homePhone", "faxNumber"), json.getObject("communication").keys());
		assertEquals("Communication", json.getObject("communication").getString("@type"));
		assertEquals("Developer", json.getObject("communication").getString("jobTitle"));
		assertEquals("English", json.getObject("communication").getString("language"));
		assertEquals("user@work.ca", json.getObject("communication").getString("email"));
		assertEquals(List.of("@type", "number", "extension"), json.getObject("communication").getObject("homePhone").keys());
		assertEquals("Telephone", json.getObject("communication").getObject("homePhone").getString("@type"));
		assertEquals("5551234567", json.getObject("communication").getObject("homePhone").getString("number"));
		assertEquals("42", json.getObject("communication").getObject("homePhone").getString("extension"));
		assertEquals("8881234567", json.getObject("communication").getString("faxNumber"));
		assertEquals(List.of("@type", "sector", "unit", "classification"), json.getObject("position").keys());
		assertEquals("BusinessPosition", json.getObject("position").getString("@type"));
		assertEquals("External", json.getObject("position").getString("sector"));
		assertEquals("Solutions", json.getObject("position").getString("unit"));
		assertEquals("Developer", json.getObject("position").getString("classification"));		
		
		Identifier personId = new Identifier(json.getString("personId"));
		
		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/persons/" + personId)
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "position"), json.keys());
		assertEquals("PersonDetails", json.getString("@type"));
		assertEquals(personId.toString(), json.getString("personId"));
		assertEquals(organizationId.toString(), json.getString("organizationId"));
		assertEquals("Active", json.getString("status"));
		assertEquals("Anderson, Adam A", json.getString("displayName"));
		assertEquals(List.of("@type", "salutation", "firstName", "middleName", "lastName"), json.getObject("legalName").keys());
		assertEquals("PersonName", json.getObject("legalName").getString("@type"));
		assertEquals("Mr.", json.getObject("legalName").getString("salutation"));
		assertEquals("Adam", json.getObject("legalName").getString("firstName"));
		assertEquals("A", json.getObject("legalName").getString("middleName"));
		assertEquals("Anderson", json.getObject("legalName").getString("lastName"));
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), json.getObject("address").keys());
		assertEquals("MailingAddress", json.getObject("address").getString("@type"));
		assertEquals("123 Main St", json.getObject("address").getString("street"));
		assertEquals("Ottawa", json.getObject("address").getString("city"));
		assertEquals("Quebec", json.getObject("address").getString("province"));
		assertEquals("Canada", json.getObject("address").getString("country"));
		assertEquals("K1K1K1", json.getObject("address").getString("postalCode"));
		assertEquals(List.of("@type", "jobTitle", "language", "email", "homePhone", "faxNumber"), json.getObject("communication").keys());
		assertEquals("Communication", json.getObject("communication").getString("@type"));
		assertEquals("Developer", json.getObject("communication").getString("jobTitle"));
		assertEquals("English", json.getObject("communication").getString("language"));
		assertEquals("user@work.ca", json.getObject("communication").getString("email"));
		assertEquals(List.of("@type", "number", "extension"), json.getObject("communication").getObject("homePhone").keys());
		assertEquals("Telephone", json.getObject("communication").getObject("homePhone").getString("@type"));
		assertEquals("5551234567", json.getObject("communication").getObject("homePhone").getString("number"));
		assertEquals("42", json.getObject("communication").getObject("homePhone").getString("extension"));
		assertEquals("8881234567", json.getObject("communication").getString("faxNumber"));
		assertEquals(List.of("@type", "sector", "unit", "classification"), json.getObject("position").keys());
		assertEquals("BusinessPosition", json.getObject("position").getString("@type"));
		assertEquals("External", json.getObject("position").getString("sector"));
		assertEquals("Solutions", json.getObject("position").getString("unit"));
		assertEquals("Developer", json.getObject("position").getString("classification"));

		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/persons/" + personId)
			.header("Locale", Lang.FRENCH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "position"), json.keys());
		assertEquals("PersonDetails", json.getString("@type"));
		assertEquals(personId.toString(), json.getString("personId"));
		assertEquals(organizationId.toString(), json.getString("organizationId"));
		assertEquals("Actif", json.getString("status"));
		assertEquals("Anderson, Adam A", json.getString("displayName"));
		assertEquals(List.of("@type", "salutation", "firstName", "middleName", "lastName"), json.getObject("legalName").keys());
		assertEquals("PersonName", json.getObject("legalName").getString("@type"));
		assertEquals("M.", json.getObject("legalName").getString("salutation"));
		assertEquals("Adam", json.getObject("legalName").getString("firstName"));
		assertEquals("A", json.getObject("legalName").getString("middleName"));
		assertEquals("Anderson", json.getObject("legalName").getString("lastName"));
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), json.getObject("address").keys());
		assertEquals("MailingAddress", json.getObject("address").getString("@type"));
		assertEquals("123 Main St", json.getObject("address").getString("street"));
		assertEquals("Ottawa", json.getObject("address").getString("city"));
		assertEquals("Québec", json.getObject("address").getString("province"));
		assertEquals("Canada", json.getObject("address").getString("country"));
		assertEquals("K1K1K1", json.getObject("address").getString("postalCode"));
		assertEquals(List.of("@type", "jobTitle", "language", "email", "homePhone", "faxNumber"), json.getObject("communication").keys());
		assertEquals("Communication", json.getObject("communication").getString("@type"));
		assertEquals("Developer", json.getObject("communication").getString("jobTitle"));
		assertEquals("Anglais", json.getObject("communication").getString("language"));
		assertEquals("user@work.ca", json.getObject("communication").getString("email"));
		assertEquals(List.of("@type", "number", "extension"), json.getObject("communication").getObject("homePhone").keys());
		assertEquals("Telephone", json.getObject("communication").getObject("homePhone").getString("@type"));
		assertEquals("5551234567", json.getObject("communication").getObject("homePhone").getString("number"));
		assertEquals("42", json.getObject("communication").getObject("homePhone").getString("extension"));
		assertEquals("8881234567", json.getObject("communication").getString("faxNumber"));
		assertEquals(List.of("@type", "sector", "unit", "classification"), json.getObject("position").keys());
		assertEquals("BusinessPosition", json.getObject("position").getString("@type"));
		assertEquals("External", json.getObject("position").getString("sector"));
		assertEquals("Solutions", json.getObject("position").getString("unit"));
		assertEquals("Développeur", json.getObject("position").getString("classification"));
		
		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/persons/" + personId))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "position"), json.keys());
		assertEquals("PersonDetails", json.getString("@type"));
		assertEquals(personId.toString(), json.getString("personId"));
		assertEquals(organizationId.toString(), json.getString("organizationId"));
		assertEquals("active", json.getString("status"));
		assertEquals("Anderson, Adam A", json.getString("displayName"));
		assertEquals(List.of("@type", "salutation", "firstName", "middleName", "lastName"), json.getObject("legalName").keys());
		assertEquals("PersonName", json.getObject("legalName").getString("@type"));
		assertEquals("3", json.getObject("legalName").getString("salutation"));
		assertEquals("Adam", json.getObject("legalName").getString("firstName"));
		assertEquals("A", json.getObject("legalName").getString("middleName"));
		assertEquals("Anderson", json.getObject("legalName").getString("lastName"));
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), json.getObject("address").keys());
		assertEquals("MailingAddress", json.getObject("address").getString("@type"));
		assertEquals("123 Main St", json.getObject("address").getString("street"));
		assertEquals("Ottawa", json.getObject("address").getString("city"));
		assertEquals("QC", json.getObject("address").getString("province"));
		assertEquals("CA", json.getObject("address").getString("country"));
		assertEquals("K1K1K1", json.getObject("address").getString("postalCode"));
		assertEquals(List.of("@type", "jobTitle", "language", "email", "homePhone", "faxNumber"), json.getObject("communication").keys());
		assertEquals("Communication", json.getObject("communication").getString("@type"));
		assertEquals("Developer", json.getObject("communication").getString("jobTitle"));
		assertEquals("EN", json.getObject("communication").getString("language"));
		assertEquals("user@work.ca", json.getObject("communication").getString("email"));
		assertEquals(List.of("@type", "number", "extension"), json.getObject("communication").getObject("homePhone").keys());
		assertEquals("Telephone", json.getObject("communication").getObject("homePhone").getString("@type"));
		assertEquals("5551234567", json.getObject("communication").getObject("homePhone").getString("number"));
		assertEquals("42", json.getObject("communication").getObject("homePhone").getString("extension"));
		assertEquals("8881234567", json.getObject("communication").getString("faxNumber"));
		assertEquals(List.of("@type", "sector", "unit", "classification"), json.getObject("position").keys());
		assertEquals("BusinessPosition", json.getObject("position").getString("@type"));
		assertEquals("1", json.getObject("position").getString("sector"));
		assertEquals("1", json.getObject("position").getString("unit"));
		assertEquals("1", json.getObject("position").getString("classification"));

		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/persons")
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
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(0).keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(0).getString("@type"));
		assertEquals(personId.toString(), json.getArray("content").getObject(0).getString("personId"));
		assertEquals(organizationId.toString(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(0).getString("status"));
		assertEquals("Anderson, Adam A", json.getArray("content").getObject(0).getString("displayName"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(1).keys());
		assertEquals("PersonSummary", json.getArray("content").getObject(1).getString("@type"));
		assertEquals(systemPersonId.toString(), json.getArray("content").getObject(1).getString("personId"));
		assertEquals(systemOrgId.toString(), json.getArray("content").getObject(1).getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(1).getString("status"));
		assertEquals("Admin, System", json.getArray("content").getObject(1).getString("displayName"));
	}
	
	@Test
	public void testGetPersonDetails() throws Exception {
		Identifier personId = crm.createPerson(organizationId, BOB, US_ADDRESS, WORK_COMMUNICATIONS, BUSINESS_POSITION).getPersonId();
		
		JsonObject data = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/rest/persons/" + personId)
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
		assertEquals("Robert, Bob K", data.getString("displayName"));
		assertEquals(List.of("@type", "firstName", "middleName", "lastName"), data.getObject("legalName").keys());
		assertEquals("PersonName", data.getObject("legalName").getString("@type"));
		assertEquals("Bob", data.getObject("legalName").getString("firstName"));
		assertEquals("K", data.getObject("legalName").getString("middleName"));
		assertEquals("Robert", data.getObject("legalName").getString("lastName"));
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), data.getObject("address").keys());
		assertEquals("MailingAddress", data.getObject("address").getString("@type"));
		assertEquals("465 Huntington Ave", data.getObject("address").getString("street"));
		assertEquals("Boston", data.getObject("address").getString("city"));
		assertEquals("MA", data.getObject("address").getString("province"));
		assertEquals("US", data.getObject("address").getString("country"));
		assertEquals("02115", data.getObject("address").getString("postalCode"));
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
		
		JsonObject english = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/rest/persons/" + personId)
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
		assertEquals("Robert, Bob K", english.getString("displayName"));
		assertEquals(List.of("@type", "firstName", "middleName", "lastName"), english.getObject("legalName").keys());
		assertEquals("PersonName", english.getObject("legalName").getString("@type"));
		assertEquals("Bob", english.getObject("legalName").getString("firstName"));
		assertEquals("K", english.getObject("legalName").getString("middleName"));
		assertEquals("Robert", english.getObject("legalName").getString("lastName"));
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), english.getObject("address").keys());
		assertEquals("MailingAddress", english.getObject("address").getString("@type"));
		assertEquals("465 Huntington Ave", english.getObject("address").getString("street"));
		assertEquals("Boston", english.getObject("address").getString("city"));
		assertEquals("Massachusetts", english.getObject("address").getString("province"));
		assertEquals("United States", english.getObject("address").getString("country"));
		assertEquals("02115", english.getObject("address").getString("postalCode"));
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
		
		JsonObject french = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/rest/persons/" + personId)
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
		assertEquals("Robert, Bob K", french.getString("displayName"));
		assertEquals(List.of("@type", "firstName", "middleName", "lastName"), french.getObject("legalName").keys());
		assertEquals("PersonName", french.getObject("legalName").getString("@type"));
		assertEquals("Bob", french.getObject("legalName").getString("firstName"));
		assertEquals("K", french.getObject("legalName").getString("middleName"));
		assertEquals("Robert", french.getObject("legalName").getString("lastName"));
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), french.getObject("address").keys());
		assertEquals("MailingAddress", french.getObject("address").getString("@type"));
		assertEquals("465 Huntington Ave", french.getObject("address").getString("street"));
		assertEquals("Boston", french.getObject("address").getString("city"));
		assertEquals("Massachusetts", french.getObject("address").getString("province"));
		assertEquals("États-Unis d'Amérique", french.getObject("address").getString("country"));
		assertEquals("02115", french.getObject("address").getString("postalCode"));
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
	}
	
	@Test
	public void testGetPersonSummary() throws Exception {
		Identifier personId = crm.createPerson(organizationId, BOB, US_ADDRESS, WORK_COMMUNICATIONS, BUSINESS_POSITION).getPersonId();
		
		JsonObject data = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/rest/persons/" + personId + "/summary")
				.header("Locale", Lang.ROOT))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(data, "data");
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), data.keys());
		assertEquals("PersonSummary", data.getString("@type"));
		assertEquals(personId.toString(), data.getString("personId"));
		assertEquals(organizationId.toString(), data.getString("organizationId"));
		assertEquals("active", data.getString("status"));
		assertEquals("Robert, Bob K", data.getString("displayName"));
		
		JsonObject english = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/rest/persons/" + personId + "/summary")
				.header("Locale", Lang.ENGLISH))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), english.keys());
		assertEquals("PersonSummary", english.getString("@type"));
		assertEquals(personId.toString(), english.getString("personId"));
		assertEquals(organizationId.toString(), english.getString("organizationId"));
		assertEquals("Active", english.getString("status"));
		assertEquals("Robert, Bob K", english.getString("displayName"));
		
		JsonObject french = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/rest/persons/" + personId + "/summary")
				.header("Locale", Lang.FRENCH))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), french.keys());
		assertEquals("PersonSummary", french.getString("@type"));
		assertEquals(personId.toString(), french.getString("personId"));
		assertEquals(organizationId.toString(), french.getString("organizationId"));
		assertEquals("Actif", french.getString("status"));
		assertEquals("Robert, Bob K", french.getString("displayName"));
	}
	
	@Test
	public void testGetPersonName() throws Exception {
		Identifier personId = crm.createPerson(organizationId, DAN, US_ADDRESS, WORK_COMMUNICATIONS, BUSINESS_POSITION).getPersonId();
		
		JsonObject data = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/rest/persons/" + personId + "/name")
				.header("Locale", Lang.ROOT))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(data, "data");
		assertEquals(List.of("@type", "salutation", "firstName", "middleName", "lastName"), data.keys());
		assertEquals("PersonName", data.getString("@type"));
		assertEquals("3", data.getString("salutation"));
		assertEquals("Daniel", data.getString("firstName"));
		assertEquals("D", data.getString("middleName"));
		assertEquals("O'Sullivan", data.getString("lastName"));

		JsonObject english = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/rest/persons/" + personId + "/name")
				.header("Locale", Lang.ENGLISH))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("@type", "salutation", "firstName", "middleName", "lastName"), english.keys());
		assertEquals("PersonName", english.getString("@type"));
		assertEquals("Mr.", english.getString("salutation"));
		assertEquals("Daniel", english.getString("firstName"));
		assertEquals("D", english.getString("middleName"));
		assertEquals("O'Sullivan", english.getString("lastName"));
		
		JsonObject french = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/rest/persons/" + personId + "/name")
				.header("Locale", Lang.FRENCH))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("@type", "salutation", "firstName", "middleName", "lastName"), french.keys());
		assertEquals("PersonName", french.getString("@type"));
		assertEquals("M.", french.getString("salutation"));
		assertEquals("Daniel", french.getString("firstName"));
		assertEquals("D", french.getString("middleName"));
		assertEquals("O'Sullivan", french.getString("lastName"));
	}
	
	@Test
	public void testGetPersonAddress() throws Exception {
		Identifier personId = crm.createPerson(organizationId, BOB, US_ADDRESS, WORK_COMMUNICATIONS, BUSINESS_POSITION).getPersonId();
		
		JsonObject data = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/rest/persons/" + personId + "/address")
				.header("Locale", Lang.ROOT))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(data, "data");
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), data.keys());
		assertEquals("MailingAddress", data.getString("@type"));
		assertEquals("465 Huntington Ave", data.getString("street"));
		assertEquals("Boston", data.getString("city"));
		assertEquals("MA", data.getString("province"));
		assertEquals("US", data.getString("country"));
		assertEquals("02115", data.getString("postalCode"));

		JsonObject english = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/rest/persons/" + personId + "/address")
				.header("Locale", Lang.ENGLISH))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), english.keys());
		assertEquals("MailingAddress", english.getString("@type"));
		assertEquals("465 Huntington Ave", english.getString("street"));
		assertEquals("Boston", english.getString("city"));
		assertEquals("Massachusetts", english.getString("province"));
		assertEquals("United States", english.getString("country"));
		assertEquals("02115", english.getString("postalCode"));
		
		JsonObject french = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/rest/persons/" + personId + "/address")
				.header("Locale", Lang.FRENCH))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), french.keys());
		assertEquals("MailingAddress", french.getString("@type"));
		assertEquals("465 Huntington Ave", french.getString("street"));
		assertEquals("Boston", french.getString("city"));
		assertEquals("Massachusetts", french.getString("province"));
		assertEquals("États-Unis d'Amérique", french.getString("country"));
		assertEquals("02115", french.getString("postalCode"));
	}
	
	@Test
	public void testGetPersonCommunication() throws Exception {
		Identifier personId = crm.createPerson(organizationId, BOB, US_ADDRESS, WORK_COMMUNICATIONS, BUSINESS_POSITION).getPersonId();
		
		JsonObject data = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/rest/persons/" + personId + "/communication")
				.header("Locale", Lang.ROOT))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(data, "data");
		assertEquals(List.of("@type", "jobTitle", "language", "email", "homePhone", "faxNumber"), data.keys());
		assertEquals("Communication", data.getString("@type"));
		assertEquals("Developer", data.getString("jobTitle"));
		assertEquals("EN", data.getString("language"));
		assertEquals("user@work.ca", data.getString("email"));
		assertEquals(List.of("@type", "number", "extension"), data.getObject("homePhone").keys());
		assertEquals("Telephone", data.getObject("homePhone").getString("@type"));
		assertEquals("5551234567", data.getObject("homePhone").getString("number"));
		assertEquals("42", data.getObject("homePhone").getString("extension"));
		assertEquals("8881234567", data.getString("faxNumber"));

		JsonObject english = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/rest/persons/" + personId + "/communication")
				.header("Locale", Lang.ENGLISH))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("@type", "jobTitle", "language", "email", "homePhone", "faxNumber"), english.keys());
		assertEquals("Communication", english.getString("@type"));
		assertEquals("Developer", english.getString("jobTitle"));
		assertEquals("English", english.getString("language"));
		assertEquals("user@work.ca", english.getString("email"));
		assertEquals(List.of("@type", "number", "extension"), english.getObject("homePhone").keys());
		assertEquals("Telephone", english.getObject("homePhone").getString("@type"));
		assertEquals("5551234567", english.getObject("homePhone").getString("number"));
		assertEquals("42", english.getObject("homePhone").getString("extension"));
		assertEquals("8881234567", english.getString("faxNumber"));
		
		JsonObject french = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/rest/persons/" + personId + "/communication")
				.header("Locale", Lang.FRENCH))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("@type", "jobTitle", "language", "email", "homePhone", "faxNumber"), french.keys());
		assertEquals("Communication", french.getString("@type"));
		assertEquals("Developer", french.getString("jobTitle"));
		assertEquals("Anglais", french.getString("language"));
		assertEquals("user@work.ca", french.getString("email"));
		assertEquals(List.of("@type", "number", "extension"), french.getObject("homePhone").keys());
		assertEquals("Telephone", french.getObject("homePhone").getString("@type"));
		assertEquals("5551234567", french.getObject("homePhone").getString("number"));
		assertEquals("42", french.getObject("homePhone").getString("extension"));
		assertEquals("8881234567", french.getString("faxNumber"));
	}
	
	@Test
	public void testGetPersonPosition() throws Exception {
		Identifier personId = crm.createPerson(organizationId, BOB, US_ADDRESS, WORK_COMMUNICATIONS, BUSINESS_POSITION).getPersonId();
		
		JsonObject data = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/rest/persons/" + personId + "/position")
				.header("Locale", Lang.ROOT))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(data, "data");
		assertEquals(List.of("@type", "sector", "unit", "classification"), data.keys());
		assertEquals("BusinessPosition", data.getString("@type"));
		assertEquals("1", data.getString("sector"));
		assertEquals("1", data.getString("unit"));
		assertEquals("1", data.getString("classification"));

		JsonObject english = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/rest/persons/" + personId + "/position")
				.header("Locale", Lang.ENGLISH))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("@type", "sector", "unit", "classification"), english.keys());
		assertEquals("BusinessPosition", english.getString("@type"));
		assertEquals("External", english.getString("sector"));
		assertEquals("Solutions", english.getString("unit"));
		assertEquals("Developer", english.getString("classification"));
		
		JsonObject french = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/rest/persons/" + personId + "/position")
				.header("Locale", Lang.FRENCH))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("@type", "sector", "unit", "classification"), french.keys());
		assertEquals("BusinessPosition", french.getString("@type"));
		assertEquals("External", french.getString("sector"));
		assertEquals("Solutions", french.getString("unit"));
		assertEquals("Développeur", french.getString("classification"));
	}
	
	@Test
	public void testUpdatingDisplayName() throws Exception {
		Identifier personId = crm.createPerson(organizationId, BOB, US_ADDRESS, WORK_COMMUNICATIONS, BUSINESS_POSITION).getPersonId();
		
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.patch("/rest/persons/" + personId)
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("name", new PersonNameJsonTransformer(crm).format(CHLOE, Lang.ENGLISH))
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "position"), json.keys());
		assertEquals("PersonDetails", json.getString("@type"));
		assertEquals(personId.toString(), json.getString("personId"));
		assertEquals(organizationId.toString(), json.getString("organizationId"));
		assertEquals("Active", json.getString("status"));
		assertEquals("LaRue, Chloé", json.getString("displayName"));
		assertEquals(List.of("@type", "firstName", "lastName"), json.getObject("legalName").keys());
		assertEquals("PersonName", json.getObject("legalName").getString("@type"));
		assertEquals("Chloé", json.getObject("legalName").getString("firstName"));
		assertEquals("LaRue", json.getObject("legalName").getString("lastName"));
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), json.getObject("address").keys());
		assertEquals("MailingAddress", json.getObject("address").getString("@type"));
		assertEquals("465 Huntington Ave", json.getObject("address").getString("street"));
		assertEquals("Boston", json.getObject("address").getString("city"));
		assertEquals("Massachusetts", json.getObject("address").getString("province"));
		assertEquals("United States", json.getObject("address").getString("country"));
		assertEquals("02115", json.getObject("address").getString("postalCode"));
		assertEquals(List.of("@type", "jobTitle", "language", "email", "homePhone", "faxNumber"), json.getObject("communication").keys());
		assertEquals("Communication", json.getObject("communication").getString("@type"));
		assertEquals("Developer", json.getObject("communication").getString("jobTitle"));
		assertEquals("English", json.getObject("communication").getString("language"));
		assertEquals("user@work.ca", json.getObject("communication").getString("email"));
		assertEquals(List.of("@type", "number", "extension"), json.getObject("communication").getObject("homePhone").keys());
		assertEquals("Telephone", json.getObject("communication").getObject("homePhone").getString("@type"));
		assertEquals("5551234567", json.getObject("communication").getObject("homePhone").getString("number"));
		assertEquals("42", json.getObject("communication").getObject("homePhone").getString("extension"));
		assertEquals("8881234567", json.getObject("communication").getString("faxNumber"));
		assertEquals(List.of("@type", "sector", "unit", "classification"), json.getObject("position").keys());
		assertEquals("BusinessPosition", json.getObject("position").getString("@type"));
		assertEquals("External", json.getObject("position").getString("sector"));
		assertEquals("Solutions", json.getObject("position").getString("unit"));
		assertEquals("Developer", json.getObject("position").getString("classification"));
	}
	
	@Test
	public void testUpdatingAddress() throws Exception {
		Identifier personId = crm.createPerson(organizationId, BOB, US_ADDRESS, WORK_COMMUNICATIONS, BUSINESS_POSITION).getPersonId();
		
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.patch("/rest/persons/" + personId)
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("address", new MailingAddressJsonTransformer(crm).format(MX_ADDRESS, Lang.ENGLISH))
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "position"), json.keys());
		assertEquals("PersonDetails", json.getString("@type"));
		assertEquals(personId.toString(), json.getString("personId"));
		assertEquals(organizationId.toString(), json.getString("organizationId"));
		assertEquals("Active", json.getString("status"));
		assertEquals("Robert, Bob K", json.getString("displayName"));
		assertEquals(List.of("@type", "firstName", "middleName", "lastName"), json.getObject("legalName").keys());
		assertEquals("PersonName", json.getObject("legalName").getString("@type"));
		assertEquals("Bob", json.getObject("legalName").getString("firstName"));
		assertEquals("K", json.getObject("legalName").getString("middleName"));
		assertEquals("Robert", json.getObject("legalName").getString("lastName"));
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), json.getObject("address").keys());
		assertEquals("MailingAddress", json.getObject("address").getString("@type"));
		assertEquals("120 Col. Hipodromo Condesa", json.getObject("address").getString("street"));
		assertEquals("Monterrey", json.getObject("address").getString("city"));
		assertEquals("Nuevo Leon", json.getObject("address").getString("province"));
		assertEquals("Mexico", json.getObject("address").getString("country"));
		assertEquals("06100", json.getObject("address").getString("postalCode"));
		assertEquals(List.of("@type", "jobTitle", "language", "email", "homePhone", "faxNumber"), json.getObject("communication").keys());
		assertEquals("Communication", json.getObject("communication").getString("@type"));
		assertEquals("Developer", json.getObject("communication").getString("jobTitle"));
		assertEquals("English", json.getObject("communication").getString("language"));
		assertEquals("user@work.ca", json.getObject("communication").getString("email"));
		assertEquals(List.of("@type", "number", "extension"), json.getObject("communication").getObject("homePhone").keys());
		assertEquals("Telephone", json.getObject("communication").getObject("homePhone").getString("@type"));
		assertEquals("5551234567", json.getObject("communication").getObject("homePhone").getString("number"));
		assertEquals("42", json.getObject("communication").getObject("homePhone").getString("extension"));
		assertEquals("8881234567", json.getObject("communication").getString("faxNumber"));
		assertEquals(List.of("@type", "sector", "unit", "classification"), json.getObject("position").keys());
		assertEquals("BusinessPosition", json.getObject("position").getString("@type"));
		assertEquals("External", json.getObject("position").getString("sector"));
		assertEquals("Solutions", json.getObject("position").getString("unit"));
		assertEquals("Developer", json.getObject("position").getString("classification"));
	}

	@Test
	public void testUpdatingCommunication() throws Exception {
		Identifier personId = crm.createPerson(organizationId, BOB, US_ADDRESS, WORK_COMMUNICATIONS, BUSINESS_POSITION).getPersonId();
		
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.patch("/rest/persons/" + personId)
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("communication", new CommunicationJsonTransformer(crm).format(HOME_COMMUNICATIONS, Lang.ENGLISH))
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "position"), json.keys());
		assertEquals("PersonDetails", json.getString("@type"));
		assertEquals(personId.toString(), json.getString("personId"));
		assertEquals(organizationId.toString(), json.getString("organizationId"));
		assertEquals("Active", json.getString("status"));
		assertEquals("Robert, Bob K", json.getString("displayName"));
		assertEquals(List.of("@type", "firstName", "middleName", "lastName"), json.getObject("legalName").keys());
		assertEquals("PersonName", json.getObject("legalName").getString("@type"));
		assertEquals("Bob", json.getObject("legalName").getString("firstName"));
		assertEquals("K", json.getObject("legalName").getString("middleName"));
		assertEquals("Robert", json.getObject("legalName").getString("lastName"));
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), json.getObject("address").keys());
		assertEquals("MailingAddress", json.getObject("address").getString("@type"));
		assertEquals("465 Huntington Ave", json.getObject("address").getString("street"));
		assertEquals("Boston", json.getObject("address").getString("city"));
		assertEquals("Massachusetts", json.getObject("address").getString("province"));
		assertEquals("United States", json.getObject("address").getString("country"));
		assertEquals("02115", json.getObject("address").getString("postalCode"));
		assertEquals(List.of("@type", "language", "email", "homePhone"), json.getObject("communication").keys());
		assertEquals("Communication", json.getObject("communication").getString("@type"));
		assertEquals("English", json.getObject("communication").getString("language"));
		assertEquals("user@home.ca", json.getObject("communication").getString("email"));
		assertEquals(List.of("@type", "number"), json.getObject("communication").getObject("homePhone").keys());
		assertEquals("Telephone", json.getObject("communication").getObject("homePhone").getString("@type"));
		assertEquals("5558883333", json.getObject("communication").getObject("homePhone").getString("number"));
		assertEquals(List.of("@type", "sector", "unit", "classification"), json.getObject("position").keys());
		assertEquals("BusinessPosition", json.getObject("position").getString("@type"));
		assertEquals("External", json.getObject("position").getString("sector"));
		assertEquals("Solutions", json.getObject("position").getString("unit"));
		assertEquals("Developer", json.getObject("position").getString("classification"));
	}

	@Test
	public void testUpdatingPosition() throws Exception {
		Identifier personId = crm.createPerson(organizationId, BOB, US_ADDRESS, WORK_COMMUNICATIONS, BUSINESS_POSITION).getPersonId();
		
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.patch("/rest/persons/" + personId)
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("position", new BusinessPositionJsonTransformer(crm).format(DEVELOPER_POSITION, Lang.ENGLISH))
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "position"), json.keys());
		assertEquals("PersonDetails", json.getString("@type"));
		assertEquals(personId.toString(), json.getString("personId"));
		assertEquals(organizationId.toString(), json.getString("organizationId"));
		assertEquals("Active", json.getString("status"));
		assertEquals("Robert, Bob K", json.getString("displayName"));
		assertEquals(List.of("@type", "firstName", "middleName", "lastName"), json.getObject("legalName").keys());
		assertEquals("PersonName", json.getObject("legalName").getString("@type"));
		assertEquals("Bob", json.getObject("legalName").getString("firstName"));
		assertEquals("K", json.getObject("legalName").getString("middleName"));
		assertEquals("Robert", json.getObject("legalName").getString("lastName"));
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), json.getObject("address").keys());
		assertEquals("MailingAddress", json.getObject("address").getString("@type"));
		assertEquals("465 Huntington Ave", json.getObject("address").getString("street"));
		assertEquals("Boston", json.getObject("address").getString("city"));
		assertEquals("Massachusetts", json.getObject("address").getString("province"));
		assertEquals("United States", json.getObject("address").getString("country"));
		assertEquals("02115", json.getObject("address").getString("postalCode"));
		assertEquals(List.of("@type", "jobTitle", "language", "email", "homePhone", "faxNumber"), json.getObject("communication").keys());
		assertEquals("Communication", json.getObject("communication").getString("@type"));
		assertEquals("Developer", json.getObject("communication").getString("jobTitle"));
		assertEquals("English", json.getObject("communication").getString("language"));
		assertEquals("user@work.ca", json.getObject("communication").getString("email"));
		assertEquals(List.of("@type", "number", "extension"), json.getObject("communication").getObject("homePhone").keys());
		assertEquals("Telephone", json.getObject("communication").getObject("homePhone").getString("@type"));
		assertEquals("5551234567", json.getObject("communication").getObject("homePhone").getString("number"));
		assertEquals("42", json.getObject("communication").getObject("homePhone").getString("extension"));
		assertEquals("8881234567", json.getObject("communication").getString("faxNumber"));
		assertEquals(List.of("@type", "sector", "unit", "classification"), json.getObject("position").keys());
		assertEquals("BusinessPosition", json.getObject("position").getString("@type"));
		assertEquals("Compliance", json.getObject("position").getString("sector"));
		assertEquals("Data Management", json.getObject("position").getString("unit"));
		assertEquals("Team Lead", json.getObject("position").getString("classification"));
	}

	@Test
	public void testEnableDisablePerson() throws Exception {
		Identifier personId = crm.createPerson(organizationId, BOB, US_ADDRESS, WORK_COMMUNICATIONS, BUSINESS_POSITION).getPersonId();
		assertEquals(Status.ACTIVE, crm.findPersonSummary(personId).getStatus());

		JsonArray error1 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/rest/persons/" + personId + "/disable")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(personId.toString(), error1.getObject(0).getString("identifier"));
		assertEquals("error", error1.getObject(0).getString("type"));
		assertEquals("confirm", error1.getObject(0).getString("path"));
		assertEquals("You must send in the confirmation message", error1.getObject(0).getString("reason"));
		assertEquals(Status.ACTIVE, crm.findPersonSummary(personId).getStatus());

		JsonArray error2 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/rest/persons/" + personId + "/disable")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("confirm", false)
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(personId.toString(), error2.getObject(0).getString("identifier"));
		assertEquals("error", error2.getObject(0).getString("type"));
		assertEquals("confirm", error2.getObject(0).getString("path"));
		assertEquals("You must send in the confirmation message", error2.getObject(0).getString("reason"));
		assertEquals(Status.ACTIVE, crm.findPersonSummary(personId).getStatus());

		JsonArray error3 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/rest/persons/" + personId + "/disable")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("confirm", "Test")
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(personId.toString(), error3.getObject(0).getString("identifier"));
		assertEquals("error", error3.getObject(0).getString("type"));
		assertEquals("confirm", error3.getObject(0).getString("path"));
		assertEquals("Confirmation message must be a boolean", error3.getObject(0).getString("reason"));
		assertEquals(Status.ACTIVE, crm.findPersonSummary(personId).getStatus());

		JsonObject disable = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.put("/rest/persons/" + personId + "/disable")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("confirm", true)
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(disable, "disable");
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), disable.keys());
		assertEquals("PersonSummary", disable.getString("@type"));
		assertEquals(personId.toString(), disable.getString("personId"));
		assertEquals(organizationId.toString(), disable.getString("organizationId"));
		assertEquals("Inactive", disable.getString("status"));
		assertEquals("Robert, Bob K", disable.getString("displayName"));
		
		JsonArray error4 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/rest/persons/" + personId + "/enable")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(personId.toString(), error4.getObject(0).getString("identifier"));
		assertEquals("error", error4.getObject(0).getString("type"));
		assertEquals("confirm", error4.getObject(0).getString("path"));
		assertEquals("You must send in the confirmation message", error4.getObject(0).getString("reason"));
		assertEquals(Status.INACTIVE, crm.findPersonSummary(personId).getStatus());
		
		JsonArray error5 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/rest/persons/" + personId + "/enable")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("confirm", false)
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(personId.toString(), error5.getObject(0).getString("identifier"));
		assertEquals("error", error5.getObject(0).getString("type"));
		assertEquals("confirm", error5.getObject(0).getString("path"));
		assertEquals("You must send in the confirmation message", error5.getObject(0).getString("reason"));
		assertEquals(Status.INACTIVE, crm.findPersonSummary(personId).getStatus());
		
		JsonArray error6 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/rest/persons/" + personId + "/enable")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("confirm", "test")
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(personId.toString(), error6.getObject(0).getString("identifier"));
		assertEquals("error", error6.getObject(0).getString("type"));
		assertEquals("confirm", error6.getObject(0).getString("path"));
		assertEquals("Confirmation message must be a boolean", error6.getObject(0).getString("reason"));
		assertEquals(Status.INACTIVE, crm.findPersonSummary(personId).getStatus());
	
		JsonObject enable = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.put("/rest/persons/" + personId + "/enable")
			.header("Locale", Lang.FRENCH)
			.content(new JsonObject()
				.with("confirm", true)
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(enable, "enable");
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), enable.keys());
		assertEquals("PersonSummary", enable.getString("@type"));
		assertEquals(personId.toString(), enable.getString("personId"));
		assertEquals(organizationId.toString(), enable.getString("organizationId"));
		assertEquals("Actif", enable.getString("status"));
		assertEquals("Robert, Bob K", enable.getString("displayName"));
	}
//	
//	@Test
//	public void testPersonWithLongName() throws Exception {
//		JsonArray json = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
//			.post("/rest/persons")
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
//			.post("/rest/persons")
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
