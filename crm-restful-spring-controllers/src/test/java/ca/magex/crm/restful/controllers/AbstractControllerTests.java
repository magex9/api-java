package ca.magex.crm.restful.controllers;

import static ca.magex.crm.test.CrmAsserts.PERSON_NAME;
import static ca.magex.crm.test.CrmAsserts.SYS_ADMIN;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.services.Crm;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = {
		MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED,
		MagexCrmProfiles.CRM_NO_AUTH
})
public abstract class AbstractControllerTests {
	
	@Autowired protected Crm crm;
	
	@Autowired protected MockMvc mockMvc;
	
	public void initialize() {
		crm.reset();
		crm.initializeSystem(SYS_ADMIN.getEnglishName(), PERSON_NAME, "admin@localhost", "system", "admin");
	}
	
}
