package ca.magex.crm.restful.controllers;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import ca.magex.crm.api.system.Lang;

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
	
}
