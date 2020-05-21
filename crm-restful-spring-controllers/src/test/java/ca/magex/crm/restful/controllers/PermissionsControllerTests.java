package ca.magex.crm.restful.controllers;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.mapping.data.DataArray;
import ca.magex.crm.mapping.data.DataObject;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = {
		MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED,
		MagexCrmProfiles.CRM_NO_AUTH
})
public class PermissionsControllerTests {

	@Autowired private MockMvc mockMvc;
	
	@Test
	public void testCreateGroup() throws Exception {

		DataObject json = new DataObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/groups")
			.header("Locale", Lang.ENGLISH))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(1, json.getInt("page"));
		assertEquals(0, json.getInt("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(DataArray.class, json.get("content").getClass());
		assertEquals(0, json.getArray("content").size());
		
		json = new DataObject(mockMvc.perform(MockMvcRequestBuilders
			.post("/api/groups")
			.header("Locale", Lang.ENGLISH)
			.content(new DataObject()
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
		
		json = new DataObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/groups/" + groupId)
			.header("Locale", Lang.ENGLISH))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());

		assertEquals(groupId.toString(), json.getString("groupId"));
		assertEquals("Active", json.getString("status"));
		assertEquals("Group", json.getString("name"));

		json = new DataObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/groups/" + groupId)
			.header("Locale", Lang.FRENCH))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());

		assertEquals(groupId.toString(), json.getString("groupId"));
		assertEquals("Actif", json.getString("status"));
		assertEquals("Groupe", json.getString("name"));

		json = new DataObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/groups/" + groupId))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(groupId.toString(), json.getString("groupId"));
		assertEquals("active", json.getString("status"));
		assertEquals("GRP", json.getString("name"));

		json = new DataObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/api/groups")
			.header("Locale", Lang.ENGLISH))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(1, json.getInt("page"));
		assertEquals(1, json.getInt("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(DataArray.class, json.get("content").getClass());
		assertEquals(1, json.getArray("content").size());
		assertEquals(groupId.toString(), json.getArray("content").getObject(0).getString("groupId"));
		assertEquals("active", json.getArray("content").getObject(0).getString("status"));
		assertEquals("GRP", json.getArray("content").getObject(0).getString("name"));
			

	}
		
}
