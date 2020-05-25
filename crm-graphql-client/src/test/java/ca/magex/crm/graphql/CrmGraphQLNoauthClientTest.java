package ca.magex.crm.graphql;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.services.CrmInitializationService;
import ca.magex.crm.graphql.client.CrmServicesGraphQLClientImpl;
import ca.magex.crm.test.CrmServicesTestSuite;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = {
		MagexCrmProfiles.AUTH_EMBEDDED_JWT,
		MagexCrmProfiles.CRM_NO_AUTH,
		MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED
	})
public class CrmGraphQLNoauthClientTest {

	@LocalServerPort private int randomPort;
	
	@MockBean CrmInitializationService initializationService;

	@Test
	public void runTests() throws Exception {
		Mockito.when(initializationService.isInitialized()).thenReturn(true);
		
		/* we are running these tests with an embedded authentication server so everything is on the same servlet */
		CrmServicesGraphQLClientImpl crmServices = new CrmServicesGraphQLClientImpl("http://localhost:" + randomPort + "/crm/graphql");

		CrmServicesTestSuite testSuite = new CrmServicesTestSuite();		
		ReflectionTestUtils.setField(testSuite, "initializationService", initializationService);
		ReflectionTestUtils.setField(testSuite, "lookupService", crmServices);
		ReflectionTestUtils.setField(testSuite, "organizationService", crmServices);
		ReflectionTestUtils.setField(testSuite, "locationService", crmServices);
		ReflectionTestUtils.setField(testSuite, "personService", crmServices);
		ReflectionTestUtils.setField(testSuite, "userService", crmServices);
		ReflectionTestUtils.setField(testSuite, "permissionService", crmServices);

		testSuite.runAllTests();
	}
}
