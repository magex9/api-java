package ca.magex.crm.restful.client.services;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.PlatformTransactionManager;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.CrmProfiles;
import ca.magex.crm.api.authentication.CrmAuthenticationService;
import ca.magex.crm.api.services.CrmConfigurationService;
import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.restful.client.RestTemplateClient;
import ca.magex.crm.restful.client.config.RestfulClientTestConfig;
import ca.magex.crm.spring.security.auth.AuthProfiles;
import ca.magex.crm.test.AbstractOptionServiceTests;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { RestfulClientTestConfig.class })
@ActiveProfiles(profiles = {
		AuthProfiles.EMBEDDED_HMAC,
		CrmProfiles.BASIC_NO_AUTH,
		CrmProfiles.DEV
})
public class RestfulOptionServiceTests extends AbstractOptionServiceTests {

	@LocalServerPort private int randomPort;
	
	@MockBean PlatformTransactionManager txManager;
	
	@MockBean CrmAuthenticationService auth;
	
	@Autowired CrmConfigurationService config;
	
	@Autowired Crm crm;
		
	private CrmOptionService remoteServicesAdapter = null;	
	
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
	protected CrmOptionService options() {
		return remoteServicesAdapter;
	}
		
	@Before
	@Override
	public void setup() {	
		super.setup();
		remoteServicesAdapter = new RestTemplateClient("http://localhost:" + randomPort + "/crm", null, "admin", "admin").getServices();
	}
}