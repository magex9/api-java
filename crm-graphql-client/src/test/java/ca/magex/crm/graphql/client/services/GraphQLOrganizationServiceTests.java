package ca.magex.crm.graphql.client.services;

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
import ca.magex.crm.api.adapters.CrmServicesAdapter;
import ca.magex.crm.api.authentication.CrmAuthenticationService;
import ca.magex.crm.api.services.CrmConfigurationService;
import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.graphql.client.GraphQLClient;
import ca.magex.crm.graphql.client.service.GraphQLOrganizationService;
import ca.magex.crm.graphql.config.GraphQLClientTestConfig;
import ca.magex.crm.test.AbstractOrganizationServiceTests;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { GraphQLClientTestConfig.class })
@ActiveProfiles(profiles = {
		CrmProfiles.AUTH_EMBEDDED_JWT,
		CrmProfiles.CRM_NO_AUTH,
		CrmProfiles.DEV
})
public class GraphQLOrganizationServiceTests extends AbstractOrganizationServiceTests{

	@LocalServerPort private int randomPort;
	
	@MockBean PlatformTransactionManager txManager;
	
	@MockBean CrmAuthenticationService auth;
	
	@Autowired Crm crm;
	
	private CrmServicesAdapter remoteServicesAdapter = null;	
	
	@Override
	protected CrmConfigurationService config() {
		return crm;
	}
	
	@Override
	protected CrmOptionService options() {
		return crm;
	}
	
	@Override
	protected CrmAuthenticationService auth() {
		return auth;
	}
	
	@Override
	protected CrmServices crmServices() {
		return remoteServicesAdapter;
	}
		
	@Before
	@Override
	public void setup() {	
		super.setup();		
		/* setup our remote graphql service that is to be tested */
		GraphQLClient client = new GraphQLClient(
				"http://localhost:" + randomPort + "/crm/graphql",
				"/organization-service-queries.properties");		
		remoteServicesAdapter = new CrmServicesAdapter(crm, crm, new GraphQLOrganizationService(client), crm, crm, crm);
	}
}
