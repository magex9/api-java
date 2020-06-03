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

public class LookupBusinessSectorTests extends AbstractControllerTests {
	
	@Before
	public void setup() {
		initialization.reset();
	}
	
	@Test
	public void testListRootBusinessSectors() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/business/sectors"))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		//printLookupAsserts(json);
		assertEquals(4, json.getInt("total"));
		assertEquals(JsonArray.class, json.get("content").getClass());
		assertEquals(4, json.getArray("content").size());
		assertEquals("1", json.getArray("content").getString(0));
		assertEquals("2", json.getArray("content").getString(1));
		assertEquals("3", json.getArray("content").getString(2));
		assertEquals("4", json.getArray("content").getString(3));
	}
	
	@Test
	public void testFindRootBusinessSectors() throws Exception {
		JsonText json = (JsonText)JsonParser.parse(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/business/sectors/4"))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals("4", json.value());
	}
	
	@Test
	public void testListEnglishBusinessSectors() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/business/sectors")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		//printLookupAsserts(json);
		assertEquals(4, json.getInt("total"));
		assertEquals(JsonArray.class, json.get("content").getClass());
		assertEquals(4, json.getArray("content").size());
		assertEquals("Compliance", json.getArray("content").getString(0));
		assertEquals("External", json.getArray("content").getString(1));
		assertEquals("Information Technology", json.getArray("content").getString(2));
		assertEquals("Intel", json.getArray("content").getString(3));
	}
	
	@Test
	public void testFindEnglishBusinessSector() throws Exception {
		JsonText json = (JsonText)JsonParser.parse(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/business/sectors/4")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals("Information Technology", json.value());
	}
	
	@Test
	public void testListFrenchBusinessSectors() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/business/sectors")
			.header("Locale", Lang.FRENCH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		//printLookupAsserts(json);
		assertEquals(4, json.getInt("total"));
		assertEquals(JsonArray.class, json.get("content").getClass());
		assertEquals(4, json.getArray("content").size());
		assertEquals("Compliance", json.getArray("content").getString(0));
		assertEquals("External", json.getArray("content").getString(1));
		assertEquals("Intel", json.getArray("content").getString(2));
		assertEquals("Technologie Informatique", json.getArray("content").getString(3));
	}
	
	@Test
	public void testFindFrenchBusinessSector() throws Exception {
		JsonText json = (JsonText)JsonParser.parse(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/business/sectors/4")
			.header("Locale", Lang.FRENCH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals("Technologie Informatique", json.value());
	}
	
}
