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

public class LookupProvinceTests extends AbstractControllerTests {
	
	@Before
	public void setup() {
		initialization.reset();
	}
	
	@Test
	public void testListRootCanadianProvinces() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/countries/ca/provinces"))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(13, json.getInt("total"));
		assertEquals(JsonArray.class, json.get("content").getClass());
		assertEquals(13, json.getArray("content").size());
		assertEquals("AB", json.getArray("content").getString(0));
		assertEquals("BC", json.getArray("content").getString(1));
		assertEquals("MB", json.getArray("content").getString(2));
	    assertEquals("NB", json.getArray("content").getString(3));
	    assertEquals("NL", json.getArray("content").getString(4));
	    assertEquals("NS", json.getArray("content").getString(5));
	    assertEquals("NT", json.getArray("content").getString(6));
	    assertEquals("NU", json.getArray("content").getString(7));
	    assertEquals("ON", json.getArray("content").getString(8));
	    assertEquals("PE", json.getArray("content").getString(9));
	    assertEquals("QC", json.getArray("content").getString(10));
	    assertEquals("SK", json.getArray("content").getString(11));
	    assertEquals("YT", json.getArray("content").getString(12));
	}
	
	@Test
	public void testFindRootCanadianProvince() throws Exception {
		JsonText json = (JsonText)JsonParser.parse(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/countries/ca/provinces/bc"))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals("BC", json.value());
	}
	
	@Test
	public void testListEnglishCanadianProvinces() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/countries/ca/provinces")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(13, json.getInt("total"));
		assertEquals(JsonArray.class, json.get("content").getClass());
		assertEquals(13, json.getArray("content").size());
		assertEquals("Alberta", json.getArray("content").getString(0));
		assertEquals("British Columbia", json.getArray("content").getString(1));
		assertEquals("Manitoba", json.getArray("content").getString(2));
		assertEquals("New Brunswick", json.getArray("content").getString(3));
		assertEquals("Newfoundland and Labrador", json.getArray("content").getString(4));
		assertEquals("Northwest Territories", json.getArray("content").getString(5));
		assertEquals("Nova Scotia", json.getArray("content").getString(6));
		assertEquals("Nunavut", json.getArray("content").getString(7));
		assertEquals("Ontario", json.getArray("content").getString(8));
		assertEquals("Prince Edward Island", json.getArray("content").getString(9));
		assertEquals("Quebec", json.getArray("content").getString(10));
		assertEquals("Saskatchewan", json.getArray("content").getString(11));
		assertEquals("Yukon", json.getArray("content").getString(12));
	}
	
	@Test
	public void testFindEnglishCanadianProvince() throws Exception {
		JsonText json = (JsonText)JsonParser.parse(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/countries/ca/provinces/bc")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals("British Columbia", json.value());
	}
	
	@Test
	public void testListFrenchCanadianProvinces() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/countries/ca/provinces")
			.header("Locale", Lang.FRENCH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(13, json.getInt("total"));
		assertEquals(JsonArray.class, json.get("content").getClass());
		assertEquals(13, json.getArray("content").size());
		assertEquals("Alberta", json.getArray("content").getString(0));
		assertEquals("Colombie-Britannique", json.getArray("content").getString(1));
		assertEquals("Île-du-Prince-Èdouard", json.getArray("content").getString(2));
		assertEquals("Manitoba", json.getArray("content").getString(3));
		assertEquals("Nouveau-Brunswick", json.getArray("content").getString(4));
		assertEquals("Nouvelle-Ècosse", json.getArray("content").getString(5));
		assertEquals("Nunavut", json.getArray("content").getString(6));
		assertEquals("Ontario", json.getArray("content").getString(7));
		assertEquals("Québec", json.getArray("content").getString(8));
		assertEquals("Saskatchewan", json.getArray("content").getString(9));
		assertEquals("Terre-Neuve et Labrador", json.getArray("content").getString(10));
		assertEquals("Territoires du Nord-Ouest", json.getArray("content").getString(11));
		assertEquals("Yukon", json.getArray("content").getString(12));
	}
	
	@Test
	public void testFindFrenchCanadianProvince() throws Exception {
		JsonText json = (JsonText)JsonParser.parse(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/countries/ca/provinces/bc")
			.header("Locale", Lang.FRENCH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals("Colombie-Britannique", json.value());
	}
	
	@Test
	public void testListRootAmericanStates() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/countries/us/provinces"))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(51, json.getInt("total"));
		assertEquals(JsonArray.class, json.get("content").getClass());
		assertEquals(51, json.getArray("content").size());
		assertEquals("AK", json.getArray("content").getString(0));
		assertEquals("AL", json.getArray("content").getString(1));
		assertEquals("AR", json.getArray("content").getString(2));
		assertEquals("AZ", json.getArray("content").getString(3));
		assertEquals("CA", json.getArray("content").getString(4));
		assertEquals("CO", json.getArray("content").getString(5));
		assertEquals("CT", json.getArray("content").getString(6));
		assertEquals("DC", json.getArray("content").getString(7));
		assertEquals("DE", json.getArray("content").getString(8));
		assertEquals("FL", json.getArray("content").getString(9));
		assertEquals("GA", json.getArray("content").getString(10));
		assertEquals("HI", json.getArray("content").getString(11));
		assertEquals("IA", json.getArray("content").getString(12));
		assertEquals("ID", json.getArray("content").getString(13));
		assertEquals("IL", json.getArray("content").getString(14));
		assertEquals("IN", json.getArray("content").getString(15));
		assertEquals("KS", json.getArray("content").getString(16));
		assertEquals("KY", json.getArray("content").getString(17));
		assertEquals("LA", json.getArray("content").getString(18));
		assertEquals("MA", json.getArray("content").getString(19));
		assertEquals("MD", json.getArray("content").getString(20));
		assertEquals("ME", json.getArray("content").getString(21));
		assertEquals("MI", json.getArray("content").getString(22));
		assertEquals("MN", json.getArray("content").getString(23));
		assertEquals("MO", json.getArray("content").getString(24));
		assertEquals("MS", json.getArray("content").getString(25));
		assertEquals("MT", json.getArray("content").getString(26));
		assertEquals("NC", json.getArray("content").getString(27));
		assertEquals("ND", json.getArray("content").getString(28));
		assertEquals("NE", json.getArray("content").getString(29));
		assertEquals("NH", json.getArray("content").getString(30));
		assertEquals("NJ", json.getArray("content").getString(31));
		assertEquals("NM", json.getArray("content").getString(32));
		assertEquals("NV", json.getArray("content").getString(33));
		assertEquals("NY", json.getArray("content").getString(34));
		assertEquals("OH", json.getArray("content").getString(35));
		assertEquals("OK", json.getArray("content").getString(36));
		assertEquals("OR", json.getArray("content").getString(37));
		assertEquals("PA", json.getArray("content").getString(38));
		assertEquals("RI", json.getArray("content").getString(39));
		assertEquals("SC", json.getArray("content").getString(40));
		assertEquals("SD", json.getArray("content").getString(41));
		assertEquals("TN", json.getArray("content").getString(42));
		assertEquals("TX", json.getArray("content").getString(43));
		assertEquals("UT", json.getArray("content").getString(44));
		assertEquals("VA", json.getArray("content").getString(45));
		assertEquals("VT", json.getArray("content").getString(46));
		assertEquals("WA", json.getArray("content").getString(47));
		assertEquals("WI", json.getArray("content").getString(48));
		assertEquals("WV", json.getArray("content").getString(49));
		assertEquals("WY", json.getArray("content").getString(50));
	}
	
	@Test
	public void testListEnglishAmericanStates() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/countries/us/provinces")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(51, json.getInt("total"));
		assertEquals(JsonArray.class, json.get("content").getClass());
		assertEquals(51, json.getArray("content").size());
		assertEquals("Alabama", json.getArray("content").getString(0));
		assertEquals("Alaska", json.getArray("content").getString(1));
		assertEquals("Arizona", json.getArray("content").getString(2));
		assertEquals("Arkansas", json.getArray("content").getString(3));
		assertEquals("California", json.getArray("content").getString(4));
		assertEquals("Colorado", json.getArray("content").getString(5));
		assertEquals("Connecticut", json.getArray("content").getString(6));
		assertEquals("Delaware", json.getArray("content").getString(7));
		assertEquals("District of Columbia", json.getArray("content").getString(8));
		assertEquals("Florida", json.getArray("content").getString(9));
		assertEquals("Georgia", json.getArray("content").getString(10));
		assertEquals("Hawaii", json.getArray("content").getString(11));
		assertEquals("Idaho", json.getArray("content").getString(12));
		assertEquals("Illinois", json.getArray("content").getString(13));
		assertEquals("Indiana", json.getArray("content").getString(14));
		assertEquals("Iowa", json.getArray("content").getString(15));
		assertEquals("Kansas", json.getArray("content").getString(16));
		assertEquals("Kentucky", json.getArray("content").getString(17));
		assertEquals("Louisiana", json.getArray("content").getString(18));
		assertEquals("Maine", json.getArray("content").getString(19));
		assertEquals("Maryland", json.getArray("content").getString(20));
		assertEquals("Massachusetts", json.getArray("content").getString(21));
		assertEquals("Michigan", json.getArray("content").getString(22));
		assertEquals("Minnesota", json.getArray("content").getString(23));
		assertEquals("Mississippi", json.getArray("content").getString(24));
		assertEquals("Missouri", json.getArray("content").getString(25));
		assertEquals("Montana", json.getArray("content").getString(26));
		assertEquals("Nebraska", json.getArray("content").getString(27));
		assertEquals("Nevada", json.getArray("content").getString(28));
		assertEquals("New Hampshire", json.getArray("content").getString(29));
		assertEquals("New Jersey", json.getArray("content").getString(30));
		assertEquals("New Mexico", json.getArray("content").getString(31));
		assertEquals("New York", json.getArray("content").getString(32));
		assertEquals("North Carolina", json.getArray("content").getString(33));
		assertEquals("North Dakota", json.getArray("content").getString(34));
		assertEquals("Ohio", json.getArray("content").getString(35));
		assertEquals("Oklahoma", json.getArray("content").getString(36));
		assertEquals("Oregon", json.getArray("content").getString(37));
		assertEquals("Pennsylvania", json.getArray("content").getString(38));
		assertEquals("Rhode Island", json.getArray("content").getString(39));
		assertEquals("South Carolina", json.getArray("content").getString(40));
		assertEquals("South Dakota", json.getArray("content").getString(41));
		assertEquals("Tennessee", json.getArray("content").getString(42));
		assertEquals("Texas", json.getArray("content").getString(43));
		assertEquals("Utah", json.getArray("content").getString(44));
		assertEquals("Vermont", json.getArray("content").getString(45));
		assertEquals("Virginia", json.getArray("content").getString(46));
		assertEquals("Washington", json.getArray("content").getString(47));
		assertEquals("West Virginia", json.getArray("content").getString(48));
		assertEquals("Wisconsin", json.getArray("content").getString(49));
		assertEquals("Wyoming", json.getArray("content").getString(50));
	}
	
	@Test
	public void testListFrenchAmericanStates() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/countries/us/provinces")
			.header("Locale", Lang.FRENCH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(51, json.getInt("total"));
		assertEquals(JsonArray.class, json.get("content").getClass());
		assertEquals(51, json.getArray("content").size());
		assertEquals("Alabama", json.getArray("content").getString(0));
		assertEquals("Alaska", json.getArray("content").getString(1));
		assertEquals("Arizona", json.getArray("content").getString(2));
		assertEquals("Arkansas", json.getArray("content").getString(3));
		assertEquals("Californie", json.getArray("content").getString(4));
		assertEquals("Carolina du Sud", json.getArray("content").getString(5));
		assertEquals("Caroline du Nord", json.getArray("content").getString(6));
		assertEquals("Colorado", json.getArray("content").getString(7));
		assertEquals("Connecticut", json.getArray("content").getString(8));
		assertEquals("Dakota du Nord", json.getArray("content").getString(9));
		assertEquals("Dakota du Sud", json.getArray("content").getString(10));
		assertEquals("Delaware", json.getArray("content").getString(11));
		assertEquals("District fédéral de Columbia", json.getArray("content").getString(12));
		assertEquals("Floride", json.getArray("content").getString(13));
		assertEquals("Géorgie", json.getArray("content").getString(14));
		assertEquals("Hawai", json.getArray("content").getString(15));
		assertEquals("Idaho", json.getArray("content").getString(16));
		assertEquals("Illinois", json.getArray("content").getString(17));
		assertEquals("Indiana", json.getArray("content").getString(18));
		assertEquals("Iowa", json.getArray("content").getString(19));
		assertEquals("Kansas", json.getArray("content").getString(20));
		assertEquals("Kentucky", json.getArray("content").getString(21));
		assertEquals("Louisiane", json.getArray("content").getString(22));
		assertEquals("Maine", json.getArray("content").getString(23));
		assertEquals("Maryland", json.getArray("content").getString(24));
		assertEquals("Massachusetts", json.getArray("content").getString(25));
		assertEquals("Michigan", json.getArray("content").getString(26));
		assertEquals("Minnesota", json.getArray("content").getString(27));
		assertEquals("Mississippi", json.getArray("content").getString(28));
		assertEquals("Missouri", json.getArray("content").getString(29));
		assertEquals("Montana", json.getArray("content").getString(30));
		assertEquals("Nebraska", json.getArray("content").getString(31));
		assertEquals("Nevada", json.getArray("content").getString(32));
		assertEquals("New Hampshire", json.getArray("content").getString(33));
		assertEquals("New Jersey", json.getArray("content").getString(34));
		assertEquals("New York", json.getArray("content").getString(35));
		assertEquals("Nouveau-Mexique", json.getArray("content").getString(36));
		assertEquals("Ohio", json.getArray("content").getString(37));
		assertEquals("Oklahoma", json.getArray("content").getString(38));
		assertEquals("Orégon", json.getArray("content").getString(39));
		assertEquals("Pennsylvanie", json.getArray("content").getString(40));
		assertEquals("Rhode Island", json.getArray("content").getString(41));
		assertEquals("Tennessee", json.getArray("content").getString(42));
		assertEquals("Texas", json.getArray("content").getString(43));
		assertEquals("Utah", json.getArray("content").getString(44));
		assertEquals("Vermont", json.getArray("content").getString(45));
		assertEquals("Virginie", json.getArray("content").getString(46));
		assertEquals("Virginie-Occidentale", json.getArray("content").getString(47));
		assertEquals("Washington", json.getArray("content").getString(48));
		assertEquals("Wisconsin", json.getArray("content").getString(49));
		assertEquals("Wyoming", json.getArray("content").getString(50));
	}
	
	@Test
	public void testListRootMexicanProvinces() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/countries/mx/provinces"))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(32, json.getInt("total"));
		assertEquals(JsonArray.class, json.get("content").getClass());
		assertEquals(32, json.getArray("content").size());
		assertEquals("AG", json.getArray("content").getString(0));
		assertEquals("BA", json.getArray("content").getString(1));
		assertEquals("BJ", json.getArray("content").getString(2));
		assertEquals("CE", json.getArray("content").getString(3));
		assertEquals("CH", json.getArray("content").getString(4));
		assertEquals("CI", json.getArray("content").getString(5));
		assertEquals("CL", json.getArray("content").getString(6));
		assertEquals("CU", json.getArray("content").getString(7));
		assertEquals("DF", json.getArray("content").getString(8));
		assertEquals("DO", json.getArray("content").getString(9));
		assertEquals("GR", json.getArray("content").getString(10));
		assertEquals("GU", json.getArray("content").getString(11));
		assertEquals("HL", json.getArray("content").getString(12));
		assertEquals("JL", json.getArray("content").getString(13));
		assertEquals("MC", json.getArray("content").getString(14));
		assertEquals("MR", json.getArray("content").getString(15));
		assertEquals("MX", json.getArray("content").getString(16));
		assertEquals("NA", json.getArray("content").getString(17));
		assertEquals("NL", json.getArray("content").getString(18));
		assertEquals("OA", json.getArray("content").getString(19));
		assertEquals("PB", json.getArray("content").getString(20));
		assertEquals("QR", json.getArray("content").getString(21));
		assertEquals("QU", json.getArray("content").getString(22));
		assertEquals("SI", json.getArray("content").getString(23));
		assertEquals("SL", json.getArray("content").getString(24));
		assertEquals("SO", json.getArray("content").getString(25));
		assertEquals("TA", json.getArray("content").getString(26));
		assertEquals("TB", json.getArray("content").getString(27));
		assertEquals("TL", json.getArray("content").getString(28));
		assertEquals("VC", json.getArray("content").getString(29));
		assertEquals("YU", json.getArray("content").getString(30));
		assertEquals("ZA", json.getArray("content").getString(31));
	}
	
	@Test
	public void testListEnglishMexicanProvinces() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/countries/mx/provinces")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(32, json.getInt("total"));
		assertEquals(JsonArray.class, json.get("content").getClass());
		assertEquals(32, json.getArray("content").size());
		assertEquals("Aguascalientas", json.getArray("content").getString(0));
		assertEquals("Baja California (North)", json.getArray("content").getString(1));
		assertEquals("Baja California (South)", json.getArray("content").getString(2));
		assertEquals("Campeche", json.getArray("content").getString(3));
		assertEquals("Chiapas", json.getArray("content").getString(4));
		assertEquals("Chihuahua", json.getArray("content").getString(5));
		assertEquals("Coahuila de Zaragoza", json.getArray("content").getString(6));
		assertEquals("Colima", json.getArray("content").getString(7));
		assertEquals("Distrito", json.getArray("content").getString(8));
		assertEquals("Durango", json.getArray("content").getString(9));
		assertEquals("Guanajuato", json.getArray("content").getString(10));
		assertEquals("Guerreo", json.getArray("content").getString(11));
		assertEquals("Hidalgo", json.getArray("content").getString(12));
		assertEquals("Jalisco", json.getArray("content").getString(13));
		assertEquals("Mexico (State)", json.getArray("content").getString(14));
		assertEquals("Michoacan de Ocampo", json.getArray("content").getString(15));
		assertEquals("Morelos", json.getArray("content").getString(16));
		assertEquals("Nayarit", json.getArray("content").getString(17));
		assertEquals("Nuevo Leon", json.getArray("content").getString(18));
		assertEquals("Oaxaca", json.getArray("content").getString(19));
		assertEquals("Puebla", json.getArray("content").getString(20));
		assertEquals("Queretaro de Arteaga", json.getArray("content").getString(21));
		assertEquals("Quintana Roo", json.getArray("content").getString(22));
		assertEquals("San Luis Potosi", json.getArray("content").getString(23));
		assertEquals("Sinaloa", json.getArray("content").getString(24));
		assertEquals("Sonora", json.getArray("content").getString(25));
		assertEquals("Tabasco", json.getArray("content").getString(26));
		assertEquals("Tamaulipas", json.getArray("content").getString(27));
		assertEquals("Tlaxcala", json.getArray("content").getString(28));
		assertEquals("Veracruz-Llave", json.getArray("content").getString(29));
		assertEquals("Yucatan", json.getArray("content").getString(30));
		assertEquals("Zacatecas", json.getArray("content").getString(31));
	}
	
	@Test
	public void testListFrenchMexicanProvinces() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/countries/mx/provinces")
			.header("Locale", Lang.FRENCH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(32, json.getInt("total"));
		assertEquals(JsonArray.class, json.get("content").getClass());
		assertEquals(32, json.getArray("content").size());
		assertEquals("Aguascalientas", json.getArray("content").getString(0));
		assertEquals("Baja California (Nord)", json.getArray("content").getString(1));
		assertEquals("Baja California (Sud)", json.getArray("content").getString(2));
		assertEquals("Campeche", json.getArray("content").getString(3));
		assertEquals("Chiapas", json.getArray("content").getString(4));
		assertEquals("Chihuahua", json.getArray("content").getString(5));
		assertEquals("Coahuila de Zaragoza", json.getArray("content").getString(6));
		assertEquals("Colima", json.getArray("content").getString(7));
		assertEquals("Distrito (Federal)", json.getArray("content").getString(8));
		assertEquals("Durango", json.getArray("content").getString(9));
		assertEquals("Guanajuato", json.getArray("content").getString(10));
		assertEquals("Guerreo", json.getArray("content").getString(11));
		assertEquals("Hidalgo", json.getArray("content").getString(12));
		assertEquals("Jalisco", json.getArray("content").getString(13));
		assertEquals("Mexico (Ètat)", json.getArray("content").getString(14));
		assertEquals("Michoacan de Ocampo", json.getArray("content").getString(15));
		assertEquals("Morelos", json.getArray("content").getString(16));
		assertEquals("Nayarit", json.getArray("content").getString(17));
		assertEquals("Nuevo Leon", json.getArray("content").getString(18));
		assertEquals("Oaxaca", json.getArray("content").getString(19));
		assertEquals("Puebla", json.getArray("content").getString(20));
		assertEquals("Queretaro de Arteaga", json.getArray("content").getString(21));
		assertEquals("Quintana Roo", json.getArray("content").getString(22));
		assertEquals("San Luis Potosi", json.getArray("content").getString(23));
		assertEquals("Sinaloa", json.getArray("content").getString(24));
		assertEquals("Sonora", json.getArray("content").getString(25));
		assertEquals("Tabasco", json.getArray("content").getString(26));
		assertEquals("Tamaulipas", json.getArray("content").getString(27));
		assertEquals("Tlaxcala", json.getArray("content").getString(28));
		assertEquals("Veracruz-Llave", json.getArray("content").getString(29));
		assertEquals("Yucatan", json.getArray("content").getString(30));
		assertEquals("Zacatecas", json.getArray("content").getString(31));
	}
	
	@Test
	public void testFindEnglishProvinceNL() throws Exception {
		JsonText ca = (JsonText)JsonParser.parse(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/countries/ca/provinces/nl")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals("Newfoundland and Labrador", ca.value());

		JsonText mx = (JsonText)JsonParser.parse(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/lookup/countries/mx/provinces/nl")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals("Nuevo Leon", mx.value());
	}
		
}
