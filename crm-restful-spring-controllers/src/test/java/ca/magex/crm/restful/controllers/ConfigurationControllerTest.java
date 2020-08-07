package ca.magex.crm.restful.controllers;

import static org.junit.Assert.*;

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
			.get("/rest/api.json")
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
	public void testConfigPathsExist() throws Exception {
		JsonObject json = getJsonConfig();
		Map<String, List<String>> paths = new HashMap<>();
		paths.put("/rest/organizations", List.of("get", "post"));
		paths.put("/rest/organizations/count", List.of("get"));
		paths.put("/rest/organizations/{organizationId}", List.of("get", "patch"));
		paths.put("/rest/organizations/{organizationId}/summary", List.of("get"));
		paths.put("/rest/organizations/{organizationId}/mainLocation", List.of("get"));
		paths.put("/rest/organizations/{organizationId}/enable", List.of("put"));
		paths.put("/rest/organizations/{organizationId}/disable", List.of("put"));
		paths.put("/rest/locations", List.of("get", "post"));
		paths.put("/rest/locations/count", List.of("get"));
		paths.put("/rest/locations/{locationId}", List.of("get", "patch"));
		paths.put("/rest/locations/{locationId}/summary", List.of("get"));
		paths.put("/rest/locations/{locationId}/enable", List.of("put"));
		paths.put("/rest/locations/{locationId}/disable", List.of("put"));
		paths.put("/rest/persons", List.of("get", "post"));
		paths.put("/rest/persons/count", List.of("get"));
		paths.put("/rest/persons/{personId}", List.of("get", "patch"));
		paths.put("/rest/persons/{personId}/summary", List.of("get"));
		paths.put("/rest/persons/{personId}/legalName", List.of("get"));
		paths.put("/rest/persons/{personId}/address", List.of("get"));
		paths.put("/rest/persons/{personId}/communication", List.of("get"));
		paths.put("/rest/persons/{personId}/position", List.of("get"));
		paths.put("/rest/persons/{personId}/user", List.of("get"));
		paths.put("/rest/persons/{personId}/enable", List.of("put"));
		paths.put("/rest/persons/{personId}/disable", List.of("put"));
		paths.put("/rest/persons/{personId}/roles", List.of("get", "post"));
		paths.put("/rest/persons/{personId}/roles/{roleId}", List.of("put", "delete"));
		paths.put("/rest/lookups/{lookupId}/{locale}", List.of("get"));
		
		assertEquals(paths.size(), json.getObject("paths").size());
		for (String key : paths.keySet()) {
			assertTrue(json.getObject("paths").contains(key));
			assertEquals(paths.get(key), json.getObject("paths").getObject(key).keys());
		}
	}
	
	@Test
	public void testConfigComponentsExist() throws Exception {
		JsonObject json = getJsonConfig();
		Map<String, List<String>> paths = new HashMap<>();
		JsonObject schemas = json.getObject("components").getObject("schemas");
		for (String key : schemas.keys()) {
			System.out.println("components.put(\"" + key + "\", List.of(" + schemas.getObject(key).keys().stream().map(i -> "\"" + i + "\"").collect(Collectors.joining(", ")) + ");");
		}
		
		
	}
	
}
