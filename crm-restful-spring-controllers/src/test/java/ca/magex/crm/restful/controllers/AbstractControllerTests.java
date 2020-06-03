package ca.magex.crm.restful.controllers;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.services.CrmInitializationService;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.json.model.JsonObject;

import static ca.magex.crm.test.CrmAsserts.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = {
		MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED,
		MagexCrmProfiles.CRM_NO_AUTH
})
public abstract class AbstractControllerTests {
	
	@Autowired protected CrmInitializationService initialization;

	@Autowired protected CrmPermissionService permissions;
	
	@Autowired protected CrmOrganizationService organizations;
	
	@Autowired protected CrmLocationService locations;
	
	@Autowired protected CrmPersonService persons;
	
	@Autowired protected CrmUserService users;

	@Autowired protected MockMvc mockMvc;
	
	public void initialize() {
		initialization.reset();
		initialization.initializeSystem(SYS_ADMIN.getEnglishName(), PERSON_NAME, "admin@localhost", "system", "admin");
	}
	
	public void printLookupAsserts(JsonObject json) {
		System.out.println("====================================================");
		System.out.println("\t\t//printLookupAsserts(json);");
		System.out.println("\t\tassertEquals(" + json.getArray("content").size() + ", json.getInt(\"total\"));");
		System.out.println("\t\tassertEquals(JsonArray.class, json.get(\"content\").getClass());");
		System.out.println("\t\tassertEquals(" + json.getArray("content").size() + ", json.getArray(\"content\").size());");
		for (int i = 0; i < json.getArray("content").size(); i++) {
			System.out.println("\t\tassertEquals(\"" + json.getArray("content").getString(i) + "\", json.getArray(\"content\").getString(" + i + "));");
		}
		System.out.println("====================================================");
	}
	
}
