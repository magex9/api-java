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
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonObject;
import ca.magex.json.util.LoremIpsumGenerator;

public class GroupsControllerTests extends AbstractControllerTests {
	
	@Before
	public void setup() {
		initialize();
	}
	
	@Test
	public void testCreateGroup() throws Exception {
		// Get the initial list of groups to make sure they are blank
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/groups")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(4, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(4, json.getArray("content").size());
		assertEquals(List.of("@type", "groupId", "status", "code", "name"), json.getArray("content").getObject(0).keys());
		assertEquals("Group", json.getArray("content").getObject(0).getString("@type"));
		assertEquals("Active", json.getArray("content").getObject(0).getString("status"));
		assertEquals("APP", json.getArray("content").getObject(0).getString("code"));
		assertEquals("Application", json.getArray("content").getObject(0).getString("name"));
		assertEquals(List.of("@type", "groupId", "status", "code", "name"), json.getArray("content").getObject(1).keys());
		assertEquals("Group", json.getArray("content").getObject(1).getString("@type"));
		assertEquals("Active", json.getArray("content").getObject(1).getString("status"));
		assertEquals("CRM", json.getArray("content").getObject(1).getString("code"));
		assertEquals("Customer Relationship Management", json.getArray("content").getObject(1).getString("name"));
		assertEquals(List.of("@type", "groupId", "status", "code", "name"), json.getArray("content").getObject(2).keys());
		assertEquals("Group", json.getArray("content").getObject(2).getString("@type"));
		assertEquals("Active", json.getArray("content").getObject(2).getString("status"));
		assertEquals("ORG", json.getArray("content").getObject(2).getString("code"));
		assertEquals("Organization", json.getArray("content").getObject(2).getString("name"));
		assertEquals(List.of("@type", "groupId", "status", "code", "name"), json.getArray("content").getObject(3).keys());
		assertEquals("Group", json.getArray("content").getObject(3).getString("@type"));
		assertEquals("Active", json.getArray("content").getObject(3).getString("status"));
		assertEquals("SYS", json.getArray("content").getObject(3).getString("code"));
		assertEquals("System", json.getArray("content").getObject(3).getString("name"));
		
		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.post("/rest/groups")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("code", "GRP")
				.with("englishName", "Group")
				.with("frenchName", "Groupe")
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertTrue(json.getString("groupId").matches("[A-Za-z0-9]+"));
		assertEquals("Active", json.getString("status"));
		Identifier groupId = new Identifier(json.getString("groupId"));
		
		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/groups/" + groupId)
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());

		assertEquals(groupId.toString(), json.getString("groupId"));
		assertEquals("Active", json.getString("status"));
		assertEquals("GRP", json.getString("code"));
		assertEquals("Group", json.getString("name"));

		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/groups/" + groupId)
			.header("Locale", Lang.FRENCH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());

		assertEquals(groupId.toString(), json.getString("groupId"));
		assertEquals("Actif", json.getString("status"));
		assertEquals("Groupe", json.getString("name"));

		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/groups/" + groupId))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(groupId.toString(), json.getString("groupId"));
		assertEquals("active", json.getString("status"));
		assertEquals("GRP", json.getString("name"));

		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/groups")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		//JsonAsserts.print(json, "json");
		assertEquals(1, json.getInt("page"));
		assertEquals(5, json.getInt("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(JsonArray.class, json.get("content").getClass());
		assertEquals(5, json.getArray("content").size());
		assertEquals(groupId.toString(), json.getArray("content").getObject(2).getString("groupId"));
		assertEquals("Active", json.getArray("content").getObject(2).getString("status"));
		assertEquals("Group", json.getArray("content").getObject(2).getString("name"));
	}
	
	@Test
	public void testCreateGroupEnglishNameTests() throws Exception {
		JsonArray missing = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.post("/rest/groups")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("code", "GRP")
				//.with("englishName", "Group")
				.with("frenchName", "Groupe")
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			.andReturn().getResponse().getContentAsString());
		assertEquals(1, missing.size());
		assertEquals("error", missing.getObject(0).getString("type"));
		assertEquals("englishName", missing.getObject(0).getString("path"));
		assertEquals("Field is mandatory", missing.getObject(0).getString("reason"));
			
		JsonArray spaces = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.post("/rest/groups")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("code", "GRP")
				.with("englishName", "  ")
				.with("frenchName", "Groupe")
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			.andReturn().getResponse().getContentAsString());
		assertEquals(1, spaces.size());
		assertEquals("error", spaces.getObject(0).getString("type"));
		assertEquals("englishName", spaces.getObject(0).getString("path"));
		assertEquals("An English description is required", spaces.getObject(0).getString("reason"));
				
		JsonArray classCast = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.post("/rest/groups")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("code", "GRP")
				.with("englishName", true)
				.with("frenchName", "Groupe")
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			.andReturn().getResponse().getContentAsString());
		assertEquals(1, classCast.size());
		assertEquals("error", classCast.getObject(0).getString("type"));
		assertEquals("englishName", classCast.getObject(0).getString("path"));
		assertEquals("Invalid format", classCast.getObject(0).getString("reason"));
					
		JsonArray maxLength = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.post("/rest/groups")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("code", "GRP")
				.with("englishName", LoremIpsumGenerator.buildWords(20))
				.with("frenchName", "Groupe")
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			.andReturn().getResponse().getContentAsString());
		assertEquals(1, maxLength.size());
		assertEquals("error", maxLength.getObject(0).getString("type"));
		assertEquals("englishName", maxLength.getObject(0).getString("path"));
		assertEquals("English name must be 50 characters or less", maxLength.getObject(0).getString("reason"));
	}
	
	@Test
	public void testFirstPageEnglishSortGroup() throws Exception {
		LOCALIZED_SORTING_OPTIONS.forEach(l -> crm.createGroup(l));
		
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/groups")
			.queryParam("order", "englishName")
			.queryParam("direction", "asc")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(1, json.getInt("page"));
		assertEquals(36, json.getInt("total"));
		assertEquals(true, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(JsonArray.class, json.get("content").getClass());
		assertEquals(10, json.getArray("content").size());
		assertEquals(LOCALIZED_SORTED_ENGLISH_ASC.subList(0, 10), json.getArray("content").stream()
			.map(e -> ((JsonObject)e).getString("name")).collect(Collectors.toList()));
	}
	
	@Test
	public void testSecondPageEnglishSortGroup() throws Exception {
		LOCALIZED_SORTING_OPTIONS.forEach(l -> crm.createGroup(l));
		JsonObject page2 = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/groups")
			.queryParam("page", "2")
			.queryParam("limit", "5")
			.queryParam("order", "frenchName")
			.queryParam("direction", "asc")
			.header("Locale", Lang.FRENCH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(2, page2.getInt("page"));
		assertEquals(36, page2.getInt("total"));
		assertEquals(true, page2.getBoolean("hasNext"));
		assertEquals(true, page2.getBoolean("hasPrevious"));
		assertEquals(JsonArray.class, page2.get("content").getClass());
		assertEquals(5, page2.getArray("content").size());
		assertEquals(LOCALIZED_SORTED_FRENCH_ASC.subList(5, 10), page2.getArray("content").stream()
			.map(e -> ((JsonObject)e).getString("name")).collect(Collectors.toList()));
	}
	
	@Test
	public void testInactiveEnglishGroup() throws Exception {
		LOCALIZED_SORTING_OPTIONS.forEach(l -> crm.createGroup(l));
		crm.disableGroup(crm.findGroupByCode("E").getGroupId());
		crm.disableGroup(crm.findGroupByCode("F").getGroupId());
		crm.disableGroup(crm.findGroupByCode("H").getGroupId());
		
		List<String> INACTIVE_ENGLISH_ASC = List.of(
			crm.findGroupByCode("E").getName(Lang.ENGLISH),
			crm.findGroupByCode("F").getName(Lang.ENGLISH),
			crm.findGroupByCode("H").getName(Lang.ENGLISH)
		);
		
		JsonObject inativeEnglishAsc = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/groups")
			.queryParam("status", "Inactive")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(1, inativeEnglishAsc.getInt("page"));
		assertEquals(3, inativeEnglishAsc.getInt("total"));
		assertEquals(false, inativeEnglishAsc.getBoolean("hasNext"));
		assertEquals(false, inativeEnglishAsc.getBoolean("hasPrevious"));
		assertEquals(JsonArray.class, inativeEnglishAsc.get("content").getClass());
		assertEquals(3, inativeEnglishAsc.getArray("content").size());
		assertEquals(INACTIVE_ENGLISH_ASC, inativeEnglishAsc.getArray("content").stream()
			.map(e -> ((JsonObject)e).getString("name")).collect(Collectors.toList()));
	}
	
	@Test
	public void testInactiveSortWithNotLocaleGroup() throws Exception {
		LOCALIZED_SORTING_OPTIONS.forEach(l -> crm.createGroup(l));
		crm.disableGroup(crm.findGroupByCode("E").getGroupId());
		crm.disableGroup(crm.findGroupByCode("F").getGroupId());
		crm.disableGroup(crm.findGroupByCode("H").getGroupId());
		
		JsonObject inativeCodeDesc = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/groups")
			.queryParam("status", "inactive")
			.queryParam("order", "code")
			.queryParam("direction", "desc"))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(1, inativeCodeDesc.getInt("page"));
		assertEquals(3, inativeCodeDesc.getInt("total"));
		assertEquals(false, inativeCodeDesc.getBoolean("hasNext"));
		assertEquals(false, inativeCodeDesc.getBoolean("hasPrevious"));
		assertEquals(JsonArray.class, inativeCodeDesc.get("content").getClass());
		assertEquals(3, inativeCodeDesc.getArray("content").size());
		assertEquals(List.of("H", "F", "E"), inativeCodeDesc.getArray("content").stream()
			.map(e -> ((JsonObject)e).getString("name")).collect(Collectors.toList()));
	}
	
	@Test
	public void testUpdatingGroup() throws Exception {
		Identifier groupId = crm.createGroup(new Localized("ORIG", "Original", "First")).getGroupId();
		
		JsonObject orig = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/groups/" + groupId)
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(groupId.toString(), orig.getString("groupId"));
		assertEquals("Active", orig.getString("status"));
		assertEquals("ORIG", orig.getString("code"));
		assertEquals("Original", orig.getString("name"));

		JsonObject updated = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.patch("/rest/groups/" + groupId)
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("code", "ORIG")
				.with("englishName", "Updated")
				.with("frenchName", "Second")
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(groupId.toString(), updated.getString("groupId"));
		assertEquals("Active", updated.getString("status"));
		assertEquals("ORIG", updated.getString("code"));
		assertEquals("Updated", updated.getString("name"));
		
		JsonObject english = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/groups/" + groupId)
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(groupId.toString(), english.getString("groupId"));
		assertEquals("Active", english.getString("status"));
		assertEquals("ORIG", english.getString("code"));
		assertEquals("Updated", english.getString("name"));
		
		JsonObject french = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/groups/" + groupId)
			.header("Locale", Lang.FRENCH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(groupId.toString(), french.getString("groupId"));
		assertEquals("Actif", french.getString("status"));
		assertEquals("ORIG", french.getString("code"));
		assertEquals("Second", french.getString("name"));
		
		JsonArray errors = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.patch("/rest/groups/" + groupId)
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("code", "IMMUTABLE")
				.with("englishName", "Invalid")
				.with("frenchName", "Third")
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(1, errors.size());
		assertEquals("error", errors.getObject(0).getString("type"));
		assertEquals("code", errors.getObject(0).getString("path"));
		assertEquals("Group code must not change during updates", errors.getObject(0).getString("reason"));
		
	}
	
	@Test
	public void testGroupFilterByEnglishName() throws Exception {
		LOCALIZED_SORTING_OPTIONS.forEach(l -> crm.createGroup(l));
		
		JsonObject inativeEnglishAsc = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/groups")
			.queryParam("name", "re")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(1, inativeEnglishAsc.getInt("page"));
		assertEquals(5, inativeEnglishAsc.getInt("total"));
		assertEquals(false, inativeEnglishAsc.getBoolean("hasNext"));
		assertEquals(false, inativeEnglishAsc.getBoolean("hasPrevious"));
		assertEquals(JsonArray.class, inativeEnglishAsc.get("content").getClass());
		assertEquals(5, inativeEnglishAsc.getArray("content").size());
		assertEquals(List.of("$ Store", "Customer Relationship Management", "Montreal", "French", "resume"), inativeEnglishAsc.getArray("content").stream()
			.map(e -> ((JsonObject)e).getString("name")).collect(Collectors.toList()));

		JsonObject englishNameFilter = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/groups")
			.queryParam("englishName", "re")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(inativeEnglishAsc, englishNameFilter);
	}
	
	@Test
	public void testGroupFilterByFrenchName() throws Exception {
		LOCALIZED_SORTING_OPTIONS.forEach(l -> crm.createGroup(l));
		
		JsonObject inativeFrenchAsc = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/groups")
			.queryParam("name", "ou")
			.header("Locale", Lang.FRENCH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(1, inativeFrenchAsc.getInt("page"));
		assertEquals(3, inativeFrenchAsc.getInt("total"));
		assertEquals(false, inativeFrenchAsc.getBoolean("hasNext"));
		assertEquals(false, inativeFrenchAsc.getBoolean("hasPrevious"));
		assertEquals(JsonArray.class, inativeFrenchAsc.get("content").getClass());
		assertEquals(3, inativeFrenchAsc.getArray("content").size());
		assertEquals(List.of("Tout", "tout à fait", "Tout le"), inativeFrenchAsc.getArray("content").stream()
			.map(e -> ((JsonObject)e).getString("name")).collect(Collectors.toList()));
		
		JsonObject frenchNameFilter = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/groups")
			.queryParam("frenchName", "ou")
			.header("Locale", Lang.FRENCH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(inativeFrenchAsc, frenchNameFilter);
	}
	
	@Test
	public void testGroupFilterByCode() throws Exception {
		LOCALIZED_SORTING_OPTIONS.forEach(l -> crm.createGroup(l));
		
		JsonObject activeCodeAsc = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/groups")
			.queryParam("name", "A"))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(1, activeCodeAsc.getInt("page"));
		assertEquals(1, activeCodeAsc.getInt("total"));
		assertEquals(false, activeCodeAsc.getBoolean("hasNext"));
		assertEquals(false, activeCodeAsc.getBoolean("hasPrevious"));
		assertEquals(JsonArray.class, activeCodeAsc.get("content").getClass());
		assertEquals(1, activeCodeAsc.getArray("content").size());
		assertEquals(List.of("A"), activeCodeAsc.getArray("content").stream()
			.map(e -> ((JsonObject)e).getString("name")).collect(Collectors.toList()));
		
		JsonObject codeFilter = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/groups")
			.queryParam("code", "A"))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(activeCodeAsc, codeFilter);
	}
	
	@Test
	public void testEnableDisableGroup() throws Exception {
		Identifier groupId = crm.createGroup(GROUP).getGroupId();
		assertEquals(Status.ACTIVE, crm.findGroup(groupId).getStatus());

		JsonArray error1 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/rest/groups/" + groupId + "/disable")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(groupId.toString(), error1.getObject(0).getString("identifier"));
		assertEquals("error", error1.getObject(0).getString("type"));
		assertEquals("confirm", error1.getObject(0).getString("path"));
		assertEquals("You must send in the confirmation message", error1.getObject(0).getString("reason"));
		assertEquals(Status.ACTIVE, crm.findGroup(groupId).getStatus());

		JsonArray error2 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/rest/groups/" + groupId + "/disable")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("confirm", false)
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(groupId.toString(), error2.getObject(0).getString("identifier"));
		assertEquals("error", error2.getObject(0).getString("type"));
		assertEquals("confirm", error2.getObject(0).getString("path"));
		assertEquals("You must send in the confirmation message", error2.getObject(0).getString("reason"));
		assertEquals(Status.ACTIVE, crm.findGroup(groupId).getStatus());

		JsonArray error3 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/rest/groups/" + groupId + "/disable")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("confirm", "Test")
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(groupId.toString(), error3.getObject(0).getString("identifier"));
		assertEquals("error", error3.getObject(0).getString("type"));
		assertEquals("confirm", error3.getObject(0).getString("path"));
		assertEquals("Confirmation message must be a boolean", error3.getObject(0).getString("reason"));
		assertEquals(Status.ACTIVE, crm.findGroup(groupId).getStatus());

		JsonObject disable = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.put("/rest/groups/" + groupId + "/disable")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("confirm", true)
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(groupId.toString(), disable.getString("groupId"));
		assertEquals("Inactive", disable.getString("status"));
		assertEquals("GRP", disable.getString("code"));
		assertEquals("Group", disable.getString("name"));
		assertEquals(Status.INACTIVE, crm.findGroup(groupId).getStatus());
		
		JsonArray error4 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/rest/groups/" + groupId + "/enable")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(groupId.toString(), error4.getObject(0).getString("identifier"));
		assertEquals("error", error4.getObject(0).getString("type"));
		assertEquals("confirm", error4.getObject(0).getString("path"));
		assertEquals("You must send in the confirmation message", error4.getObject(0).getString("reason"));
		assertEquals(Status.INACTIVE, crm.findGroup(groupId).getStatus());
		
		JsonArray error5 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/rest/groups/" + groupId + "/enable")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("confirm", false)
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(groupId.toString(), error5.getObject(0).getString("identifier"));
		assertEquals("error", error5.getObject(0).getString("type"));
		assertEquals("confirm", error5.getObject(0).getString("path"));
		assertEquals("You must send in the confirmation message", error5.getObject(0).getString("reason"));
		assertEquals(Status.INACTIVE, crm.findGroup(groupId).getStatus());
		
		JsonArray error6 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/rest/groups/" + groupId + "/enable")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("confirm", "test")
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(groupId.toString(), error6.getObject(0).getString("identifier"));
		assertEquals("error", error6.getObject(0).getString("type"));
		assertEquals("confirm", error6.getObject(0).getString("path"));
		assertEquals("Confirmation message must be a boolean", error6.getObject(0).getString("reason"));
		assertEquals(Status.INACTIVE, crm.findGroup(groupId).getStatus());
	
		JsonObject enable = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.put("/rest/groups/" + groupId + "/enable")
			.header("Locale", Lang.FRENCH)
			.content(new JsonObject()
				.with("confirm", true)
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(groupId.toString(), enable.getString("groupId"));
		assertEquals("Actif", enable.getString("status"));
		assertEquals("GRP", enable.getString("code"));
		assertEquals("Groupe", enable.getString("name"));
		assertEquals(Status.ACTIVE, crm.findGroup(groupId).getStatus());
	}
	
}