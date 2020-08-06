package ca.magex.crm.restful.client;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.CrmProfiles;
import ca.magex.crm.api.authentication.CrmAuthenticationService;
import ca.magex.crm.api.services.CrmConfigurationService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.test.AbstractUserServiceTests;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { RestfulClientTestConfig.class })
@ActiveProfiles(profiles = {
		CrmProfiles.AUTH_EMBEDDED_JWT,
		CrmProfiles.CRM_NO_AUTH,
		CrmProfiles.DEV
})
public class RestfulUserServiceTests extends AbstractUserServiceTests {

	@LocalServerPort private int randomPort;
	
	@MockBean CrmAuthenticationService auth;
	
	@Autowired CrmConfigurationService config;
	
	@Autowired Crm crm;
	
	private CrmUserService remoteServicesAdapter = null;	
	
	@Override
	protected CrmConfigurationService config() {
		return config;
	}
	
	@Override
	protected CrmServices crm() {
		return crm;
	}
	
	@Override
	protected CrmAuthenticationService auth() {
		return auth;
	}
	
	@Override
	protected CrmUserService users() {
		return remoteServicesAdapter;
	}
		
	@Before
	@Override
	public void setup() {	
		super.setup();
		remoteServicesAdapter = new RestTemplateClient("http://localhost:" + randomPort + "/crm", null, "admin", "admin").getServices();
	}

}