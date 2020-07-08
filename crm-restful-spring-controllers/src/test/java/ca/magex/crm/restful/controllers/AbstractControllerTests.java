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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.CrmProfiles;
import ca.magex.crm.api.authentication.CrmAuthenticationService;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonParser;

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
	
	public String request(HttpMethod method, Object path, Locale locale, HttpStatus status, JsonObject content) throws Exception {
		return mockMvc.perform(MockMvcRequestBuilders
			.request(method, "/rest" + path)
			.header("Content-Type", locale == null ? "application/json+ld" : "application/json")
			.header("Locale", locale == null ? "" : locale)
			.content(content == null ? "{}" : content.toString()))
			.andExpect(result -> assertEquals("Status", status.value(), result.getResponse().getStatus()))
			.andReturn().getResponse().getContentAsString();
	}
	
	@SuppressWarnings("unchecked")
	public <E extends JsonElement> E get(Object path) throws Exception {
		return (E)JsonParser.parse(request(HttpMethod.GET, path, null, HttpStatus.OK, null));
	}

	@SuppressWarnings("unchecked")
	public <E extends JsonElement> E get(Object path, Locale locale, HttpStatus status) throws Exception {
		return (E)JsonParser.parse(request(HttpMethod.GET, path, locale, status, null));
	}

	@SuppressWarnings("unchecked")
	public <E extends JsonElement> E post(Object path, JsonObject content) throws Exception {
		return (E)JsonParser.parse(request(HttpMethod.POST, path, null, HttpStatus.OK, content));
	}
	
	@SuppressWarnings("unchecked")
	public <E extends JsonElement> E post(Object path, Locale locale, HttpStatus status, JsonObject content) throws Exception {
		return (E)JsonParser.parse(request(HttpMethod.POST, path, locale, status, content));
	}
	
	@SuppressWarnings("unchecked")
	public <E extends JsonElement> E patch(Object path, JsonObject content) throws Exception {
		return (E)JsonParser.parse(request(HttpMethod.PATCH, path, null, HttpStatus.OK, content));
	}
	
	@SuppressWarnings("unchecked")
	public <E extends JsonElement> E patch(Object path, Locale locale, HttpStatus status, JsonObject content) throws Exception {
		return (E)JsonParser.parse(request(HttpMethod.PATCH, path, locale, status, content));
	}
	
	@SuppressWarnings("unchecked")
	public <E extends JsonElement> E put(Object path, JsonObject content) throws Exception {
		return (E)JsonParser.parse(request(HttpMethod.PUT, path, null, HttpStatus.OK, content));
	}
	
	@SuppressWarnings("unchecked")
	public <E extends JsonElement> E put(Object path, Locale locale, HttpStatus status, JsonObject content) throws Exception {
		return (E)JsonParser.parse(request(HttpMethod.PUT, path, locale, status, content));
	}
	
}
