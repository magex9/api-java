package ca.magex.crm.restful.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import ca.magex.crm.api.system.Lang;
import ca.magex.json.model.JsonObject;

public class ConfigurationControllerTest extends AbstractControllerTests {
	
	@Before
	public void setup() {
		config.reset();
	}
	
	@Test
	public void testSwaggerUI() throws Exception {
		String html = mockMvc.perform(MockMvcRequestBuilders
			.get("/rest")
			.header("Locale", Lang.ENGLISH))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString();
		
		assertTrue(html.contains("/crm/rest/api.json"));
	}
	
	@Test
	public void testJsonConfig() throws Exception {
		String json = mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/api.json")
			.header("Locale", Lang.ENGLISH))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString();
		assertTrue(json.replaceAll("\r", "").startsWith("{\n" + 
				"  \"openapi\": \"3.0.0\",\n" + 
				"  \"info\": {\n" + 
				"    \"version\": \"1.0.0\",\n" + 
				"    \"title\": \"Customer Relationship Management\"\n" + 
				"  },"));
	}
	
	public JsonObject getJsonConfig() throws Exception {
		return new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/oas.json")
			.header("Locale", Lang.ENGLISH))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
	}
	
	@Test
	public void testRootElementsExist() throws Exception {
		JsonObject json = getJsonConfig();
		assertEquals(List.of("openapi", "info", "security", "servers", "paths", "components"), json.getObject("paths").getObject("/rest/persons").getObject("get").getArray("parameters").getObject(6).keys());
	}
	
	@Test
	public void testExpectedPathMethods() throws Exception {
		Map<String, List<String>> map = new HashMap<>();
		map.put("/rest/organizations", List.of("get", "post"));
		map.put("/rest/organizations/details", List.of("get"));
		map.put("/rest/organizations/count", List.of("get"));
		map.put("/rest/organizations/{organizationId}", List.of("get", "patch"));
		map.put("/rest/organizations/{organizationId}/details", List.of("get"));
		map.put("/rest/organizations/{organizationId}/mainLocation", List.of("get", "put"));
		map.put("/rest/organizations/{organizationId}/mainContact", List.of("get", "put"));
		map.put("/rest/organizations/{organizationId}/authenticationGroups", List.of("get", "put"));
		map.put("/rest/organizations/{organizationId}/businessGroups", List.of("get", "put"));
		map.put("/rest/organizations/{organizationId}/enable", List.of("put"));
		map.put("/rest/organizations/{organizationId}/disable", List.of("put"));
//		map.put("/rest/locations", List.of("get", "post"));
//		map.put("/rest/locations/count", List.of("get"));
//		map.put("/rest/locations/{locationId}", List.of("get", "patch"));
//		map.put("/rest/locations/{locationId}/summary", List.of("get"));
//		map.put("/rest/locations/{locationId}/enable", List.of("put"));
//		map.put("/rest/locations/{locationId}/disable", List.of("put"));
//		map.put("/rest/persons", List.of("get", "post"));
//		map.put("/rest/persons/count", List.of("get"));
//		map.put("/rest/persons/{personId}", List.of("get", "patch"));
//		map.put("/rest/persons/{personId}/summary", List.of("get"));
//		map.put("/rest/persons/{personId}/legalName", List.of("get"));
//		map.put("/rest/persons/{personId}/address", List.of("get"));
//		map.put("/rest/persons/{personId}/communication", List.of("get"));
//		map.put("/rest/persons/{personId}/position", List.of("get"));
//		map.put("/rest/persons/{personId}/user", List.of("get"));
//		map.put("/rest/persons/{personId}/enable", List.of("put"));
//		map.put("/rest/persons/{personId}/disable", List.of("put"));
//		map.put("/rest/persons/{personId}/roles", List.of("get", "post"));
//		map.put("/rest/persons/{personId}/roles/{roleId}", List.of("put", "delete"));
//		map.put("/rest/lookups/{lookupId}/{locale}", List.of("get"));
		
		JsonObject paths = getJsonConfig().getObject("paths");
		assertEquals(map.size(), paths.size());
		for (String key : map.keySet()) {
			assertTrue(paths.contains(key));
			assertEquals(map.get(key), paths.getObject(key).keys());
		}
	}
	
	@Test
	public void testExpectedSchemas() throws Exception {
		Map<String, List<String>> map = new HashMap<>();
		map.put("OrganizationSummary", List.of("description", "type", "required", "properties"));
		map.put("OrganizationDetails", List.of("description", "type", "required", "properties"));
		map.put("LocationSummary", List.of("description", "type", "required", "properties"));
		map.put("LocationDetails", List.of("description", "type", "required", "properties"));
		map.put("PersonSummary", List.of("description", "type", "required", "properties"));
		map.put("PersonDetails", List.of("description", "type", "required", "properties"));
		map.put("UserSummary", List.of("description", "type", "required", "properties"));
		map.put("UserDetails", List.of("description", "type", "required", "properties"));
		map.put("PersonName", List.of("description", "type", "required", "properties"));
		map.put("Telephone", List.of("description", "type", "required", "properties"));
		map.put("Communication", List.of("description", "type", "required", "properties"));
		map.put("MailingAddress", List.of("description", "type", "required", "properties"));
		map.put("Message", List.of("description", "type", "required", "properties"));
		map.put("Type", List.of("description", "type", "oneOf"));
		map.put("Status", List.of("description", "type", "oneOf"));
		map.put("BadRequestException", List.of("description", "type", "required", "properties"));
		map.put("PermissionDeniedException", List.of("description", "type", "required", "properties"));
		map.put("ItemNotFoundException", List.of("description", "type", "required", "properties"));
		map.put("ApiException", List.of("description", "type", "required", "properties"));
		
		JsonObject schemas = getJsonConfig().getObject("components").getObject("schemas");
		assertEquals(map.size(), schemas.size());
		for (String key : map.keySet()) {
			assertTrue(schemas.contains(key));
			assertEquals(map.get(key), schemas.getObject(key).keys());
		}
	}
	
}
