package ca.magex.crm.restful.controllers;

import static ca.magex.crm.test.CrmAsserts.assertSingleJsonMessage;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.id.AuthenticationGroupIdentifier;
import ca.magex.crm.api.system.id.MessageTypeIdentifier;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonObject;

public class JunitControllerTests extends AbstractControllerTests {
	
	@Before
	public void setup() {
		initialize();
	}
	
	@Test
	public void testBadRequestExceptionRootLocale() throws Exception {
		JsonArray json = post("/junit/400", null, HttpStatus.BAD_REQUEST, new JsonObject());
		assertSingleJsonMessage(json, new AuthenticationGroupIdentifier("junit"), MessageTypeIdentifier.ERROR, "path", "http://api.magex.ca/crm/rest/options/phrases/validation/field/required");
	}
	
	@Test
	public void testBadRequestExceptionEnglishLocale() throws Exception {
		JsonArray json = post("/junit/400", Lang.ENGLISH, HttpStatus.BAD_REQUEST, new JsonObject());
		assertSingleJsonMessage(json, new AuthenticationGroupIdentifier("junit"), MessageTypeIdentifier.ERROR, "path", "Field is required");
	}
	
	@Test
	public void testBadRequestExceptionFrenchLocale() throws Exception {
		JsonArray json = post("/junit/400", Lang.FRENCH, HttpStatus.BAD_REQUEST, new JsonObject());
		assertSingleJsonMessage(json, new AuthenticationGroupIdentifier("junit"), MessageTypeIdentifier.ERROR, "path", "Champ requis");
	}
	
	@Test
	public void testPermissionDeniedException() throws Exception {
		String content = mockMvc.perform(MockMvcRequestBuilders
			.post("/rest/junit/403"))
			.andExpect(MockMvcResultMatchers.status().isForbidden())
			.andReturn().getResponse().getContentAsString();
		assertEquals("", content);
	}
	
	@Test
	public void testItemNotFoundException() throws Exception {
		String content = mockMvc.perform(MockMvcRequestBuilders
			.post("/rest/junit/404"))
			.andExpect(MockMvcResultMatchers.status().isNotFound())
			.andReturn().getResponse().getContentAsString();
		assertEquals("", content);
	}
	
	@Test
	public void testExcetpion() throws Exception {
		String content = mockMvc.perform(MockMvcRequestBuilders
			.post("/rest/junit/500"))
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
		assertEquals(Lang.ROOT, controller.extractLocale(buildRequest(null, "application/json")));
		assertEquals(Lang.ENGLISH, controller.extractLocale(buildRequest(Lang.ENGLISH, "application/json")));
		assertEquals(Lang.FRENCH, controller.extractLocale(buildRequest(Lang.FRENCH, "application/json")));
		assertNull(controller.extractLocale(buildRequest(null, "application/json+ld")));
		assertNull(controller.extractLocale(buildRequest(Lang.ENGLISH, "application/json+ld")));
		assertNull(controller.extractLocale(buildRequest(Lang.FRENCH, "application/json+ld")));
	}
	
	@Test
	public void testIdentifierValidLinked() throws Exception {
		JsonObject json = post("/junit/identifier/testId", null, HttpStatus.OK, new JsonObject()
			.with("testId", new AuthenticationGroupIdentifier("ORG").toString()));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), json.keys());
		assertEquals("http://api.magex.ca/crm/schema/options/AuthenticationGroups", json.getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/authentication-groups/grp", json.getString("@id"));
		assertEquals("GRP", json.getString("@value"));
		assertEquals("Group", json.getString("@en"));
		assertEquals("Groupe", json.getString("@fr"));
	}
	
	@Test
	public void testIdentifierClassCastException() throws Exception {
		JsonArray json = post("/junit/identifier/status", Lang.ENGLISH, HttpStatus.BAD_REQUEST, new JsonObject());
		assertSingleJsonMessage(json, null, MessageTypeIdentifier.ERROR, "status", "Field is required");
	}
	
	@Test
	public void testIdentifierNoSuchElementException() throws Exception {
		JsonArray json = post("/junit/identifier/groupId", Lang.ENGLISH, HttpStatus.BAD_REQUEST, new JsonObject().with("groupId", false));
		assertSingleJsonMessage(json, null, MessageTypeIdentifier.ERROR, "groupId", "Format is invalid");
	}
	
	@Test
	public void testStringsClassCastException() throws Exception {
		JsonArray json = post("/junit/strings/status", Lang.ENGLISH, HttpStatus.BAD_REQUEST, new JsonObject());
		assertSingleJsonMessage(json, null, MessageTypeIdentifier.ERROR, "status", "Field is required");
	}
	
	@Test
	public void testStringsNoSuchElementException() throws Exception {
		JsonArray json = post("/junit/strings/groupId", Lang.ENGLISH, HttpStatus.BAD_REQUEST, new JsonObject().with("groupId", false));
		assertSingleJsonMessage(json, null, MessageTypeIdentifier.ERROR, "groupId", "Format is invalid");
	}
	
	@Test
	public void testObjectClassCastException() throws Exception {
		JsonArray json = post("/junit/object/status", Lang.ENGLISH, HttpStatus.BAD_REQUEST, new JsonObject());
		assertSingleJsonMessage(json, null, MessageTypeIdentifier.ERROR, "status", "Field is required");
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
