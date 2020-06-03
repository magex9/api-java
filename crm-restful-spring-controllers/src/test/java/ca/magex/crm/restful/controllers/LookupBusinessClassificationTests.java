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

public class LookupBusinessClassificationTests extends AbstractControllerTests {
	
	@Before
	public void setup() {
		initialization.reset();
	}
	
	@Test
	public void testListRootBusinessSectors() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/business/classifications"))
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
			.get("/api/lookup/business/classifications/4"))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals("4", json.value());
	}
	
	@Test
	public void testListEnglishBusinessSectors() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/business/classifications")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		//printLookupAsserts(json);
		assertEquals(4, json.getInt("total"));
		assertEquals(JsonArray.class, json.get("content").getClass());
		assertEquals(4, json.getArray("content").size());
		assertEquals("Developer", json.getArray("content").getString(0));
		assertEquals("Manager", json.getArray("content").getString(1));
		assertEquals("Systems Analyst", json.getArray("content").getString(2));
		assertEquals("Team Lead", json.getArray("content").getString(3));
	}
	
	@Test
	public void testFindEnglishBusinessSector() throws Exception {
		JsonText json = (JsonText)JsonParser.parse(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/business/classifications/4")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals("Systems Analyst", json.value());
	}
	
	@Test
	public void testListFrenchBusinessSectors() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/business/classifications")
			.header("Locale", Lang.FRENCH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		//printLookupAsserts(json);
		assertEquals(4, json.getInt("total"));
		assertEquals(JsonArray.class, json.get("content").getClass());
		assertEquals(4, json.getArray("content").size());
		assertEquals("Analyste", json.getArray("content").getString(0));
		assertEquals("Chef d'équipe", json.getArray("content").getString(1));
		assertEquals("Développeur", json.getArray("content").getString(2));
		assertEquals("Gestionnaire", json.getArray("content").getString(3));
	}
	
	@Test
	public void testFindFrenchBusinessSector() throws Exception {
		JsonText json = (JsonText)JsonParser.parse(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/business/classifications/4")
			.header("Locale", Lang.FRENCH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals("Analyste", json.value());
	}
	
}
