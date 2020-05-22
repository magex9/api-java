package ca.magex.crm.restful.controllers;

import static ca.magex.crm.test.CrmAsserts.PERSON_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Before;
import org.junit.Ignore;
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
import ca.magex.crm.api.filters.GroupsFilter;
import ca.magex.crm.api.services.CrmInitializationService;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.system.Lang;
import ca.magex.json.model.JsonObject;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = {
		MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED,
		MagexCrmProfiles.CRM_NO_AUTH
})
@Ignore
public class PermissionsControllerJwtTests {
	
	@Autowired private CrmInitializationService initiailziation;

	@Autowired private CrmPermissionService permissions;

	@Autowired private MockMvc mockMvc;
	
	@Before
	public void setup() {
		initiailziation.reset();
		initiailziation.initializeSystem("System", PERSON_NAME, "system@admin.com", "system", "admin");
	}
	
	@Test
	public void testUnauthenticatedCreateGroup() throws Exception {
		assertEquals(1, permissions.findGroups(permissions.defaultGroupsFilter(), GroupsFilter.getDefaultPaging()).getTotalElements());
		
		mockMvc.perform(MockMvcRequestBuilders
			.post("/api/groups")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("code", "GRP")
				.with("englishName", "Group")
				.with("frenchName", "Groupe")
				.toString()))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isUnauthorized())
			.andReturn().getResponse().getContentAsString();

		assertEquals(1, permissions.findGroups(permissions.defaultGroupsFilter(), GroupsFilter.getDefaultPaging()).getTotalElements());
	}
	
	@Test
	public void testAuthenticatedCreateGroup() throws Exception {
		assertEquals(1, permissions.findGroups(permissions.defaultGroupsFilter(), GroupsFilter.getDefaultPaging()).getTotalElements());
		
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
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

		assertEquals(2, permissions.findGroups(permissions.defaultGroupsFilter(), GroupsFilter.getDefaultPaging()).getTotalElements());
	}
	
}
