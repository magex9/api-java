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
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.spring.security.auth.AuthProfiles;
import ca.magex.crm.test.AbstractPersonServiceTests;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { RestfulClientTestConfig.class })
@ActiveProfiles(profiles = {
		AuthProfiles.EMBEDDED_HMAC,
		CrmProfiles.BASIC_NO_AUTH,
		CrmProfiles.DEV
})
public class RestfulPersonServiceTests extends AbstractPersonServiceTests {

	@LocalServerPort private int randomPort;
	
	@MockBean CrmAuthenticationService auth;
	
	@Autowired Crm crm;
		
	private CrmPersonService remoteServicesAdapter = null;	
	
	@Override
	protected Crm config() {
		return crm;
	}
	
	@Override
	protected CrmAuthenticationService auth() {
		return auth;
	}
	
	@Override
	protected CrmPersonService persons() {
		return remoteServicesAdapter;
	}
		
	@Before
	@Override
	public void setup() {	
		super.setup();
		remoteServicesAdapter = new RestTemplateClient("http://localhost:" + randomPort + "/crm", null, "admin", "admin").getServices();
	}

}