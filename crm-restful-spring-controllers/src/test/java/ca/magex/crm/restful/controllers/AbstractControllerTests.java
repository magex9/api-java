package ca.magex.crm.restful.controllers;

import static ca.magex.crm.test.CrmAsserts.ORG_AUTH_GROUPS;
import static ca.magex.crm.test.CrmAsserts.ORG_BIZ_GROUPS;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_EMAIL;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_ORG;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_PERSON;
import static org.junit.Assert.assertEquals;

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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.CrmProfiles;
import ca.magex.crm.api.authentication.CrmAuthenticationService;
import ca.magex.crm.api.system.id.AuthenticationGroupIdentifier;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
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
	
	public OrganizationIdentifier getSystemOrganizationIdentifier() {
		return crm.findOrganizationSummaries(crm.defaultOrganizationsFilter().withAuthenticationGroup(AuthenticationGroupIdentifier.SYS)).getSingleItem().getOrganizationId();
	}
	
	public LocationIdentifier getSystemLocationIdentifier() {
		return crm.findOrganizationDetails(getSystemOrganizationIdentifier()).getMainLocationId();
	}
	
	public PersonIdentifier getSystemAdminIdentifier() {
		return crm.findOrganizationDetails(getSystemOrganizationIdentifier()).getMainContactId();
	}
	
	public OrganizationIdentifier createTestOrganization() {
		return crm.createOrganization("Test Org", ORG_AUTH_GROUPS, ORG_BIZ_GROUPS).getOrganizationId();
	}
	
	public String request(HttpMethod method, Object path, Locale locale, HttpStatus status, JsonObject content) throws Exception {
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
			.request(method, "/rest" + path)
			.header("Content-Type", locale == null ? "application/json+ld" : "application/json")
			.header("Locale", locale == null ? "" : locale);
		
		if (content != null) {
			if (method == HttpMethod.GET) {
				for (String key : content.keys()) {
					builder = builder.queryParam(key, JsonElement.unwrap(content.get(key)).toString());
				}
			} else {
				builder = builder.content(content.toString());
			}
		}
		
		return mockMvc.perform(builder)
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
	public <E extends JsonElement> E get(Object path, Locale locale, HttpStatus status, JsonObject content) throws Exception {
		return (E)JsonParser.parse(request(HttpMethod.GET, path, locale, status, content));
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
