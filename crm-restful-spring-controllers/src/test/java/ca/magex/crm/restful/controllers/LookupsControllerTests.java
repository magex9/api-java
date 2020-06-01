package ca.magex.crm.restful.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import ca.magex.crm.api.system.Lang;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonObject;

public class LookupsControllerTests extends AbstractControllerTests {
	
	@Before
	public void setup() {
		initialization.reset();
	}
	
	@Test
	public void testListRootStatuses() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/status"))
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
	public void testListEnglishStatuses() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/status")
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
	public void testListFrenchStatuses() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/status")
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
		assertEquals("Mrs.", json.getArray("content").getString(1));
		assertEquals("Mr.", json.getArray("content").getString(2));
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
		assertEquals("Mlle.", json.getArray("content").getString(0));
		assertEquals("Mme.", json.getArray("content").getString(1));
		assertEquals("M.", json.getArray("content").getString(2));
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
	public void testListRootCountries() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/countries"))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(253, json.getInt("total"));
		assertEquals(JsonArray.class, json.get("content").getClass());
		assertEquals(253, json.getArray("content").size());
		assertEquals("CA", json.getArray("content").getString(0));
		assertEquals("US", json.getArray("content").getString(1));
		assertEquals("MX", json.getArray("content").getString(2));
		assertEquals("AD", json.getArray("content").getString(3));
		assertEquals("AE", json.getArray("content").getString(4));
		assertEquals("AF", json.getArray("content").getString(5));
		assertEquals("ZW", json.getArray("content").getString(251));
		assertEquals("ZZ", json.getArray("content").getString(252));
	}
	
	@Test
	public void testListEnglishCountries() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/countries")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(253, json.getInt("total"));
		assertEquals(JsonArray.class, json.get("content").getClass());
		assertEquals(253, json.getArray("content").size());
		assertEquals("Canada", json.getArray("content").getString(0));
		assertEquals("United States", json.getArray("content").getString(1));
		assertEquals("Mexico", json.getArray("content").getString(2));
		assertEquals("Afghanistan", json.getArray("content").getString(3));
		assertEquals("Åland Islands", json.getArray("content").getString(4));
		assertEquals("Albania", json.getArray("content").getString(5));
		assertEquals("Algeria", json.getArray("content").getString(6));
		assertEquals("Zimbabwe", json.getArray("content").getString(251));
		assertEquals("Other", json.getArray("content").getString(252));
	}
	
	@Test
	public void testListFrenchCountries() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/countries")
			.header("Locale", Lang.FRENCH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(253, json.getInt("total"));
		assertEquals(JsonArray.class, json.get("content").getClass());
		assertEquals(253, json.getArray("content").size());
		assertEquals("Canada", json.getArray("content").getString(0));
		assertEquals("États-Unis d'Amérique", json.getArray("content").getString(1));
		assertEquals("Mexique", json.getArray("content").getString(2));
		assertEquals("Afghanistan", json.getArray("content").getString(3));
		assertEquals("Afrique du Sud", json.getArray("content").getString(4));
		assertEquals("Albanie", json.getArray("content").getString(5));
		assertEquals("Zimbabwe", json.getArray("content").getString(251));
		assertEquals("Autre", json.getArray("content").getString(252));
	}
	
}
