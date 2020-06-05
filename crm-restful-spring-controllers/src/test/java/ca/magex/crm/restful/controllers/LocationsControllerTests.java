package ca.magex.crm.restful.controllers;

import static ca.magex.crm.test.CrmAsserts.MAILING_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.MX_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.NL_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.ORG_NAME;
import static ca.magex.crm.test.CrmAsserts.US_ADDRESS;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.transform.json.MailingAddressJsonTransformer;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonObject;

public class LocationsControllerTests extends AbstractControllerTests {

	private Identifier organizationId;
	
	@Before
	public void setup() {
		initialize();
		organizationId = crm.createOrganization("Test Org", List.of("ORG")).getOrganizationId();
	}
	
	@Test
	public void testCreateLocation() throws Exception {
		// Get the initial list of groups to make sure they are blank
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/locations")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(1, json.getInt("page"));
		assertEquals(0, json.getInt("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(JsonArray.class, json.get("content").getClass());
		assertEquals(0, json.getArray("content").size());
		
		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.post("/api/locations")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("organizationId", organizationId.toString())
				.with("displayName", ORG_NAME.getEnglishName())
				.with("reference", "LOC")
				.with("address", new MailingAddressJsonTransformer(crm).format(MAILING_ADDRESS, Lang.ENGLISH))
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName", "address"), json.keys());
		assertEquals("LocationDetails", json.getString("@type"));
		assertEquals(organizationId.toString(), json.getString("organizationId"));
		assertEquals("Active", json.getString("status"));
		assertEquals("LOC", json.getString("reference"));
		assertEquals("Organization", json.getString("displayName"));
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), json.getObject("address").keys());
		assertEquals("MailingAddress", json.getObject("address").getString("@type"));
		assertEquals("123 Main St", json.getObject("address").getString("street"));
		assertEquals("Ottawa", json.getObject("address").getString("city"));
		assertEquals("Quebec", json.getObject("address").getString("province"));
		assertEquals("Canada", json.getObject("address").getString("country"));
		assertEquals("K1K1K1", json.getObject("address").getString("postalCode"));
		Identifier locationId = new Identifier(json.getString("locationId"));
		
		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/locations/" + locationId)
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName", "address"), json.keys());
		assertEquals("LocationDetails", json.getString("@type"));
		assertEquals(locationId.toString(), json.getString("locationId"));
		assertEquals(organizationId.toString(), json.getString("organizationId"));
		assertEquals("Active", json.getString("status"));
		assertEquals("LOC", json.getString("reference"));
		assertEquals("Organization", json.getString("displayName"));
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), json.getObject("address").keys());
		assertEquals("MailingAddress", json.getObject("address").getString("@type"));
		assertEquals("123 Main St", json.getObject("address").getString("street"));
		assertEquals("Ottawa", json.getObject("address").getString("city"));
		assertEquals("Quebec", json.getObject("address").getString("province"));
		assertEquals("Canada", json.getObject("address").getString("country"));
		assertEquals("K1K1K1", json.getObject("address").getString("postalCode"));

		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/locations/" + locationId)
			.header("Locale", Lang.FRENCH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName", "address"), json.keys());
		assertEquals("LocationDetails", json.getString("@type"));
		assertEquals(locationId.toString(), json.getString("locationId"));
		assertEquals(organizationId.toString(), json.getString("organizationId"));
		assertEquals("Actif", json.getString("status"));
		assertEquals("LOC", json.getString("reference"));
		assertEquals("Organization", json.getString("displayName"));
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), json.getObject("address").keys());
		assertEquals("MailingAddress", json.getObject("address").getString("@type"));
		assertEquals("123 Main St", json.getObject("address").getString("street"));
		assertEquals("Ottawa", json.getObject("address").getString("city"));
		assertEquals("Québec", json.getObject("address").getString("province"));
		assertEquals("Canada", json.getObject("address").getString("country"));
		assertEquals("K1K1K1", json.getObject("address").getString("postalCode"));
		
		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/locations/" + locationId))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName", "address"), json.keys());
		assertEquals("LocationDetails", json.getString("@type"));
		assertEquals(locationId.toString(), json.getString("locationId"));
		assertEquals(organizationId.toString(), json.getString("organizationId"));
		assertEquals("active", json.getString("status"));
		assertEquals("LOC", json.getString("reference"));
		assertEquals("Organization", json.getString("displayName"));
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), json.getObject("address").keys());
		assertEquals("MailingAddress", json.getObject("address").getString("@type"));
		assertEquals("123 Main St", json.getObject("address").getString("street"));
		assertEquals("Ottawa", json.getObject("address").getString("city"));
		assertEquals("QC", json.getObject("address").getString("province"));
		assertEquals("CA", json.getObject("address").getString("country"));
		assertEquals("K1K1K1", json.getObject("address").getString("postalCode"));

		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/locations")
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
		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName"), json.getArray("content").getObject(0).keys());
		assertEquals("LocationSummary", json.getArray("content").getObject(0).getString("@type"));
		assertEquals(locationId.toString(), json.getArray("content").getObject(0).getString("locationId"));
		assertEquals(organizationId.toString(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(0).getString("status"));
		assertEquals("LOC", json.getArray("content").getObject(0).getString("reference"));
		assertEquals("Organization", json.getArray("content").getObject(0).getString("displayName"));
	}
	
	@Test
	public void testGetLocationDetails() throws Exception {
		Identifier locationId = crm.createLocation(organizationId, "Nuevo Leon", "NUEVOLEON", MX_ADDRESS).getLocationId();
		
		JsonObject data = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/api/locations/" + locationId)
				.header("Locale", Lang.ROOT))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(data, "data");
		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName", "address"), data.keys());
		assertEquals("LocationDetails", data.getString("@type"));
		assertEquals(locationId.toString(), data.getString("locationId"));
		assertEquals(organizationId.toString(), data.getString("organizationId"));
		assertEquals("active", data.getString("status"));
		assertEquals("NUEVOLEON", data.getString("reference"));
		assertEquals("Nuevo Leon", data.getString("displayName"));
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), data.getObject("address").keys());
		assertEquals("MailingAddress", data.getObject("address").getString("@type"));
		assertEquals("120 Col. Hipodromo Condesa", data.getObject("address").getString("street"));
		assertEquals("Monterrey", data.getObject("address").getString("city"));
		assertEquals("NL", data.getObject("address").getString("province"));
		assertEquals("MX", data.getObject("address").getString("country"));
		assertEquals("06100", data.getObject("address").getString("postalCode"));
		
		JsonObject english = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/api/locations/" + locationId)
				.header("Locale", Lang.ENGLISH))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName", "address"), english.keys());
		assertEquals("LocationDetails", english.getString("@type"));
		assertEquals(locationId.toString(), english.getString("locationId"));
		assertEquals(organizationId.toString(), english.getString("organizationId"));
		assertEquals("Active", english.getString("status"));
		assertEquals("NUEVOLEON", english.getString("reference"));
		assertEquals("Nuevo Leon", english.getString("displayName"));
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), english.getObject("address").keys());
		assertEquals("MailingAddress", english.getObject("address").getString("@type"));
		assertEquals("120 Col. Hipodromo Condesa", english.getObject("address").getString("street"));
		assertEquals("Monterrey", english.getObject("address").getString("city"));
		assertEquals("Nuevo Leon", english.getObject("address").getString("province"));
		assertEquals("Mexico", english.getObject("address").getString("country"));
		assertEquals("06100", english.getObject("address").getString("postalCode"));
		
		JsonObject french = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/api/locations/" + locationId)
				.header("Locale", Lang.FRENCH))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName", "address"), french.keys());
		assertEquals("LocationDetails", french.getString("@type"));
		assertEquals(locationId.toString(), french.getString("locationId"));
		assertEquals(organizationId.toString(), french.getString("organizationId"));
		assertEquals("Actif", french.getString("status"));
		assertEquals("NUEVOLEON", french.getString("reference"));
		assertEquals("Nuevo Leon", french.getString("displayName"));
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), french.getObject("address").keys());
		assertEquals("MailingAddress", french.getObject("address").getString("@type"));
		assertEquals("120 Col. Hipodromo Condesa", french.getObject("address").getString("street"));
		assertEquals("Monterrey", french.getObject("address").getString("city"));
		assertEquals("Nuevo Leon", french.getObject("address").getString("province"));
		assertEquals("Mexique", french.getObject("address").getString("country"));
		assertEquals("06100", french.getObject("address").getString("postalCode"));
	}
	
	@Test
	public void testGetLocationSummary() throws Exception {
		Identifier locationId = crm.createLocation(organizationId, "Main Location", "MAIN", MAILING_ADDRESS).getLocationId();
		
		JsonObject data = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/api/locations/" + locationId + "/summary")
				.header("Locale", Lang.ROOT))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(data, "data");
		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName"), data.keys());
		assertEquals("LocationSummary", data.getString("@type"));
		assertEquals(locationId.toString(), data.getString("locationId"));
		assertEquals(organizationId.toString(), data.getString("organizationId"));
		assertEquals("active", data.getString("status"));
		assertEquals("MAIN", data.getString("reference"));
		assertEquals("Main Location", data.getString("displayName"));
		
		JsonObject english = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/api/locations/" + locationId + "/summary")
				.header("Locale", Lang.ENGLISH))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName"), english.keys());
		assertEquals("LocationSummary", english.getString("@type"));
		assertEquals(locationId.toString(), english.getString("locationId"));
		assertEquals(organizationId.toString(), english.getString("organizationId"));
		assertEquals("Active", english.getString("status"));
		assertEquals("MAIN", english.getString("reference"));
		assertEquals("Main Location", english.getString("displayName"));
		
		JsonObject french = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/api/locations/" + locationId + "/summary")
				.header("Locale", Lang.FRENCH))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName"), french.keys());
		assertEquals("LocationSummary", french.getString("@type"));
		assertEquals(locationId.toString(), french.getString("locationId"));
		assertEquals(organizationId.toString(), french.getString("organizationId"));
		assertEquals("Actif", french.getString("status"));
		assertEquals("MAIN", french.getString("reference"));
		assertEquals("Main Location", french.getString("displayName"));
	}
	
	@Test
	public void testGetLocationAddress() throws Exception {
		Identifier locationId = crm.createLocation(organizationId, "Labrador City", "NEWFOUNDLAND", NL_ADDRESS).getLocationId();
		
		JsonObject data = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/api/locations/" + locationId + "/address")
				.header("Locale", Lang.ROOT))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(data, "data");
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), data.keys());
		assertEquals("MailingAddress", data.getString("@type"));
		assertEquals("90 Avalon Drive", data.getString("street"));
		assertEquals("Labrador City", data.getString("city"));
		assertEquals("NL", data.getString("province"));
		assertEquals("CA", data.getString("country"));
		assertEquals("A2V 2Y2", data.getString("postalCode"));

		JsonObject english = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/api/locations/" + locationId + "/address")
				.header("Locale", Lang.ENGLISH))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), english.keys());
		assertEquals("MailingAddress", english.getString("@type"));
		assertEquals("90 Avalon Drive", english.getString("street"));
		assertEquals("Labrador City", english.getString("city"));
		assertEquals("Newfoundland and Labrador", english.getString("province"));
		assertEquals("Canada", english.getString("country"));
		assertEquals("A2V 2Y2", english.getString("postalCode"));
		
		JsonObject french = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
				.get("/api/locations/" + locationId + "/address")
				.header("Locale", Lang.FRENCH))
				//.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), french.keys());
		assertEquals("MailingAddress", french.getString("@type"));
		assertEquals("90 Avalon Drive", french.getString("street"));
		assertEquals("Labrador City", french.getString("city"));
		assertEquals("Terre-Neuve et Labrador", french.getString("province"));
		assertEquals("Canada", french.getString("country"));
		assertEquals("A2V 2Y2", french.getString("postalCode"));
	}
	
	@Test
	public void testUpdatingDisplayName() throws Exception {
		Identifier locationId = crm.createLocation(organizationId, "Main Location", "MAIN", MAILING_ADDRESS).getLocationId();
		
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.patch("/api/locations/" + locationId)
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("displayName", "Updated name")
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName", "address"), json.keys());
		assertEquals("LocationDetails", json.getString("@type"));
		assertEquals(locationId.toString(), json.getString("locationId"));
		assertEquals(organizationId.toString(), json.getString("organizationId"));
		assertEquals("Active", json.getString("status"));
		assertEquals("MAIN", json.getString("reference"));
		assertEquals("Updated name", json.getString("displayName"));
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), json.getObject("address").keys());
		assertEquals("MailingAddress", json.getObject("address").getString("@type"));
		assertEquals("123 Main St", json.getObject("address").getString("street"));
		assertEquals("Ottawa", json.getObject("address").getString("city"));
		assertEquals("Quebec", json.getObject("address").getString("province"));
		assertEquals("Canada", json.getObject("address").getString("country"));
		assertEquals("K1K1K1", json.getObject("address").getString("postalCode"));
	}
	
	@Test
	public void testUpdatingAddress() throws Exception {
		Identifier locationId = crm.createLocation(organizationId, "Main Location", "MAIN", MAILING_ADDRESS).getLocationId();
		
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.patch("/api/locations/" + locationId)
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("address", new MailingAddressJsonTransformer(crm).format(US_ADDRESS, Lang.ENGLISH))
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("@type", "locationId", "organizationId", "status", "reference", "displayName", "address"), json.keys());
		assertEquals("LocationDetails", json.getString("@type"));
		assertEquals(locationId.toString(), json.getString("locationId"));
		assertEquals(organizationId.toString(), json.getString("organizationId"));
		assertEquals("Active", json.getString("status"));
		assertEquals("MAIN", json.getString("reference"));
		assertEquals("Main Location", json.getString("displayName"));
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), json.getObject("address").keys());
		assertEquals("MailingAddress", json.getObject("address").getString("@type"));
		assertEquals("465 Huntington Ave", json.getObject("address").getString("street"));
		assertEquals("Boston", json.getObject("address").getString("city"));
		assertEquals("Massachusetts", json.getObject("address").getString("province"));
		assertEquals("United States", json.getObject("address").getString("country"));
		assertEquals("02115", json.getObject("address").getString("postalCode"));
	}

	@Test
	public void testEnableDisableLocation() throws Exception {
		Identifier locationId = crm.createLocation(organizationId, "Main Location", "MAIN", MAILING_ADDRESS).getLocationId();
		assertEquals(Status.ACTIVE, crm.findLocationSummary(locationId).getStatus());

		JsonArray error1 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/api/locations/" + locationId + "/disable")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(locationId.toString(), error1.getObject(0).getString("identifier"));
		assertEquals("error", error1.getObject(0).getString("type"));
		assertEquals("confirm", error1.getObject(0).getString("path"));
		assertEquals("You must send in the confirmation message", error1.getObject(0).getString("reason"));
		assertEquals(Status.ACTIVE, crm.findLocationSummary(locationId).getStatus());

		JsonArray error2 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/api/locations/" + locationId + "/disable")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("confirm", false)
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(locationId.toString(), error2.getObject(0).getString("identifier"));
		assertEquals("error", error2.getObject(0).getString("type"));
		assertEquals("confirm", error2.getObject(0).getString("path"));
		assertEquals("You must send in the confirmation message", error2.getObject(0).getString("reason"));
		assertEquals(Status.ACTIVE, crm.findLocationSummary(locationId).getStatus());

		JsonArray error3 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/api/locations/" + locationId + "/disable")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("confirm", "Test")
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(locationId.toString(), error3.getObject(0).getString("identifier"));
		assertEquals("error", error3.getObject(0).getString("type"));
		assertEquals("confirm", error3.getObject(0).getString("path"));
		assertEquals("Confirmation message must be a boolean", error3.getObject(0).getString("reason"));
		assertEquals(Status.ACTIVE, crm.findLocationSummary(locationId).getStatus());

		JsonObject disable = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.put("/api/locations/" + locationId + "/disable")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("confirm", true)
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(locationId.toString(), disable.getString("locationId"));
		assertEquals("Inactive", disable.getString("status"));
		assertEquals(ORG_NAME.getEnglishName(), disable.getString("displayName"));
		assertEquals(Status.INACTIVE, crm.findLocationSummary(locationId).getStatus());
		
		JsonArray error4 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/api/locations/" + locationId + "/enable")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(locationId.toString(), error4.getObject(0).getString("identifier"));
		assertEquals("error", error4.getObject(0).getString("type"));
		assertEquals("confirm", error4.getObject(0).getString("path"));
		assertEquals("You must send in the confirmation message", error4.getObject(0).getString("reason"));
		assertEquals(Status.INACTIVE, crm.findLocationSummary(locationId).getStatus());
		
		JsonArray error5 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/api/locations/" + locationId + "/enable")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("confirm", false)
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(locationId.toString(), error5.getObject(0).getString("identifier"));
		assertEquals("error", error5.getObject(0).getString("type"));
		assertEquals("confirm", error5.getObject(0).getString("path"));
		assertEquals("You must send in the confirmation message", error5.getObject(0).getString("reason"));
		assertEquals(Status.INACTIVE, crm.findLocationSummary(locationId).getStatus());
		
		JsonArray error6 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/api/locations/" + locationId + "/enable")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("confirm", "test")
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(locationId.toString(), error6.getObject(0).getString("identifier"));
		assertEquals("error", error6.getObject(0).getString("type"));
		assertEquals("confirm", error6.getObject(0).getString("path"));
		assertEquals("Confirmation message must be a boolean", error6.getObject(0).getString("reason"));
		assertEquals(Status.INACTIVE, crm.findLocationSummary(locationId).getStatus());
	
		JsonObject enable = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.put("/api/locations/" + locationId + "/enable")
			.header("Locale", Lang.FRENCH)
			.content(new JsonObject()
				.with("confirm", true)
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(locationId.toString(), enable.getString("locationId"));
		assertEquals("Actif", enable.getString("status"));
		assertEquals(ORG_NAME.getEnglishName(), disable.getString("displayName"));
		assertEquals(Status.ACTIVE, crm.findLocationSummary(locationId).getStatus());
	}
//	
//	@Test
//	public void testLocationWithLongName() throws Exception {
//		JsonArray json = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
//			.post("/api/locations")
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
//	public void testLocationWithNoName() throws Exception {
//		JsonArray json = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
//			.post("/api/locations")
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
