package ca.magex.crm.restful.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Type;
import ca.magex.json.model.JsonObject;

public class ConfigurationControllerTest extends AbstractControllerTests {
	
	@Before
	public void setup() {
		crm.reset();
	}
	
	@Test
	public void testSwaggerUI() throws Exception {
		String html = mockMvc.perform(MockMvcRequestBuilders
			.get("/rest")
			.header("Locale", Lang.ENGLISH))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString();
		
		assertTrue(html.contains("/crm/rest/api.json"));
	}
	
	@Test
	public void testJsonConfig() throws Exception {
		String yaml = mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/api.json")
			.header("Locale", Lang.ENGLISH))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString();
		assertTrue(yaml.replaceAll("\r", "")
			.startsWith("{\n" + 
					"  \"openapi\": \"3.0.0\",\n" + 
					"  \"info\": {\n" + 
					"    \"version\": \"1.0.0\",\n" + 
					"    \"title\": \"Customer Relationship Management\"\n" + 
					"  }"));
	}
	
	@Test
	public void testInitialized() throws Exception {
		String json = mockMvc.perform(MockMvcRequestBuilders
			.get("/rest/initialized")
			.header("Locale", Lang.ENGLISH))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString();
		assertEquals("false", json);
	}
	
	@Test
	public void testInitialize() throws Exception {
		assertFalse(crm.isInitialized());
		
		assertEquals(0L, crm.findOptions(crm.defaultOptionsFilter(), OptionsFilter.getDefaultPaging()).getTotalElements());
		assertEquals(0L, crm.findOrganizationSummaries(crm.defaultOrganizationsFilter()).getTotalElements());
		assertEquals(0L, crm.findLocationSummaries(crm.defaultLocationsFilter()).getTotalElements());
		assertEquals(0L, crm.findPersonSummaries(crm.defaultPersonsFilter()).getTotalElements());
		assertEquals(0L, crm.findUsers(crm.defaultUsersFilter()).getTotalElements());
		
		assertEquals("true", mockMvc.perform(MockMvcRequestBuilders
			.post("/rest/initialize")
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
		assertTrue(crm.isInitialized());

		assertEquals(List.of("APP", "CRM", "ORG", "SYS"), crm.findOptions(crm.defaultOptionsFilter().withType(Type.AUTHENTICATION_GROUP), OptionsFilter.getDefaultPaging()).stream().map(g -> g.getCode()).collect(Collectors.toList()));
		assertEquals(List.of("APP/AUTHENTICATOR", "CRM/ADMIN", "CRM/USER", "ORG/ADMIN", "ORG/USER", "SYS/ACCESS", "SYS/ACTUATOR", "SYS/ADMIN"), crm.findOptions(crm.defaultOptionsFilter().withType(Type.AUTHENTICATION_ROLE), OptionsFilter.getDefaultPaging()).stream().map(r -> r.getCode()).collect(Collectors.toList()));
		assertEquals(List.of("System Orgainzation"), crm.findOrganizationSummaries(crm.defaultOrganizationsFilter()).stream().map(o -> o.getDisplayName()).collect(Collectors.toList()));
		assertEquals(List.of("System Administrator"), crm.findLocationSummaries(crm.defaultLocationsFilter()).stream().map(l -> l.getDisplayName()).collect(Collectors.toList()));
		assertEquals(List.of("Last, First"), crm.findPersonSummaries(crm.defaultPersonsFilter()).stream().map(p -> p.getDisplayName()).collect(Collectors.toList()));
		assertEquals(List.of("system"), crm.findUsers(crm.defaultUsersFilter()).stream().map(u -> u.getUsername()).collect(Collectors.toList()));

		assertEquals("true", mockMvc.perform(MockMvcRequestBuilders
			.post("/rest/initialize")
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
		crm.dump(baos);
		String[] lines = baos.toString().split("\n");
		assertEquals(184, lines.length);
	}
	
}
