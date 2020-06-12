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

public class LookupStatusControllerTests extends AbstractControllerTests {
	
	@Before
	public void setup() {
		crm.reset();
	}
	
	@Test
	public void testListRootStatuses() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/lookup/status"))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(3, json.getInt("total"));
		assertEquals(JsonArray.class, json.get("content").getClass());
		assertEquals(3, json.getArray("content").size());
		assertEquals("active", json.getArray("content").getString(0));
		assertEquals("inactive", json.getArray("content").getString(1));
		assertEquals("pending", json.getArray("content").getString(2));
	}
	
	@Test
	public void testFindRootStatus() throws Exception {
		JsonText json = (JsonText)JsonParser.parse(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/lookup/status/active"))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals("active", json.value());
	}
	
	@Test
	public void testListEnglishStatuses() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/lookup/status")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(3, json.getInt("total"));
		assertEquals(JsonArray.class, json.get("content").getClass());
		assertEquals(3, json.getArray("content").size());
		assertEquals("Active", json.getArray("content").getString(0));
		assertEquals("Inactive", json.getArray("content").getString(1));
		assertEquals("Pending", json.getArray("content").getString(2));
	}
	
	@Test
	public void testFindEnglishStatus() throws Exception {
		JsonText json = (JsonText)JsonParser.parse(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/lookup/status/active")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals("Active", json.value());
	}
	
	@Test
	public void testListFrenchStatuses() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/lookup/status")
			.header("Locale", Lang.FRENCH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(3, json.getInt("total"));
		assertEquals(JsonArray.class, json.get("content").getClass());
		assertEquals(3, json.getArray("content").size());
		assertEquals("Actif", json.getArray("content").getString(0));
		assertEquals("Inactif", json.getArray("content").getString(1));
		assertEquals("En attente", json.getArray("content").getString(2));
	}
	
	@Test
	public void testFindFrenchStatus() throws Exception {
		JsonText json = (JsonText)JsonParser.parse(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/lookup/status/active")
			.header("Locale", Lang.FRENCH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals("Actif", json.value());
	}
		
}
