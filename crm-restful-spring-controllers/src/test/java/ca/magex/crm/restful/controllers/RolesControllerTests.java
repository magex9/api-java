package ca.magex.crm.restful.controllers;

import static ca.magex.crm.test.CrmAsserts.ADMIN;
import static ca.magex.crm.test.CrmAsserts.LOCALIZED_SORTED_ENGLISH_ASC;
import static ca.magex.crm.test.CrmAsserts.LOCALIZED_SORTED_FRENCH_ASC;
import static ca.magex.crm.test.CrmAsserts.LOCALIZED_SORTING_OPTIONS;
import static ca.magex.crm.test.CrmAsserts.SYS;
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

public class RolesControllerTests extends AbstractControllerTests {
	
	private Localized role;
	
	private Identifier sysId;
	
	@Before
	public void setup() {
		initialize();
		role = new Localized("NEW_ROLE", "New Role", "Nouveau rôle");
		sysId = crm.findGroupByCode(SYS.getCode()).getGroupId();
	}

	@Test
	public void testCreateRole() throws Exception {
		// Get the initial list of groups to make sure they are blank
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/roles")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(8, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(8, json.getArray("content").size());
		
		assertEquals(List.of("Authorization Requestor", "CRM Admin", "CRM Viewer", "Organization Admin", "Organization Viewer", "System Access", "System Actuator", "System Administrator"), 
				json.getArray("content", JsonObject.class).stream().map(r -> r.getString("name")).collect(Collectors.toList()));
		
		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.post("/rest/roles")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("groupId", sysId.toString())
				.with("code", role.getCode())
				.with("englishName", role.getEnglishName())
				.with("frenchName", role.getFrenchName())
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertTrue(json.getString("groupId").matches("[A-Za-z0-9]+"));
		assertEquals("Active", json.getString("status"));
		Identifier roleId = new Identifier(json.getString("roleId"));
		
		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/roles/" + roleId)
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(roleId.toString(), json.getString("roleId"));
		assertEquals(sysId.toString(), json.getString("groupId"));
		assertEquals("Active", json.getString("status"));
		assertEquals(role.getCode(), json.getString("code"));
		assertEquals(role.getEnglishName(), json.getString("name"));

		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/roles/" + roleId)
			.header("Locale", Lang.FRENCH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(roleId.toString(), json.getString("roleId"));
		assertEquals(sysId.toString(), json.getString("groupId"));
		assertEquals("Actif", json.getString("status"));
		assertEquals(role.getFrenchName(), json.getString("name"));

		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/roles/" + roleId))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(roleId.toString(), json.getString("roleId"));
		assertEquals(sysId.toString(), json.getString("groupId"));
		assertEquals("active", json.getString("status"));
		assertEquals(role.getCode(), json.getString("name"));

		json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/roles")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(1, json.getInt("page"));
		assertEquals(9, json.getInt("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(JsonArray.class, json.get("content").getClass());
		assertEquals(9, json.getArray("content").size());
		
		assertEquals(List.of("Authorization Requestor", "CRM Admin", "CRM Viewer", "New Role", "Organization Admin", "Organization Viewer", "System Access", "System Actuator", "System Administrator"), 
			json.getArray("content", JsonObject.class).stream().map(r -> r.getString("name")).collect(Collectors.toList()));
	}
	
	@Test
	public void testCreateRoleEnglishNameTests() throws Exception {
		JsonArray missing = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.post("/rest/roles")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("groupId", sysId.toString())
				.with("code", role.getCode())
				//.with("englishName", role.getEnglishName())
				.with("frenchName", role.getFrenchName())
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			.andReturn().getResponse().getContentAsString());
		assertEquals(1, missing.size());
		assertEquals("error", missing.getObject(0).getString("type"));
		assertEquals("englishName", missing.getObject(0).getString("path"));
		assertEquals("Field is mandatory", missing.getObject(0).getString("reason"));
			
		JsonArray spaces = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.post("/rest/roles")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("groupId", sysId.toString())
				.with("code", role.getCode())
				.with("englishName", "  ")
				.with("frenchName", role.getFrenchName())
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			.andReturn().getResponse().getContentAsString());
		System.out.println(spaces);
		assertEquals(1, spaces.size());
		assertEquals("error", spaces.getObject(0).getString("type"));
		assertEquals("englishName", spaces.getObject(0).getString("path"));
		assertEquals("An English description is required", spaces.getObject(0).getString("reason"));
				
		JsonArray classCast = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.post("/rest/roles")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("groupId", sysId.toString())
				.with("code", role.getCode())
				.with("englishName", true)
				.with("frenchName", role.getFrenchName())
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			.andReturn().getResponse().getContentAsString());
		assertEquals(1, classCast.size());
		assertEquals("error", classCast.getObject(0).getString("type"));
		assertEquals("englishName", classCast.getObject(0).getString("path"));
		assertEquals("Invalid format", classCast.getObject(0).getString("reason"));
					
		JsonArray maxLength = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.post("/rest/roles")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("groupId", sysId.toString())
				.with("code", role.getCode())
				.with("englishName", LoremIpsumGenerator.buildWords(20))
				.with("frenchName", role.getFrenchName())
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
	public void testFirstPageEnglishSortRole() throws Exception {
		LOCALIZED_SORTING_OPTIONS.forEach(l -> crm.createRole(sysId, l));
		
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/roles")
			.queryParam("order", "englishName")
			.queryParam("direction", "asc")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(1, json.getInt("page"));
		assertEquals(40, json.getInt("total"));
		assertEquals(true, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(JsonArray.class, json.get("content").getClass());
		assertEquals(10, json.getArray("content").size());
		assertEquals(LOCALIZED_SORTED_ENGLISH_ASC.subList(0, 10), json.getArray("content").stream()
			.map(e -> ((JsonObject)e).getString("name")).collect(Collectors.toList()));
	}
	
	@Test
	public void testSecondPageEnglishSortRole() throws Exception {
		LOCALIZED_SORTING_OPTIONS.forEach(l -> crm.createRole(sysId, l));
		
		JsonObject page2 = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/roles")
			.queryParam("page", "2")
			.queryParam("limit", "5")
			.queryParam("order", "frenchName")
			.queryParam("direction", "asc")
			.header("Locale", Lang.FRENCH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(2, page2.getInt("page"));
		assertEquals(40, page2.getInt("total"));
		assertEquals(true, page2.getBoolean("hasNext"));
		assertEquals(true, page2.getBoolean("hasPrevious"));
		assertEquals(JsonArray.class, page2.get("content").getClass());
		assertEquals(5, page2.getArray("content").size());
		assertEquals(LOCALIZED_SORTED_FRENCH_ASC.subList(5, 10), page2.getArray("content").stream()
			.map(e -> ((JsonObject)e).getString("name")).collect(Collectors.toList()));
	}
	
	@Test
	public void testInactiveEnglishRole() throws Exception {
		LOCALIZED_SORTING_OPTIONS.forEach(l -> crm.createRole(sysId, l));
		
		crm.disableRole(crm.findRoleByCode("E").getRoleId());
		crm.disableRole(crm.findRoleByCode("F").getRoleId());
		crm.disableRole(crm.findRoleByCode("H").getRoleId());
		
		List<String> INACTIVE_ENGLISH_ASC = List.of(
			crm.findRoleByCode("E").getName(Lang.ENGLISH),
			crm.findRoleByCode("F").getName(Lang.ENGLISH),
			crm.findRoleByCode("H").getName(Lang.ENGLISH)
		);
		
		JsonObject inativeEnglishAsc = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/roles")
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
	public void testFilterRolesByGroupId() throws Exception {
		crm.createRole(sysId, role).getRoleId();
		crm.disableRole(crm.createRole(sysId, ADMIN).getRoleId());
		
		JsonObject orig = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/roles")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(10, orig.getInt("total"));
		
		Identifier groupId = crm.createGroup(new Localized("NEW_GROUP", "New Group", "Nouveau groupe")).getGroupId();
		crm.createRole(groupId, new Localized("ROLE_A", "Role A", "A Role")).getRoleId();
		crm.createRole(groupId, new Localized("ROLE_B", "Role B", "B Role")).getRoleId();
		
		JsonObject all = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/roles")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(12, all.getInt("total"));
		
		JsonObject inactive = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/roles")
			.queryParam("status", "Inactive")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(1, inactive.getInt("total"));
			
		JsonObject activeOrg = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/roles")
			.queryParam("groupId", groupId.toString())
			.queryParam("status", "Actif")
			.header("Locale", Lang.FRENCH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(2, activeOrg.getInt("total"));
				
		JsonObject inactiveOrg = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/roles")
			.queryParam("groupId", groupId.toString())
			.queryParam("status", "Inactive")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(0, inactiveOrg.getInt("total"));
					
		JsonObject allOrg = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/roles")
			.queryParam("groupId", groupId.toString())
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(2, allOrg.getInt("total"));
	}
	
	@Test
	public void testInactiveSortWithNotLocaleRole() throws Exception {
		LOCALIZED_SORTING_OPTIONS.forEach(l -> crm.createRole(sysId, l));
		
		crm.disableRole(crm.findRoleByCode("E").getRoleId());
		crm.disableRole(crm.findRoleByCode("F").getRoleId());
		crm.disableRole(crm.findRoleByCode("H").getRoleId());
		
		JsonObject inativeCodeDesc = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/roles")
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
	public void testUpdatingRole() throws Exception {
		Identifier roleId = crm.createRole(sysId, new Localized("ORIG", "Original", "First")).getRoleId();
		
		JsonObject orig = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/roles/" + roleId)
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(roleId.toString(), orig.getString("roleId"));
		assertEquals("Active", orig.getString("status"));
		assertEquals("ORIG", orig.getString("code"));
		assertEquals("Original", orig.getString("name"));

		JsonObject updated = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.patch("/rest/roles/" + roleId)
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("code", "ORIG")
				.with("englishName", "Updated")
				.with("frenchName", "Second")
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(roleId.toString(), updated.getString("roleId"));
		assertEquals("Active", updated.getString("status"));
		assertEquals("ORIG", updated.getString("code"));
		assertEquals("Updated", updated.getString("name"));
		
		JsonObject english = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/roles/" + roleId)
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(roleId.toString(), english.getString("roleId"));
		assertEquals("Active", english.getString("status"));
		assertEquals("ORIG", english.getString("code"));
		assertEquals("Updated", english.getString("name"));
		
		JsonObject french = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/roles/" + roleId)
			.header("Locale", Lang.FRENCH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(roleId.toString(), french.getString("roleId"));
		assertEquals("Actif", french.getString("status"));
		assertEquals("ORIG", french.getString("code"));
		assertEquals("Second", french.getString("name"));
		
		JsonArray errors = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.patch("/rest/roles/" + roleId)
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
		assertEquals("Role code must not change during updates", errors.getObject(0).getString("reason"));
	}
	
	@Test
	public void testRoleFilterByEnglishName() throws Exception {
		LOCALIZED_SORTING_OPTIONS.forEach(l -> crm.createRole(sysId, l));
		
		JsonObject inativeEnglishAsc = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/roles")
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
		assertEquals(List.of("Authorization Requestor", "$ Store", "Montreal", "French", "resume"), inativeEnglishAsc.getArray("content").stream()
			.map(e -> ((JsonObject)e).getString("name")).collect(Collectors.toList()));

		JsonObject englishNameFilter = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/roles")
			.queryParam("englishName", "re")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(inativeEnglishAsc, englishNameFilter);
	}
	
	@Test
	public void testRoleFilterByFrenchName() throws Exception {
		LOCALIZED_SORTING_OPTIONS.forEach(l -> crm.createRole(sysId, l));
		
		JsonObject inativeFrenchAsc = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/roles")
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
			.get("/rest/roles")
			.queryParam("frenchName", "ou")
			.header("Locale", Lang.FRENCH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(inativeFrenchAsc, frenchNameFilter);
	}
	
	@Test
	public void testRoleFilterByCode() throws Exception {
		LOCALIZED_SORTING_OPTIONS.forEach(l -> crm.createRole(sysId, l));
		
		JsonObject activeCodeAsc = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/roles")
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
			.get("/rest/roles")
			.queryParam("code", "A"))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(activeCodeAsc, codeFilter);
	}
	
	@Test
	public void testEnableDisableRole() throws Exception {
		Identifier roleId = crm.createRole(sysId, role).getRoleId();
		assertEquals(Status.ACTIVE, crm.findRole(roleId).getStatus());

		JsonArray error1 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/rest/roles/" + roleId + "/disable")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(roleId.toString(), error1.getObject(0).getString("identifier"));
		assertEquals("error", error1.getObject(0).getString("type"));
		assertEquals("confirm", error1.getObject(0).getString("path"));
		assertEquals("You must send in the confirmation message", error1.getObject(0).getString("reason"));
		assertEquals(Status.ACTIVE, crm.findRole(roleId).getStatus());

		JsonArray error2 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/rest/roles/" + roleId + "/disable")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("confirm", false)
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(roleId.toString(), error2.getObject(0).getString("identifier"));
		assertEquals("error", error2.getObject(0).getString("type"));
		assertEquals("confirm", error2.getObject(0).getString("path"));
		assertEquals("You must send in the confirmation message", error2.getObject(0).getString("reason"));
		assertEquals(Status.ACTIVE, crm.findRole(roleId).getStatus());

		JsonArray error3 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/rest/roles/" + roleId + "/disable")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("confirm", "Test")
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(roleId.toString(), error3.getObject(0).getString("identifier"));
		assertEquals("error", error3.getObject(0).getString("type"));
		assertEquals("confirm", error3.getObject(0).getString("path"));
		assertEquals("Confirmation message must be a boolean", error3.getObject(0).getString("reason"));
		assertEquals(Status.ACTIVE, crm.findRole(roleId).getStatus());

		JsonObject disable = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.put("/rest/roles/" + roleId + "/disable")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("confirm", true)
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(roleId.toString(), disable.getString("roleId"));
		assertEquals("Inactive", disable.getString("status"));
		assertEquals(role.getCode(), disable.getString("code"));
		assertEquals(role.getEnglishName(), disable.getString("name"));
		assertEquals(Status.INACTIVE, crm.findRole(roleId).getStatus());
		
		JsonArray error4 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/rest/roles/" + roleId + "/enable")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(roleId.toString(), error4.getObject(0).getString("identifier"));
		assertEquals("error", error4.getObject(0).getString("type"));
		assertEquals("confirm", error4.getObject(0).getString("path"));
		assertEquals("You must send in the confirmation message", error4.getObject(0).getString("reason"));
		assertEquals(Status.INACTIVE, crm.findRole(roleId).getStatus());
		
		JsonArray error5 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/rest/roles/" + roleId + "/enable")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("confirm", false)
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(roleId.toString(), error5.getObject(0).getString("identifier"));
		assertEquals("error", error5.getObject(0).getString("type"));
		assertEquals("confirm", error5.getObject(0).getString("path"));
		assertEquals("You must send in the confirmation message", error5.getObject(0).getString("reason"));
		assertEquals(Status.INACTIVE, crm.findRole(roleId).getStatus());
		
		JsonArray error6 = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/rest/roles/" + roleId + "/enable")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("confirm", "test")
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andReturn().getResponse().getContentAsString());
		assertEquals(roleId.toString(), error6.getObject(0).getString("identifier"));
		assertEquals("error", error6.getObject(0).getString("type"));
		assertEquals("confirm", error6.getObject(0).getString("path"));
		assertEquals("Confirmation message must be a boolean", error6.getObject(0).getString("reason"));
		assertEquals(Status.INACTIVE, crm.findRole(roleId).getStatus());
	
		JsonObject enable = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.put("/rest/roles/" + roleId + "/enable")
			.header("Locale", Lang.FRENCH)
			.content(new JsonObject()
				.with("confirm", true)
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(roleId.toString(), enable.getString("roleId"));
		assertEquals("Actif", enable.getString("status"));
		assertEquals(role.getCode(), enable.getString("code"));
		assertEquals(role.getFrenchName(), enable.getString("name"));
		assertEquals(Status.ACTIVE, crm.findRole(roleId).getStatus());
	}
	

	
}
