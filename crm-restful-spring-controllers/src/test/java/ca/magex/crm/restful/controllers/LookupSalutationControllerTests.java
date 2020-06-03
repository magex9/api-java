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

public class LookupSalutationControllerTests extends AbstractControllerTests {
	
	@Before
	public void setup() {
		crm.reset();
	}
	
	@Test
	public void testListRootSalutations() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/salutations"))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(3, json.getInt("total"));
		assertEquals(JsonArray.class, json.get("content").getClass());
		assertEquals(3, json.getArray("content").size());
		assertEquals("1", json.getArray("content").getString(0));
		assertEquals("2", json.getArray("content").getString(1));
		assertEquals("3", json.getArray("content").getString(2));
	}
	
	@Test
	public void testFindRootSalutation() throws Exception {
		JsonText json = (JsonText)JsonParser.parse(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/salutations/3"))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals("3", json.value());
	}
	
	@Test
	public void testListEnglishSalutations() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/salutations")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(3, json.getInt("total"));
		assertEquals(JsonArray.class, json.get("content").getClass());
		assertEquals(3, json.getArray("content").size());
		assertEquals("Miss", json.getArray("content").getString(0));
		assertEquals("Mr.", json.getArray("content").getString(1));
		assertEquals("Mrs.", json.getArray("content").getString(2));
	}
	
	@Test
	public void testFindEnglishSalutation() throws Exception {
		JsonText json = (JsonText)JsonParser.parse(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/salutations/3")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals("Mr.", json.value());
	}
	
	@Test
	public void testListFrenchSalutations() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/salutations")
			.header("Locale", Lang.FRENCH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(3, json.getInt("total"));
		assertEquals(JsonArray.class, json.get("content").getClass());
		assertEquals(3, json.getArray("content").size());
		assertEquals("M.", json.getArray("content").getString(0));
		assertEquals("Mlle.", json.getArray("content").getString(1));
		assertEquals("Mme.", json.getArray("content").getString(2));
	}
	
	@Test
	public void testFindFrenchSalutation() throws Exception {
		JsonText json = (JsonText)JsonParser.parse(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/salutations/3")
			.header("Locale", Lang.FRENCH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals("M.", json.value());
	}
	
}
