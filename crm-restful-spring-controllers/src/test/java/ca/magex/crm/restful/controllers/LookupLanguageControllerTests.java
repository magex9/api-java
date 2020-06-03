package ca.magex.crm.restful.controllers;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import ca.magex.crm.api.system.Lang;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonParser;
import ca.magex.json.model.JsonText;

public class LookupLanguageControllerTests extends AbstractControllerTests {
	
	@Before
	public void setup() {
		initialization.reset();
	}
	
	@Test
	public void testListRootLanguages() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/languages"))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(2, json.getInt("total"));
		assertEquals(JsonArray.class, json.get("content").getClass());
		assertEquals(2, json.getArray("content").size());
		assertEquals("EN", json.getArray("content").getString(0));
		assertEquals("FR", json.getArray("content").getString(1));
	}
	
	@Test
	public void testFindRootLanguages() throws Exception {
		JsonText json = (JsonText)JsonParser.parse(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/languages/en"))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals("EN", json.value());
	}
	
	@Test
	public void testListEnglishLanguages() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/languages")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(2, json.getInt("total"));
		assertEquals(JsonArray.class, json.get("content").getClass());
		assertEquals(2, json.getArray("content").size());
		assertEquals("English", json.getArray("content").getString(0));
		assertEquals("French", json.getArray("content").getString(1));
	}
	
	@Test
	public void testFindEnglishLanguage() throws Exception {
		JsonText json = (JsonText)JsonParser.parse(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/languages/en")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals("English", json.value());
	}
	
	@Test
	public void testListFrenchLanguages() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/languages")
			.header("Locale", Lang.FRENCH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(2, json.getInt("total"));
		assertEquals(JsonArray.class, json.get("content").getClass());
		assertEquals(2, json.getArray("content").size());
		assertEquals("Anglais", json.getArray("content").getString(0));
		assertEquals("Francais", json.getArray("content").getString(1));
	}
	
	@Test
	public void testFindFrenchLanguage() throws Exception {
		JsonText json = (JsonText)JsonParser.parse(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/languages/en")
			.header("Locale", Lang.FRENCH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals("Anglais", json.value());
	}
	
	
}
