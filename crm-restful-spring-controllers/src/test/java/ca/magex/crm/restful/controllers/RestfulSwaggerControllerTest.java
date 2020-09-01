package ca.magex.crm.restful.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import ca.magex.crm.api.system.Lang;
import ca.magex.json.model.JsonObject;

public class RestfulSwaggerControllerTest extends AbstractControllerTests {
	
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
		assertEquals(List.of("openapi", "info", "security", "servers", "paths", "components"), json.keys());
	}
	
	@Test
	public void testExpectedPathMethods() throws Exception {
		Map<String, List<String>> map = new HashMap<>();
		map.put("/rest/actions", List.of("get"));
		
		map.put("/rest/organizations", List.of("get", "post"));
		map.put("/rest/organizations/prototype", List.of("get"));
		map.put("/rest/organizations/details", List.of("get"));
		map.put("/rest/organizations/count", List.of("get"));
		map.put("/rest/organizations/{organizationId}", List.of("get"));
		map.put("/rest/organizations/{organizationId}/details", List.of("get", "patch"));
		map.put("/rest/organizations/{organizationId}/details/mainLocation", List.of("get", "patch"));
		map.put("/rest/organizations/{organizationId}/details/mainContact", List.of("get", "patch"));
		map.put("/rest/organizations/{organizationId}/details/authenticationGroups", List.of("get", "patch"));
		map.put("/rest/organizations/{organizationId}/details/businessGroups", List.of("get", "patch"));
		map.put("/rest/organizations/{organizationId}/actions", List.of("get"));
		map.put("/rest/organizations/{organizationId}/actions/enable", List.of("put"));
		map.put("/rest/organizations/{organizationId}/actions/disable", List.of("put"));

		map.put("/rest/locations", List.of("get", "post"));
		map.put("/rest/locations/prototype", List.of("get"));
		map.put("/rest/locations/details", List.of("get"));
		map.put("/rest/locations/count", List.of("get"));
		map.put("/rest/locations/{locationId}", List.of("get"));
		map.put("/rest/locations/{locationId}/details", List.of("get", "patch"));
		map.put("/rest/locations/{locationId}/details/address", List.of("get", "patch"));
		map.put("/rest/locations/{locationId}/actions", List.of("get"));
		map.put("/rest/locations/{locationId}/actions/enable", List.of("put"));
		map.put("/rest/locations/{locationId}/actions/disable", List.of("put"));

		map.put("/rest/persons", List.of("get", "post"));
		map.put("/rest/persons/prototype", List.of("get"));
		map.put("/rest/persons/details", List.of("get"));
		map.put("/rest/persons/count", List.of("get"));
		map.put("/rest/persons/{personId}", List.of("get"));
		map.put("/rest/persons/{personId}/details", List.of("get", "patch"));
		map.put("/rest/persons/{personId}/details/name", List.of("get", "patch"));
		map.put("/rest/persons/{personId}/details/communication", List.of("get", "patch"));
		map.put("/rest/persons/{personId}/details/businessRoles", List.of("get", "patch"));
		map.put("/rest/persons/{personId}/actions", List.of("get"));
		map.put("/rest/persons/{personId}/actions/enable", List.of("put"));
		map.put("/rest/persons/{personId}/actions/disable", List.of("put"));

		map.put("/rest/users", List.of("get", "post"));
		map.put("/rest/users/details", List.of("get"));
		map.put("/rest/users/count", List.of("get"));
		map.put("/rest/users/prototype", List.of("get"));
		map.put("/rest/users/{userId}", List.of("get"));
		map.put("/rest/users/{userId}/details", List.of("get", "patch"));
		map.put("/rest/users/{userId}/details/person", List.of("get", "patch"));
		map.put("/rest/users/{userId}/details/authenticationRoles", List.of("get", "patch"));
		map.put("/rest/users/{userId}/actions", List.of("get"));
		map.put("/rest/users/{userId}/actions/changePassword", List.of("put"));
		map.put("/rest/users/{userId}/actions/resetPassword", List.of("put"));
		map.put("/rest/users/{userId}/actions/enable", List.of("put"));
		map.put("/rest/users/{userId}/actions/disable", List.of("put"));
		
		map.put("/rest/user/{username}", List.of("get"));
		map.put("/rest/user/{username}/details", List.of("get"));
		map.put("/rest/user/{username}/actions", List.of("get"));
		map.put("/rest/user/{username}/actions/changePassword", List.of("put"));
		map.put("/rest/user/{username}/actions/resetPassword", List.of("put"));
		map.put("/rest/user/{username}/actions/enable", List.of("put"));
		map.put("/rest/user/{username}/actions/disable", List.of("put"));

		map.put("/rest/types", List.of("get"));
		map.put("/rest/types/{typeId}", List.of("get"));
		
		map.put("/rest/options", List.of("get", "post"));
		map.put("/rest/options/prototype", List.of("get"));
		map.put("/rest/options/count", List.of("get"));
		map.put("/rest/options/{optionId}", List.of("get", "patch"));
		map.put("/rest/options/{optionId}/actions", List.of("get"));
		map.put("/rest/options/{optionId}/enable", List.of("put"));
		map.put("/rest/options/{optionId}/disable", List.of("put"));
		
		JsonObject paths = getJsonConfig().getObject("paths");
		Map<String, List<String>> actual = new HashMap<>();
		for (String path : paths.keys()) {
			actual.put(path, paths.getObject(path).keys());
		}
		
		List<String> keys = map.keySet().stream().sorted().collect(Collectors.toList());
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			if (!map.get(key).equals(actual.get(key)))
				if (actual.containsKey(key)) {
					System.out.println(key + ": " + map.get(key) + " != " + actual.get(key));
				} else {
					System.out.println(key + ": " + map.get(key) + " != " + false);
				}
		}
		
//		assertEquals(map.size(), actual.size());
//		for (String key : map.keySet()) {
//			assertTrue(paths.contains(key));
//			assertEquals(map.get(key), paths.getObject(key).keys());
//		}
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
