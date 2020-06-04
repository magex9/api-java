package ca.magex.crm.restful.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.test.CrmAsserts;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonObject;

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
	
	@Test
	public void testExtractBodyIOException() throws Exception {
		JunitController controller = new JunitController();
		HttpServletRequest req = new MockHttpServletRequest() {
			@Override
			public ServletInputStream getInputStream() {
				return new ServletInputStream() {

					@Override
					public int read() throws IOException {
						throw new IOException("Junit Exception");
					}

					@Override
					public void setReadListener(ReadListener readListener) {

					}
					
					@Override
					public boolean isReady() {
						return false;
					}
					
					@Override
					public boolean isFinished() {
						return false;
					}
				};
			}
		};
		try {
			controller.extractBody(req);
		} catch (RuntimeException e) {
			assertEquals(IOException.class, e.getCause().getClass());
		}
	}
	
	private HttpServletRequest buildRequest(Locale locale, String contentType) {
		MockHttpServletRequest req = new MockHttpServletRequest();
		if (locale != null)
			req.addHeader("Locale", locale);
		req.setContentType(contentType);
		return req;
	}
	
	@Test
	public void testExtractLocale() throws Exception {
		JunitController controller = new JunitController();
//		assertEquals(Lang.ROOT, controller.extractLocale(buildRequest(null, "application/json")));
//		assertEquals(Lang.ENGLISH, controller.extractLocale(buildRequest(Lang.ENGLISH, "application/json")));
//		assertEquals(Lang.FRENCH, controller.extractLocale(buildRequest(Lang.FRENCH, "application/json")));
//		assertNull(controller.extractLocale(buildRequest(null, "application/json+ld")));
		assertNull(controller.extractLocale(buildRequest(Lang.ENGLISH, "application/json+ld")));
		assertNull(controller.extractLocale(buildRequest(Lang.FRENCH, "application/json+ld")));
	}
	
	@Test
	public void testIdentifierValid() throws Exception {
		JsonObject json = new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.post("/api/junit/identifier/testId")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("testId", "test")
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString());
		assertEquals(List.of("@type", "groupId", "status", "code", "name"), json.keys());
		assertEquals("Group", json.getString("@type"));
		assertEquals("test", json.getString("groupId"));
		assertEquals("Active", json.getString("status"));
		assertEquals("GRP", json.getString("code"));
		assertEquals("Group", json.getString("name"));	
	}
	
	@Test
	public void testIdentifierClassCastException() throws Exception {
		JsonArray json = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.post("/api/junit/identifier/status")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			.andReturn().getResponse().getContentAsString());
		CrmAsserts.assertSingleJsonMessage(json, null, "error", "status", "Field is mandatory");
	}
	
	@Test
	public void testIdentifierNoSuchElementException() throws Exception {
		JsonArray json = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.post("/api/junit/identifier/groupId")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("groupId", false)
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			.andReturn().getResponse().getContentAsString());
		CrmAsserts.assertSingleJsonMessage(json, null, "error", "groupId", "Invalid format");
	}
	
	@Test
	public void testStringsClassCastException() throws Exception {
		JsonArray json = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.post("/api/junit/strings/status")
			.header("Locale", Lang.ENGLISH))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			.andReturn().getResponse().getContentAsString());
		CrmAsserts.assertSingleJsonMessage(json, null, "error", "status", "Field is mandatory");
	}
	
	@Test
	public void testStringsNoSuchElementException() throws Exception {
		JsonArray json = new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.post("/api/junit/strings/groupId")
			.header("Locale", Lang.ENGLISH)
			.content(new JsonObject()
				.with("groupId", false)
				.toString()))
			//.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			.andReturn().getResponse().getContentAsString());
		CrmAsserts.assertSingleJsonMessage(json, null, "error", "groupId", "Invalid format");
	}
	
	@Test
	public void testBuildingActions() throws Exception {
		JsonObject json = new JunitController().action("name", "title", "method", "href");
		assertEquals(List.of("name", "title", "method", "href"), json.keys());
		assertEquals("name", json.getString("name"));
		assertEquals("title", json.getString("title"));
		assertEquals("method", json.getString("method"));
		assertEquals("href", json.getString("href"));	}
	
}
