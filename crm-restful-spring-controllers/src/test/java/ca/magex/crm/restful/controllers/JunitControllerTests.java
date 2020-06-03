package ca.magex.crm.restful.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.test.CrmAsserts;
import ca.magex.json.model.JsonArray;

public class JunitControllerTests extends AbstractControllerTests {
	
	@Test
	public void testBadRequestExceptionRootLocale() throws Exception {
		JsonArray json = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.post("/api/junit/400"))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			.andReturn().getResponse().getContentAsString());
		CrmAsserts.assertSingleJsonMessage(json, new Identifier("junit"), "error", "path", "RSN");
	}
	
	@Test
	public void testBadRequestExceptionEnglishLocale() throws Exception {
		JsonArray json = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.post("/api/junit/400")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			.andReturn().getResponse().getContentAsString());
		CrmAsserts.assertSingleJsonMessage(json, new Identifier("junit"), "error", "path", "English Reason");
	}
	
	@Test
	public void testBadRequestExceptionFrenchLocale() throws Exception {
		JsonArray json = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.post("/api/junit/400")
			.header("Locale", Lang.FRENCH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			.andReturn().getResponse().getContentAsString());
		CrmAsserts.assertSingleJsonMessage(json, new Identifier("junit"), "error", "path", "French Reason");
	}
	
	@Test
	public void testPermissionDeniedException() throws Exception {
		String content = mockMvc.perform(MockMvcRequestBuilders
			.post("/api/junit/403"))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isForbidden())
			.andReturn().getResponse().getContentAsString();
		assertEquals("", content);
	}
	
	@Test
	public void testItemNotFoundException() throws Exception {
		String content = mockMvc.perform(MockMvcRequestBuilders
			.post("/api/junit/404"))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isNotFound())
			.andReturn().getResponse().getContentAsString();
		assertEquals("", content);
	}
	
	@Test
	public void testExcetpion() throws Exception {
		String content = mockMvc.perform(MockMvcRequestBuilders
			.post("/api/junit/500"))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isInternalServerError())
			.andReturn().getResponse().getContentAsString();
		assertEquals("", content);
	}
	
}
