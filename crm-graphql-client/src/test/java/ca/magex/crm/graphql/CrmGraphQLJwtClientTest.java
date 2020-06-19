package ca.magex.crm.graphql;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import ca.magex.crm.api.CrmProfiles;
import ca.magex.crm.api.policies.basic.BasicPolicies;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.services.CrmInitializationService;
import ca.magex.crm.graphql.client.CrmServicesGraphQLClientImpl;
import ca.magex.crm.test.CrmServicesTestSuite;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = {
	CrmProfiles.AUTH_EMBEDDED_JWT,
	CrmProfiles.CRM_AUTH,
	CrmProfiles.CRM_DATASTORE_CENTRALIZED
})
@Ignore
public class CrmGraphQLJwtClientTest {

	@LocalServerPort private int randomPort;
	
	@Test
	public void runTests() throws Exception {
		/* we are running these tests with an embedded authentication server so everything is on the same servlet */
		CrmServicesGraphQLClientImpl crmServices = new CrmServicesGraphQLClientImpl("http://localhost:" + randomPort + "/crm/graphql");
		crmServices.authenticateJwt("http://localhost:" + randomPort + "/crm/authenticate", "admin", "admin");
		
		CrmServicesTestSuite testSuite = new CrmServicesTestSuite(new Crm(
				Mockito.mock(CrmInitializationService.class), 
				crmServices, 
				new BasicPolicies(crmServices, crmServices, crmServices, crmServices, crmServices, crmServices)));
		testSuite.runAllTests();
	}
}