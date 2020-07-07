package ca.magex.crm.restful.controllers;

import static ca.magex.crm.test.CrmAsserts.SYSTEM_EMAIL;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_ORG;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_PERSON;
import static org.springframework.test.util.AssertionErrors.assertEquals;

import java.util.Locale;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.CrmProfiles;
import ca.magex.crm.api.authentication.CrmAuthenticationService;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonObject;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = {
		CrmProfiles.CRM_DATASTORE_CENTRALIZED,
		CrmProfiles.CRM_NO_AUTH
})
public abstract class AbstractControllerTests {
	
	@Autowired protected Crm crm;
	
	@Autowired protected CrmAuthenticationService auth;
	
	@Autowired protected MockMvc mockMvc;
	
	public void initialize() {
		crm.reset();
		crm.initializeSystem(SYSTEM_ORG, SYSTEM_PERSON, SYSTEM_EMAIL, "admin", "admin");
		auth.login("admin", "admin");
	}
	
	public JsonObject get(Object path, Locale locale, HttpStatus status) throws Exception {
		return new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest" + path)
			.header("Content-Type", locale == null ? "application/json+ld" : "application/json")
			.header("Locale", locale == null ? "" : locale))
			.andExpect(result -> assertEquals("Status", status.value(), result.getResponse().getStatus()))
			.andReturn().getResponse().getContentAsString());
	}
	
	public JsonArray gets(Object path, Locale locale, HttpStatus status) throws Exception {
		return new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.get("/rest" + path)
			.header("Content-Type", locale == null ? "application/json+ld" : "application/json")
			.header("Locale", locale == null ? "" : locale))
			.andExpect(result -> assertEquals("Status", status.value(), result.getResponse().getStatus()))
			.andReturn().getResponse().getContentAsString());
	}

	public JsonObject post(Object path, Locale locale, HttpStatus status, JsonObject content) throws Exception {
		return new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.post("/rest" + path)
			.header("Content-Type", locale == null ? "application/json+ld" : "application/json")
			.header("Locale", locale == null ? "" : locale)
			.content(content.toString()))
			.andExpect(result -> assertEquals("Status", status.value(), result.getResponse().getStatus()))
			.andReturn().getResponse().getContentAsString());
	}
	
	public JsonArray posts(Object path, Locale locale, HttpStatus status, JsonObject content) throws Exception {
		return new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.post("/rest" + path)
			.header("Content-Type", locale == null ? "application/json+ld" : "application/json")
			.header("Locale", locale == null ? "" : locale)
			.content(content.toString()))
			.andExpect(result -> assertEquals("Status", status.value(), result.getResponse().getStatus()))
			.andReturn().getResponse().getContentAsString());
	}
	
	public JsonObject patch(Object path, Locale locale, HttpStatus status, JsonObject content) throws Exception {
		return new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.patch("/rest" + path)
			.header("Content-Type", locale == null ? "application/json+ld" : "application/json")
			.header("Locale", locale == null ? "" : locale)
			.content(content.toString()))
			.andExpect(result -> assertEquals("Status", status.value(), result.getResponse().getStatus()))
			.andReturn().getResponse().getContentAsString());
	}
	
	public JsonArray patches(Object path, Locale locale, HttpStatus status, JsonObject content) throws Exception {
		return new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.patch("/rest" + path)
			.header("Content-Type", locale == null ? "application/json+ld" : "application/json")
			.header("Locale", locale == null ? "" : locale)
			.content(content.toString()))
			.andExpect(result -> assertEquals("Status", status.value(), result.getResponse().getStatus()))
			.andReturn().getResponse().getContentAsString());
	}
	
	public JsonObject put(Object path, Locale locale, HttpStatus status, JsonObject content) throws Exception {
		return new JsonObject(mockMvc.perform(MockMvcRequestBuilders
			.put("/rest" + path)
			.header("Content-Type", locale == null ? "application/json+ld" : "application/json")
			.header("Locale", locale == null ? "" : locale)
			.content(content.toString()))
			.andExpect(result -> assertEquals("Status", status.value(), result.getResponse().getStatus()))
			.andReturn().getResponse().getContentAsString());
	}
	
	public JsonArray puts(Object path, Locale locale, HttpStatus status, JsonObject content) throws Exception {
		return new JsonArray(mockMvc.perform(MockMvcRequestBuilders
			.put("/rest" + path)
			.header("Content-Type", locale == null ? "application/json+ld" : "application/json")
			.header("Locale", locale == null ? "" : locale)
			.content(content == null ? "" : content.toString()))
			.andExpect(result -> assertEquals("Status", status.value(), result.getResponse().getStatus()))
			.andReturn().getResponse().getContentAsString());
	}
	
}
