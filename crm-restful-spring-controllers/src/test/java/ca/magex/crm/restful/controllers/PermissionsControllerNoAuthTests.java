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
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.services.CrmInitializationService;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonObject;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = {
		MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED,
		MagexCrmProfiles.CRM_NO_AUTH
})
public class PermissionsControllerNoAuthTests {
	
	@Autowired private CrmInitializationService initiailziation;

	@Autowired private CrmPermissionService permissions;

	@Autowired private MockMvc mockMvc;
	
	@Before
	public void setup() {
		initiailziation.reset();
	}
	
	@Test
	public void testCreateGroup() throws Exception {
		// Get the initial list of groups to make sure they are blank
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/groups")
			.header("Locale", Lang.ENGLISH))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(1, json.getInt("page"));
		assertEquals(0, json.getInt("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(JsonArray.class, json.get("content").getClass());
		assertEquals(0, json.getArray("content").size());
		
		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.post("/api/groups")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("code", "GRP")
				.with("englishName", "Group")
				.with("frenchName", "Groupe")
				.toString()))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertTrue(json.getString("groupId").matches("[A-Za-z0-9]+"));
		assertEquals("Active", json.getString("status"));
		Identifier groupId = new Identifier(json.getString("groupId"));
		
		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/groups/" + groupId)
			.header("Locale", Lang.ENGLISH))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());

		assertEquals(groupId.toString(), json.getString("groupId"));
		assertEquals("Active", json.getString("status"));
		assertEquals("GRP", json.getString("code"));
		assertEquals("Group", json.getString("name"));

		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/groups/" + groupId)
			.header("Locale", Lang.FRENCH))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());

		assertEquals(groupId.toString(), json.getString("groupId"));
		assertEquals("Actif", json.getString("status"));
		assertEquals("Groupe", json.getString("name"));

		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/groups/" + groupId))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(groupId.toString(), json.getString("groupId"));
		assertEquals("active", json.getString("status"));
		assertEquals("GRP", json.getString("name"));

		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/groups")
			.header("Locale", Lang.ENGLISH))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(1, json.getInt("page"));
		assertEquals(1, json.getInt("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(JsonArray.class, json.get("content").getClass());
		assertEquals(1, json.getArray("content").size());
		assertEquals(groupId.toString(), json.getArray("content").getObject(0).getString("groupId"));
		assertEquals("Active", json.getArray("content").getObject(0).getString("status"));
		assertEquals("Group", json.getArray("content").getObject(0).getString("name"));
	}
	
	@Test
	public void testFirstPageEnglishSort() throws Exception {
		LOCALIZED_SORTING_OPTIONS.forEach(l -> permissions.createGroup(l));
		
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/groups")
			.queryParam("order", "englishName")
			.queryParam("direction", "asc")
			.header("Locale", Lang.ENGLISH))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(1, json.getInt("page"));
		assertEquals(32, json.getInt("total"));
		assertEquals(true, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(JsonArray.class, json.get("content").getClass());
		assertEquals(10, json.getArray("content").size());
		assertEquals(LOCALIZED_SORTED_ENGLISH_ASC.subList(0, 10), json.getArray("content").stream()
			.map(e -> ((JsonObject)e).getString("name")).collect(Collectors.toList()));
	}
	
	@Test
	public void testSecondPageEnglishSort() throws Exception {
		LOCALIZED_SORTING_OPTIONS.forEach(l -> permissions.createGroup(l));
		JsonObject page2 = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/groups")
			.queryParam("page", "2")
			.queryParam("limit", "5")
			.queryParam("order", "frenchName")
			.queryParam("direction", "asc")
			.header("Locale", Lang.FRENCH))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(2, page2.getInt("page"));
		assertEquals(32, page2.getInt("total"));
		assertEquals(true, page2.getBoolean("hasNext"));
		assertEquals(true, page2.getBoolean("hasPrevious"));
		assertEquals(JsonArray.class, page2.get("content").getClass());
		assertEquals(5, page2.getArray("content").size());
		assertEquals(LOCALIZED_SORTED_FRENCH_ASC.subList(5, 10), page2.getArray("content").stream()
			.map(e -> ((JsonObject)e).getString("name")).collect(Collectors.toList()));
	}
	
	@Test
	public void testInactiveEnglish() throws Exception {
		LOCALIZED_SORTING_OPTIONS.forEach(l -> permissions.createGroup(l));
		permissions.disableGroup(permissions.findGroupByCode("E").getGroupId());
		permissions.disableGroup(permissions.findGroupByCode("F").getGroupId());
		permissions.disableGroup(permissions.findGroupByCode("H").getGroupId());
		
		List<String> INACTIVE_ENGLISH_ASC = List.of(
			permissions.findGroupByCode("E").getName(Lang.ENGLISH),
			permissions.findGroupByCode("F").getName(Lang.ENGLISH),
			permissions.findGroupByCode("H").getName(Lang.ENGLISH)
		);
		
		JsonObject inativeEnglishAsc = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/groups")
			.queryParam("status", "Inactive")
			.header("Locale", Lang.ENGLISH))
			.andDo(MockMvcResultHandlers.print())
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
	public void testInactiveSortWithNotLocale() throws Exception {
		LOCALIZED_SORTING_OPTIONS.forEach(l -> permissions.createGroup(l));
		permissions.disableGroup(permissions.findGroupByCode("E").getGroupId());
		permissions.disableGroup(permissions.findGroupByCode("F").getGroupId());
		permissions.disableGroup(permissions.findGroupByCode("H").getGroupId());
		
		JsonObject inativeCodeDesc = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/groups")
			.queryParam("status", "inactive")
			.queryParam("order", "code")
			.queryParam("direction", "desc"))
			.andDo(MockMvcResultHandlers.print())
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
		Identifier groupId = permissions.createGroup(new Localized("ORIG", "Original", "First")).getGroupId();
		
		JsonObject orig = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/groups/" + groupId)
			.header("Locale", Lang.ENGLISH))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(groupId.toString(), orig.getString("groupId"));
		assertEquals("Active", orig.getString("status"));
		assertEquals("ORIG", orig.getString("code"));
		assertEquals("Original", orig.getString("name"));

		JsonObject updated = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.patch("/api/groups/" + groupId)
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("code", "ORIG")
				.with("englishName", "Updated")
				.with("frenchName", "Second")
				.toString()))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(groupId.toString(), updated.getString("groupId"));
		assertEquals("Active", updated.getString("status"));
		assertEquals("ORIG", updated.getString("code"));
		assertEquals("Updated", updated.getString("name"));
		
		JsonObject english = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/groups/" + groupId)
			.header("Locale", Lang.ENGLISH))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(groupId.toString(), english.getString("groupId"));
		assertEquals("Active", english.getString("status"));
		assertEquals("ORIG", english.getString("code"));
		assertEquals("Updated", english.getString("name"));
		
		JsonObject french = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/groups/" + groupId)
			.header("Locale", Lang.FRENCH))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(groupId.toString(), french.getString("groupId"));
		assertEquals("Actif", french.getString("status"));
		assertEquals("ORIG", french.getString("code"));
		assertEquals("Second", french.getString("name"));
		
		JsonArray errors = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.patch("/api/groups/" + groupId)
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("code", "IMMUTABLE")
				.with("englishName", "Invalid")
				.with("frenchName", "Third")
				.toString()))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(1, errors.size());
		assertEquals("error", errors.getObject(0).getString("type"));
		assertEquals("code", errors.getObject(0).getString("path"));
		assertEquals("Group code must not change during updates", errors.getObject(0).getString("reason"));
		
	}
	
	@Test
	public void testGroupFilterByEnglishName() throws Exception {
		LOCALIZED_SORTING_OPTIONS.forEach(l -> permissions.createGroup(l));
		
		JsonObject inativeEnglishAsc = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/groups")
			.queryParam("name", "re")
			.header("Locale", Lang.ENGLISH))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(1, inativeEnglishAsc.getInt("page"));
		assertEquals(4, inativeEnglishAsc.getInt("total"));
		assertEquals(false, inativeEnglishAsc.getBoolean("hasNext"));
		assertEquals(false, inativeEnglishAsc.getBoolean("hasPrevious"));
		assertEquals(JsonArray.class, inativeEnglishAsc.get("content").getClass());
		assertEquals(4, inativeEnglishAsc.getArray("content").size());
		assertEquals(List.of("$ Store", "Montreal", "French", "resume"), inativeEnglishAsc.getArray("content").stream()
			.map(e -> ((JsonObject)e).getString("name")).collect(Collectors.toList()));
	}
	
	@Test
	public void testGroupFilterByFrenchName() throws Exception {
		LOCALIZED_SORTING_OPTIONS.forEach(l -> permissions.createGroup(l));
		
		JsonObject inativeEnglishAsc = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/groups")
			.queryParam("name", "ou")
			.header("Locale", Lang.FRENCH))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(1, inativeEnglishAsc.getInt("page"));
		assertEquals(3, inativeEnglishAsc.getInt("total"));
		assertEquals(false, inativeEnglishAsc.getBoolean("hasNext"));
		assertEquals(false, inativeEnglishAsc.getBoolean("hasPrevious"));
		assertEquals(JsonArray.class, inativeEnglishAsc.get("content").getClass());
		assertEquals(3, inativeEnglishAsc.getArray("content").size());
		assertEquals(List.of("Tout", "tout à fait", "Tout le"), inativeEnglishAsc.getArray("content").stream()
			.map(e -> ((JsonObject)e).getString("name")).collect(Collectors.toList()));
	}
	
	@Test
	public void testGroupFilterByCode() throws Exception {
		LOCALIZED_SORTING_OPTIONS.forEach(l -> permissions.createGroup(l));
		
		JsonObject inativeEnglishAsc = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/groups")
			.queryParam("name", "A"))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(1, inativeEnglishAsc.getInt("page"));
		assertEquals(1, inativeEnglishAsc.getInt("total"));
		assertEquals(false, inativeEnglishAsc.getBoolean("hasNext"));
		assertEquals(false, inativeEnglishAsc.getBoolean("hasPrevious"));
		assertEquals(JsonArray.class, inativeEnglishAsc.get("content").getClass());
		assertEquals(1, inativeEnglishAsc.getArray("content").size());
		assertEquals(List.of("A"), inativeEnglishAsc.getArray("content").stream()
			.map(e -> ((JsonObject)e).getString("name")).collect(Collectors.toList()));
	}
	
	@Test
	public void testEnableDisable() throws Exception {
		Identifier groupId = permissions.createGroup(GROUP).getGroupId();
		assertEquals(Status.ACTIVE, permissions.findGroup(groupId).getStatus());

		JsonArray error1 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/api/groups/" + groupId + "/disable")
			.header("Locale", Lang.ENGLISH))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(groupId.toString(), error1.getObject(0).getString("identifier"));
		assertEquals("error", error1.getObject(0).getString("type"));
		assertEquals("confirm", error1.getObject(0).getString("path"));
		assertEquals("You must send in the confirmation message", error1.getObject(0).getString("reason"));
		assertEquals(Status.ACTIVE, permissions.findGroup(groupId).getStatus());

		JsonObject disable = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.put("/api/groups/" + groupId + "/disable")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("confirm", true)
				.toString()))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(groupId.toString(), disable.getString("groupId"));
		assertEquals("Inactive", disable.getString("status"));
		assertEquals("GRP", disable.getString("code"));
		assertEquals("Group", disable.getString("name"));
		assertEquals(Status.INACTIVE, permissions.findGroup(groupId).getStatus());
		
		JsonArray error2 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/api/groups/" + groupId + "/enable")
			.header("Locale", Lang.ENGLISH))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(groupId.toString(), error2.getObject(0).getString("identifier"));
		assertEquals("error", error2.getObject(0).getString("type"));
		assertEquals("confirm", error2.getObject(0).getString("path"));
		assertEquals("You must send in the confirmation message", error2.getObject(0).getString("reason"));
		assertEquals(Status.INACTIVE, permissions.findGroup(groupId).getStatus());
	
		JsonObject enable = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.put("/api/groups/" + groupId + "/enable")
			.header("Locale", Lang.FRENCH)
			.content(new JsonObject()
				.with("confirm", true)
				.toString()))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(groupId.toString(), enable.getString("groupId"));
		assertEquals("Actif", enable.getString("status"));
		assertEquals("GRP", enable.getString("code"));
		assertEquals("Groupe", enable.getString("name"));
		assertEquals(Status.ACTIVE, permissions.findGroup(groupId).getStatus());
	}

}
