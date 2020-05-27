package ca.magex.crm.restful.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import ca.magex.crm.api.filters.GroupsFilter;
import ca.magex.crm.api.filters.RolesFilter;
import ca.magex.crm.api.system.Lang;
import ca.magex.json.model.JsonObject;

public class InitializationControllerTest extends AbstractControllerTests {
	
	@Before
	public void setup() {
		initiailziation.reset();
	}
	
	@Test
	public void testYamlConfig() throws Exception {
		String yaml = mockMvc.perform(MockMvcRequestBuilders
			.get("/api.yaml")
			.header("Locale", Lang.ENGLISH))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString();
		assertTrue(yaml.replaceAll("\r", "")
			.startsWith("openapi: 3.0.0\n" + 
				"info:\n" + 
				"  version: 1.0.0\n" + 
				"  title: API Reporting\n"));
	}
	
	@Test
	public void testJsonConfig() throws Exception {
		String yaml = mockMvc.perform(MockMvcRequestBuilders
			.get("/api.json")
			.header("Locale", Lang.ENGLISH))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString();
		assertTrue(yaml.replaceAll("\r", "")
			.startsWith("{\n" + 
					"  \"openapi\": \"3.0.0\",\n" + 
					"  \"info\": {\n" + 
					"    \"version\": \"1.0.0\",\n" + 
					"    \"title\": \"API Reporting\"\n" + 
					"  }"));
	}
	
	@Test
	public void testInitialized() throws Exception {
		String json = mockMvc.perform(MockMvcRequestBuilders
			.get("/initialized")
			.header("Locale", Lang.ENGLISH))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString();
		assertEquals("false", json);
	}
	
	@Test
	public void testInitialize() throws Exception {
		assertFalse(initiailziation.isInitialized());
		
		assertEquals(Long.valueOf(0), permissions.findGroups(permissions.defaultGroupsFilter(), GroupsFilter.getDefaultPaging()).getTotalElements());
		assertEquals(Long.valueOf(0), permissions.findRoles(permissions.defaultRolesFilter(), RolesFilter.getDefaultPaging()).getTotalElements());
		assertEquals(Long.valueOf(0), organizations.findOrganizationSummaries(organizations.defaultOrganizationsFilter()).getTotalElements());
		assertEquals(Long.valueOf(0), locations.findLocationSummaries(locations.defaultLocationsFilter()).getTotalElements());
		assertEquals(Long.valueOf(0), persons.findPersonSummaries(persons.defaultPersonsFilter()).getTotalElements());
		assertEquals(Long.valueOf(0), users.findUsers(users.defaultUsersFilter()).getTotalElements());
		
		assertEquals("true", mockMvc.perform(MockMvcRequestBuilders
			.post("/initialize")
			.content(new JsonObject()
				.with("displayName", "System Orgainzation")
				.with("firstName", "First")
				.with("lastName", "Last")
				.with("email", "system@admin.com")
				.with("username", "system")
				.with("password", "admin")
				.toString())
			.header("Locale", Lang.ENGLISH))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertTrue(initiailziation.isInitialized());

		assertEquals(List.of("APP", "CRM", "ORG", "SYS"), permissions.findGroups(permissions.defaultGroupsFilter(), GroupsFilter.getDefaultPaging()).stream().map(g -> g.getCode()).collect(Collectors.toList()));
		assertEquals(List.of("APP_AUTH_REQUEST", "CRM_ADMIN", "CRM_USER", "ORG_ADMIN", "ORG_USER", "SYS_ACCESS", "SYS_ACTUATOR", "SYS_ADMIN"), permissions.findRoles(permissions.defaultRolesFilter(), RolesFilter.getDefaultPaging()).stream().map(r -> r.getCode()).collect(Collectors.toList()));
		assertEquals(List.of("System Orgainzation"), organizations.findOrganizationSummaries(organizations.defaultOrganizationsFilter()).stream().map(o -> o.getDisplayName()).collect(Collectors.toList()));
		assertEquals(List.of(), locations.findLocationSummaries(locations.defaultLocationsFilter()).stream().map(l -> l.getDisplayName()).collect(Collectors.toList()));
		assertEquals(List.of("Last, First"), persons.findPersonSummaries(persons.defaultPersonsFilter()).stream().map(p -> p.getDisplayName()).collect(Collectors.toList()));
		assertEquals(List.of("system"), users.findUsers(users.defaultUsersFilter()).stream().map(u -> u.getUsername()).collect(Collectors.toList()));

		assertEquals("true", mockMvc.perform(MockMvcRequestBuilders
			.post("/initialize")
			.content(new JsonObject()
				.with("displayName", "System Orgainzation")
				.with("firstName", "First")
				.with("lastName", "Last")
				.with("email", "system@admin.com")
				.with("username", "system")
				.with("password", "admin")
				.toString())
			.header("Locale", Lang.ENGLISH))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		initiailziation.dump(baos);
		String[] lines = baos.toString().split("\n");
		assertEquals(15, lines.length);
	}
	
}
