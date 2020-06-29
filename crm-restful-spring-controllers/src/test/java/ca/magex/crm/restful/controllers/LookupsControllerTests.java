package ca.magex.crm.restful.controllers;

import static ca.magex.crm.test.CrmAsserts.GROUP;
import static ca.magex.crm.test.CrmAsserts.LOCALIZED_SORTED_ENGLISH_ASC;
import static ca.magex.crm.test.CrmAsserts.LOCALIZED_SORTED_FRENCH_ASC;
import static ca.magex.crm.test.CrmAsserts.LOCALIZED_SORTING_OPTIONS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.test.CrmAsserts;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonAsserts;
import ca.magex.json.model.JsonObject;
import ca.magex.json.util.LoremIpsumGenerator;

public class LookupsControllerTests extends AbstractControllerTests {
	
	@Before
	public void setup() {
		initialize();
	}
	
	@Test
	public void testCreateLookup() throws Exception {
		// Get the initial list of lookups to make sure they are blank
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/lookups")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(12, json.getNumber("total"));
		assertEquals(true, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(10, json.getArray("content").size());
		
		System.out.println(json);

		CrmAsserts.printList(json.getArray("content", JsonObject.class).stream().map(l -> l.contains("parent") ? l.getString("code") + ":" + l.getObject("parent").getString("code") : l.getString("code")).sorted().collect(Collectors.toList()), String.class);
		
		List<String> lookupNames = List.of("BusinessClassification", "BusinessSector", "BusinessUnit:EXECS", "BusinessUnit:IMIT", "Country",
				"Language", "Locale", "Province:CA", "Province:MX", "Province:US", "Salutation", "Status");

		assertEquals(lookupNames, json.getArray("content", JsonObject.class).stream().map(l -> l.contains("parent") ? l.getString("code") + ":" + l.getObject("parent").getString("code") : l.getString("code")).collect(Collectors.toList()));

//		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
//		assertEquals(1, json.getNumber("page"));
//		assertEquals(10, json.getNumber("limit"));
//		assertEquals(12, json.getNumber("total"));
//		assertEquals(false, json.getBoolean("hasNext"));
//		assertEquals(false, json.getBoolean("hasPrevious"));
//		assertEquals(4, json.getArray("content").size());
//		assertEquals(List.of("@type", "lookupId", "status", "code", "name"), json.getArray("content").getObject(0).keys());
//		assertEquals("Lookup", json.getArray("content").getObject(0).getString("@type"));
//		assertEquals("Active", json.getArray("content").getObject(0).getString("status"));
//		assertEquals("APP", json.getArray("content").getObject(0).getString("code"));
//		assertEquals("Application", json.getArray("content").getObject(0).getString("name"));
//		assertEquals(List.of("@type", "lookupId", "status", "code", "name"), json.getArray("content").getObject(1).keys());
//		assertEquals("Lookup", json.getArray("content").getObject(1).getString("@type"));
//		assertEquals("Active", json.getArray("content").getObject(1).getString("status"));
//		assertEquals("CRM", json.getArray("content").getObject(1).getString("code"));
//		assertEquals("Customer Relationship Management", json.getArray("content").getObject(1).getString("name"));
//		assertEquals(List.of("@type", "lookupId", "status", "code", "name"), json.getArray("content").getObject(2).keys());
//		assertEquals("Lookup", json.getArray("content").getObject(2).getString("@type"));
//		assertEquals("Active", json.getArray("content").getObject(2).getString("status"));
//		assertEquals("ORG", json.getArray("content").getObject(2).getString("code"));
//		assertEquals("Organization", json.getArray("content").getObject(2).getString("name"));
//		assertEquals(List.of("@type", "lookupId", "status", "code", "name"), json.getArray("content").getObject(3).keys());
//		assertEquals("Lookup", json.getArray("content").getObject(3).getString("@type"));
//		assertEquals("Active", json.getArray("content").getObject(3).getString("status"));
//		assertEquals("SYS", json.getArray("content").getObject(3).getString("code"));
//		assertEquals("System", json.getArray("content").getObject(3).getString("name"));
//		
//		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
//			.post("/rest/lookups")
//			.header("Locale", Lang.ENGLISH)
//			.content(new JsonObject()
//				.with("code", "GRP")
//				.with("englishName", "Lookup")
//				.with("frenchName", "Lookupe")
//				.toString()))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().isOk())
//			.andReturn().getResponse().getContentAsString());
//		assertTrue(json.getString("lookupId").matches("[A-Za-z0-9]+"));
//		assertEquals("Active", json.getString("status"));
//		Identifier lookupId = new Identifier(json.getString("lookupId"));
//		
//		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
//			.get("/rest/lookups/" + lookupId)
//			.header("Locale", Lang.ENGLISH))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().isOk())
//			.andReturn().getResponse().getContentAsString());
//
//		assertEquals(lookupId.toString(), json.getString("lookupId"));
//		assertEquals("Active", json.getString("status"));
//		assertEquals("GRP", json.getString("code"));
//		assertEquals("Lookup", json.getString("name"));
//
//		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
//			.get("/rest/lookups/" + lookupId)
//			.header("Locale", Lang.FRENCH))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().isOk())
//			.andReturn().getResponse().getContentAsString());
//
//		assertEquals(lookupId.toString(), json.getString("lookupId"));
//		assertEquals("Actif", json.getString("status"));
//		assertEquals("Lookupe", json.getString("name"));
//
//		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
//			.get("/rest/lookups/" + lookupId))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().isOk())
//			.andReturn().getResponse().getContentAsString());
//		assertEquals(lookupId.toString(), json.getString("lookupId"));
//		assertEquals("active", json.getString("status"));
//		assertEquals("GRP", json.getString("name"));
//
//		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
//			.get("/rest/lookups")
//			.header("Locale", Lang.ENGLISH))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().isOk())
//			.andReturn().getResponse().getContentAsString());
//		//JsonAsserts.print(json, "json");
//		assertEquals(1, json.getInt("page"));
//		assertEquals(5, json.getInt("total"));
//		assertEquals(false, json.getBoolean("hasNext"));
//		assertEquals(false, json.getBoolean("hasPrevious"));
//		assertEquals(JsonArray.class, json.get("content").getClass());
//		assertEquals(5, json.getArray("content").size());
//		assertEquals(lookupId.toString(), json.getArray("content").getObject(2).getString("lookupId"));
//		assertEquals("Active", json.getArray("content").getObject(2).getString("status"));
//		assertEquals("Lookup", json.getArray("content").getObject(2).getString("name"));
	}
	
//	@Test
//	public void testCreateLookupEnglishNameTests() throws Exception {
//		JsonArray missing = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
//			.post("/rest/lookups")
//			.header("Locale", Lang.ENGLISH)
//			.content(new JsonObject()
//				.with("code", "GRP")
//				//.with("englishName", "Lookup")
//				.with("frenchName", "Lookupe")
//				.toString()))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().isBadRequest())
//			.andReturn().getResponse().getContentAsString());
//		assertEquals(1, missing.size());
//		assertEquals("error", missing.getObject(0).getString("type"));
//		assertEquals("englishName", missing.getObject(0).getString("path"));
//		assertEquals("Field is mandatory", missing.getObject(0).getString("reason"));
//			
//		JsonArray spaces = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
//			.post("/rest/lookups")
//			.header("Locale", Lang.ENGLISH)
//			.content(new JsonObject()
//				.with("code", "GRP")
//				.with("englishName", "  ")
//				.with("frenchName", "Lookupe")
//				.toString()))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().isBadRequest())
//			.andReturn().getResponse().getContentAsString());
//		assertEquals(1, spaces.size());
//		assertEquals("error", spaces.getObject(0).getString("type"));
//		assertEquals("englishName", spaces.getObject(0).getString("path"));
//		assertEquals("An English description is required", spaces.getObject(0).getString("reason"));
//				
//		JsonArray classCast = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
//			.post("/rest/lookups")
//			.header("Locale", Lang.ENGLISH)
//			.content(new JsonObject()
//				.with("code", "GRP")
//				.with("englishName", true)
//				.with("frenchName", "Lookupe")
//				.toString()))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().isBadRequest())
//			.andReturn().getResponse().getContentAsString());
//		assertEquals(1, classCast.size());
//		assertEquals("error", classCast.getObject(0).getString("type"));
//		assertEquals("englishName", classCast.getObject(0).getString("path"));
//		assertEquals("Invalid format", classCast.getObject(0).getString("reason"));
//					
//		JsonArray maxLength = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
//			.post("/rest/lookups")
//			.header("Locale", Lang.ENGLISH)
//			.content(new JsonObject()
//				.with("code", "GRP")
//				.with("englishName", LoremIpsumGenerator.buildWords(20))
//				.with("frenchName", "Lookupe")
//				.toString()))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().isBadRequest())
//			.andReturn().getResponse().getContentAsString());
//		assertEquals(1, maxLength.size());
//		assertEquals("error", maxLength.getObject(0).getString("type"));
//		assertEquals("englishName", maxLength.getObject(0).getString("path"));
//		assertEquals("English name must be 50 characters or less", maxLength.getObject(0).getString("reason"));
//	}
//	
//	@Test
//	public void testFirstPageEnglishSortLookup() throws Exception {
//		LOCALIZED_SORTING_OPTIONS.forEach(l -> crm.createLookup(l, null));
//		
//		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
//			.get("/rest/lookups")
//			.queryParam("order", "englishName")
//			.queryParam("direction", "asc")
//			.header("Locale", Lang.ENGLISH))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().isOk())
//			.andReturn().getResponse().getContentAsString());
//		assertEquals(1, json.getInt("page"));
//		assertEquals(36, json.getInt("total"));
//		assertEquals(true, json.getBoolean("hasNext"));
//		assertEquals(false, json.getBoolean("hasPrevious"));
//		assertEquals(JsonArray.class, json.get("content").getClass());
//		assertEquals(10, json.getArray("content").size());
//		assertEquals(LOCALIZED_SORTED_ENGLISH_ASC.subList(0, 10), json.getArray("content").stream()
//			.map(e -> ((JsonObject)e).getString("name")).collect(Collectors.toList()));
//	}
//	
//	@Test
//	public void testSecondPageEnglishSortLookup() throws Exception {
//		LOCALIZED_SORTING_OPTIONS.forEach(l -> crm.createLookup(l, null));
//		JsonObject page2 = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
//			.get("/rest/lookups")
//			.queryParam("page", "2")
//			.queryParam("limit", "5")
//			.queryParam("order", "frenchName")
//			.queryParam("direction", "asc")
//			.header("Locale", Lang.FRENCH))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().isOk())
//			.andReturn().getResponse().getContentAsString());
//		assertEquals(2, page2.getInt("page"));
//		assertEquals(36, page2.getInt("total"));
//		assertEquals(true, page2.getBoolean("hasNext"));
//		assertEquals(true, page2.getBoolean("hasPrevious"));
//		assertEquals(JsonArray.class, page2.get("content").getClass());
//		assertEquals(5, page2.getArray("content").size());
//		assertEquals(LOCALIZED_SORTED_FRENCH_ASC.subList(5, 10), page2.getArray("content").stream()
//			.map(e -> ((JsonObject)e).getString("name")).collect(Collectors.toList()));
//	}
//	
//	@Test
//	public void testInactiveEnglishLookup() throws Exception {
//		LOCALIZED_SORTING_OPTIONS.forEach(l -> crm.createLookup(l, null));
//		crm.disableLookup(crm.findLookupByCode("E").getLookupId());
//		crm.disableLookup(crm.findLookupByCode("F").getLookupId());
//		crm.disableLookup(crm.findLookupByCode("H").getLookupId());
//		
//		List<String> INACTIVE_ENGLISH_ASC = List.of(
//			crm.findLookupByCode("E").getName(Lang.ENGLISH),
//			crm.findLookupByCode("F").getName(Lang.ENGLISH),
//			crm.findLookupByCode("H").getName(Lang.ENGLISH)
//		);
//		
//		JsonObject inativeEnglishAsc = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
//			.get("/rest/lookups")
//			.queryParam("status", "Inactive")
//			.header("Locale", Lang.ENGLISH))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().isOk())
//			.andReturn().getResponse().getContentAsString());
//		assertEquals(1, inativeEnglishAsc.getInt("page"));
//		assertEquals(3, inativeEnglishAsc.getInt("total"));
//		assertEquals(false, inativeEnglishAsc.getBoolean("hasNext"));
//		assertEquals(false, inativeEnglishAsc.getBoolean("hasPrevious"));
//		assertEquals(JsonArray.class, inativeEnglishAsc.get("content").getClass());
//		assertEquals(3, inativeEnglishAsc.getArray("content").size());
//		assertEquals(INACTIVE_ENGLISH_ASC, inativeEnglishAsc.getArray("content").stream()
//			.map(e -> ((JsonObject)e).getString("name")).collect(Collectors.toList()));
//	}
//	
//	@Test
//	public void testInactiveSortWithNotLocaleLookup() throws Exception {
//		LOCALIZED_SORTING_OPTIONS.forEach(l -> crm.createLookup(l, null));
//		crm.disableLookup(crm.findLookupByCode("E").getLookupId());
//		crm.disableLookup(crm.findLookupByCode("F").getLookupId());
//		crm.disableLookup(crm.findLookupByCode("H").getLookupId());
//		
//		JsonObject inativeCodeDesc = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
//			.get("/rest/lookups")
//			.queryParam("status", "inactive")
//			.queryParam("order", "code")
//			.queryParam("direction", "desc"))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().isOk())
//			.andReturn().getResponse().getContentAsString());
//		assertEquals(1, inativeCodeDesc.getInt("page"));
//		assertEquals(3, inativeCodeDesc.getInt("total"));
//		assertEquals(false, inativeCodeDesc.getBoolean("hasNext"));
//		assertEquals(false, inativeCodeDesc.getBoolean("hasPrevious"));
//		assertEquals(JsonArray.class, inativeCodeDesc.get("content").getClass());
//		assertEquals(3, inativeCodeDesc.getArray("content").size());
//		assertEquals(List.of("H", "F", "E"), inativeCodeDesc.getArray("content").stream()
//			.map(e -> ((JsonObject)e).getString("name")).collect(Collectors.toList()));
//	}
//	
//	@Test
//	public void testUpdatingLookup() throws Exception {
//		Identifier lookupId = crm.createLookup(new Localized("ORIG", "Original", "First"), null).getLookupId();
//		
//		JsonObject orig = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
//			.get("/rest/lookups/" + lookupId)
//			.header("Locale", Lang.ENGLISH))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().isOk())
//			.andReturn().getResponse().getContentAsString());
//		assertEquals(lookupId.toString(), orig.getString("lookupId"));
//		assertEquals("Active", orig.getString("status"));
//		assertEquals("ORIG", orig.getString("code"));
//		assertEquals("Original", orig.getString("name"));
//
//		JsonObject updated = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
//			.patch("/rest/lookups/" + lookupId)
//			.header("Locale", Lang.ENGLISH)
//			.content(new JsonObject()
//				.with("code", "ORIG")
//				.with("englishName", "Updated")
//				.with("frenchName", "Second")
//				.toString()))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().isOk())
//			.andReturn().getResponse().getContentAsString());
//		assertEquals(lookupId.toString(), updated.getString("lookupId"));
//		assertEquals("Active", updated.getString("status"));
//		assertEquals("ORIG", updated.getString("code"));
//		assertEquals("Updated", updated.getString("name"));
//		
//		JsonObject english = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
//			.get("/rest/lookups/" + lookupId)
//			.header("Locale", Lang.ENGLISH))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().isOk())
//			.andReturn().getResponse().getContentAsString());
//		assertEquals(lookupId.toString(), english.getString("lookupId"));
//		assertEquals("Active", english.getString("status"));
//		assertEquals("ORIG", english.getString("code"));
//		assertEquals("Updated", english.getString("name"));
//		
//		JsonObject french = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
//			.get("/rest/lookups/" + lookupId)
//			.header("Locale", Lang.FRENCH))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().isOk())
//			.andReturn().getResponse().getContentAsString());
//		assertEquals(lookupId.toString(), french.getString("lookupId"));
//		assertEquals("Actif", french.getString("status"));
//		assertEquals("ORIG", french.getString("code"));
//		assertEquals("Second", french.getString("name"));
//		
//		JsonArray errors = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
//			.patch("/rest/lookups/" + lookupId)
//			.header("Locale", Lang.ENGLISH)
//			.content(new JsonObject()
//				.with("code", "IMMUTABLE")
//				.with("englishName", "Invalid")
//				.with("frenchName", "Third")
//				.toString()))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
//			.andReturn().getResponse().getContentAsString());
//		assertEquals(1, errors.size());
//		assertEquals("error", errors.getObject(0).getString("type"));
//		assertEquals("code", errors.getObject(0).getString("path"));
//		assertEquals("Lookup code must not change during updates", errors.getObject(0).getString("reason"));
//		
//	}
//	
//	@Test
//	public void testLookupFilterByEnglishName() throws Exception {
//		LOCALIZED_SORTING_OPTIONS.forEach(l -> crm.createLookup(l, null));
//		
//		JsonObject inativeEnglishAsc = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
//			.get("/rest/lookups")
//			.queryParam("name", "re")
//			.header("Locale", Lang.ENGLISH))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().isOk())
//			.andReturn().getResponse().getContentAsString());
//		assertEquals(1, inativeEnglishAsc.getInt("page"));
//		assertEquals(5, inativeEnglishAsc.getInt("total"));
//		assertEquals(false, inativeEnglishAsc.getBoolean("hasNext"));
//		assertEquals(false, inativeEnglishAsc.getBoolean("hasPrevious"));
//		assertEquals(JsonArray.class, inativeEnglishAsc.get("content").getClass());
//		assertEquals(5, inativeEnglishAsc.getArray("content").size());
//		assertEquals(List.of("$ Store", "Customer Relationship Management", "Montreal", "French", "resume"), inativeEnglishAsc.getArray("content").stream()
//			.map(e -> ((JsonObject)e).getString("name")).collect(Collectors.toList()));
//
//		JsonObject englishNameFilter = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
//			.get("/rest/lookups")
//			.queryParam("englishName", "re")
//			.header("Locale", Lang.ENGLISH))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().isOk())
//			.andReturn().getResponse().getContentAsString());
//		assertEquals(inativeEnglishAsc, englishNameFilter);
//	}
//	
//	@Test
//	public void testLookupFilterByFrenchName() throws Exception {
//		LOCALIZED_SORTING_OPTIONS.forEach(l -> crm.createLookup(l, null));
//		
//		JsonObject inativeFrenchAsc = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
//			.get("/rest/lookups")
//			.queryParam("name", "ou")
//			.header("Locale", Lang.FRENCH))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().isOk())
//			.andReturn().getResponse().getContentAsString());
//		assertEquals(1, inativeFrenchAsc.getInt("page"));
//		assertEquals(3, inativeFrenchAsc.getInt("total"));
//		assertEquals(false, inativeFrenchAsc.getBoolean("hasNext"));
//		assertEquals(false, inativeFrenchAsc.getBoolean("hasPrevious"));
//		assertEquals(JsonArray.class, inativeFrenchAsc.get("content").getClass());
//		assertEquals(3, inativeFrenchAsc.getArray("content").size());
//		assertEquals(List.of("Tout", "tout à fait", "Tout le"), inativeFrenchAsc.getArray("content").stream()
//			.map(e -> ((JsonObject)e).getString("name")).collect(Collectors.toList()));
//		
//		JsonObject frenchNameFilter = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
//			.get("/rest/lookups")
//			.queryParam("frenchName", "ou")
//			.header("Locale", Lang.FRENCH))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().isOk())
//			.andReturn().getResponse().getContentAsString());
//		assertEquals(inativeFrenchAsc, frenchNameFilter);
//	}
//	
//	@Test
//	public void testLookupFilterByCode() throws Exception {
//		LOCALIZED_SORTING_OPTIONS.forEach(l -> crm.createLookup(l, null));
//		
//		JsonObject activeCodeAsc = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
//			.get("/rest/lookups")
//			.queryParam("name", "A"))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().isOk())
//			.andReturn().getResponse().getContentAsString());
//		assertEquals(1, activeCodeAsc.getInt("page"));
//		assertEquals(1, activeCodeAsc.getInt("total"));
//		assertEquals(false, activeCodeAsc.getBoolean("hasNext"));
//		assertEquals(false, activeCodeAsc.getBoolean("hasPrevious"));
//		assertEquals(JsonArray.class, activeCodeAsc.get("content").getClass());
//		assertEquals(1, activeCodeAsc.getArray("content").size());
//		assertEquals(List.of("A"), activeCodeAsc.getArray("content").stream()
//			.map(e -> ((JsonObject)e).getString("name")).collect(Collectors.toList()));
//		
//		JsonObject codeFilter = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
//			.get("/rest/lookups")
//			.queryParam("code", "A"))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().isOk())
//			.andReturn().getResponse().getContentAsString());
//		assertEquals(activeCodeAsc, codeFilter);
//	}
//	
//	@Test
//	public void testEnableDisableLookup() throws Exception {
//		Identifier lookupId = crm.createLookup(GROUP, null).getLookupId();
//		assertEquals(Status.ACTIVE, crm.findLookup(lookupId).getStatus());
//
//		JsonArray error1 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
//			.put("/rest/lookups/" + lookupId + "/disable")
//			.header("Locale", Lang.ENGLISH))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
//			.andReturn().getResponse().getContentAsString());
//		assertEquals(lookupId.toString(), error1.getObject(0).getString("identifier"));
//		assertEquals("error", error1.getObject(0).getString("type"));
//		assertEquals("confirm", error1.getObject(0).getString("path"));
//		assertEquals("You must send in the confirmation message", error1.getObject(0).getString("reason"));
//		assertEquals(Status.ACTIVE, crm.findLookup(lookupId).getStatus());
//
//		JsonArray error2 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
//			.put("/rest/lookups/" + lookupId + "/disable")
//			.header("Locale", Lang.ENGLISH)
//			.content(new JsonObject()
//				.with("confirm", false)
//				.toString()))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
//			.andReturn().getResponse().getContentAsString());
//		assertEquals(lookupId.toString(), error2.getObject(0).getString("identifier"));
//		assertEquals("error", error2.getObject(0).getString("type"));
//		assertEquals("confirm", error2.getObject(0).getString("path"));
//		assertEquals("You must send in the confirmation message", error2.getObject(0).getString("reason"));
//		assertEquals(Status.ACTIVE, crm.findLookup(lookupId).getStatus());
//
//		JsonArray error3 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
//			.put("/rest/lookups/" + lookupId + "/disable")
//			.header("Locale", Lang.ENGLISH)
//			.content(new JsonObject()
//				.with("confirm", "Test")
//				.toString()))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
//			.andReturn().getResponse().getContentAsString());
//		assertEquals(lookupId.toString(), error3.getObject(0).getString("identifier"));
//		assertEquals("error", error3.getObject(0).getString("type"));
//		assertEquals("confirm", error3.getObject(0).getString("path"));
//		assertEquals("Confirmation message must be a boolean", error3.getObject(0).getString("reason"));
//		assertEquals(Status.ACTIVE, crm.findLookup(lookupId).getStatus());
//
//		JsonObject disable = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
//			.put("/rest/lookups/" + lookupId + "/disable")
//			.header("Locale", Lang.ENGLISH)
//			.content(new JsonObject()
//				.with("confirm", true)
//				.toString()))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().isOk())
//			.andReturn().getResponse().getContentAsString());
//		assertEquals(lookupId.toString(), disable.getString("lookupId"));
//		assertEquals("Inactive", disable.getString("status"));
//		assertEquals("GRP", disable.getString("code"));
//		assertEquals("Lookup", disable.getString("name"));
//		assertEquals(Status.INACTIVE, crm.findLookup(lookupId).getStatus());
//		
//		JsonArray error4 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
//			.put("/rest/lookups/" + lookupId + "/enable")
//			.header("Locale", Lang.ENGLISH))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
//			.andReturn().getResponse().getContentAsString());
//		assertEquals(lookupId.toString(), error4.getObject(0).getString("identifier"));
//		assertEquals("error", error4.getObject(0).getString("type"));
//		assertEquals("confirm", error4.getObject(0).getString("path"));
//		assertEquals("You must send in the confirmation message", error4.getObject(0).getString("reason"));
//		assertEquals(Status.INACTIVE, crm.findLookup(lookupId).getStatus());
//		
//		JsonArray error5 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
//			.put("/rest/lookups/" + lookupId + "/enable")
//			.header("Locale", Lang.ENGLISH)
//			.content(new JsonObject()
//				.with("confirm", false)
//				.toString()))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
//			.andReturn().getResponse().getContentAsString());
//		assertEquals(lookupId.toString(), error5.getObject(0).getString("identifier"));
//		assertEquals("error", error5.getObject(0).getString("type"));
//		assertEquals("confirm", error5.getObject(0).getString("path"));
//		assertEquals("You must send in the confirmation message", error5.getObject(0).getString("reason"));
//		assertEquals(Status.INACTIVE, crm.findLookup(lookupId).getStatus());
//		
//		JsonArray error6 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
//			.put("/rest/lookups/" + lookupId + "/enable")
//			.header("Locale", Lang.ENGLISH)
//			.content(new JsonObject()
//				.with("confirm", "test")
//				.toString()))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
//			.andReturn().getResponse().getContentAsString());
//		assertEquals(lookupId.toString(), error6.getObject(0).getString("identifier"));
//		assertEquals("error", error6.getObject(0).getString("type"));
//		assertEquals("confirm", error6.getObject(0).getString("path"));
//		assertEquals("Confirmation message must be a boolean", error6.getObject(0).getString("reason"));
//		assertEquals(Status.INACTIVE, crm.findLookup(lookupId).getStatus());
//	
//		JsonObject enable = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
//			.put("/rest/lookups/" + lookupId + "/enable")
//			.header("Locale", Lang.FRENCH)
//			.content(new JsonObject()
//				.with("confirm", true)
//				.toString()))
//			//.andDo(MockMvcResultHandlers.print())
//			.andExpect(MockMvcResultMatchers.status().isOk())
//			.andReturn().getResponse().getContentAsString());
//		assertEquals(lookupId.toString(), enable.getString("lookupId"));
//		assertEquals("Actif", enable.getString("status"));
//		assertEquals("GRP", enable.getString("code"));
//		assertEquals("Lookupe", enable.getString("name"));
//		assertEquals(Status.ACTIVE, crm.findLookup(lookupId).getStatus());
//	}
	
}
